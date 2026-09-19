package dynamicqr.dto;

public class PasswordTemporalResponse {

    private String passwordTemporal;

    public PasswordTemporalResponse() {}

    public PasswordTemporalResponse(String passwordTemporal) {
        this.passwordTemporal = passwordTemporal;
    }

    public String getPasswordTemporal() {
        return passwordTemporal;
    }

    public void setPasswordTemporal(String passwordTemporal) {
        this.passwordTemporal = passwordTemporal;
    }
}
