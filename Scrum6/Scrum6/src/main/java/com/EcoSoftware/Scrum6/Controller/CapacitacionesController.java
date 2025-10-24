package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.*;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;
import com.EcoSoftware.Scrum6.Service.CapacitacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/capacitaciones")
public class CapacitacionesController {

    @Autowired
    private CapacitacionesService service;

    // ===========================
    // CAPACITACIONES
    // ===========================
    @PostMapping
    public ResponseEntity<CapacitacionDTO> crearCapacitacion(@RequestBody CapacitacionDTO dto) {
        return ResponseEntity.ok(service.crearCapacitacion(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CapacitacionDTO> actualizarCapacitacion(@PathVariable Long id, @RequestBody CapacitacionDTO dto) {
        return ResponseEntity.ok(service.actualizarCapacitacion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCapacitacion(@PathVariable Long id) {
        service.eliminarCapacitacion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapacitacionDTO> obtenerCapacitacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerCapacitacionPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CapacitacionDTO>> listarCapacitaciones() {
        return ResponseEntity.ok(service.listarTodasCapacitaciones());
    }

    // ===========================
    // MODULOS
    // ===========================
    @PostMapping("/modulos")
    public ResponseEntity<ModuloDTO> crearModulo(@RequestBody ModuloDTO dto) {
        return ResponseEntity.ok(service.crearModulo(dto));
    }

    @PutMapping("/modulos/{id}")
    public ResponseEntity<ModuloDTO> actualizarModulo(@PathVariable Long id, @RequestBody ModuloDTO dto) {
        return ResponseEntity.ok(service.actualizarModulo(id, dto));
    }

    @DeleteMapping("/modulos/{id}")
    public ResponseEntity<Void> eliminarModulo(@PathVariable Long id) {
        service.eliminarModulo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{capacitacionId}/modulos")
    public ResponseEntity<List<ModuloDTO>> listarModulosPorCapacitacion(@PathVariable Long capacitacionId) {
        return ResponseEntity.ok(service.listarModulosPorCapacitacion(capacitacionId));
    }

    // ===========================
    // INSCRIPCIONES
    // ===========================
    @PostMapping("/inscripciones")
    public ResponseEntity<InscripcionDTO> inscribirse(@RequestParam Long usuarioId, @RequestParam Long cursoId) {
        return ResponseEntity.ok(service.inscribirse(usuarioId, cursoId));
    }

    @PutMapping("/inscripciones/{id}")
    public ResponseEntity<InscripcionDTO> actualizarEstadoInscripcion(@PathVariable Long id, @RequestParam EstadoCurso estado) {
        return ResponseEntity.ok(service.actualizarEstadoInscripcion(id, estado));
    }

    @GetMapping("/inscripciones/usuario/{usuarioId}")
    public ResponseEntity<List<InscripcionDTO>> listarInscripcionesPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarInscripcionesPorUsuario(usuarioId));
    }

    @GetMapping("/inscripciones/curso/{cursoId}")
    public ResponseEntity<List<InscripcionDTO>> listarInscripcionesPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(service.listarInscripcionesPorCurso(cursoId));
    }

    // ===========================
    // PROGRESO
    // ===========================
    @PostMapping("/progreso")
    public ResponseEntity<ProgresoDTO> registrarProgreso(@RequestBody ProgresoDTO dto) {
        return ResponseEntity.ok(service.registrarProgreso(dto));
    }

    @PutMapping("/progreso/{id}")
    public ResponseEntity<ProgresoDTO> actualizarProgreso(@PathVariable Long id, @RequestBody ProgresoDTO dto) {
        return ResponseEntity.ok(service.actualizarProgreso(id, dto));
    }

    @GetMapping("/progreso/usuario/{usuarioId}")
    public ResponseEntity<List<ProgresoDTO>> listarProgresosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarProgresosPorUsuario(usuarioId));
    }

    @GetMapping("/progreso/curso/{cursoId}")
    public ResponseEntity<List<ProgresoDTO>> listarProgresosPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(service.listarProgresosPorCurso(cursoId));
    }
}
