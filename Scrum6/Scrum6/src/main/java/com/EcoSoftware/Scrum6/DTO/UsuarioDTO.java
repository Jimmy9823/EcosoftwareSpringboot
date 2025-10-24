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
    private Long rolId;

    @NotBlank
    private String nombre;

    @NotBlank
    private String contrasena;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String cedula;

    @NotBlank
    private String telefono;

    private String direccion;


    private String barrio;


    private String localidad;

    private String nit;

    private String representanteLegal;

    private String zona_de_trabajo;

    private String horario;

    private String tipoMaterial;

    private Integer cantidad_minima;

    private String imagen_perfil;

    private String certificaciones;

    private Boolean estado;

    private LocalDateTime fechaCreacion;
}

