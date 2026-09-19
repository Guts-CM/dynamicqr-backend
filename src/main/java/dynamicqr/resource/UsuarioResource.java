package dynamicqr.resource;

import dynamicqr.dto.MensajeResponse;
import dynamicqr.dto.PasswordTemporalResponse;
import dynamicqr.dto.UsuarioCreateRequest;
import dynamicqr.dto.UsuarioResponse;
import dynamicqr.dto.UsuarioUpdateRequest;
import dynamicqr.security.SecurityUtils;
import dynamicqr.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    private final UsuarioService usuarioService;
    private final SecurityUtils securityUtils;

    public UsuarioResource(UsuarioService usuarioService, SecurityUtils securityUtils) {
        this.usuarioService = usuarioService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<UsuarioResponse> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioResponse findById(@PathVariable Integer id) {
        return usuarioService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MensajeResponse> create(@Valid @RequestBody UsuarioCreateRequest request) {
        try {
            usuarioService.create(request, securityUtils.currentUserId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MensajeResponse("usuario creado correctamente"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse("hubo un error al crear el usuario"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensajeResponse> update(
            @PathVariable Integer id, @Valid @RequestBody UsuarioUpdateRequest request) {
        try {
            usuarioService.update(id, request, securityUtils.currentUserId());
            return ResponseEntity.ok(new MensajeResponse("usuario actualizado correctamente"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse("hubo un error al actualizar el usuario"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> toggleActivo(@PathVariable Integer id) {
        try {
            boolean activo = usuarioService.toggleActivo(id, securityUtils.currentUserId());
            String mensaje = activo
                    ? "usuario activado correctamente"
                    : "usuario eliminado correctamente";
            return ResponseEntity.ok(new MensajeResponse(mensaje));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(new MensajeResponse("hubo un error al eliminar o activar el usuario"));
        }
    }

    @PostMapping("/{id}/password-temporal")
    public PasswordTemporalResponse generarPasswordTemporal(@PathVariable Integer id) {
        String temporal = usuarioService.generarPasswordTemporal(id, securityUtils.currentUserId());
        return new PasswordTemporalResponse(temporal);
    }
}
