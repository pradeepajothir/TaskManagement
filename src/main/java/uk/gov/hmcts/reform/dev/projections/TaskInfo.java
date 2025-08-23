package uk.gov.hmcts.reform.dev.projections;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Projection for {@link uk.gov.hmcts.reform.dev.enities.Task}
 *
 * To hide unnecessary info from repository response.
 */
public interface TaskInfo {
    Long getId();
    String getTitle();
    String getDescription();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getDueDate();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCreatedAt();
}
