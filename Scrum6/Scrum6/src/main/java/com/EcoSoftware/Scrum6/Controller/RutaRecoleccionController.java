package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaRecoleccionController {

    private final RutaRecoleccionService rutaService;

    // ---------------- CREAR RUTA ----------------
    @PostMapping
    public ResponseEntity<RutaRecoleccionDTO> crear(@RequestBody RutaRecoleccionDTO rutaDTO) {
        RutaRecoleccionDTO saved = rutaService.crearRuta(rutaDTO);
        return ResponseEntity.ok(saved);
    }

    // ---------------- OBTENER POR ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return rutaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------------- LISTAR TODAS ----------------
    @GetMapping
    public ResponseEntity<List<RutaRecoleccionDTO>> listarTodas() {
        List<RutaRecoleccionDTO> dtos = rutaService.listarTodas();
        return ResponseEntity.ok(dtos);
    }

    // ---------------- LISTAR POR RECOLECTOR ----------------
    @GetMapping("/recolector/{id}")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarPorRecolector(@PathVariable Long id) {
        List<RutaRecoleccionDTO> dtos = rutaService.listarPorRecolector(id);
        return ResponseEntity.ok(dtos);
    }

    // ---------------- ACTUALIZAR RUTA ----------------
    @PutMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> actualizar(
            @PathVariable Long id,
            @RequestBody RutaRecoleccionDTO rutaDTO
    ) {
        RutaRecoleccionDTO updated = rutaService.actualizarRuta(id, rutaDTO);
        return ResponseEntity.ok(updated);
    }

    // ---------------- ELIMINAR RUTA ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- ASIGNAR RECOLECCIONES ----------------
    @PostMapping("/{id}/recolecciones")
    public ResponseEntity<RutaRecoleccionDTO> asignarRecolecciones(
            @PathVariable Long id,
            @RequestBody List<Long> recoleccionIds
    ) {
        RutaRecoleccionDTO updated = rutaService.asignarRecolecciones(id, recoleccionIds);
        return ResponseEntity.ok(updated);
    }

    // ---------------- ESTABLECER PUNTO DE INICIO ----------------
    @PostMapping("/{id}/inicio/{recoleccionId}")
    public ResponseEntity<RutaRecoleccionDTO> establecerPuntoInicio(
            @PathVariable Long id,
            @PathVariable Long recoleccionId
    ) {
        RutaRecoleccionDTO updated = rutaService.establecerPuntoInicio(id, recoleccionId);
        return ResponseEntity.ok(updated);
    }

    // ---------------- ACTUALIZAR ESTADO DE RECOLECCIÓN ----------------
    @PatchMapping("/{rutaId}/recolecciones/{recoleccionId}/estado")
    public ResponseEntity<RutaRecoleccionDTO> actualizarEstado(
            @PathVariable Long rutaId,
            @PathVariable Long recoleccionId,
            @RequestParam String nuevoEstado
    ) {
        RutaRecoleccionDTO updated = rutaService.actualizarEstadoEnRuta(rutaId, recoleccionId, nuevoEstado);
        return ResponseEntity.ok(updated);
    }
}


