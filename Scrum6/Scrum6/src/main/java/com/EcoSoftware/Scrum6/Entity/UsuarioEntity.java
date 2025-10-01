package com.EcoSoftware.Scrum6.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false)
    private String telefono;

    @Column
    private String direccion;

    @Column(nullable = false)
    private String barrio;

    @Column(nullable = false)
    private String localidad;

    @Column
    private String nit;                      // Para empresas

    @Column
    private String representanteLegal;       // Para empresas

    @Column
    private String zona_de_trabajo;          // Para recicladores / empresas

    @Column
    private String horario;                  // Horario general

    @Column
    private String tipoMaterial;             // Para recicladores / empresas

    @Column
    private Integer cantidad_minima;         // Para empresas

    @Column
    private String imagen_perfil;

    @Column
    private String certificaciones;

    @Column(nullable = false)
    private Boolean estado = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}

