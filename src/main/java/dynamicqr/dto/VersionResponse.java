package dynamicqr.dto;

import java.time.LocalDateTime;

public class VersionResponse {

    private Integer versionesId;
    private Integer qrId;
    private String destinoAnterior;
    private String destinoNuevo;
    private String nombreAnterior;
    private String nombreNuevo;
    private String nota;
    private Integer usuarioCreador;
    private LocalDateTime fechaCreacion;

    public Integer getVersionesId() {
        return versionesId;
    }

    public void setVersionesId(Integer versionesId) {
        this.versionesId = versionesId;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public String getDestinoAnterior() {
        return destinoAnterior;
    }

    public void setDestinoAnterior(String destinoAnterior) {
        this.destinoAnterior = destinoAnterior;
    }

    public String getDestinoNuevo() {
        return destinoNuevo;
    }

    public void setDestinoNuevo(String destinoNuevo) {
        this.destinoNuevo = destinoNuevo;
    }

    public String getNombreAnterior() {
        return nombreAnterior;
    }

    public void setNombreAnterior(String nombreAnterior) {
        this.nombreAnterior = nombreAnterior;
    }

    public String getNombreNuevo() {
        return nombreNuevo;
    }

    public void setNombreNuevo(String nombreNuevo) {
        this.nombreNuevo = nombreNuevo;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Integer getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Integer usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
