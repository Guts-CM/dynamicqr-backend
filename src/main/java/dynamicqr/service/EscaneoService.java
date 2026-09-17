package dynamicqr.service;

import dynamicqr.domain.Escaneo;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EscaneoService {

    private final EscaneoRepository escaneoRepository;

    public EscaneoService(EscaneoRepository escaneoRepository) {
        this.escaneoRepository = escaneoRepository;
    }

    @Transactional(readOnly = true)
    public List<Escaneo> findAll() {
        return escaneoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Escaneo findById(Integer id) {
        return escaneoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escaneo " + id + " no encontrado"));
    }

    public Escaneo create(Escaneo escaneo) {
        escaneo.setEscaneosId(null);
        return escaneoRepository.save(escaneo);
    }

    public Escaneo update(Integer id, Escaneo datos) {
        Escaneo actual = findById(id);
        actual.setQrId(datos.getQrId());
        actual.setIp(datos.getIp());
        actual.setUserAgent(datos.getUserAgent());
        actual.setDispositivo(datos.getDispositivo());
        actual.setNavegador(datos.getNavegador());
        actual.setSistemaOperativo(datos.getSistemaOperativo());
        actual.setPais(datos.getPais());
        actual.setCiudad(datos.getCiudad());
        actual.setReferrer(datos.getReferrer());
        actual.setUsuarioEditor(datos.getUsuarioEditor());
        return escaneoRepository.save(actual);
    }

    public void delete(Integer id) {
        Escaneo actual = findById(id);
        escaneoRepository.delete(actual);
    }
}
