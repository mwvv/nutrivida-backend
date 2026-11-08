package com.nutrivida.backend.service;

import com.nutrivida.backend.model.Usuario;
import com.nutrivida.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final String ROL_POR_DEFECTO = "CLIENTE";

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        usuario.setId(null); // fuerza un INSERT aunque el JSON traiga un id
        if (usuario.getRol() == null) {
            usuario.setRol(ROL_POR_DEFECTO);
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario datos) {
        Optional<Usuario> existente = usuarioRepository.findById(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }
        datos.setId(id);
        if (datos.getRol() == null) {
            datos.setRol(existente.get().getRol()); // conserva el rol actual si el JSON no lo trae
        }
        return Optional.of(usuarioRepository.save(datos));
    }

    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }
}
