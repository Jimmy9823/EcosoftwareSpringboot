package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Security.TokenJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    @Value("${google.clientId}")
private String googleClientId;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenJWT tokenJWT;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "idToken requerido"));
        }

        // 1) Validar token con Google (tokeninfo)
        String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        Map<String, Object> tokenInfo;
        try {
            tokenInfo = restTemplate.getForObject(tokenInfoUrl, Map.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (tokenInfo == null || tokenInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido"));
        }

        // 2) Verificar aud (client id)
        String aud = (String) tokenInfo.get("aud");
        if (aud == null || !aud.equals(googleClientId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token no dirigido a esta aplicación"));
        }

        // 3) Verificar email_verified
        String emailVerified = (String) tokenInfo.get("email_verified");
        if (emailVerified == null || !emailVerified.equals("true")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Correo de Google no verificado"));
        }

        // 4) Extraer datos
        String correo = (String) tokenInfo.get("email");
        String nombre = (String) tokenInfo.get("name");
        String picture = (String) tokenInfo.get("picture");

        if (correo == null || correo.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Correo no disponible en token"));
        }

        // 5) Buscar o crear usuario
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByCorreoAndEstadoTrue(correo);
        UsuarioEntity usuario;
        if (usuarioOpt.isEmpty()) {
            // Crear usuario mínimo. ADAPTA campos obligatorios según tu entidad
            usuario = new UsuarioEntity();
            // Buscar rol por defecto (ajusta el id si es distinto)
            RolEntity rol = rolRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado (id=2)"));
            usuario.setRol(rol);
            usuario.setNombre(nombre != null ? nombre : correo);
            usuario.setCorreo(correo);
            // Generar contraseña aleatoria cifrada (no se usará directamente)
            usuario.setContrasena(passwordEncoder.encode(UUID.randomUUID().toString()));
            // Asignar valores a campos NOT NULL o requeridos (adáptalos si tus columnas son NOT NULL)
            usuario.setCedula("0000000000"); // si no permites null — ajusta
            usuario.setTelefono("");
            usuario.setEstado(true);
            // fechas se manejan con @PrePersist si ya las tienes
            usuario = usuarioRepository.save(usuario);
        } else {
            usuario = usuarioOpt.get();
        }

        // 6) Generar el JWT interno y devolver info útil al front
        String token = tokenJWT.generarToken(usuario.getCorreo());

        Map<String, Object> response = Map.of(
                "mensaje", "Login con Google exitoso",
                "token", token,
                "correo", usuario.getCorreo(),
                "rol", usuario.getRol().getNombre(),
                "nombre", usuario.getNombre(),
                "picture", picture
        );
        return ResponseEntity.ok(response);
    }
}
