package uk.gov.hmcts.reform.dev.utlis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SanitizerTest {

    @Test
    void sanitize_removesScriptTag() {
        String input = "<div>Hello<script>alert('x');</script></div>";
        String sanitized = Sanitizer.sanitize(input);
        assertFalse(sanitized.contains("<script>"));
        assertTrue(sanitized.contains("Hello"));
    }
}
