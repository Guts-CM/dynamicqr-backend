package dynamicqr.service;

import dynamicqr.domain.Escaneo;
import dynamicqr.domain.Qr;
import dynamicqr.dto.EscaneoResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EscaneoService {

    private final EscaneoRepository escaneoRepository;

    public EscaneoService(EscaneoRepository escaneoRepository) {
        this.escaneoRepository = escaneoRepository;
    }

    @Transactional(readOnly = true)
    public List<EscaneoResponse> findAll() {
        return escaneoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EscaneoResponse> findByQrId(Integer qrId) {
        return escaneoRepository.findByQrIdOrderByFechaCreacionDesc(qrId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EscaneoResponse findById(Integer id) {
        return toResponse(escaneoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Escaneo " + id + " no encontrado")));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Escaneo registrar(Qr qr, HttpServletRequest request) {
        String userAgent = UserAgentParser.truncar(request.getHeader("User-Agent"), 2000);
        Escaneo escaneo = new Escaneo();
        escaneo.setQrId(qr.getQrId());
        escaneo.setIp(UserAgentParser.ipCliente(request));
        escaneo.setUserAgent(userAgent);
        escaneo.setDispositivo(UserAgentParser.dispositivo(userAgent));
        escaneo.setNavegador(UserAgentParser.navegador(userAgent));
        escaneo.setSistemaOperativo(UserAgentParser.sistemaOperativo(userAgent));
        escaneo.setReferrer(UserAgentParser.truncar(request.getHeader("Referer"), 2000));
        escaneo.setUsuarioCreador(qr.getUsuarioCreador());
        escaneo.setUsuarioEditor(qr.getUsuarioCreador());
        return escaneoRepository.save(escaneo);
    }

    private EscaneoResponse toResponse(Escaneo escaneo) {
        EscaneoResponse response = new EscaneoResponse();
        response.setEscaneosId(escaneo.getEscaneosId());
        response.setQrId(escaneo.getQrId());
        response.setIp(escaneo.getIp());
        response.setUserAgent(escaneo.getUserAgent());
        response.setDispositivo(escaneo.getDispositivo());
        response.setNavegador(escaneo.getNavegador());
        response.setSistemaOperativo(escaneo.getSistemaOperativo());
        response.setPais(escaneo.getPais());
        response.setCiudad(escaneo.getCiudad());
        response.setReferrer(escaneo.getReferrer());
        response.setUsuarioCreador(escaneo.getUsuarioCreador());
        response.setFechaCreacion(escaneo.getFechaCreacion());
        return response;
    }
}
