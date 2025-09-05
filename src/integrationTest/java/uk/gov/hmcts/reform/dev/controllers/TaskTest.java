package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.services.SessionService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TaskTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    SessionService sessionService;

    private String token;

    @BeforeEach
    void seedSession() {
        token = sessionService.issueToken("user_name").token();
    }

    @DisplayName("Root returns welcome with 200 when authorized")
    @Test
    void welcomeRootEndpoint() throws Exception {
        mockMvc.perform(
                get("/")
                    .header("Authorization", "Bearer " + token) // satisfy TokenAuthFilter
            )
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.startsWith("Welcome")));
    }

    @DisplayName("Add task returns welcome with 200 when authorized")
    @Test
    void addTask() throws Exception {
        mockMvc.perform(
                post("/api/tasks")
                    .header("Authorization", "Bearer " + token)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content("""
                                   {
                                       "title":"New task",
                                       "description":"With in memory DB",
                                       "status":"in progress",
                                       "due_date": "2025-08-27T00:00:00"
                                   }
                                 """)
            )
            .andExpect(status().isOk());
    }
}
