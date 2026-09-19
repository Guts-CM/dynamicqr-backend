package dynamicqr.resource;

import dynamicqr.dto.CambioPasswordRequest;
import dynamicqr.dto.LoginRequest;
import dynamicqr.dto.RegisterRequest;
import dynamicqr.dto.TokenResponse;
import dynamicqr.dto.UsuarioCreateRequest;
import dynamicqr.dto.UsuarioResponse;
import dynamicqr.security.AuthService;
import dynamicqr.service.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthResource(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/password")
    public TokenResponse cambiarPassword(@Valid @RequestBody CambioPasswordRequest request) {
        return authService.cambiarPassword(
                request.getEmail(), request.getPassword(), request.getPasswordNueva());
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request) {
        UsuarioCreateRequest createRequest = new UsuarioCreateRequest();
        createRequest.setEmail(request.getEmail());
        createRequest.setNombre(request.getNombre());
        createRequest.setApellidoPaterno(request.getApellidoPaterno());
        createRequest.setApellidoMaterno(request.getApellidoMaterno());
        UsuarioResponse creado = usuarioService.create(createRequest, null, request.getPassword());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/{id}")
                .buildAndExpand(creado.getUsuariosId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }
}
