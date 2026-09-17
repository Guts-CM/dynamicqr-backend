package dynamicqr.service;

import dynamicqr.domain.Version;
import dynamicqr.dto.VersionResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VersionService {

    private final VersionRepository versionRepository;

    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    @Transactional(readOnly = true)
    public List<VersionResponse> findAll() {
        return versionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<VersionResponse> findByQrId(Integer qrId) {
        return versionRepository.findByQrIdOrderByFechaCreacionDesc(qrId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VersionResponse findById(Integer id) {
        return toResponse(versionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Version " + id + " no encontrada")));
    }

    public void registrarCambio(
            Integer qrId,
            String nombreAnterior,
            String nombreNuevo,
            String destinoAnterior,
            String destinoNuevo,
            String nota,
            Integer actorId) {
        boolean nombreCambio = !Objects.equals(nombreAnterior, nombreNuevo);
        boolean destinoCambio = !Objects.equals(destinoAnterior, destinoNuevo);
        if (!nombreCambio && !destinoCambio) {
            return;
        }

        Version version = new Version();
        version.setQrId(qrId);
        version.setNombreAnterior(nombreAnterior);
        version.setNombreNuevo(nombreNuevo);
        version.setDestinoAnterior(destinoAnterior);
        version.setDestinoNuevo(destinoNuevo);
        String notaLimpia = nota == null || nota.isBlank() ? null : nota.trim();
        if (notaLimpia != null && notaLimpia.length() > 255) {
            notaLimpia = notaLimpia.substring(0, 255);
        }
        version.setNota(notaLimpia);
        version.setUsuarioCreador(actorId);
        version.setUsuarioEditor(actorId);
        versionRepository.save(version);
    }

    private VersionResponse toResponse(Version version) {
        VersionResponse response = new VersionResponse();
        response.setVersionesId(version.getVersionesId());
        response.setQrId(version.getQrId());
        response.setDestinoAnterior(version.getDestinoAnterior());
        response.setDestinoNuevo(version.getDestinoNuevo());
        response.setNombreAnterior(version.getNombreAnterior());
        response.setNombreNuevo(version.getNombreNuevo());
        response.setNota(version.getNota());
        response.setUsuarioCreador(version.getUsuarioCreador());
        response.setFechaCreacion(version.getFechaCreacion());
        return response;
    }
}
