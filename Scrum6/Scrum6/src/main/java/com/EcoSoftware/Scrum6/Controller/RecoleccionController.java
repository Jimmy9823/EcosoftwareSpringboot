package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RecoleccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recolecciones")
@RequiredArgsConstructor
public class RecoleccionController {

    private final RecoleccionService recoleccionService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<RecoleccionDTO>> listarTodas() {
        return ResponseEntity.ok(
                recoleccionService.listarTodas().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return recoleccionService.obtenerPorId(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // -> LISTAR RECOLECCIONES DE UN RECOLECTOR (público)
    @GetMapping("/recolector/{id}")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRecolector(@PathVariable Long id) {
        return ResponseEntity.ok(
                recoleccionService.listarPorRecolector(id).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // -> LISTAR RECOLECCIONES DEL RECOLECTOR AUTENTICADO (todas las asignadas)
    @GetMapping("/mis-recolecciones")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRecolector() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        UsuarioEntity recolector = usuarioRepository.findByCorreoAndEstadoTrue(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));

        return ResponseEntity.ok(
                recoleccionService.listarPorRecolector(recolector.getIdUsuario()).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // -> LISTAR RECOLECCIONES EN PROCESO SIN RUTA (para crear rutas)
    @GetMapping("/mis-recolecciones-en-proceso")
    public ResponseEntity<List<RecoleccionDTO>> listarMisRecoleccionesEnProcesoSinRuta() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        UsuarioEntity recolector = usuarioRepository.findByCorreoAndEstadoTrue(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));

        List<RecoleccionDTO> dtos = recoleccionService
                .listarSinRutaPorRecolector(recolector.getIdUsuario())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/activas")
    public ResponseEntity<List<RecoleccionDTO>> listarActivas() {
        return ResponseEntity.ok(
                recoleccionService.listarActivas().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/ruta/{id}")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRuta(@PathVariable Long id) {
        return ResponseEntity.ok(
                recoleccionService.listarPorRuta(id).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<RecoleccionDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoRecoleccion estado
    ) {
        RecoleccionEntity actualizadaEstado = recoleccionService.actualizarEstado(id, estado);
        return ResponseEntity.ok(toDto(actualizadaEstado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecoleccionDTO> actualizarRecoleccion(
            @PathVariable Long id,
            @RequestBody RecoleccionDTO dto
    ) {
        RecoleccionEntity actualizar = recoleccionService.actualizarRecoleccion(id, dto);
        return ResponseEntity.ok(toDto(actualizar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogicamente(@PathVariable Long id) {
        recoleccionService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }

    private RecoleccionDTO toDto(RecoleccionEntity r) {
        RecoleccionDTO dto = new RecoleccionDTO();
        dto.setIdRecoleccion(r.getIdRecoleccion());
        dto.setSolicitudId(r.getSolicitud() != null ? r.getSolicitud().getIdSolicitud() : null);
        dto.setRecolectorId(r.getRecolector() != null ? r.getRecolector().getIdUsuario() : null);
        dto.setRutaId(r.getRuta() != null ? r.getRuta().getIdRuta() : null);
        dto.setEstado(r.getEstado());
        dto.setFechaRecoleccion(r.getFechaRecoleccion());
        dto.setOrdenParada(r.getOrdenParada());           // <- añadido
        dto.setObservaciones(r.getObservaciones());
        dto.setEvidencia(r.getEvidencia());
        dto.setFechaCreacionRecoleccion(r.getFechaCreacionRecoleccion());
        return dto;
    }
}
