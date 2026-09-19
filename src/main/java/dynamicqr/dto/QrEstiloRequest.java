package dynamicqr.dto;

import dynamicqr.domain.QrFormaModulo;
import dynamicqr.domain.QrFormaOjo;

public class QrEstiloRequest {

    private String colorModulos = "#111827";
    private String colorFondo = "#FFFFFF";
    private String colorGradiente;
    private QrFormaModulo formaModulo = QrFormaModulo.redondeado;
    private QrFormaOjo formaOjo = QrFormaOjo.redondeado;

    public String getColorModulos() {
        return colorModulos;
    }

    public void setColorModulos(String colorModulos) {
        this.colorModulos = colorModulos;
    }

    public String getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(String colorFondo) {
        this.colorFondo = colorFondo;
    }

    public String getColorGradiente() {
        return colorGradiente;
    }

    public void setColorGradiente(String colorGradiente) {
        this.colorGradiente = colorGradiente;
    }

    public QrFormaModulo getFormaModulo() {
        return formaModulo;
    }

    public void setFormaModulo(QrFormaModulo formaModulo) {
        this.formaModulo = formaModulo;
    }

    public QrFormaOjo getFormaOjo() {
        return formaOjo;
    }

    public void setFormaOjo(QrFormaOjo formaOjo) {
        this.formaOjo = formaOjo;
    }
}
