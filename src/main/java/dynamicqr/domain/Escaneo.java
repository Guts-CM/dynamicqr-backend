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
        name = "escaneos",
        schema = "dynamicqr",
        indexes = {
            @Index(name = "ix_escaneos_qr_id", columnList = "qr_id"),
            @Index(name = "ix_escaneos_fecha_creacion", columnList = "fecha_creacion"),
            @Index(name = "ix_escaneos_usuario_creador", columnList = "usuario_creador"),
            @Index(name = "ix_escaneos_usuario_editor", columnList = "usuario_editor")
        }
)
public class Escaneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "escaneos_id", nullable = false)
    private Integer escaneosId;

    @Column(name = "qr_id", nullable = false)
    private Integer qrId;

    @Column(name = "ip", columnDefinition = "inet")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "dispositivo", length = 80)
    private String dispositivo;

    @Column(name = "navegador", length = 80)
    private String navegador;

    @Column(name = "sistema_operativo", length = 80)
    private String sistemaOperativo;

    @Column(name = "pais", length = 80)
    private String pais;

    @Column(name = "ciudad", length = 120)
    private String ciudad;

    @Column(name = "referrer")
    private String referrer;

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

    public Integer getEscaneosId() {
        return escaneosId;
    }

    public void setEscaneosId(Integer escaneosId) {
        this.escaneosId = escaneosId;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getNavegador() {
        return navegador;
    }

    public void setNavegador(String navegador) {
        this.navegador = navegador;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
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
        if (!(o instanceof Escaneo escaneo)) {
            return false;
        }
        return escaneosId != null && Objects.equals(escaneosId, escaneo.escaneosId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
