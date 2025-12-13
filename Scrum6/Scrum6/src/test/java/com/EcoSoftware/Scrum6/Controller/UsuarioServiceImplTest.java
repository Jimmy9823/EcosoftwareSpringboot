// Archivo: src/test/java/com/EcoSoftware/Scrum6/Implement/UsuarioServiceImplTest.java
package com.EcoSoftware.Scrum6.Controller;  // ¡CORREGIDO!

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Implement.UsuarioServiceImpl;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    // 🎭 ACTORES DE REPARTO
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @Mock private TemplateEngine templateEngine;

    // 🎭 ACTOR PRINCIPAL
    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    // 🎭 UTILERÍA
    private UsuarioEntity usuarioEntity;
    private UsuarioDTO usuarioDTO;
    private RolEntity rolEntity;

    @BeforeEach
    void prepararEscenario() {
        // 📋 CREAMOS UN ROL DE EJEMPLO
        rolEntity = new RolEntity();
        rolEntity.setIdRol(1L);
        rolEntity.setNombre("Ciudadano");

        // 📋 CREAMOS UN USUARIO ENTITY DE EJEMPLO
        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setIdUsuario(1L);
        usuarioEntity.setNombre("María López");
        usuarioEntity.setCorreo("maria@test.com");
        usuarioEntity.setCedula("987654321");
        usuarioEntity.setTelefono("3012345678");
        usuarioEntity.setContrasena("passwordEncriptada");
        usuarioEntity.setEstado(true);
        usuarioEntity.setRol(rolEntity);
        usuarioEntity.setFechaCreacion(LocalDateTime.now());

        // 📋 CREAMOS UN USUARIO DTO DE EJEMPLO
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setIdUsuario(1L);
        usuarioDTO.setNombre("María López");
        usuarioDTO.setCorreo("maria@test.com");
        usuarioDTO.setRolId(1L);
        usuarioDTO.setCedula("987654321");
        usuarioDTO.setTelefono("3012345678");
        usuarioDTO.setContrasena("password123");
    }

    // 🎬 ESCENA 1: Listar todos los usuarios
    @Test
    void listarUsuarios_DebeRetornarListaDeDTOs() {
        // ARRANGE
        when(usuarioRepository.findAllByOrderByIdUsuarioAsc())
                .thenReturn(Arrays.asList(usuarioEntity));

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        // ACT
        List<UsuarioDTO> resultado = usuarioService.listarUsuarios();

        // ASSERT
        assertNotNull(resultado, "La lista no debería ser null");
        assertEquals(1, resultado.size(), "Debería tener 1 usuario");
        assertEquals("María López", resultado.get(0).getNombre(),
                "El nombre debería ser 'María López'");

        // VERIFY
        verify(usuarioRepository, times(1)).findAllByOrderByIdUsuarioAsc();
        verify(modelMapper, times(1)).map(usuarioEntity, UsuarioDTO.class);
    }

    // 🎬 ESCENA 2: Obtener usuario por ID (éxito)
    @Test
    void obtenerUsuarioPorId_CuandoUsuarioExiste_DebeRetornarDTO() {
        // ARRANGE
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));
        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        // ACT
        UsuarioDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

        // ASSERT
        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals(1L, resultado.getIdUsuario(), "El ID debería ser 1");
        assertEquals("maria@test.com", resultado.getCorreo(),
                "El correo debería coincidir");
    }

    // 🎬 ESCENA 3: Obtener usuario por ID (no existe)
    @Test
    void obtenerUsuarioPorId_CuandoUsuarioNoExiste_DebeLanzarExcepcion() {
        // ARRANGE
        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        Exception excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerUsuarioPorId(99L);
        });

        assertEquals("Persona no encontrada con ID: 99", excepcion.getMessage(),
                "El mensaje de error debería ser específico");
    }

    // 🎬 ESCENA 4: Crear usuario exitosamente
    @Test
    void crearUsuario_CuandoDatosSonValidos_DebeCrearYEnviarCorreo() {
        // ARRANGE
        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(rolEntity));

        when(modelMapper.map(usuarioDTO, UsuarioEntity.class))
                .thenReturn(usuarioEntity);

        when(passwordEncoder.encode("password123"))
                .thenReturn("passwordEncriptada123");

        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenReturn(usuarioEntity);

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        when(templateEngine.process(eq("email-bienvenida"), any()))
                .thenReturn("<h1>Bienvenida María</h1>");

        // ACT
        UsuarioDTO resultado = usuarioService.crearUsuario(usuarioDTO);

        // ASSERT
        assertNotNull(resultado, "Debería retornar un usuario DTO");

        // VERIFY
        verify(rolRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
        verify(emailService, times(1)).enviarCorreo(
                eq("maria@test.com"),
                eq("¡Bienvenido a EcoSoftware!"),
                anyString()
        );
    }

    // 🎬 ESCENA 5: Crear usuario sin rol (debe fallar)
    @Test
    void crearUsuario_CuandoNoTieneRol_DebeLanzarExcepcion() {
        // ARRANGE
        usuarioDTO.setRolId(null);

        // ACT & ASSERT
        Exception excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(usuarioDTO);
        });

        assertEquals("El rol es obligatorio", excepcion.getMessage());

        // VERIFY que NO se llamó a save
        verify(usuarioRepository, never()).save(any());
    }

    // 🎬 ESCENA 6: Aprobar usuario pendiente
    @Test
    void aprobarUsuario_CuandoUsuarioEstaPendiente_DebeActivar() {
        // ARRANGE
        usuarioEntity.setEstado(false);
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));

        // ACT
        usuarioService.aprobarUsuario(1L);

        // ASSERT
        assertTrue(usuarioEntity.getEstado(), "El estado debería ser TRUE (activo)");
        assertNotNull(usuarioEntity.getFechaActualizacion(),
                "Debería tener fecha de actualización");

        // VERIFY
        verify(usuarioRepository, times(1)).save(usuarioEntity);
    }

    // 🎬 ESCENA 7: Rechazar usuario pendiente
    @Test
    void rechazarUsuario_CuandoUsuarioEstaPendiente_DebeEliminar() {
        // ARRANGE
        usuarioEntity.setEstado(false);
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));

        // ACT
        usuarioService.rechazarUsuario(1L);

        // VERIFY
        verify(usuarioRepository, times(1)).delete(usuarioEntity);
    }

    // 🎬 ESCENA 8: Encontrar por nombre (coincidencia exacta) - ¡CORREGIDO!
    @Test
    void encontrarPorNombre_CuandoNombreCoincideExactamente_DebeRetornarUsuario() {
        // ARRANGE
        // CORRECCIÓN: findByNombreAndEstadoTrue devuelve Optional<UsuarioEntity>
        // NO Optional<List<UsuarioEntity>>
        when(usuarioRepository.findByNombreAndEstadoTrue("María López"))
                .thenReturn(Optional.of(usuarioEntity)); // ¡SOLO el Entity, NO List!

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        // ACT
        List<UsuarioDTO> resultado = usuarioService.encontrarPorNombre("María López");

        // ASSERT
        assertEquals(1, resultado.size(), "Debería encontrar 1 usuario");
        assertEquals("María López", resultado.get(0).getNombre());
    }

    // 🎬 ESCENA 9: Encontrar por documento (cédula) - ¡CORREGIDO!
    @Test
    void encontrarPorDocumento_CuandoEsCedula_DebeRetornarUsuario() {
        // ARRANGE
        // CORRECCIÓN: findByCedulaAndEstadoTrue devuelve Optional<UsuarioEntity>
        when(usuarioRepository.findByCedulaAndEstadoTrue("987654321"))
                .thenReturn(Optional.of(usuarioEntity)); // ¡SOLO el Entity!

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        // ACT
        List<UsuarioDTO> resultado = usuarioService.encontrarPorDocumento("987654321");

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("987654321", resultado.get(0).getCedula());
    }

    // 🎬 ESCENA 10: Generar plantilla Excel para Reciclador
    @Test
    void generarPlantillaExcelPorRol_CuandoRolEsReciclador_DebeGenerarBytes() {
        // ACT
        byte[] plantilla = usuarioService.generarPlantillaExcelPorRol("Reciclador");

        // ASSERT
        assertNotNull(plantilla, "La plantilla no debería ser null");
        assertTrue(plantilla.length > 0, "La plantilla debería tener contenido");
    }

    // 🎬 ESCENA 11: Listar usuarios pendientes
    @Test
    void listarUsuariosPendientes_DebeRetornarSoloInactivos() {
        // ARRANGE
        usuarioEntity.setEstado(false);
        when(usuarioRepository.findByEstadoFalse())
                .thenReturn(Arrays.asList(usuarioEntity));
        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        // ACT
        List<UsuarioDTO> resultado = usuarioService.listarUsuariosPendientes();

        // ASSERT
        assertEquals(1, resultado.size());
        assertNotNull(resultado.get(0));
    }

    // 🎬 ESCENA 12: Contar usuarios pendientes
    @Test
    void contarUsuariosPendientes_DebeRetornarCantidadCorrecta() {
        // ARRANGE
        when(usuarioRepository.countByEstadoFalse())
                .thenReturn(5L);

        // ACT
        Long cantidad = usuarioService.contarUsuariosPendientes();

        // ASSERT
        assertEquals(5L, cantidad, "Debería haber 5 usuarios pendientes");
    }

    // 🎬 ESCENA 13: Actualizar usuario - ¡NUEVA PRUEBA!
    @Test
    void actualizarUsuario_CuandoExiste_DebeActualizar() {
        // ARRANGE
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuarioEntity));

        UsuarioEditarDTO dtoActualizado = new UsuarioEditarDTO();
        dtoActualizado.setNombre("María López Actualizada");
        dtoActualizado.setTelefono("999999999");

        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenReturn(usuarioEntity);

        when(modelMapper.map(usuarioEntity, UsuarioEditarDTO.class))
                .thenReturn(dtoActualizado);

        // ACT
        UsuarioEditarDTO resultado = usuarioService.actualizarUsuario(1L, dtoActualizado);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("María López Actualizada", resultado.getNombre());
        assertEquals("999999999", resultado.getTelefono());
    }

    // 🎬 ESCENA 14: Eliminación lógica
    @Test
    void eliminacionPorEstado_CuandoUsuarioExiste_DebeActualizarEstado() {
        // ARRANGE
        when(usuarioRepository.eliminacionLogica(1L))
                .thenReturn(1); // 1 fila actualizada

        // ACT
        usuarioService.eliminacionPorEstado(1L);

        // VERIFY
        verify(usuarioRepository, times(1)).eliminacionLogica(1L);
    }

    // 🎬 ESCENA 15: Encontrar por correo
    @Test
    void encontrarPorCorreo_CuandoCorreoExiste_DebeRetornarUsuario() {
        // ARRANGE
        when(usuarioRepository.findByCorreoAndEstadoTrue("maria@test.com"))
                .thenReturn(Optional.of(usuarioEntity));

        when(modelMapper.map(usuarioEntity, UsuarioDTO.class))
                .thenReturn(usuarioDTO);

        // ACT
        List<UsuarioDTO> resultado = usuarioService.encontrarPorCorreo("maria@test.com");

        // ASSERT
        assertEquals(1, resultado.size());
        assertEquals("maria@test.com", resultado.get(0).getCorreo());
    }
}