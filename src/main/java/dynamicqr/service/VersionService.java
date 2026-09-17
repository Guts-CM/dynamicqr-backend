package dynamicqr.service;

import dynamicqr.domain.Version;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
    public List<Version> findAll() {
        return versionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Version findById(Integer id) {
        return versionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Version " + id + " no encontrada"));
    }

    public Version create(Version version) {
        version.setVersionesId(null);
        return versionRepository.save(version);
    }

    public Version update(Integer id, Version datos) {
        Version actual = findById(id);
        actual.setQrId(datos.getQrId());
        actual.setDestinoAnterior(datos.getDestinoAnterior());
        actual.setDestinoNuevo(datos.getDestinoNuevo());
        actual.setNombreAnterior(datos.getNombreAnterior());
        actual.setNombreNuevo(datos.getNombreNuevo());
        actual.setNota(datos.getNota());
        actual.setUsuarioEditor(datos.getUsuarioEditor());
        return versionRepository.save(actual);
    }

    public void delete(Integer id) {
        Version actual = findById(id);
        versionRepository.delete(actual);
    }
}
