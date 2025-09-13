package com.EcoSoftware.Scrum6.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data


public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long idUsuario;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)

    @NotNull(message = "El rol es obligatorio")
    private RolEntity rol;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 70, message = "El nombre debe tener entre 2 y 70 caracteres")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @Column(nullable = false, unique = true)
    @Email(message = "El correo debe ser válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "La cédula es obligatoria")
    private String cedula;

    @Column(nullable = false)
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Column(unique = true)
    private String NIT;

    @Column
    private String direccion;

    @Column(nullable = false)
    @NotBlank(message = "El barrio es obligatorio")
    private String barrio;

    @Column(nullable = false)
    @NotBlank(message = "La localidad es obligatoria")
    private String localidad;

    @Column
    private String zona_de_trabajo;

    @Column(nullable = false)
    @NotBlank(message = "El tipo de rol es obligatorio")
    private String tipo_rol;

    @Column
    private String horario;

    @Column
    private String certificaciones;

    @Column
    private String imagen_perfil;

    @Column
    private Integer cantidad_minima;

    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

}
