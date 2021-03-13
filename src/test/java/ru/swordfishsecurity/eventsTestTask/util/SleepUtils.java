package ru.swordfishsecurity.eventsTestTask.util;

import org.apache.commons.lang3.RandomUtils;

import java.time.Duration;

/**
 * Convenient facade to 'Thread.sleep' method
 */
public class SleepUtils {

    public static void sleep(int fromMillis, int toMillis) throws InterruptedException {
        int msToSleep = RandomUtils.nextInt(fromMillis, toMillis);
        Thread.sleep(msToSleep);
    }

    public static void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    public static void sleep(Duration duration) throws InterruptedException {
        long millis = duration.toMillis();
        sleep(millis);
    }

    public static void sleep(Duration from, Duration to) throws InterruptedException {
        int fromMillis = Math.toIntExact(from.toMillis());
        int toMillis = Math.toIntExact(to.toMillis());
        sleep(fromMillis, toMillis);
    }
}
