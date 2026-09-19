package dynamicqr.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordTemporalHasher {

    static final String PREFIX = "$tmp$";
    private static final int SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64_DEC = Base64.getUrlDecoder();

    private PasswordTemporalHasher() {}

    public static String encode(String rawPassword) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return PREFIX + B64.encodeToString(salt) + "$" + B64.encodeToString(digest(salt, rawPassword));
    }

    public static boolean esTemporal(String stored) {
        return stored != null && stored.startsWith(PREFIX);
    }

    public static boolean matches(String stored, String rawPassword) {
        if (!esTemporal(stored) || rawPassword == null) {
            return false;
        }

        String payload = stored.substring(PREFIX.length());
        int separator = payload.indexOf('$');
        if (separator <= 0 || separator == payload.length() - 1) {
            return false;
        }

        try {
            byte[] salt = B64_DEC.decode(payload.substring(0, separator));
            byte[] expected = B64_DEC.decode(payload.substring(separator + 1));
            byte[] actual = digest(salt, rawPassword);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] digest(byte[] salt, String rawPassword) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(salt);
            return sha.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no disponible", ex);
        }
    }
}
