package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "*")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    //Trae todos los usuarios registrados al cargar el modulo usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarPersonas() {
        List<UsuarioDTO> personas = usuarioService.listarUsuarios();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }

    //Trae usuarios por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Registra un nuevo usuario
    @PostMapping
    public ResponseEntity<UsuarioDTO> insertarUsuario(@Valid @RequestBody UsuarioDTO usuario) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEditarDTO> actualizarUsuario(@Valid @RequestBody UsuarioEditarDTO usuarioDTO, @PathVariable Long id) {
        try{
            UsuarioEditarDTO actualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarPersona(id);
        return ResponseEntity.ok("Usuario eliminado");
    }
}
