package com.EcoSoftware.Scrum6.Service;
import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public interface UsuarioService {

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO obtenerUsuarioPorId(Long idUsuario);

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO actualizarPersona(Long idUsuario, UsuarioDTO usuarioDTO);

    void eliminarPersona(Long idUsuario);

}
