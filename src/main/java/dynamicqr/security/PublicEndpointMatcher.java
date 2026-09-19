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

        if (isPublicAuth(uri, servlet, combined)) {
            return true;
        }
        // Servidor publico: descomentar para dejar abierta la URL corta de conteo.
        // if (isRedirect(uri, servlet, combined)) {
        //     return true;
        // }
        return isGet(request) && isAsset(uri, servlet, combined);
    }

    private boolean isPublicAuth(String uri, String servlet, String combined) {
        return isAuthPath(uri, servlet, combined, "/api/auth/login")
                || isAuthPath(uri, servlet, combined, "/api/auth/password");
    }

    private boolean isAuthPath(String uri, String servlet, String combined, String path) {
        return uri.endsWith(path) || servlet.equals(path) || combined.equals(path);
    }

    // private boolean isRedirect(String uri, String servlet, String combined) {
    //     return looksLikeRedirect(uri) || looksLikeRedirect(servlet) || looksLikeRedirect(combined);
    // }
    //
    // private boolean looksLikeRedirect(String path) {
    //     return path.matches(".*/r/\\d+/?");
    // }

    private boolean isAsset(String uri, String servlet, String combined) {
        return looksLikeAsset(uri) || looksLikeAsset(servlet) || looksLikeAsset(combined);
    }

    private boolean looksLikeAsset(String path) {
        return path.matches(".*/api/qr/\\d+/(svg|png)/?");
    }

    private boolean isGet(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
