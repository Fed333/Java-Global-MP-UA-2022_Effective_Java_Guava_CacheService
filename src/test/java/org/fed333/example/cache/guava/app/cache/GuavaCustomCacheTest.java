package org.fed333.example.cache.guava.app.cache;

import org.fed333.example.cache.guava.app.entry.CacheValue;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GuavaCustomCacheTest {

    @Autowired
    private CustomCache<String, CacheValue> cache;

    private static final Logger mockedLogger = Mockito.mock(Logger.class);


   static {
        MockedStatic<LoggerFactory> loggerFactoryMockedStatic = Mockito.mockStatic(LoggerFactory.class);
        loggerFactoryMockedStatic.when(()->LoggerFactory.getLogger(any(String.class))).thenReturn(mockedLogger);
        loggerFactoryMockedStatic.when(()->LoggerFactory.getLogger(any(Class.class))).thenReturn(mockedLogger);
    }


    @Test
    public void cache_MaxSizeIs100_000() {
        fillCacheWithTestData(200_000, "String ");

        Assertions.assertEquals(100_000, cache.getSize());
    }

    @Test
    public void cache_isLeastRecentlyUsedRemains() throws InterruptedException {
        fillCacheWithTestData(10, "Number ");

        for (int i = 0; i < 10; i++) {
            cache.get("Number 5");
            cache.get("Number 6");
            cache.get("Number 7");
            cache.get("Number 8");
            cache.get("Number 9");
        }

        cache.get("Number 0");
        Thread.sleep(1000);
        cache.get("Number 1");
        Thread.sleep(1000);
        cache.get("Number 2");
        Thread.sleep(1000);
        cache.get("Number 3");
        Thread.sleep(1000);
        cache.get("Number 4");
        cache.get("Number 0");
        Thread.sleep(1000);

        Assertions.assertNotNull(cache.get("Number 0"));
        Assertions.assertNotNull(cache.get("Number 1"));
        Assertions.assertNotNull(cache.get("Number 2"));
        Assertions.assertNotNull(cache.get("Number 3"));
        Assertions.assertNotNull(cache.get("Number 4"));

        Assertions.assertNull(cache.get("Number 5"));
        Assertions.assertNull(cache.get("Number 6"));
        Assertions.assertNull(cache.get("Number 7"));
        Assertions.assertNull(cache.get("Number 8"));
        Assertions.assertNull(cache.get("Number 9"));

    }

    @Test
    public void cache_RemoveEntryLog() throws InterruptedException {

        String expectedKey = "Entry to remove";
        cache.put(expectedKey);
        CacheValue expectedValue = cache.get(expectedKey);
        Thread.sleep(5000);
        cache.get(expectedKey);
        cache.put("");

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CacheValue> valueCaptor = ArgumentCaptor.forClass(CacheValue.class);

        verify(mockedLogger).info(messageCaptor.capture(), keyCaptor.capture(), valueCaptor.capture(), any());

        Assertions.assertEquals("Element: [key={}, value={}] has been evicted, cause: {}", messageCaptor.getValue());
        Assertions.assertEquals(expectedKey, keyCaptor.getValue());
        Assertions.assertEquals(expectedValue, valueCaptor.getValue());
    }

    @Test
    public void cache_statisticAverageTime() {

        Long begin = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            cache.put(String.valueOf(i));
        }
        Long end = System.nanoTime();
        Double expectedAverage = (end-begin)/1000.0;

        Double actualAverageTime = cache.getStatistic().getAveragePuttingTime();

        Assertions.assertTrue(Math.abs(actualAverageTime-expectedAverage) < 10000);

    }


    @Test
    public void cache_evictionsNumber() throws InterruptedException {
       cache.put("Eviction 1");
       cache.put("Eviction 2");
       cache.put("Eviction 3");
       Thread.sleep(5000);
       cache.get("Eviction 1");
       cache.get("Eviction 2");
       cache.get("Eviction 3");
       cache.put("");

       Assertions.assertEquals(3, cache.getStatistic().getEvictions());
    }

    private void fillCacheWithTestData(int size, String prefix) {
        for (int i = 0; i < size; i++) {
            cache.put(prefix + i);
        }
    }

}