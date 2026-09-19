package dynamicqr.dto;

public class QrContenidoEnvelope {

    private QrEstiloRequest estilo;
    private String payload;

    public QrEstiloRequest getEstilo() {
        return estilo;
    }

    public void setEstilo(QrEstiloRequest estilo) {
        this.estilo = estilo;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
