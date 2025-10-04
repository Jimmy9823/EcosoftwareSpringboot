package com.EcoSoftware.Scrum6.Service.Security;

import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByCorreoAndEstadoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró un usuario con el email: " + username));

        RolEntity.TipoDeRol tipoRol = usuario.getRol().getTipo();

        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(tipoRol.name());

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                Collections.singletonList(autoridad)
        );
    }
}
