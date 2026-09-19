package dynamicqr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dynamicqr.domain.QrTipo;
import dynamicqr.dto.QrContenidoEnvelope;
import dynamicqr.dto.QrEstiloRequest;
import org.springframework.stereotype.Component;

@Component
public class QrContenidoMapper {

    private final ObjectMapper objectMapper;

    public QrContenidoMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String escribir(QrTipo tipo, String payload, QrEstiloRequest estilo) {
        QrContenidoEnvelope envelope = new QrContenidoEnvelope();
        envelope.setEstilo(estilo == null ? new QrEstiloRequest() : estilo);
        envelope.setPayload(tipo == QrTipo.url ? null : payload);
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("No se pudo guardar el estilo del QR");
        }
    }

    public QrEstiloRequest leerEstilo(String contenido) {
        QrContenidoEnvelope envelope = leer(contenido);
        return envelope.getEstilo() == null ? new QrEstiloRequest() : envelope.getEstilo();
    }

    public String leerPayload(QrTipo tipo, String contenido, String destinoUrl) {
        if (tipo == QrTipo.url) {
            return destinoUrl;
        }
        QrContenidoEnvelope envelope = leer(contenido);
        if (envelope.getPayload() != null && !envelope.getPayload().isBlank()) {
            return envelope.getPayload();
        }
        return contenido;
    }

    public String destinoHistorial(QrTipo tipo, String destinoUrl, String contenido) {
        if (tipo == QrTipo.url) {
            return destinoUrl;
        }
        return leerPayload(tipo, contenido, destinoUrl);
    }

    private QrContenidoEnvelope leer(String contenido) {
        if (contenido == null || contenido.isBlank()) {
            return vacio();
        }
        try {
            JsonNode node = objectMapper.readTree(contenido);
            if (node.isObject() && node.has("estilo")) {
                return objectMapper.treeToValue(node, QrContenidoEnvelope.class);
            }
        } catch (JsonProcessingException ignored) {
            // contenido legado: texto plano, wifi, vcard, etc.
        }
        QrContenidoEnvelope envelope = vacio();
        envelope.setPayload(contenido);
        return envelope;
    }

    private QrContenidoEnvelope vacio() {
        QrContenidoEnvelope envelope = new QrContenidoEnvelope();
        envelope.setEstilo(new QrEstiloRequest());
        return envelope;
    }
}
