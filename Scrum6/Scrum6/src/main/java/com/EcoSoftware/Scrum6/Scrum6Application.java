package com.EcoSoftware.Scrum6;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Scrum6Application {

	public static void main(String[] args) {
		SpringApplication.run(Scrum6Application.class, args);
	}

    @Bean
    CommandLineRunner commandLineRunner(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Crear usuario admin si no existe
            if (usuarioRepository.findByCorreoAndEstadoTrue("admin@gmail.com").isEmpty()) {
                System.out.println("Creando usuario de prueba: admin@gmail.com");

                RolEntity rolAdmin = rolRepository.findByTipo(RolEntity.TipoDeRol.Administrador)
                        .orElseGet(() -> {
                            RolEntity nuevoRol = new RolEntity();
                            nuevoRol.setNombre("Administrador");
                            nuevoRol.setDescripcion("Rol con permisos administrativos");
                            nuevoRol.setTipo(RolEntity.TipoDeRol.Administrador);
                            return rolRepository.save(nuevoRol);
                        });

                UsuarioEntity admin = new UsuarioEntity();
                admin.setNombre("admin");
                admin.setCorreo("admin@gmail.com");
                admin.setContrasena(passwordEncoder.encode("admin123"));
                admin.setRol(rolAdmin);
                admin.setCedula("0000000000");
                admin.setTelefono("0000000000");
                usuarioRepository.save(admin);

                System.out.println("Usuario admin creado correctamente");
            }

            // Crear usuario empresa si no existe
            if (usuarioRepository.findByCorreoAndEstadoTrue("empresa@gmail.com").isEmpty()) {
                System.out.println("Creando usuario de prueba: empresa@gmail.com");

                RolEntity rolEmpresa = rolRepository.findByTipo(RolEntity.TipoDeRol.Empresa)
                        .orElseGet(() -> {
                            RolEntity nuevoRol = new RolEntity();
                            nuevoRol.setNombre("Empresa");
                            nuevoRol.setDescripcion("Rol para usuarios tipo empresa");
                            nuevoRol.setTipo(RolEntity.TipoDeRol.Empresa);
                            return rolRepository.save(nuevoRol);
                        });

                UsuarioEntity empresa = new UsuarioEntity();
                empresa.setNombre("empresa");
                empresa.setCorreo("empresa@gmail.com");
                empresa.setContrasena(passwordEncoder.encode("empresa123"));
                empresa.setRol(rolEmpresa);
                empresa.setCedula("1111111111");
                empresa.setTelefono("1111111111");
                usuarioRepository.save(empresa);

                System.out.println("Usuario empresa creado correctamente");
            }

            // Crear usuario reciclador si no existe
            if (usuarioRepository.findByCorreoAndEstadoTrue("reciclador@gmail.com").isEmpty()) {
                System.out.println("Creando usuario de prueba: reciclador@gmail.com");

                RolEntity rolReciclador = rolRepository.findByTipo(RolEntity.TipoDeRol.Reciclador)
                        .orElseGet(() -> {
                            RolEntity nuevoRol = new RolEntity();
                            nuevoRol.setNombre("Reciclador");
                            nuevoRol.setDescripcion("Rol para usuarios recicladores");
                            nuevoRol.setTipo(RolEntity.TipoDeRol.Reciclador);
                            return rolRepository.save(nuevoRol);
                        });

                UsuarioEntity reciclador = new UsuarioEntity();
                reciclador.setNombre("reciclador");
                reciclador.setCorreo("reciclador@gmail.com");
                reciclador.setContrasena(passwordEncoder.encode("reciclador123"));
                reciclador.setRol(rolReciclador);
                reciclador.setCedula("2222222222");
                reciclador.setTelefono("2222222222");
                usuarioRepository.save(reciclador);

                System.out.println("Usuario reciclador creado correctamente");
            }

            // Crear usuario ciudadano si no existe
            if (usuarioRepository.findByCorreoAndEstadoTrue("ciudadano@gmail.com").isEmpty()) {
                System.out.println("Creando usuario de prueba: ciudadano@gmail.com");

                RolEntity rolCiudadano = rolRepository.findByTipo(RolEntity.TipoDeRol.Ciudadano)
                        .orElseGet(() -> {
                            RolEntity nuevoRol = new RolEntity();
                            nuevoRol.setNombre("Ciudadano");
                            nuevoRol.setDescripcion("Rol para usuarios ciudadanos");
                            nuevoRol.setTipo(RolEntity.TipoDeRol.Ciudadano);
                            return rolRepository.save(nuevoRol);
                        });

                UsuarioEntity ciudadano = new UsuarioEntity();
                ciudadano.setNombre("ciudadano");
                ciudadano.setCorreo("ciudadano@gmail.com");
                ciudadano.setContrasena(passwordEncoder.encode("ciudadano123"));
                ciudadano.setRol(rolCiudadano);
                ciudadano.setCedula("3333333333");
                ciudadano.setTelefono("3333333333");
                usuarioRepository.save(ciudadano);

                System.out.println("Usuario ciudadano creado correctamente");
            }
        };
    }

}
