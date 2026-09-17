package dynamicqr.service;

import dynamicqr.domain.Qr;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QrService {

    private final QrRepository qrRepository;

    public QrService(QrRepository qrRepository) {
        this.qrRepository = qrRepository;
    }

    @Transactional(readOnly = true)
    public List<Qr> findAll() {
        return qrRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Qr findById(Integer id) {
        return qrRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QR " + id + " no encontrado"));
    }

    public Qr create(Qr qr) {
        qr.setQrId(null);
        return qrRepository.save(qr);
    }

    public Qr update(Integer id, Qr datos) {
        Qr actual = findById(id);
        actual.setNombre(datos.getNombre());
        actual.setTipo(datos.getTipo());
        actual.setDestinoUrl(datos.getDestinoUrl());
        actual.setContenido(datos.getContenido());
        actual.setActivo(datos.isActivo());
        actual.setTotalEscaneos(datos.getTotalEscaneos());
        actual.setUsuarioEditor(datos.getUsuarioEditor());
        return qrRepository.save(actual);
    }

    public void delete(Integer id) {
        Qr actual = findById(id);
        qrRepository.delete(actual);
    }
}
