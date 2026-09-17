package dynamicqr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "versiones",
        schema = "dynamicqr",
        indexes = {
            @Index(name = "ix_versiones_qr_id", columnList = "qr_id"),
            @Index(name = "ix_versiones_usuario_creador", columnList = "usuario_creador"),
            @Index(name = "ix_versiones_usuario_editor", columnList = "usuario_editor")
        }
)
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "versiones_id", nullable = false)
    private Integer versionesId;

    @Column(name = "qr_id", nullable = false)
    private Integer qrId;

    @Column(name = "destino_anterior")
    private String destinoAnterior;

    @Column(name = "destino_nuevo")
    private String destinoNuevo;

    @Column(name = "nombre_anterior", length = 150)
    private String nombreAnterior;

    @Column(name = "nombre_nuevo", length = 150)
    private String nombreNuevo;

    @Column(name = "nota", length = 255)
    private String nota;

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
        if (!(o instanceof Version version)) {
            return false;
        }
        return versionesId != null && Objects.equals(versionesId, version.versionesId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
