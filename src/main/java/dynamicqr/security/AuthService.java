package dynamicqr.security;

import dynamicqr.dto.TokenResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public TokenResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        UsuarioUserDetails principal = (UsuarioUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal.getUsuario());
        int duracionMinutos = (int) (jwtProperties.getExpirationMs() / 60_000L);
        return new TokenResponse(token, duracionMinutos, principal.getUsuario().getUsuariosId());
    }
}
