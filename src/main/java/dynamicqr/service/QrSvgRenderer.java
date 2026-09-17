package dynamicqr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import dynamicqr.domain.QrFormaModulo;
import dynamicqr.domain.QrFormaOjo;
import dynamicqr.dto.QrEstiloRequest;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class QrSvgRenderer {

    private static final int MODULE = 12;
    private static final int QUIET_ZONE = 2;
    private static final int FINDER = 7;

    public String render(String payload, QrEstiloRequest estilo) {
        BitMatrix matrix = encode(payload);
        int size = matrix.getWidth();
        int px = size * MODULE;
        QrEstiloRequest safe = estilo == null ? new QrEstiloRequest() : estilo;
        String colorModulos = sanitizeColor(safe.getColorModulos(), "#111827");
        String colorFondo = sanitizeBackground(safe.getColorFondo());
        String colorGradiente = sanitizeOptionalColor(safe.getColorGradiente());
        QrFormaModulo formaModulo = safe.getFormaModulo() == null
                ? QrFormaModulo.redondeado
                : safe.getFormaModulo();
        QrFormaOjo formaOjo = safe.getFormaOjo() == null ? QrFormaOjo.redondeado : safe.getFormaOjo();
        boolean usarGradiente = colorGradiente != null;
        String fill = usarGradiente ? "url(#qrGradiente)" : colorModulos;

        StringBuilder svg = new StringBuilder(size * size * 24);
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 ")
                .append(px)
                .append(' ')
                .append(px)
                .append("\" width=\"")
                .append(px)
                .append("\" height=\"")
                .append(px)
                .append("\" shape-rendering=\"geometricPrecision\">");
        if (usarGradiente) {
            svg.append("<defs><linearGradient id=\"qrGradiente\" x1=\"0\" y1=\"0\" x2=\"1\" y2=\"1\">")
                    .append("<stop offset=\"0%\" stop-color=\"")
                    .append(colorModulos)
                    .append("\"/>")
                    .append("<stop offset=\"100%\" stop-color=\"")
                    .append(colorGradiente)
                    .append("\"/>")
                    .append("</linearGradient></defs>");
        }
        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"")
                .append(colorFondo)
                .append("\"/>");
        svg.append("<g fill=\"").append(fill).append("\">");

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!matrix.get(x, y) || inFinder(x, y, size)) {
                    continue;
                }
                appendModule(svg, x, y, formaModulo);
            }
        }

        appendEye(svg, QUIET_ZONE, QUIET_ZONE, colorFondo, formaOjo);
        appendEye(svg, size - QUIET_ZONE - FINDER, QUIET_ZONE, colorFondo, formaOjo);
        appendEye(svg, QUIET_ZONE, size - QUIET_ZONE - FINDER, colorFondo, formaOjo);

        svg.append("</g></svg>");
        return svg.toString();
    }

    private BitMatrix encode(String payload) {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, QUIET_ZONE);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            return new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 0, 0, hints);
        } catch (WriterException ex) {
            throw new IllegalArgumentException("No se pudo generar la matriz del QR");
        }
    }

    private boolean inFinder(int x, int y, int size) {
        return inBlock(x, y, QUIET_ZONE, QUIET_ZONE)
                || inBlock(x, y, size - QUIET_ZONE - FINDER, QUIET_ZONE)
                || inBlock(x, y, QUIET_ZONE, size - QUIET_ZONE - FINDER);
    }

    private boolean inBlock(int x, int y, int left, int top) {
        return x >= left && x < left + FINDER && y >= top && y < top + FINDER;
    }

    private void appendModule(StringBuilder svg, int x, int y, QrFormaModulo forma) {
        int px = x * MODULE;
        int py = y * MODULE;
        switch (forma) {
            case circulo -> {
                double r = MODULE * 0.42;
                svg.append("<circle cx=\"")
                        .append(px + MODULE / 2.0)
                        .append("\" cy=\"")
                        .append(py + MODULE / 2.0)
                        .append("\" r=\"")
                        .append(r)
                        .append("\"/>");
            }
            case cuadrado -> svg.append("<rect x=\"")
                    .append(px)
                    .append("\" y=\"")
                    .append(py)
                    .append("\" width=\"")
                    .append(MODULE)
                    .append("\" height=\"")
                    .append(MODULE)
                    .append("\"/>");
            default -> svg.append("<rect x=\"")
                    .append(px)
                    .append("\" y=\"")
                    .append(py)
                    .append("\" width=\"")
                    .append(MODULE)
                    .append("\" height=\"")
                    .append(MODULE)
                    .append("\" rx=\"")
                    .append(MODULE * 0.35)
                    .append("\"/>");
        }
    }

    private void appendEye(StringBuilder svg, int left, int top, String colorFondo, QrFormaOjo forma) {
        int x = left * MODULE;
        int y = top * MODULE;
        int outer = FINDER * MODULE;
        int ring = 5 * MODULE;
        int pupil = 3 * MODULE;
        String rxOuter = forma == QrFormaOjo.redondeado ? String.valueOf(MODULE * 1.1) : "0";
        String rxInner = forma == QrFormaOjo.redondeado ? String.valueOf(MODULE * 0.75) : "0";
        String rxPupil = forma == QrFormaOjo.redondeado ? String.valueOf(MODULE * 0.55) : "0";

        svg.append("<rect x=\"")
                .append(x)
                .append("\" y=\"")
                .append(y)
                .append("\" width=\"")
                .append(outer)
                .append("\" height=\"")
                .append(outer)
                .append("\" rx=\"")
                .append(rxOuter)
                .append("\"/>");
        svg.append("<rect x=\"")
                .append(x + MODULE)
                .append("\" y=\"")
                .append(y + MODULE)
                .append("\" width=\"")
                .append(ring)
                .append("\" height=\"")
                .append(ring)
                .append("\" rx=\"")
                .append(rxInner)
                .append("\" fill=\"")
                .append(colorFondo)
                .append("\"/>");
        svg.append("<rect x=\"")
                .append(x + MODULE * 2)
                .append("\" y=\"")
                .append(y + MODULE * 2)
                .append("\" width=\"")
                .append(pupil)
                .append("\" height=\"")
                .append(pupil)
                .append("\" rx=\"")
                .append(rxPupil)
                .append("\"/>");
    }

    private String sanitizeColor(String value, String fallback) {
        String sanitized = sanitizeOptionalColor(value);
        return sanitized == null ? fallback : sanitized;
    }

    private String sanitizeBackground(String value) {
        if (value != null && "transparent".equalsIgnoreCase(value.trim())) {
            return "none";
        }
        return sanitizeColor(value, "#FFFFFF");
    }

    private String sanitizeOptionalColor(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.matches("#[0-9A-Fa-f]{3,8}")) {
            return trimmed;
        }
        return null;
    }
}
