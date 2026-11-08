package com.nutrivida.backend.repository;

import com.nutrivida.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
