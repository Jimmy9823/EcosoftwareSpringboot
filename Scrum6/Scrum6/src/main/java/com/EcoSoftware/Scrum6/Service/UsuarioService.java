package com.EcoSoftware.Scrum6.Service;
import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import java.util.List;


public interface UsuarioService {

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO obtenerUsuarioPorId(Long idUsuario);

    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);

    UsuarioEditarDTO actualizarUsuario(Long idUsuario, UsuarioEditarDTO usuDTO);

    void eliminarPersona(Long idUsuario);

    void eliminacionPorEstado(Long idUsuario);

    List<UsuarioDTO> encontrarPorDocumento(String documento);
    List<UsuarioDTO> encontrarPorNombre(String nombre);
    List<UsuarioDTO> encontrarPorCorreo(String correo);


}
