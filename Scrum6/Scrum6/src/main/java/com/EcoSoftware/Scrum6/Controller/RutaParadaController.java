package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import com.EcoSoftware.Scrum6.Service.RutaParadaService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paradas")
@RequiredArgsConstructor
public class RutaParadaController {

    private final RutaParadaService rutaParadaService;

    // ----------------------------------------------------------
    // AGREGAR PARADA A UNA RUTA
    // ----------------------------------------------------------
    @PostMapping("/ruta/{rutaId}")
    public ResponseEntity<?> agregarParada(
            @PathVariable Long rutaId,
            @RequestParam Long recoleccionId,
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        try {
            RutaParadaDTO dto = rutaParadaService.agregarParada(rutaId, recoleccionId, lat, lng);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ----------------------------------------------------------
    // LISTAR PARADAS DE UNA RUTA
    // ----------------------------------------------------------
    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<?> listarParadas(@PathVariable Long rutaId) {
        try {
            List<RutaParadaDTO> paradas = rutaParadaService.listarParadas(rutaId);
            return ResponseEntity.ok(paradas);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ----------------------------------------------------------
    // MOVER PARADA HACIA ARRIBA
    // ----------------------------------------------------------
    @PutMapping("/{paradaId}/arriba")
    public ResponseEntity<?> moverArriba(@PathVariable Long paradaId) {
        try {
            RutaParadaDTO dto = rutaParadaService.moverArriba(paradaId);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ----------------------------------------------------------
    // MOVER PARADA HACIA ABAJO
    // ----------------------------------------------------------
    @PutMapping("/{paradaId}/abajo")
    public ResponseEntity<?> moverAbajo(@PathVariable Long paradaId) {
        try {
            RutaParadaDTO dto = rutaParadaService.moverAbajo(paradaId);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // ----------------------------------------------------------
    // ACTUALIZAR ESTADO DE UNA PARADA
    // ----------------------------------------------------------
    @PutMapping("/{paradaId}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Long paradaId,
            @RequestParam EstadoParada estado
    ) {
        try {
            RutaParadaDTO dto = rutaParadaService.actualizarEstado(paradaId, estado);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
