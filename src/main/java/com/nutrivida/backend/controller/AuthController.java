package com.nutrivida.backend.controller;

import com.nutrivida.backend.dto.AuthResponse;
import com.nutrivida.backend.dto.LoginRequest;
import com.nutrivida.backend.dto.RegisterRequest;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
import com.nutrivida.backend.security.JwtService;
import com.nutrivida.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login y registro con JWT")
public class AuthController {

    private static final String ROL_REGISTRO = "CLIENTE";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Iniciar sesión y obtener un token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales correctas, devuelve el token"),
            @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Usa UsuarioDetailsService + BCrypt para comparar la contraseña con el hash guardado
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.correo(), request.contrasena()));

            String rol = auth.getAuthorities().iterator().next().getAuthority().replaceFirst("^ROLE_", "");
            return ResponseEntity.ok(new AuthResponse(jwtService.generarToken(auth.getName(), rol)));
        } catch (AuthenticationException e) {
            return error(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
    }

    @Operation(summary = "Registrar un nuevo cliente y obtener un token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado con rol CLIENTE"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Correo o RUN ya registrados")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByCorreo(request.correo()).isPresent()) {
            return error(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setRun(request.run());
        usuario.setNombre(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setCorreo(request.correo());
        usuario.setContrasena(request.contrasena()); // UsuarioService.guardar la encripta
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setRegionId(request.regionId());
        usuario.setComunaId(request.comunaId());
        usuario.setObjetivo(request.objetivo());
        usuario.setTipoAlimentacion(request.tipoAlimentacion());
        usuario.setRestricciones(request.restricciones());
        usuario.setRol(ROL_REGISTRO); // forzado: nunca se toma del body

        try {
            Usuario creado = usuarioService.guardar(usuario);
            String token = jwtService.generarToken(creado.getCorreo(), creado.getRol());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
        } catch (DataIntegrityViolationException e) {
            // RUN duplicado, o región/comuna/objetivo que no cumplen las FK/CHECK de la tabla
            return error(HttpStatus.CONFLICT, "No se pudo registrar: RUN duplicado o datos que no cumplen las reglas");
        }
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of(
                "status", estado.value(),
                "error", estado.getReasonPhrase(),
                "mensaje", mensaje));
    }
}
