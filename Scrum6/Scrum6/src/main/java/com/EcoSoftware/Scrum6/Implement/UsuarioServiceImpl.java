package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().
                map(this::convertirADTO).toList();
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario).
                orElseThrow(()->new RuntimeException("Persona no encontrada con ID: " + idUsuario));
        return convertirADTO(usuarioEntity);
    }


    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        UsuarioEntity entity = modelMapper.map(usuarioDTO, UsuarioEntity.class);

        if (usuarioDTO.getRolId() != null) {
            RolEntity rol = rolRepository.findById(usuarioDTO.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + usuarioDTO.getRolId()));
            entity.setRol(rol);
        } else {
            throw new RuntimeException("El rol es obligatorio");
        }

        UsuarioEntity saved = usuarioRepository.save(entity);
        return modelMapper.map(saved, UsuarioDTO.class);
    }

    @Override
    public UsuarioEditarDTO actualizarUsuario(Long idUsuario, UsuarioEditarDTO usuarioDTO) {
        UsuarioEntity usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        String rol= usuarioExistente.getRol().getNombre();
        String zonaOriginal= usuarioExistente.getZona_de_trabajo();
        Integer cantidadMinima = usuarioExistente.getCantidad_minima();
        modelMapper.map(usuarioDTO, usuarioExistente);

        if (rol.equals("Ciudadano")){
                usuarioExistente.setZona_de_trabajo(zonaOriginal);
                usuarioExistente.setCantidad_minima(cantidadMinima);
        }

        UsuarioEntity usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return convertirAEditarUsuarioDTO(usuarioActualizado);
    }

    @Override
    public void eliminarPersona(Long idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        usuarioEntity.setEstado(false);
        usuarioRepository.delete(usuarioEntity);
    }

    private UsuarioEntity convertirAEntity(UsuarioDTO dto) {
        return modelMapper.map(dto, UsuarioEntity.class);
    }

    public UsuarioDTO convertirADTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioDTO.class);
    }

    public UsuarioEditarDTO convertirAEditarUsuarioDTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioEditarDTO.class);
    }

}
