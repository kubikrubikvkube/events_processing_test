package ru.swordfishsecurity.eventsTestTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Async listener of {@link SomeEntityEvent}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SomeEntityEventListener {

    private final SomeEntityEventPostProcessor postProcessor;
    private final SomeEntityEventsCache cache;
    private final SomeEntityEventsStats stats;


    @Async
    @EventListener
    public void onEvent(SomeEntityEvent event) {
        log.debug("SomeEntityEventListener received event: {}", event);
        if (cache.isPresent(event)) {
            stats.incrementRejectedAsNoiseCount();
        } else {
            cache.put(event);
            stats.incrementRoutedToPostProcessorCount();
            postProcessor.process(event);
        }
    }
}