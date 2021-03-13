package ru.swordfishsecurity.eventsTestTask;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Some long-running {@link SomeEntityEvent} post-processor
 */
@Slf4j
@Component
public class SomeEntityEventPostProcessor {
    private final long minSleep = Duration.ofSeconds(5).toMillis();
    private final long maxSleep = Duration.ofSeconds(15).toMillis();
    private final AtomicLong processed = new AtomicLong();

    @SneakyThrows({InterruptedException.class})
    public void process(SomeEntityEvent event) {
        String threadName = Thread.currentThread().getName();
        long sleepTime = RandomUtils.nextLong(minSleep, maxSleep);
        log.debug("Imitating synchronous work for thread: '{}'. Sleeping for {} ms while ```processing``` event: {}", threadName, sleepTime, event);
        Thread.sleep(sleepTime);
        processed.incrementAndGet();
        log.debug("Thread '{}' waked up after sleeping for {} ms", threadName, sleepTime);
    }
}

