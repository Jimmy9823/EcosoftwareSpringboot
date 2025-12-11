package com.EcoSoftware.Scrum6.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.EcoSoftware.Scrum6.Enums.EstadoParada;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ruta_parada")
public class RutaParadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParada;

    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutaRecoleccionEntity ruta;

    // Cada parada corresponde a 1 recolección
    @OneToOne
    @JoinColumn(name = "recoleccion_id", nullable = false, unique = true)
    private RecoleccionEntity recoleccion;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoParada estado = EstadoParada.Pendiente;

    private LocalDateTime fechaAtencion;
}
