package uk.gov.hmcts.reform.dev.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class RootController {

    private static final Logger logger = LoggerFactory.getLogger(RootController.class);

    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        logger.info("[RootController][WELCOME] Received request for the root endpoint");
        String message = """
            Welcome to test-backend!
            Available resources:
            - API Documentation: /swagger-ui.html
            - Task Management: /tasks
            """;
        logger.info("[RootController][WELCOME] Returning welcome message");
        return ok(message);
    }

}
