package ru.swordfishsecurity.eventsTestTask;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
@Component
public class SomeEntityEventsStats {
    private final AtomicInteger routedToPostProcessorCount = new AtomicInteger();
    private final AtomicInteger rejectedAsNoiseCount = new AtomicInteger();
    private final AtomicInteger totalProcessedCount = new AtomicInteger();

    public void incrementRoutedToPostProcessorCount() {
        totalProcessedCount.incrementAndGet();
        routedToPostProcessorCount.incrementAndGet();
    }

    public void incrementRejectedAsNoiseCount() {
        totalProcessedCount.incrementAndGet();
        rejectedAsNoiseCount.incrementAndGet();
    }

}
