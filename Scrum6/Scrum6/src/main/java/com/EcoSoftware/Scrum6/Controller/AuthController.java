package com.EcoSoftware.Scrum6.Controller;

// Importaciones necesarias para el controlador de autenticación
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Security.TokenJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Controlador REST encargado de manejar la autenticación de usuarios (login)
@RestController
@RequestMapping("/api/auth") // Ruta base para las peticiones del controlador
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") // Permite solicitudes desde el frontend Angular
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para acceder a los datos del usuario

    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador de contraseñas para validar contraseñas encriptadas

    @Autowired
    private TokenJWT tokenJWT; // Clase encargada de generar tokens JWT

    /**
     * Endpoint de login: recibe correo y contraseña, valida y genera token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        // Extraer correo y contraseña del cuerpo de la petición
        String correo = request.get("correo");
        String contrasena = request.get("contrasena");

        // Buscar usuario activo por correo
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByCorreoAndEstadoTrue(correo);

        // Validar si el usuario no existe o está inactivo
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo no registrado o usuario inactivo"));
        }

        // Obtener el usuario encontrado
        UsuarioEntity usuario = usuarioOpt.get();

        // Verificar que la contraseña ingresada coincida con la almacenada (encriptada)
        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Contraseña incorrecta"));
        }

        // Generar token JWT para el usuario autenticado
        String token = tokenJWT.generarToken(usuario.getCorreo());

        // Crear mapa de respuesta con datos relevantes
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Inicio de sesión exitoso");
        response.put("token", token); // Token JWT generado
        response.put("correo", usuario.getCorreo()); // Correo del usuario autenticado
        response.put("rol", usuario.getRol().getNombre()); // Rol asociado al usuario

        // Retornar respuesta exitosa con los datos
        return ResponseEntity.ok(response);
    }
}
