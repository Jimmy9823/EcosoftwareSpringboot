package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins= "*")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    public ResponseEntity<List<UsuarioDTO>> findAll(){
        List<UsuarioDTO> personas = usuarioService.findAll();
        return ResponseEntity.ok(personas);
    }
}
