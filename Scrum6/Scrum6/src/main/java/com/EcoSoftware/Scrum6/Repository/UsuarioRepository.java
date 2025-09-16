package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {



    Optional<UsuarioEntity> findByNITAndEstadoTrue(String NIT);
    Optional<UsuarioEntity> findByNombreAndEstadoTrue(String nombre);
    Optional<UsuarioEntity> findByCorreoAndEstadoTrue(String correo);
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.estado = false WHERE u.idUsuario = :id")
    void softDeleteById(@Param("id") Long id);
}
