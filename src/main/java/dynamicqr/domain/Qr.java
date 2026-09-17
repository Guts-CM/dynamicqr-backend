package dynamicqr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "qr",
        schema = "dynamicqr",
        indexes = {
            @Index(name = "ix_qr_activo", columnList = "activo"),
            @Index(name = "ix_qr_usuario_creador", columnList = "usuario_creador"),
            @Index(name = "ix_qr_usuario_editor", columnList = "usuario_editor")
        }
)
public class Qr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qr_id", nullable = false)
    private Integer qrId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private QrTipo tipo = QrTipo.url;

    @Column(name = "destino_url")
    private String destinoUrl;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "total_escaneos", nullable = false)
    private int totalEscaneos = 0;

    @Column(name = "usuario_creador", nullable = false)
    private Integer usuarioCreador;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_editor", nullable = false)
    private Integer usuarioEditor;

    @Column(name = "fecha_edicion", nullable = false)
    private LocalDateTime fechaEdicion;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (fechaCreacion == null) {
            fechaCreacion = now;
        }
        fechaEdicion = now;
    }

    @PreUpdate
    void onUpdate() {
        fechaEdicion = LocalDateTime.now();
    }

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

    public LocalDateTime getFechaEdicion() {
        return fechaEdicion;
    }

    public void setFechaEdicion(LocalDateTime fechaEdicion) {
        this.fechaEdicion = fechaEdicion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Qr qr)) {
            return false;
        }
        return qrId != null && Objects.equals(qrId, qr.qrId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
