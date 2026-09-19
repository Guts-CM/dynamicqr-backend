package dynamicqr.service;

import dynamicqr.domain.Usuario;
import dynamicqr.dto.UsuarioCreateRequest;
import dynamicqr.dto.UsuarioResponse;
import dynamicqr.dto.UsuarioUpdateRequest;
import dynamicqr.security.PasswordTemporalHasher;
import jakarta.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private static final String TEMP_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int TEMP_LENGTH = 10;
    private static final SecureRandom TEMP_RANDOM = new SecureRandom();

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

    @Transactional(readOnly = true)
    public Map<Integer, String> nombresPorId(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }

        List<Integer> validos = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (validos.isEmpty()) {
            return Map.of();
        }

        return usuarioRepository.findAllById(validos).stream()
                .map((usuario) -> Map.entry(usuario.getUsuariosId(), nombreCompleto(usuario)))
                .filter((entry) -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (left, right) -> left));
    }

    public String nombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        String completo = Stream.of(usuario.getNombre(), usuario.getApellidoPaterno(), usuario.getApellidoMaterno())
                .filter(parte -> parte != null && !parte.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return completo.isBlank() ? null : completo;
    }

    public UsuarioResponse create(UsuarioCreateRequest request, Integer actorId) {
        return create(request, actorId, null);
    }

    public UsuarioResponse create(UsuarioCreateRequest request, Integer actorId, String rawPassword) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setNombre(request.getNombre());
        usuario.setApellidoPaterno(blankToNull(request.getApellidoPaterno()));
        usuario.setApellidoMaterno(blankToNull(request.getApellidoMaterno()));
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
        actual.setApellidoPaterno(blankToNull(request.getApellidoPaterno()));
        if (request.getApellidoMaterno() != null) {
            actual.setApellidoMaterno(blankToNull(request.getApellidoMaterno()));
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

    public String generarPasswordTemporal(Integer id, Integer editorId) {
        Usuario actual = findEntity(id);
        String temporal = nuevaPasswordTemporal();
        actual.setPasswordHash(PasswordTemporalHasher.encode(temporal));
        actual.setUsuarioEditor(editorId);
        usuarioRepository.save(actual);
        return temporal;
    }

    public void establecerPasswordArgon2(Usuario usuario, String rawPassword) {
        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        usuarioRepository.save(usuario);
    }

    public static boolean esArgon2(String passwordHash) {
        return passwordHash != null && passwordHash.startsWith("$argon2");
    }

    private String nuevaPasswordTemporal() {
        StringBuilder builder = new StringBuilder(TEMP_LENGTH);
        for (int i = 0; i < TEMP_LENGTH; i++) {
            builder.append(TEMP_ALPHABET.charAt(TEMP_RANDOM.nextInt(TEMP_ALPHABET.length())));
        }
        return builder.toString();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
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
        response.setActivo(usuario.isActivo());
        return response;
    }
}
