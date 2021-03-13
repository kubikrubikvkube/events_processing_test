package ru.swordfishsecurity.eventsTestTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SomeEntityEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(SomeEntityEvent someEntityEvent) {
        log.debug("Publishing event: '{}'",someEntityEvent);
        eventPublisher.publishEvent(someEntityEvent);
        log.debug("Event: '{}' published", someEntityEvent);
    }
}
