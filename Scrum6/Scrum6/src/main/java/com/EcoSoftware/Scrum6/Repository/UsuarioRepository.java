package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {


    void findByRol(RolEntity rol);
    Optional<UsuarioEntity> findByNitAndEstadoTrue(String nit);
    Optional<UsuarioEntity> findByCedulaAndEstadoTrue(String cedula);
    List<UsuarioEntity> findByNITContainingIgnoreCaseAndEstadoTrue(String nit);
    List<UsuarioEntity> findByCedulaContainingIgnoreCaseAndEstadoTrue(String cedula);
    Optional<UsuarioEntity> findByNombreAndEstadoTrue(String nombre);
    List<UsuarioEntity> findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre);
    Optional<UsuarioEntity> findByCorreoAndEstadoTrue(String correo);

    List<UsuarioEntity> findByCorreoContainingIgnoreCaseAndEstadoTrue(String correo);
    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.estado = false WHERE u.idUsuario = :id")
    int eliminacionLogica(@Param("id") Long id);
}
