package com.EcoSoftware.Scrum6.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioDTO {
    private Long idUsuario;

    @NotNull(message = "El rol es obligatorio")
    private Long rolId; // Solo almacenamos el ID del rol para el DTO

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 70, message = "El nombre debe tener entre 2 y 70 caracteres")
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String correo;

    @NotBlank(message = "La cédula es obligatoria")
    private String cedula;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    private String nit;

    private String direccion;

    @NotBlank(message = "El barrio es obligatorio")
    private String barrio;

    @NotBlank(message = "La localidad es obligatoria")
    private String localidad;

    private String zona_de_trabajo;

    private String horario;

    private String certificaciones;

    private String imagen_perfil;

    private Integer cantidad_minima;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}
