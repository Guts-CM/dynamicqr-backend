package dynamicqr.service;

import dynamicqr.domain.Usuario;
import dynamicqr.dto.UsuarioCreateRequest;
import dynamicqr.dto.UsuarioResponse;
import dynamicqr.dto.UsuarioUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Integer id) {
        return toResponse(findEntity(id));
    }

    public UsuarioResponse create(UsuarioCreateRequest request, Integer actorId) {
        return create(request, actorId, null);
    }

    public UsuarioResponse create(UsuarioCreateRequest request, Integer actorId, String rawPassword) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellidoPaterno(request.getApellidoPaterno());
        usuario.setApellidoMaterno(request.getApellidoMaterno());
        usuario.setActivo(true);
        usuario.setUsuarioCreador(actorId);
        usuario.setUsuarioEditor(actorId);
        usuario.setPasswordHash(encodePassword(
                rawPassword == null || rawPassword.isBlank()
                        ? UUID.randomUUID().toString()
                        : rawPassword));

        Usuario guardado = usuarioRepository.save(usuario);
        if (actorId == null) {
            guardado.setUsuarioCreador(guardado.getUsuariosId());
            guardado.setUsuarioEditor(guardado.getUsuariosId());
            guardado = usuarioRepository.save(guardado);
        }
        return toResponse(guardado);
    }

    public UsuarioResponse update(Integer id, UsuarioUpdateRequest request, Integer editorId) {
        Usuario actual = findEntity(id);
        if (request.getEmail() != null) {
            actual.setEmail(request.getEmail());
        }
        if (request.getNombre() != null) {
            actual.setNombre(request.getNombre());
        }
        if (request.getApellidoPaterno() != null) {
            actual.setApellidoPaterno(request.getApellidoPaterno());
        }
        if (request.getApellidoMaterno() != null) {
            actual.setApellidoMaterno(request.getApellidoMaterno());
        }
        actual.setUsuarioEditor(editorId);
        return toResponse(usuarioRepository.save(actual));
    }

    public boolean toggleActivo(Integer id, Integer editorId) {
        Usuario actual = findEntity(id);
        actual.setActivo(!actual.isActivo());
        actual.setUsuarioEditor(editorId);
        usuarioRepository.save(actual);
        return actual.isActivo();
    }

    private Usuario findEntity(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario " + id + " no encontrado"));
    }

    private String encodePassword(String password) {
        if (password != null && password.startsWith("$argon2")) {
            return password;
        }
        return passwordEncoder.encode(password);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setUsuariosId(usuario.getUsuariosId());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setApellidoPaterno(usuario.getApellidoPaterno());
        response.setApellidoMaterno(usuario.getApellidoMaterno());
        response.setUsuarioCreador(usuario.getUsuarioCreador());
        response.setFechaCreacion(usuario.getFechaCreacion());
        response.setUsuarioEditor(usuario.getUsuarioEditor());
        response.setFechaEdicion(usuario.getFechaEdicion());
        return response;
    }
}
