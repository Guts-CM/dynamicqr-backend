package dynamicqr.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.regex.Pattern;

public final class UserAgentParser {

    private static final Pattern IPV4 = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    private static final Pattern IPV6 = Pattern.compile("^[0-9a-fA-F:]+$");

    private UserAgentParser() {
    }

    public static String dispositivo(String userAgent) {
        String ua = safe(userAgent);
        if (ua.contains("ipad") || (ua.contains("android") && !ua.contains("mobile")) || ua.contains("tablet")) {
            return "tablet";
        }
        if (ua.contains("mobile") || ua.contains("iphone") || ua.contains("android") || ua.contains("ipod")) {
            return "movil";
        }
        return "escritorio";
    }

    public static String navegador(String userAgent) {
        String ua = safe(userAgent);
        if (ua.contains("edg/")) {
            return "Edge";
        }
        if (ua.contains("opr/") || ua.contains("opera")) {
            return "Opera";
        }
        if (ua.contains("chrome/") && !ua.contains("edg/") && !ua.contains("opr/")) {
            return "Chrome";
        }
        if (ua.contains("firefox/")) {
            return "Firefox";
        }
        if (ua.contains("safari/") && !ua.contains("chrome/")) {
            return "Safari";
        }
        return truncar("otro", 80);
    }

    public static String sistemaOperativo(String userAgent) {
        String ua = safe(userAgent);
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod") || ua.contains("ios")) {
            return "iOS";
        }
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("mac os") || ua.contains("macos")) {
            return "macOS";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        return truncar("otro", 80);
    }

    public static String ipCliente(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String candidate = forwarded != null && !forwarded.isBlank()
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
        return sanitizarIp(candidate);
    }

    public static String sanitizarIp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String ip = value.trim();
        if (ip.startsWith("[") && ip.endsWith("]")) {
            ip = ip.substring(1, ip.length() - 1);
        }
        int zone = ip.indexOf('%');
        if (zone >= 0) {
            ip = ip.substring(0, zone);
        }
        if (ip.startsWith("::ffff:")) {
            ip = ip.substring("::ffff:".length());
        }
        if (IPV4.matcher(ip).matches() || (ip.contains(":") && IPV6.matcher(ip).matches())) {
            return ip;
        }
        return null;
    }

    public static String truncar(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    private static String safe(String userAgent) {
        return userAgent == null ? "" : userAgent.toLowerCase(Locale.ROOT);
    }
}
