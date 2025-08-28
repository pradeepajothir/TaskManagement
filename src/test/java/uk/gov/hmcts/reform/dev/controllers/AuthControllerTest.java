package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.models.requests.LoginRequest;
import uk.gov.hmcts.reform.dev.models.responses.LoginResponse;
import uk.gov.hmcts.reform.dev.services.SessionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private SessionService sessionService;
    @InjectMocks
    private AuthController authController;

    @Test
    void login_ReturnSessionTokenAndTimoutEpoc() {
        LoginRequest loginRequest = LoginRequest
            .builder()
            .password("password")
            .username("username")
            .build();

        when(sessionService.issueToken("username")).thenReturn(
            LoginResponse.builder().token("some_token").expiresAtEpochSec(12L).build()
        );

        var response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
    }

    @Test
    void login_ReturnBadRequest() {
        LoginRequest loginRequest = LoginRequest
            .builder()
            .password("")
            .username("")
            .build();

        var response = authController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
