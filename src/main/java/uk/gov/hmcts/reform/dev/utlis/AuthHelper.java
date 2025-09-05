package uk.gov.hmcts.reform.dev.utlis;

public class AuthHelper {

    private AuthHelper() {
    }

    public static String extractBearer(String auth) {
        if (auth == null) {
            return null;
        }
        String p = "Bearer ";
        return auth.startsWith(p) ? auth.substring(p.length()) : null;
    }
}
