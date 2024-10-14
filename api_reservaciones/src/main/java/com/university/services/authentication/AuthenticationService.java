package com.university.services.authentication;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.university.models.Permiso;
import com.university.models.PermisoRol;
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
        if (!usuarioBusqueda.isEmpty()) {
            Usuario usuario = usuarioBusqueda.get();
            User.UserBuilder userBuilder = User.withUsername(username);
            ArrayList<String> rolesString = new ArrayList<>();
            for (UsuarioRol item : usuario.getRoles()) {
                System.out.println("ITEM");
                System.out.println(item);
                System.out.println(item.getRol().getPermisos());
                //Se obtienen todos los permisos del rol
                List<PermisoRol> permisosRol = item.getRol().getPermisos();
                for (PermisoRol permisoRol : permisosRol) {
                    //Se chequea si el permiso no ha sido agregado
                    if (!rolesString.contains(permisoRol.getPermiso().getNombre())) {
                        //Si no lo tiene se agrega
                        rolesString.add(permisoRol.getPermiso().getNombre());
                    }
                }
            }
            userBuilder.password(usuario.getPassword()).roles(
                  rolesString.toArray(new String[rolesString.size()])
            );
            return userBuilder.build();

        }
        throw new UsernameNotFoundException(username);
    }

}
