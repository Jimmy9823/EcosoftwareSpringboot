package com.EcoSoftware.Scrum6.Entity;

import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitud_recoleccion")
public class SolicitudRecoleccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    // Usuario ciudadano que crea la solicitud
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    // Usuario que acepta la solicitud (reciclador o empresa)
    @ManyToOne
    @JoinColumn(name = "aceptada_por_id")
    private UsuarioEntity aceptadaPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false, length = 20)
    @NotNull(message = "El tipo de residuo es obligatorio")
    private TipoResiduo tipoResiduo;

    @Column(nullable = false, length = 100)
    @NotNull(message = "La cantidad es obligatoria")
    private String cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_peticion", nullable = false)
    private EstadoPeticion estadoPeticion = EstadoPeticion.Pendiente;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "La descripción es obligatoria")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "localidad", nullable = false)
    @NotNull(message = "La localidad es obligatoria")
    private Localidad localidad;

    @Column(nullable = false, length = 255)
    @NotNull(message = "La ubicación es obligatoria")
    private String ubicacion;

    @Column(name = "evidencia", nullable = false, length = 500)
    @NotNull(message = "La evidencia es obligatoria")
    private String evidencia;

    @CreationTimestamp
    @Column(name = "fecha_creacion_solicitud", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacionSolicitud;

    @Column(name = "fecha_programada", nullable = false)
    @NotNull(message = "La fecha programada es obligatoria")
    private OffsetDateTime fechaProgramada;

    // Relación con la recolección generada (si llega a aceptarse)
    @OneToOne(mappedBy = "solicitud")
    private RecoleccionEntity recoleccion;
}


