package com.EcoSoftware.Scrum6.Security;

import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenJWT tokenJWT; // Clase que genera y valida tokens JWT

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio de usuarios

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // =====================================
        // RUTAS PÚBLICAS: no requieren token
        // =====================================
        if (path.startsWith("/api/auth/login") ||
            path.startsWith("/api/personas/registro")) {
            // Continuar sin autenticar
            filterChain.doFilter(request, response);
            return;
        }

        // =====================================
        // Validación de JWT para rutas protegidas
        // =====================================
        String authHeader = request.getHeader("Authorization");

        // Si no hay token o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer token del header
        String token = authHeader.substring(7);

        // Validar token
        if (tokenJWT.validarToken(token)) {

            // Obtener correo del usuario desde el token
            String correo = tokenJWT.obtenerCorreoDesdeToken(token);

            // Buscar usuario activo en la base de datos
            var usuario = usuarioRepository.findByCorreoAndEstadoTrue(correo);

            // Si el usuario existe y no hay autenticación previa
            if (usuario.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Construir autoridad a partir del rol del usuario
                var rolNombre = usuario.get().getRol().getNombre();
                var authority = new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + rolNombre.toUpperCase()
                );

                // Crear UserDetails con correo, contraseña y roles
                User userDetails = new User(
                        usuario.get().getCorreo(),
                        usuario.get().getContrasena(),
                        Collections.singletonList(authority)
                );

                // Crear token de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // Asociar detalles de la petición al token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer el contexto de seguridad con el usuario autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
