package uk.gov.hmcts.reform.dev.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventListener.class);

    // Log application startup event
    @org.springframework.context.event.EventListener
    public void onApplicationStartup(ContextRefreshedEvent event) {
        logger.info("[ApplicationEventListener] Application started successfully");
    }

    // Log application shutdown event
    @org.springframework.context.event.EventListener
    public void onApplicationShutdown(ContextClosedEvent event) {
        logger.info("[ApplicationEventListener] Application is shutting down");
    }
}