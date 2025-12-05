package com.EcoSoftware.Scrum6.Implement.DatosPrecargados;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CargarUsuariosBasicos implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public CargarUsuariosBasicos(UsuarioRepository usuarioRepository,
                                 RolRepository rolRepository,
                                 PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        crearUsuarioBaseSiNoExiste(
                "jaiandroaber@gmail.com",   // correo
                "Administrador",            // nombre
                "123456",                   // contraseña
                "1000070000",               // cédula
                "3070000000",               // teléfono
                RolEntity.TipoDeRol.Administrador,
                "Teusaquillo"               // localidad
        );

        crearUsuarioBaseSiNoExiste(
                "paula06sepulveda@gmail.com",
                "Ciudadano",
                "123456",
                "2000090070",
                "3000041111",
                RolEntity.TipoDeRol.Ciudadano,
                "Ciudad_Bolivar"
        );

        crearUsuarioBaseSiNoExiste(
                "danacastro2014@gmail.com",
                "Empresa",
                "123456",
                "30071200000",
                "3450002222",
                RolEntity.TipoDeRol.Empresa,
                "Antonio_Nariño"
        );

        crearUsuarioBaseSiNoExiste(
                "ecosoftware2025@gmail.com",
                "Reciclador",
                "123456",
                "400047250000",
                "3700003333",
                RolEntity.TipoDeRol.Reciclador,
                "Bosa"
        );

        System.out.println(">>> Usuarios base verificados/creados correctamente");
    }

    private void crearUsuarioBaseSiNoExiste(
            String correo,
            String nombre,
            String contrasenaPlano,
            String cedula,
            String telefono,
            RolEntity.TipoDeRol tipoRol,
            String localidad
    ) {

        // Validar si ya existe por correo
        if (usuarioRepository.findByCorreoIgnoreCase(correo).isPresent() ||
                usuarioRepository.findByCedulaAndEstadoTrue(cedula).isPresent()) {
            System.out.println("Usuario ya registrado en la BD");
            return;
        }

        // Buscar rol
        RolEntity rol = rolRepository.findByTipo(tipoRol)
                .orElseThrow(() -> new RuntimeException("ERROR: El rol " + tipoRol + " no existe aún."));

        UsuarioEntity usuario = new UsuarioEntity();
        // Datos ingresados
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(contrasenaPlano));
        usuario.setCedula(cedula);
        usuario.setTelefono(telefono);
        usuario.setRol(rol);

        usuario.setEstado(true);
        usuario.setDireccion(null);
        usuario.setBarrio(null);
        usuario.setLocalidad(localidad);
        usuario.setNit(null);
        usuario.setRepresentanteLegal(null);
        usuario.setZona_de_trabajo(null);
        usuario.setHorario(null);
        usuario.setTipoMaterial(null);
        usuario.setCantidad_minima(null);
        usuario.setImagen_perfil(null);
        usuario.setCertificaciones(null);

        usuarioRepository.save(usuario);

        System.out.println(">>> Usuario creado: " + correo);
    }
}
