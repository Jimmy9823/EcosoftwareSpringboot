package com.EcoSoftware.Scrum6.Entity;

import java.time.OffsetDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* Clase Entidad para las Rutas de Recolección */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ruta_recoleccion")
public class RutaRecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuta;

    // Recolector (usuario de rol reciclador/empresa) que genera la ruta
    @ManyToOne
    @JoinColumn(name = "recolector_id", nullable = false)
    private UsuarioEntity recolector;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
private List<RutaParadaEntity> paradas;


    // Recolecciones que se asignan a esta ruta
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL)
    private List<RecoleccionEntity> recolecciones;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre de la ruta es obligatorio")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 255)
    private String zonasCubiertas;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}

