package dynamicqr.dto;

import dynamicqr.domain.QrTipo;
import jakarta.validation.Valid;

public class QrUpdateRequest {

    private String nombre;

    private QrTipo tipo;

    private String destinoUrl;

    private String contenido;

    private String nota;

    @Valid
    private QrEstiloRequest estilo;

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

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public QrEstiloRequest getEstilo() {
        return estilo;
    }

    public void setEstilo(QrEstiloRequest estilo) {
        this.estilo = estilo;
    }
}
