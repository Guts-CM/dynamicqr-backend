package dynamicqr.dto;

import dynamicqr.domain.QrTipo;
import java.time.LocalDateTime;

public class QrResponse {

    private Integer qrId;
    private String nombre;
    private QrTipo tipo;
    private String destinoUrl;
    private String contenido;
    private boolean activo;
    private int totalEscaneos;
    private Integer usuarioCreador;
    private String nombreCreador;
    private LocalDateTime fechaCreacion;
    private Integer usuarioEditor;
    private String nombreEditor;
    private LocalDateTime fechaEdicion;
    private String urlRedireccion;
    private String urlSvg;
    private String urlPng;
    private QrEstiloRequest estilo;

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getTotalEscaneos() {
        return totalEscaneos;
    }

    public void setTotalEscaneos(int totalEscaneos) {
        this.totalEscaneos = totalEscaneos;
    }

    public Integer getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Integer usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getUsuarioEditor() {
        return usuarioEditor;
    }

    public void setUsuarioEditor(Integer usuarioEditor) {
        this.usuarioEditor = usuarioEditor;
    }

    public String getNombreEditor() {
        return nombreEditor;
    }

    public void setNombreEditor(String nombreEditor) {
        this.nombreEditor = nombreEditor;
    }

    public LocalDateTime getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(LocalDateTime fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    public String getUrlRedireccion() {
        return urlRedireccion;
    }

    public void setUrlRedireccion(String urlRedireccion) {
        this.urlRedireccion = urlRedireccion;
    }

    public String getUrlSvg() {
        return urlSvg;
    }

    public void setUrlSvg(String urlSvg) {
        this.urlSvg = urlSvg;
    }

    public String getUrlPng() {
        return urlPng;
    }

    public void setUrlPng(String urlPng) {
        this.urlPng = urlPng;
    }

    public QrEstiloRequest getEstilo() {
        return estilo;
    }

    public void setEstilo(QrEstiloRequest estilo) {
        this.estilo = estilo;
    }
}
