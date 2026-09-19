package dynamicqr.security;

import dynamicqr.domain.Usuario;
import dynamicqr.dto.TokenResponse;
import dynamicqr.service.UsuarioService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioUserDetailsService usuarioUserDetailsService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioUserDetailsService usuarioUserDetailsService,
            UsuarioService usuarioService,
            JwtService jwtService,
            JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.usuarioUserDetailsService = usuarioUserDetailsService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public TokenResponse login(String email, String password) {
        UsuarioUserDetails principal = cargarPrincipal(email);
        if (!principal.isEnabled()) {
            throw new DisabledException("Esta cuenta está desactivada");
        }

        Usuario usuario = principal.getUsuario();
        if (UsuarioService.esArgon2(usuario.getPasswordHash())) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            principal = (UsuarioUserDetails) authentication.getPrincipal();
            return emitirToken(principal.getUsuario());
        }

        if (!coincideTemporal(usuario.getPasswordHash(), password)) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        return new TokenResponse("", 0, usuario.getUsuariosId(), true);
    }

    public TokenResponse cambiarPassword(String email, String passwordTemporal, String passwordNueva) {
        UsuarioUserDetails principal = cargarPrincipal(email);
        if (!principal.isEnabled()) {
            throw new DisabledException("Esta cuenta está desactivada");
        }

        Usuario usuario = principal.getUsuario();
        if (UsuarioService.esArgon2(usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Esta cuenta no tiene una contraseña temporal");
        }
        if (!coincideTemporal(usuario.getPasswordHash(), passwordTemporal)) {
            throw new BadCredentialsException("Credenciales invalidas");
        }
        if (passwordNueva.equals(passwordTemporal)) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta a la temporal");
        }

        usuarioService.establecerPasswordArgon2(usuario, passwordNueva);
        return emitirToken(usuario);
    }

    private UsuarioUserDetails cargarPrincipal(String email) {
        try {
            return (UsuarioUserDetails) usuarioUserDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Credenciales invalidas");
        }
    }

    private TokenResponse emitirToken(Usuario usuario) {
        String token = jwtService.generateToken(usuario);
        int duracionMinutos = (int) (jwtProperties.getExpirationMs() / 60_000L);
        return new TokenResponse(token, duracionMinutos, usuario.getUsuariosId());
    }

    private boolean coincideTemporal(String almacenada, String recibida) {
        if (almacenada == null || recibida == null) {
            return false;
        }

        if (PasswordTemporalHasher.esTemporal(almacenada)) {
            return PasswordTemporalHasher.matches(almacenada, recibida);
        }

        if (UsuarioService.esArgon2(almacenada)) {
            return false;
        }

        byte[] izquierda = almacenada.getBytes(StandardCharsets.UTF_8);
        byte[] derecha = recibida.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(izquierda, derecha);
    }
}
