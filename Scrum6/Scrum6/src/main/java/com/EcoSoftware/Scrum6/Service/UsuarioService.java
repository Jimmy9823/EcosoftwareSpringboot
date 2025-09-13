package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import org.springframework.stereotype.Service;

import java.util.List;
public interface UsuarioService {
    List<UsuarioDTO> findAll();

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO editarUsuario(UsuarioDTO usuarioDTO);
}
