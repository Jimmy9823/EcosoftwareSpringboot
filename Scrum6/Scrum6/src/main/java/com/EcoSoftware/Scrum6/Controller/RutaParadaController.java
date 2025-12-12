package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;
import com.EcoSoftware.Scrum6.Service.RutaParadaService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paradas")
@RequiredArgsConstructor
public class RutaParadaController {

    private final RutaParadaService paradaService;

    @GetMapping("/{id}")
    public ResponseEntity<RutaParadaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(paradaService.obtenerPorId(id));
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<RutaParadaDTO>> obtenerPorRuta(@PathVariable Long rutaId) {
        return ResponseEntity.ok(paradaService.obtenerPorRuta(rutaId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<RutaParadaDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado
    ) {
        return ResponseEntity.ok(paradaService.actualizarEstado(id, estado));
    }
}
