package dynamicqr.service;

import dynamicqr.domain.Usuario;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario " + id + " no encontrado"));
    }

    public Usuario create(Usuario usuario) {
        usuario.setUsuariosId(null);
        return usuarioRepository.save(usuario);
    }

    public Usuario update(Integer id, Usuario datos) {
        Usuario actual = findById(id);
        actual.setEmail(datos.getEmail());
        actual.setNombre(datos.getNombre());
        actual.setApellidoPaterno(datos.getApellidoPaterno());
        actual.setApellidoMaterno(datos.getApellidoMaterno());
        if (datos.getPasswordHash() != null) {
            actual.setPasswordHash(datos.getPasswordHash());
        }
        actual.setActivo(datos.isActivo());
        actual.setUsuarioEditor(datos.getUsuarioEditor());
        return usuarioRepository.save(actual);
    }

    public void delete(Integer id) {
        Usuario actual = findById(id);
        usuarioRepository.delete(actual);
    }
}
