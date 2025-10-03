package com.EcoSoftware.Scrum6.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.itextpdf.text.DocumentException;

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

    // ================================
    //  MÉTODOS EXPORTACIÓN
    // ================================
    void exportUsuariosToExcel(String nombre, String correo, String documento, OutputStream os) throws IOException;

    void exportUsuariosToPDF(String nombre, String correo, String documento, OutputStream os) throws IOException, DocumentException;
}

