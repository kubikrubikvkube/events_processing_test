package ru.swordfishsecurity.eventsTestTask;

import com.google.common.cache.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class SomeEntityEventsCache {
    private final Cache<Integer, SomeEntityEvent> cache = buildEmptyCache();

    public void put(SomeEntityEvent event) {
        log.trace("Putting '{}' into cache", event);
        cache.put(event.hashCode(), event);
    }

    public boolean isPresent(SomeEntityEvent event) {
        SomeEntityEvent nullableEventFromCache = cache.getIfPresent(event.hashCode());
        boolean isPresent = nullableEventFromCache != null;
        log.trace("SomeEntityEvent '{}' is present into cache: {}", event, isPresent);
        return isPresent;
    }

    public CacheStats stats() {
        return cache.stats();
    }

    private Cache<Integer, SomeEntityEvent> buildEmptyCache() {
        var removalListener = new RemovalListener<Integer, SomeEntityEvent>() {
            @Override
            public void onRemoval(RemovalNotification<Integer, SomeEntityEvent> notification) {
                log.debug("{} was evicted from cache because {}", notification.getValue(), notification.getCause());
            }
        };

        return CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(1))
                .recordStats()
                .removalListener(removalListener)
                .build();

    }

}
