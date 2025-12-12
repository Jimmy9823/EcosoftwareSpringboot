package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.CrearRutaDTO;
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

    // Crear ruta simple (compatible)
    @PostMapping
    public ResponseEntity<RutaRecoleccionDTO> crear(@RequestBody RutaRecoleccionDTO dto) {
        return ResponseEntity.ok(rutaService.crearRuta(dto));
    }

    // Crear ruta completa (crear + asignar recolecciones + crear paradas)
    @PostMapping("/completa")
    public ResponseEntity<RutaRecoleccionDTO> crearCompleta(@RequestBody CrearRutaDTO dto) {
        return ResponseEntity.ok(rutaService.crearRutaCompleta(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutaRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return rutaService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mis-rutas")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarMisRutas() {
        // puedes pasar el id del recolector a listarPorRecolector desde security en service
        // para simplicidad llamamos al servicio que internamente puede usar security si lo deseas
        // Aquí devolvemos todas (o podrías filtrar por recolector con un método nuevo)
        return ResponseEntity.ok(rutaService.listarTodas());
    }

    @GetMapping("/recolector/{id}")
    public ResponseEntity<List<RutaRecoleccionDTO>> listarPorRecolector(@PathVariable Long id) {
        return ResponseEntity.ok(rutaService.listarPorRecolector(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}
