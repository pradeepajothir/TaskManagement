package uk.gov.hmcts.reform.dev.utlis;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class Sanitizer {

    private Sanitizer() {
    }

    public static String sanitize(String input) {
        return input == null ? null : Jsoup.clean(input, Safelist.basic());
    }
}
