package dynamicqr.service;

import dynamicqr.domain.Qr;
import dynamicqr.domain.QrTipo;
import dynamicqr.dto.QrCreateRequest;
import dynamicqr.dto.QrEstiloRequest;
import dynamicqr.dto.QrResponse;
import dynamicqr.dto.QrUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QrService {

    private final QrRepository qrRepository;
    private final QrSvgRenderer qrSvgRenderer;
    private final QrContenidoMapper qrContenidoMapper;
    private final VersionService versionService;
    // Uso de escaneos pendiente hasta desplegar en un servidor publico.
    @SuppressWarnings("unused")
    private final EscaneoService escaneoService;
    private final String publicBaseUrl;

    public QrService(
            QrRepository qrRepository,
            QrSvgRenderer qrSvgRenderer,
            QrContenidoMapper qrContenidoMapper,
            VersionService versionService,
            EscaneoService escaneoService,
            @Value("${app.public-base-url}") String publicBaseUrl) {
        this.qrRepository = qrRepository;
        this.qrSvgRenderer = qrSvgRenderer;
        this.qrContenidoMapper = qrContenidoMapper;
        this.versionService = versionService;
        this.escaneoService = escaneoService;
        this.publicBaseUrl = trimSlash(publicBaseUrl);
    }

    @Transactional(readOnly = true)
    public List<QrResponse> findAll() {
        return qrRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public QrResponse findById(Integer id) {
        return toResponse(findEntity(id));
    }

    public QrResponse create(QrCreateRequest request, Integer actorId) {
        QrTipo tipo = request.getTipo() == null ? QrTipo.url : request.getTipo();
        validarDatos(tipo, request.getDestinoUrl(), request.getContenido());

        Qr qr = new Qr();
        qr.setNombre(request.getNombre().trim());
        qr.setTipo(tipo);
        qr.setDestinoUrl(tipo == QrTipo.url ? request.getDestinoUrl().trim() : null);
        qr.setContenido(qrContenidoMapper.escribir(tipo, request.getContenido(), request.getEstilo()));
        qr.setActivo(true);
        qr.setTotalEscaneos(0);
        qr.setUsuarioCreador(actorId);
        qr.setUsuarioEditor(actorId);
        return toResponse(qrRepository.save(qr));
    }

    public QrResponse update(Integer id, QrUpdateRequest request, Integer editorId) {
        Qr actual = findEntity(id);
        String nombreAnterior = actual.getNombre();
        String destinoAnterior = qrContenidoMapper.destinoHistorial(
                actual.getTipo(), actual.getDestinoUrl(), actual.getContenido());
        QrEstiloRequest estiloActual = qrContenidoMapper.leerEstilo(actual.getContenido());
        String payloadActual = qrContenidoMapper.leerPayload(
                actual.getTipo(), actual.getContenido(), actual.getDestinoUrl());

        if (request.getNombre() != null) {
            if (request.getNombre().isBlank()) {
                throw new IllegalArgumentException("nombre no puede ir vacio");
            }
            actual.setNombre(request.getNombre().trim());
        }
        if (request.getTipo() != null) {
            actual.setTipo(request.getTipo());
        }
        if (request.getDestinoUrl() != null) {
            actual.setDestinoUrl(request.getDestinoUrl().isBlank() ? null : request.getDestinoUrl().trim());
        }
        QrEstiloRequest estiloNuevo = request.getEstilo() == null ? estiloActual : request.getEstilo();
        String payloadNuevo = request.getContenido() == null ? payloadActual : request.getContenido();
        validarDatos(actual.getTipo(), actual.getDestinoUrl(), payloadNuevo);
        if (actual.getTipo() != QrTipo.url) {
            actual.setDestinoUrl(null);
        }
        actual.setContenido(qrContenidoMapper.escribir(actual.getTipo(), payloadNuevo, estiloNuevo));
        actual.setUsuarioEditor(editorId);

        String destinoNuevo = qrContenidoMapper.destinoHistorial(
                actual.getTipo(), actual.getDestinoUrl(), actual.getContenido());
        versionService.registrarCambio(
                actual.getQrId(),
                nombreAnterior,
                actual.getNombre(),
                destinoAnterior,
                destinoNuevo,
                request.getNota(),
                editorId);

        return toResponse(qrRepository.save(actual));
    }

    public boolean toggleActivo(Integer id, Integer editorId) {
        Qr actual = findEntity(id);
        actual.setActivo(!actual.isActivo());
        actual.setUsuarioEditor(editorId);
        qrRepository.save(actual);
        return actual.isActivo();
    }

    @Transactional(readOnly = true)
    public String generarSvg(Integer id) {
        Qr qr = findEntity(id);
        return qrSvgRenderer.render(payloadCodificado(qr), qrContenidoMapper.leerEstilo(qr.getContenido()));
    }

    // --- Escaneos / URL corta: descomentar al desplegar en un servidor publico ---
    // @Transactional(readOnly = true)
    // public void assertRedirigible(Integer id) {
    //     Qr qr = findEntity(id);
    //     if (!qr.isActivo()) {
    //         throw new EntityNotFoundException("QR " + id + " no encontrado");
    //     }
    //     if (qr.getTipo() != QrTipo.url || qr.getDestinoUrl() == null || qr.getDestinoUrl().isBlank()) {
    //         throw new IllegalArgumentException("Este QR no redirige a una URL");
    //     }
    // }
    //
    // public String registrarEscaneoYDestino(Integer id, HttpServletRequest request) {
    //     assertRedirigible(id);
    //     Qr qr = findEntity(id);
    //     String destino = qr.getDestinoUrl();
    //     try {
    //         escaneoService.registrar(qr, request);
    //         qr.setTotalEscaneos(qr.getTotalEscaneos() + 1);
    //         qrRepository.save(qr);
    //     } catch (RuntimeException ignored) {
    //     }
    //     return destino;
    // }

    Qr findEntity(Integer id) {
        return qrRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QR " + id + " no encontrado"));
    }

    private void validarDatos(QrTipo tipo, String destinoUrl, String contenido) {
        if (tipo == QrTipo.url) {
            if (destinoUrl == null || destinoUrl.isBlank()) {
                throw new IllegalArgumentException("destinoUrl es obligatorio cuando tipo es url");
            }
            String destino = destinoUrl.trim();
            if (!destino.startsWith("http://") && !destino.startsWith("https://")) {
                throw new IllegalArgumentException("destinoUrl debe comenzar con http:// o https://");
            }
            return;
        }
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("contenido es obligatorio cuando el tipo no es url");
        }
    }

    private String payloadCodificado(Qr qr) {
        if (qr.getTipo() == QrTipo.url) {
            // Local: el SVG apunta directo a destinoUrl para que el QR funcione con el backend apagado.
            // Servidor publico: descomentar la linea de urlRedireccion y comentar el return de destinoUrl
            // para contar escaneos en /r/{id} y poder cambiar el destino sin reimprimir.
            // return urlRedireccion(qr.getQrId());
            return qr.getDestinoUrl();
        }
        return qrContenidoMapper.leerPayload(qr.getTipo(), qr.getContenido(), qr.getDestinoUrl());
    }

    private QrResponse toResponse(Qr qr) {
        QrResponse response = new QrResponse();
        response.setQrId(qr.getQrId());
        response.setNombre(qr.getNombre());
        response.setTipo(qr.getTipo());
        response.setDestinoUrl(qr.getDestinoUrl());
        response.setContenido(qr.getTipo() == QrTipo.url
                ? null
                : qrContenidoMapper.leerPayload(qr.getTipo(), qr.getContenido(), qr.getDestinoUrl()));
        response.setActivo(qr.isActivo());
        response.setTotalEscaneos(qr.getTotalEscaneos());
        response.setUsuarioCreador(qr.getUsuarioCreador());
        response.setFechaCreacion(qr.getFechaCreacion());
        response.setUsuarioEditor(qr.getUsuarioEditor());
        response.setFechaEdicion(qr.getFechaEdicion());
        // Servidor publico: response.setUrlRedireccion(qr.getTipo() == QrTipo.url ? urlRedireccion(qr.getQrId()) : null);
        response.setUrlSvg(publicBaseUrl + "/api/qr/" + qr.getQrId() + "/svg");
        response.setEstilo(qrContenidoMapper.leerEstilo(qr.getContenido()));
        return response;
    }

    // private String urlRedireccion(Integer qrId) {
    //     return publicBaseUrl + "/r/" + qrId;
    // }

    private String trimSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
