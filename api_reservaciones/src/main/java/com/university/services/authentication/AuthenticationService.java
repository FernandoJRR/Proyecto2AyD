package com.university.services.authentication;

import java.util.Optional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.university.models.Usuario;
import com.university.models.UsuarioRol;
import com.university.repositories.UsuarioRepository;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioBusqueda = usuarioRepository.findByEmail(username);
        if (!usuarioBusqueda.isEmpty()) {//si no esta vacia la busqueda
            Usuario usuario = usuarioBusqueda.get();
            User.UserBuilder userBuilder = User.withUsername(username);
            ArrayList<String> rolesString = new ArrayList<>();
            for (UsuarioRol item : usuario.getRoles()) {
                rolesString.add(item.getRol().getNombre());
            }
            userBuilder.password(usuario.getPassword()).roles(
                  rolesString.toArray(new String[rolesString.size()])
            );
            return userBuilder.build();

        }
        throw new UsernameNotFoundException(username);
    }

}
