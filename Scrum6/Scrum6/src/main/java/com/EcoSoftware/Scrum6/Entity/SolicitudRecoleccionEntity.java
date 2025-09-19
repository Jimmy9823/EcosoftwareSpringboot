package com.EcoSoftware.Scrum6.Entity;

/* Imports */
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Clase Entidad para Solicitud de Recolección */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "solicitud_recoleccion")
public class SolicitudRecoleccionEntity {

    /* Atributos */

    // Identificador único de la solicitud
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de residuo a recolectar
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residuo", nullable = false, length = 20)
    @NotBlank(message = "El tipo de residuo es obligatorio" )
    private TipoResiduo tipoResiduo;

    // Cantidad del residuo a recolectar
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "La cantidad es obligatoria")
    private BigDecimal cantidad;

    // Estado de la petición
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_peticion", nullable = false)
    private EstadoPeticion estadoPeticion = EstadoPeticion.PENDIENTE;

    // Descripción adicional de la solicitud
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "La descripción nos ayuda a entender mejor tu solicitud")
    private String descripcion;

    // Ubicación para la recolección
    @Column(nullable = false, length = 255)
    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    // Evidencia  de la solicitud
    @Column(name = "evidencia",nullable = false, length = 500)
    @NotBlank(message = "La evidencia es obligatoria para procesar la solicitud")
    private String evidencia;

    // Fecha y hora de creación de la solicitud
    @CreationTimestamp// se establece una fecha y hora automáticamente al crear la entidad
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion; //OffsetDateTime para manejar zonas horarias adecuadamente 

    // Fecha y hora programada para la recolección
    @Column(name = "fecha_programada", nullable = false)
    @NotNull(message = "La fecha programada es obligatoria")
    private OffsetDateTime fechaProgramada;

    // Relación Many-to-One con UsuarioEntity
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    /*Enums de Solicitud Recolección */
    
    //Tipos de Residuos
    public enum TipoResiduo {
        PLASTICO,
        VIDRIO,
        PAPEL_CARTON,
        ORGANICO,
        ELECTRONICO,
        METAL,
        OTRO
    }

    //Estados de la Petición
    public enum EstadoPeticion {
        PENDIENTE,
        ACEPTADA,
        CANCELADA
    }

}
