package dynamicqr.dto;

public class TokenResponse {

    private String token;
    private int duracion;
    private Integer usuarioId;
    private boolean requiereCambioPassword;

    public TokenResponse() {
    }

    public TokenResponse(String token, int duracion, Integer usuarioId) {
        this(token, duracion, usuarioId, false);
    }

    public TokenResponse(String token, int duracion, Integer usuarioId, boolean requiereCambioPassword) {
        this.token = token;
        this.duracion = duracion;
        this.usuarioId = usuarioId;
        this.requiereCambioPassword = requiereCambioPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public boolean isRequiereCambioPassword() {
        return requiereCambioPassword;
    }

    public void setRequiereCambioPassword(boolean requiereCambioPassword) {
        this.requiereCambioPassword = requiereCambioPassword;
    }
}
