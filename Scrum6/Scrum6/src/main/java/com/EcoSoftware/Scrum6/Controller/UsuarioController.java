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
@CrossOrigin(origins = " http://localhost:4200", allowCredentials = "true")
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
    //Filtra correo por exactitud y/o coincidencia
    @GetMapping("/filtrar-correo")
    public ResponseEntity<List<UsuarioDTO>> obtenerCorreo(@RequestParam String correo) {
        List<UsuarioDTO> usuarios = usuarioService.encontrarPorCorreo(correo);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    //Filtra documento (NIT o CEDULA) por exactitud y/o coincidencia
    @GetMapping("/filtrar-documento")
    public ResponseEntity<List<UsuarioDTO>> obtenerDocumento(@RequestParam String documento) {
        List<UsuarioDTO> usuarios = usuarioService.encontrarPorDocumento(documento);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    //Filtra nombre por exactitud y/o coincidencia
    @GetMapping("/filtrar-nombre")
    public ResponseEntity<List<UsuarioDTO>> obtenerNombre(@RequestParam String nombre) {
        List<UsuarioDTO> usuarios = usuarioService.encontrarPorNombre(nombre);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
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

    //Actualiza un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEditarDTO> actualizarUsuario(@Valid @RequestBody UsuarioEditarDTO usuarioDTO, @PathVariable Long id) {
        try{
            UsuarioEditarDTO actualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminacionPorEstado(@PathVariable Long id){
        try{
            usuarioService.eliminacionPorEstado(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Backend OK");
    }

    //Metodo de eliminado en base de datos, activar solo si no es suficiente eliminacion logica
    /*@DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try{usuarioService.eliminarPersona(id);
            return ResponseEntity.ok("Usuario eliminado");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }*/
}
