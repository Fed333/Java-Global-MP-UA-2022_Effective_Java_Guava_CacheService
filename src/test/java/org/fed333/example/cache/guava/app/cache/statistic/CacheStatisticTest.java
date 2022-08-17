package org.fed333.example.cache.guava.app.cache.statistic;


import org.fed333.example.cache.guava.app.cache.impl.CacheStatisticImpl;
import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CacheStatisticTest {

    private final CacheStatisticImpl statistic = new CacheStatisticImpl();

    @Test
    void incrementEvictions() {
        assertEquals(Long.valueOf(0), statistic.incrementEvictions());
        assertEquals(Long.valueOf(1), statistic.incrementEvictions());
        assertEquals(Long.valueOf(2), statistic.incrementEvictions());
        assertEquals(Long.valueOf(3), statistic.incrementEvictions());
        assertEquals(Long.valueOf(4), statistic.incrementEvictions());
    }

    @Test
    void calcAveragePuttingTime() {
        double errorDelta = 0.0001;
        assertTrue(abs(5d - statistic.calcAveragePuttingTime(5L)) < errorDelta);
        assertTrue(abs(4.5d - statistic.calcAveragePuttingTime(4L)) < errorDelta);
        assertTrue(abs(4.6666 - statistic.calcAveragePuttingTime(5L)) < errorDelta);
        assertTrue(abs(5d - statistic.calcAveragePuttingTime(6L)) < errorDelta);
    }
}