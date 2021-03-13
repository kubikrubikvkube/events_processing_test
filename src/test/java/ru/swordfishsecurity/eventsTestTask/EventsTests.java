package ru.swordfishsecurity.eventsTestTask;

import com.google.common.cache.CacheStats;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.swordfishsecurity.eventsTestTask.util.SleepUtils.sleep;

@SpringBootTest
public class EventsTests {
    private final Logger log = LoggerFactory.getLogger(EventsTests.class);

    private final Random random = new Random();

    @Autowired
    private SomeEntityEventPublisher publisher;

    @Autowired
    private SomeEntityEventsCache cache;

    @Autowired
    private SomeEntityEventsStats eventsStats;

    private static int EVENTS_TO_BE_PUBLISHED;
    private static int EVENTS_LOW_BOUND_CORRECTIVE_MS;
    private static int EVENTS_UPPER_BOUND_CORRECTIVE_MS;
    private static int EVENT_ID_LOW_BOUND;
    private static int EVENT_ID_UPPER_BOUND;
    private static int SECONDS_TO_WAIT_FOR_EVENT_PROCESSING_COMPLETION;

    @BeforeAll
    static void beforeAll() {
        EVENTS_TO_BE_PUBLISHED = 500;
        EVENTS_LOW_BOUND_CORRECTIVE_MS = 5;
        EVENTS_UPPER_BOUND_CORRECTIVE_MS = 10;
        EVENT_ID_LOW_BOUND = 1;
        EVENT_ID_UPPER_BOUND = 100;
        SECONDS_TO_WAIT_FOR_EVENT_PROCESSING_COMPLETION = 30;
    }

    @Test
    void eventsWork() throws InterruptedException {
        log.info("We're gonna publish {} random events with type 'SomeEntityEvent'", EVENTS_TO_BE_PUBLISHED);
        log.info("For close to real life events flow scenario we need to wait between publishing events");
        log.info("We will wait between {} ms and {} ms between publishing events", EVENTS_LOW_BOUND_CORRECTIVE_MS, EVENTS_UPPER_BOUND_CORRECTIVE_MS);
        log.info("Every event have an 'ID' attribute. The higher the random id upper bound - the more unique events we will publish");
        log.info("It will lead to more post processed tasks and less cache hits");
        log.info("Current low bound is: {},  upper bound is: {}", EVENT_ID_LOW_BOUND, EVENT_ID_UPPER_BOUND);
        log.info("'EntityAction' is always random");
        log.info("You may set logging level to DEBUG if you want more info about events processing in this test");

        for (int i = 0; i < EVENTS_TO_BE_PUBLISHED; i++) {
            sleep(EVENTS_LOW_BOUND_CORRECTIVE_MS, EVENTS_UPPER_BOUND_CORRECTIVE_MS);
            publisher.publishEvent(randomEvent());
        }

        log.info("All events published. We're gonna wait for {} seconds to ensure that all threads are finished.", SECONDS_TO_WAIT_FOR_EVENT_PROCESSING_COMPLETION);
        sleep(Duration.ofSeconds(SECONDS_TO_WAIT_FOR_EVENT_PROCESSING_COMPLETION));


        CacheStats cacheStats = cache.stats();
        long hitCount = cacheStats.hitCount();
        long missCount = cacheStats.missCount();
        long evictionCount = cacheStats.evictionCount();

        int rejectedAsNoiseCount = eventsStats.getRejectedAsNoiseCount().get();
        int routedToPostProcessor = eventsStats.getRoutedToPostProcessorCount().get();
        int total = eventsStats.getTotalProcessedCount().get();

        assertEquals(rejectedAsNoiseCount,hitCount,"'rejectedAsNoiseCount' and 'hitCount' should be equal");
        log.info("Rejected {} duplicate events postprocessing because 1 second of 'noise reduction' hasn't been passed", rejectedAsNoiseCount);

        assertEquals(missCount, routedToPostProcessor, "'missCount' and 'routedToPostProcessor' should be equal");
        log.info("Not found in cache and routed to post processor {} events", missCount);

        assertEquals(total, EVENTS_TO_BE_PUBLISHED, "'total' and 'EVENTS_TO_BE_PUBLISHED' should be equal");
        log.info("All {} published events should be searched in a cache",total);

        assertTrue(() -> evictionCount > 0 && evictionCount <= total,"Eviction count should be correct");
        log.info("Evicted {} entries from cache due 1s timeout",evictionCount);

        log.info("Try changing variables in 'setUp' method next time, if you want different results");
    }

    private SomeEntityEvent randomEvent() {
        var randomId = RandomUtils.nextLong(EVENT_ID_LOW_BOUND, EVENT_ID_UPPER_BOUND);
        var randomActionIndex = random.nextInt(EntityAction.values().length);
        var randomAction = EntityAction.values()[randomActionIndex];
        return new SomeEntityEvent(this, randomId, randomAction);
    }
}
