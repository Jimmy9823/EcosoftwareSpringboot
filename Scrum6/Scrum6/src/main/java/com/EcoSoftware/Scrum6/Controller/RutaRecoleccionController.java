package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rutas")
@RequiredArgsConstructor
public class RutaRecoleccionController {

    private final RutaRecoleccionService rutaService;

    @PostMapping
public ResponseEntity<RutaRecoleccionDTO> crear(@RequestBody RutaRecoleccionEntity ruta) {
    RutaRecoleccionEntity saved = rutaService.crearRuta(ruta);
    return ResponseEntity.ok(toDto(saved));
}


    @GetMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return rutaService.obtenerPorId(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RutaRecoleccionDTO>> listarTodas() {
        List<RutaRecoleccionDTO> dtos = rutaService.listarTodas()
                .stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recolector/{id}")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarPorRecolector(@PathVariable Long id) {
        List<RutaRecoleccionDTO> dtos = rutaService.listarPorRecolector(id)
                .stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> actualizar(
            @PathVariable Long id,
            @RequestBody RutaRecoleccionEntity rutaActualizada
    ) {
        RutaRecoleccionEntity updated = rutaService.actualizarRuta(id, rutaActualizada);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }

    // Mapper: Entity → DTO
    private RutaRecoleccionDTO toDto(RutaRecoleccionEntity r) {
        RutaRecoleccionDTO dto = new RutaRecoleccionDTO();
        dto.setIdRuta(r.getIdRuta());
        dto.setRecolectorId(r.getRecolector() != null ? r.getRecolector().getIdUsuario() : null);
        dto.setNombre(r.getNombre());
        dto.setDescripcion(r.getDescripcion());
        dto.setZonasCubiertas(r.getZonasCubiertas());
        dto.setFechaCreacion(r.getFechaCreacion());
        return dto;
    }
}
