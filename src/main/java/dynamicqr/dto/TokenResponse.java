package dynamicqr.dto;

public class TokenResponse {

    private String token;
    private int duracion;
    private Integer usuarioId;

    public TokenResponse() {
    }

    public TokenResponse(String token, int duracion, Integer usuarioId) {
        this.token = token;
        this.duracion = duracion;
        this.usuarioId = usuarioId;
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
}
