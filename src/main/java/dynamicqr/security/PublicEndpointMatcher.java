package dynamicqr.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class PublicEndpointMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = safe(request.getRequestURI());
        String servlet = safe(request.getServletPath());
        String pathInfo = safe(request.getPathInfo());
        String combined = servlet + pathInfo;

        if (isLogin(uri, servlet, combined)) {
            return true;
        }
        // Servidor publico: descomentar para dejar abierta la URL corta de conteo.
        // if (isRedirect(uri, servlet, combined)) {
        //     return true;
        // }
        return isGet(request) && isSvg(uri, servlet, combined);
    }

    private boolean isLogin(String uri, String servlet, String combined) {
        return uri.endsWith("/api/auth/login")
                || servlet.equals("/api/auth/login")
                || combined.equals("/api/auth/login");
    }

    // private boolean isRedirect(String uri, String servlet, String combined) {
    //     return looksLikeRedirect(uri) || looksLikeRedirect(servlet) || looksLikeRedirect(combined);
    // }
    //
    // private boolean looksLikeRedirect(String path) {
    //     return path.matches(".*/r/\\d+/?");
    // }

    private boolean isSvg(String uri, String servlet, String combined) {
        return uri.matches(".*/api/qr/\\d+/svg/?")
                || servlet.matches(".*/api/qr/\\d+/svg/?")
                || combined.matches(".*/api/qr/\\d+/svg/?");
    }

    private boolean isGet(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
