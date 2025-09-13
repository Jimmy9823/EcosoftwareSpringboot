package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {



    Optional<UsuarioEntity> findByNIT(String NIT);
    Optional<UsuarioEntity> findByNombre(String nombre);
    Optional<UsuarioEntity> findByCorreo(String correo);

}
