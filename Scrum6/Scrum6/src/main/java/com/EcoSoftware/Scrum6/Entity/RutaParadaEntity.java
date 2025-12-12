package com.EcoSoftware.Scrum6.Entity;

import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ruta_parada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RutaParadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutaRecoleccionEntity ruta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recoleccion_id", nullable = false)
    private RecoleccionEntity recoleccion;

    @Column(nullable = false)
    private Integer orden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoParada estado;

    private OffsetDateTime fechaAtencion;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;
}
