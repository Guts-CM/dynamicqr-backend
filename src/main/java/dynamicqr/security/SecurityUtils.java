package dynamicqr.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Integer currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioUserDetails details)) {
            throw new IllegalStateException("No hay un usuario autenticado en el token");
        }
        return details.getUsuario().getUsuariosId();
    }
}
