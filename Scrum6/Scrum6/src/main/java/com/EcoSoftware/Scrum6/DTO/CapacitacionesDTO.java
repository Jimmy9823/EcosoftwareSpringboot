package com.EcoSoftware.Scrum6.DTO;

import java.time.LocalDate;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;
import lombok.Data;

public class CapacitacionesDTO {

    @Data
    public static class CapacitacionDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private String numeroDeClases;
        private String duracion;
    }

    @Data
    public static class ModuloDTO {
        private Long id;
        private String duracion;
        private String descripcion;
        private Long capacitacionId;
    }

    @Data
    public static class InscripcionDTO {
        private Long id;
        private LocalDate fechaDeInscripcion;
        private EstadoCurso estadoCurso;
        private Long cursoId;
        private Long usuarioId;
    }

    @Data
    public static class ProgresoDTO {
        private Long id;
        private String progresoDelCurso;
        private String modulosCompletados;
        private String tiempoInvertido;
        private Long cursoId;
        private Long usuarioId;
    }
}
