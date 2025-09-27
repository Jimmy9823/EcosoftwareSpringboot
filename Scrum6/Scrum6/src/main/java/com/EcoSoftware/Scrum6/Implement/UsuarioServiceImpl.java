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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + idUsuario));
        return convertirADTO(usuarioEntity);
    }



    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getRolId() == null) {
            throw new RuntimeException("El rol es obligatorio");
        }

        UsuarioEntity entity = modelMapper.map(usuarioDTO, UsuarioEntity.class);

        // Rol obligatorio
        RolEntity rol = rolRepository.findById(usuarioDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + usuarioDTO.getRolId()));
        entity.setRol(rol);

        // Valores por defecto si no vienen de Angular
        if (entity.getEstado() == null) {
            entity.setEstado(true);
        }
        if (entity.getFechaCreacion() == null) {
            entity.setFechaCreacion(LocalDateTime.now());
        }
        if (entity.getFechaActualizacion() == null) {
            entity.setFechaActualizacion(LocalDateTime.now());
        }

        UsuarioEntity saved = usuarioRepository.save(entity);
        return modelMapper.map(saved, UsuarioDTO.class);
    }


    @Override
    public UsuarioEditarDTO actualizarUsuario(Long idUsuario, UsuarioEditarDTO usuarioDTO) {
        UsuarioEntity usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        String rol = usuarioExistente.getRol().getNombre();
        String zonaOriginal = usuarioExistente.getZona_de_trabajo();
        Integer cantidadMinima = usuarioExistente.getCantidad_minima();
        modelMapper.map(usuarioDTO, usuarioExistente);

        if (rol.equals("Ciudadano")) {
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
        usuarioRepository.delete(usuarioEntity);
    }

    @Override
    public void eliminacionPorEstado(Long idUsuario) {
        int filasActualizadas = usuarioRepository.eliminacionLogica(idUsuario);
        if (filasActualizadas == 0) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
    }

    //Busca por exactitud
    @Override
    public List<UsuarioDTO> encontrarPorNombre(String nombre) {
        Optional<UsuarioEntity> reExactos = usuarioRepository.findByNombreAndEstadoTrue(nombre);
        if (!reExactos.isEmpty()) {
            return reExactos.stream().map(this::convertirADTO).toList();
        } else {
            List<UsuarioEntity> parecidos = usuarioRepository.findByNombreContainingIgnoreCaseAndEstadoTrue(nombre);
            return parecidos.stream().map(this::convertirADTO).toList();
        }
    }

    @Override
    public List<UsuarioDTO> encontrarPorDocumento(String documento) {
        List<UsuarioEntity> usuarios = usuarioRepository.findByCedulaAndEstadoTrue(documento).stream().toList();
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByNitAndEstadoTrue(documento).stream().toList();
        }
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByCedulaContainingIgnoreCaseAndEstadoTrue(documento);
        }
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByNitContainingIgnoreCaseAndEstadoTrue(documento);
        }
        if (usuarios.isEmpty()) {
            throw new RuntimeException("numero de documento no encontrado");
        }
        return usuarios.stream().map(this::convertirADTO).toList();
    }

    @Override
    public List<UsuarioDTO> encontrarPorCorreo(String correo){
        List<UsuarioEntity> buscarCorreo = usuarioRepository.findByCorreoAndEstadoTrue(correo).stream().toList();
        if (buscarCorreo.isEmpty()) {
            buscarCorreo = usuarioRepository.findByCorreoContainingIgnoreCaseAndEstadoTrue(correo);
        }
        if (buscarCorreo.isEmpty()) {
            throw new RuntimeException("Correo no encontrado");
        }

        return buscarCorreo.stream().map(this::convertirADTO).toList();
    }

    /*private UsuarioEntity convertirAEntity(UsuarioDTO dto) {
        return modelMapper.map(dto, UsuarioEntity.class);
    }*/

    public UsuarioDTO convertirADTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioDTO.class);
    }

    public UsuarioEditarDTO convertirAEditarUsuarioDTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioEditarDTO.class);
    }

}
