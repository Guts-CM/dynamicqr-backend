package dynamicqr.security;

import dynamicqr.domain.Usuario;
import dynamicqr.service.UsuarioRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        return new UsuarioUserDetails(resolverUsuario(identificador));
    }

    private Usuario resolverUsuario(String identificador) {
        String valor = identificador.trim();
        if (valor.contains("@")) {
            return usuarioRepository.findByEmailIgnoreCase(valor)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        }

        List<Usuario> coincidencias = usuarioRepository.findByEmailStartingWithIgnoreCase(valor + "@");
        if (coincidencias.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        if (coincidencias.size() > 1) {
            throw new UsernameNotFoundException(
                    "Hay varios usuarios con ese inicio de correo; usa el correo completo");
        }
        return coincidencias.getFirst();
    }
}
