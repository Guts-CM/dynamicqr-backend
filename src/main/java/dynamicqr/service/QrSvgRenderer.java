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
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
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
        Style style = styleOf(estilo);
        boolean usarGradiente = style.colorGradiente != null;
        String fill = usarGradiente ? "url(#qrGradiente)" : style.colorModulos;

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
                    .append(style.colorModulos)
                    .append("\"/>")
                    .append("<stop offset=\"100%\" stop-color=\"")
                    .append(style.colorGradiente)
                    .append("\"/>")
                    .append("</linearGradient></defs>");
        }
        if (style.colorFondo != null) {
            svg.append("<rect width=\"100%\" height=\"100%\" fill=\"")
                    .append(style.colorFondo)
                    .append("\"/>");
        }
        svg.append("<g fill=\"").append(fill).append("\">");

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!matrix.get(x, y) || inFinder(x, y, size)) {
                    continue;
                }
                appendModule(svg, x, y, style.formaModulo);
            }
        }

        appendEye(svg, QUIET_ZONE, QUIET_ZONE, style.formaOjo);
        appendEye(svg, size - QUIET_ZONE - FINDER, QUIET_ZONE, style.formaOjo);
        appendEye(svg, QUIET_ZONE, size - QUIET_ZONE - FINDER, style.formaOjo);

        svg.append("</g></svg>");
        return svg.toString();
    }

    public byte[] renderPng(String payload, QrEstiloRequest estilo) {
        BitMatrix matrix = encode(payload);
        int size = matrix.getWidth();
        int px = size * MODULE;
        Style style = styleOf(estilo);
        BufferedImage image = new BufferedImage(px, px, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (style.colorFondo != null) {
            graphics.setColor(decodeColor(style.colorFondo, Color.WHITE));
            graphics.fillRect(0, 0, px, px);
        }

        Paint fill = style.colorGradiente == null
                ? decodeColor(style.colorModulos, new Color(0x11, 0x18, 0x27))
                : new GradientPaint(
                        0,
                        0,
                        decodeColor(style.colorModulos, new Color(0x11, 0x18, 0x27)),
                        px,
                        px,
                        decodeColor(style.colorGradiente, new Color(0x63, 0x66, 0xF1)));
        graphics.setPaint(fill);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (!matrix.get(x, y) || inFinder(x, y, size)) {
                    continue;
                }
                fillModule(graphics, x, y, style.formaModulo);
            }
        }

        fillEye(graphics, QUIET_ZONE, QUIET_ZONE, style.formaOjo);
        fillEye(graphics, size - QUIET_ZONE - FINDER, QUIET_ZONE, style.formaOjo);
        fillEye(graphics, QUIET_ZONE, size - QUIET_ZONE - FINDER, style.formaOjo);
        graphics.dispose();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, "png", out)) {
                throw new IllegalArgumentException("No se pudo generar el PNG del QR");
            }
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo generar el PNG del QR");
        }
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
                        .append(n(px + MODULE / 2.0))
                        .append("\" cy=\"")
                        .append(n(py + MODULE / 2.0))
                        .append("\" r=\"")
                        .append(n(r))
                        .append("\"/>");
            }
            case diamante -> svg.append("<polygon points=\"")
                    .append(diamondPoints(px, py))
                    .append("\"/>");
            case cuadrado -> appendRect(svg, px, py, MODULE, MODULE, 0);
            case suave -> appendRect(svg, px, py, MODULE, MODULE, MODULE * 0.18);
            case extra_redondeado -> appendRect(svg, px, py, MODULE, MODULE, MODULE * 0.48);
            default -> appendRect(svg, px, py, MODULE, MODULE, MODULE * 0.35);
        }
    }

    private void appendEye(StringBuilder svg, int left, int top, QrFormaOjo forma) {
        int x = left * MODULE;
        int y = top * MODULE;
        int outer = FINDER * MODULE;
        int ring = 5 * MODULE;
        int pupil = 3 * MODULE;

        if (forma == QrFormaOjo.circulo) {
            double cx = x + outer / 2.0;
            double cy = y + outer / 2.0;
            svg.append("<path fill-rule=\"evenodd\" d=\"")
                    .append(circlePath(cx, cy, outer / 2.0))
                    .append(' ')
                    .append(circlePath(cx, cy, ring / 2.0))
                    .append("\"/>");
            svg.append("<circle cx=\"")
                    .append(n(cx))
                    .append("\" cy=\"")
                    .append(n(cy))
                    .append("\" r=\"")
                    .append(n(pupil / 2.0))
                    .append("\"/>");
            return;
        }

        double rxOuter = eyeRadius(forma, MODULE * 1.1, MODULE * 2.35);
        double rxInner = eyeRadius(forma, MODULE * 0.75, MODULE * 1.7);
        double rxPupil = eyeRadius(forma, MODULE * 0.55, MODULE * 1.2);

        svg.append("<path fill-rule=\"evenodd\" d=\"")
                .append(roundedRectPath(x, y, outer, outer, rxOuter))
                .append(' ')
                .append(roundedRectPath(x + MODULE, y + MODULE, ring, ring, rxInner))
                .append("\"/>");
        appendRect(svg, x + MODULE * 2, y + MODULE * 2, pupil, pupil, rxPupil);
    }

    private void fillModule(Graphics2D graphics, int x, int y, QrFormaModulo forma) {
        int px = x * MODULE;
        int py = y * MODULE;
        switch (forma) {
            case circulo -> graphics.fill(new Ellipse2D.Double(
                    px + MODULE * 0.08, py + MODULE * 0.08, MODULE * 0.84, MODULE * 0.84));
            case diamante -> graphics.fill(diamondShape(px, py));
            case cuadrado -> graphics.fill(new RoundRectangle2D.Double(px, py, MODULE, MODULE, 0, 0));
            case suave -> graphics.fill(new RoundRectangle2D.Double(
                    px, py, MODULE, MODULE, MODULE * 0.36, MODULE * 0.36));
            case extra_redondeado -> graphics.fill(new RoundRectangle2D.Double(
                    px, py, MODULE, MODULE, MODULE * 0.96, MODULE * 0.96));
            default -> graphics.fill(new RoundRectangle2D.Double(px, py, MODULE, MODULE, MODULE * 0.7, MODULE * 0.7));
        }
    }

    private void fillEye(Graphics2D graphics, int left, int top, QrFormaOjo forma) {
        int x = left * MODULE;
        int y = top * MODULE;
        int outer = FINDER * MODULE;
        int ring = 5 * MODULE;
        int pupil = 3 * MODULE;

        if (forma == QrFormaOjo.circulo) {
            Area eye = new Area(new Ellipse2D.Double(x, y, outer, outer));
            eye.subtract(new Area(new Ellipse2D.Double(x + MODULE, y + MODULE, ring, ring)));
            graphics.fill(eye);
            graphics.fill(new Ellipse2D.Double(x + MODULE * 2, y + MODULE * 2, pupil, pupil));
            return;
        }

        double rxOuter = eyeRadius(forma, MODULE * 2.2, MODULE * 4.7);
        double rxInner = eyeRadius(forma, MODULE * 1.5, MODULE * 3.4);
        double rxPupil = eyeRadius(forma, MODULE * 1.1, MODULE * 2.4);

        Area eye = new Area(new RoundRectangle2D.Double(x, y, outer, outer, rxOuter, rxOuter));
        eye.subtract(new Area(new RoundRectangle2D.Double(x + MODULE, y + MODULE, ring, ring, rxInner, rxInner)));
        graphics.fill(eye);
        graphics.fill(new RoundRectangle2D.Double(
                x + MODULE * 2, y + MODULE * 2, pupil, pupil, rxPupil, rxPupil));
    }

    private double eyeRadius(QrFormaOjo forma, double rounded, double extra) {
        return switch (forma) {
            case extra_redondeado -> extra;
            case redondeado -> rounded;
            default -> 0;
        };
    }

    private void appendRect(StringBuilder svg, double x, double y, double w, double h, double rx) {
        svg.append("<rect x=\"")
                .append(n(x))
                .append("\" y=\"")
                .append(n(y))
                .append("\" width=\"")
                .append(n(w))
                .append("\" height=\"")
                .append(n(h))
                .append("\"");
        if (rx > 0) {
            svg.append(" rx=\"").append(n(rx)).append("\"");
        }
        svg.append("/>");
    }

    private String diamondPoints(int px, int py) {
        double cx = px + MODULE / 2.0;
        double cy = py + MODULE / 2.0;
        double inset = MODULE * 0.08;
        return n(cx) + "," + n(py + inset) + " "
                + n(px + MODULE - inset) + "," + n(cy) + " "
                + n(cx) + "," + n(py + MODULE - inset) + " "
                + n(px + inset) + "," + n(cy);
    }

    private Path2D diamondShape(int px, int py) {
        Path2D diamond = new Path2D.Double();
        double cx = px + MODULE / 2.0;
        double cy = py + MODULE / 2.0;
        double inset = MODULE * 0.08;
        diamond.moveTo(cx, py + inset);
        diamond.lineTo(px + MODULE - inset, cy);
        diamond.lineTo(cx, py + MODULE - inset);
        diamond.lineTo(px + inset, cy);
        diamond.closePath();
        return diamond;
    }

    private String circlePath(double cx, double cy, double r) {
        return "M " + n(cx - r) + "," + n(cy)
                + " a " + n(r) + " " + n(r) + " 0 1 0 " + n(r * 2) + " 0"
                + " a " + n(r) + " " + n(r) + " 0 1 0 " + n(-r * 2) + " 0 z";
    }

    private String roundedRectPath(double x, double y, double w, double h, double radius) {
        double r = Math.max(0, Math.min(radius, Math.min(w, h) / 2.0));
        if (r == 0) {
            return "M " + n(x) + "," + n(y) + " h " + n(w) + " v " + n(h) + " h " + n(-w) + " z";
        }

        return "M " + n(x + r) + "," + n(y)
                + " H " + n(x + w - r)
                + " A " + n(r) + " " + n(r) + " 0 0 1 " + n(x + w) + " " + n(y + r)
                + " V " + n(y + h - r)
                + " A " + n(r) + " " + n(r) + " 0 0 1 " + n(x + w - r) + " " + n(y + h)
                + " H " + n(x + r)
                + " A " + n(r) + " " + n(r) + " 0 0 1 " + n(x) + " " + n(y + h - r)
                + " V " + n(y + r)
                + " A " + n(r) + " " + n(r) + " 0 0 1 " + n(x + r) + " " + n(y)
                + " z";
    }

    private Style styleOf(QrEstiloRequest estilo) {
        QrEstiloRequest safe = estilo == null ? new QrEstiloRequest() : estilo;
        return new Style(
                sanitizeColor(safe.getColorModulos(), "#111827"),
                sanitizeBackground(safe.getColorFondo()),
                sanitizeOptionalColor(safe.getColorGradiente()),
                safe.getFormaModulo() == null ? QrFormaModulo.redondeado : safe.getFormaModulo(),
                safe.getFormaOjo() == null ? QrFormaOjo.redondeado : safe.getFormaOjo());
    }

    private String sanitizeColor(String value, String fallback) {
        String sanitized = sanitizeOptionalColor(value);
        return sanitized == null ? fallback : sanitized;
    }

    private String sanitizeBackground(String value) {
        if (isTransparent(value)) {
            return null;
        }
        return sanitizeColor(value, "#FFFFFF");
    }

    private boolean isTransparent(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.equalsIgnoreCase("transparent") || trimmed.equalsIgnoreCase("none");
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

    private Color decodeColor(String value, Color fallback) {
        String hex = sanitizeOptionalColor(value);
        if (hex == null) {
            return fallback;
        }
        String digits = hex.substring(1);
        if (digits.length() == 3 || digits.length() == 4) {
            StringBuilder expanded = new StringBuilder(8);
            for (int i = 0; i < digits.length(); i++) {
                expanded.append(digits.charAt(i)).append(digits.charAt(i));
            }
            digits = expanded.toString();
        }
        try {
            int r = Integer.parseInt(digits.substring(0, 2), 16);
            int g = Integer.parseInt(digits.substring(2, 4), 16);
            int b = Integer.parseInt(digits.substring(4, 6), 16);
            int a = digits.length() >= 8 ? Integer.parseInt(digits.substring(6, 8), 16) : 255;
            return new Color(r, g, b, a);
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return fallback;
        }
    }

    private String n(double value) {
        if (value == (long) value) {
            return Long.toString((long) value);
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private record Style(
            String colorModulos,
            String colorFondo,
            String colorGradiente,
            QrFormaModulo formaModulo,
            QrFormaOjo formaOjo) {}
}
