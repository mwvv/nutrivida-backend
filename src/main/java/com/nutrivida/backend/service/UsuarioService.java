package com.nutrivida.backend.service;

import com.nutrivida.backend.dto.UsuarioDTO;
import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final String ROL_POR_DEFECTO = "CLIENTE";

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO.Response> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<UsuarioDTO.Response> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toResponse);
    }

    public UsuarioDTO.Response guardar(UsuarioDTO.Request dto) {
        Usuario u = new Usuario();
        copiarDatos(dto, u);
        u.setRol(ROL_POR_DEFECTO); // todo usuario nuevo queda CLIENTE
        return toResponse(usuarioRepository.save(u));
    }

    public Optional<UsuarioDTO.Response> actualizar(Long id, UsuarioDTO.Request dto) {
        return usuarioRepository.findById(id).map(u -> {
            copiarDatos(dto, u); // el rol actual no se toca
            return toResponse(usuarioRepository.save(u));
        });
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private void copiarDatos(UsuarioDTO.Request dto, Usuario u) {
        u.setRun(dto.getRun());
        u.setNombre(dto.getNombre());
        u.setApellidos(dto.getApellidos());
        u.setCorreo(dto.getCorreo());
        u.setContrasena(dto.getContrasena());
        u.setFechaNacimiento(dto.getFechaNacimiento());
        u.setRegionId(dto.getRegionId());
        u.setComunaId(dto.getComunaId());
        u.setObjetivo(dto.getObjetivo());
        u.setTipoAlimentacion(dto.getTipoAlimentacion());
        u.setRestricciones(dto.getRestricciones());
    }

    // No incluye la contraseña
    private UsuarioDTO.Response toResponse(Usuario u) {
        UsuarioDTO.Response r = new UsuarioDTO.Response();
        r.setId(u.getId());
        r.setRun(u.getRun());
        r.setNombre(u.getNombre());
        r.setApellidos(u.getApellidos());
        r.setCorreo(u.getCorreo());
        r.setFechaNacimiento(u.getFechaNacimiento());
        r.setRegionId(u.getRegionId());
        r.setComunaId(u.getComunaId());
        r.setObjetivo(u.getObjetivo());
        r.setTipoAlimentacion(u.getTipoAlimentacion());
        r.setRestricciones(u.getRestricciones());
        r.setRol(u.getRol());
        return r;
    }
}
