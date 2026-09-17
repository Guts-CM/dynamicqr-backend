package dynamicqr.dto;

import dynamicqr.domain.QrTipo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class QrCreateRequest {

    @NotBlank
    private String nombre;

    private QrTipo tipo = QrTipo.url;

    private String destinoUrl;

    private String contenido;

    @Valid
    private QrEstiloRequest estilo = new QrEstiloRequest();

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public QrTipo getTipo() {
        return tipo;
    }

    public void setTipo(QrTipo tipo) {
        this.tipo = tipo;
    }

    public String getDestinoUrl() {
        return destinoUrl;
    }

    public void setDestinoUrl(String destinoUrl) {
        this.destinoUrl = destinoUrl;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public QrEstiloRequest getEstilo() {
        return estilo;
    }

    public void setEstilo(QrEstiloRequest estilo) {
        this.estilo = estilo;
    }
}
