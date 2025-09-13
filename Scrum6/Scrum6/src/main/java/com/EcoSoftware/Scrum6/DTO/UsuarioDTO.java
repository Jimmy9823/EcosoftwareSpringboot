package com.EcoSoftware.Scrum6.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String NIT;

    private String direccion;

    @NotBlank(message = "El barrio es obligatorio")
    private String barrio;

    @NotBlank(message = "La localidad es obligatoria")
    private String localidad;

    private String zona_de_trabajo;

    @NotBlank(message = "El tipo de rol es obligatorio")
    private String tipo_rol;

    private String horario;

    private String certificaciones;

    private String imagen_perfil;

    private Integer cantidad_minima;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
