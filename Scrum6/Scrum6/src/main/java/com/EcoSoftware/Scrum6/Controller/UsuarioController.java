package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins= "*")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
}
