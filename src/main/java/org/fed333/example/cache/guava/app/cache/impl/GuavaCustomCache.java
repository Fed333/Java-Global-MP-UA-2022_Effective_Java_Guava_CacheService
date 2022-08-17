package org.fed333.example.cache.guava.app.cache.impl;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fed333.example.cache.guava.app.cache.CustomCache;
import org.fed333.example.cache.guava.app.entry.CacheValue;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuavaCustomCache implements CustomCache<String, CacheValue> {

    private static final int MAXIMUM_SIZE = 100000;

    @Getter
    private LoadingCache<String, CacheValue> cache;

    private final CacheStatisticImpl statistic;

    @PostConstruct
    private void init() {
        CacheLoader<String, CacheValue> loader = new CacheLoader<String, CacheValue>() {
            @Override
            public CacheValue load(String s) throws Exception {
                return new CacheValue(s);
            }
        };

        RemovalListener<String, CacheValue> listener = removalNotification -> {
            if (removalNotification.wasEvicted()) {
                statistic.incrementEvictions();
                log.info("Element: [key={}, value={}] has been evicted, cause: {}", removalNotification.getKey(), removalNotification.getValue(), removalNotification.getCause());
            }
        };

        cache = CacheBuilder.newBuilder()
                .maximumSize(MAXIMUM_SIZE)
                .expireAfterAccess(5000, TimeUnit.MILLISECONDS)
                .removalListener(listener)
                .build(loader);

    }

    @Override
    public CacheValue get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(String key) {

        Instant start = Instant.ofEpochSecond(0, System.nanoTime());
        cache.getUnchecked(key);
        Instant end = Instant.ofEpochSecond(0, System.nanoTime());

        statistic.calcAveragePuttingTime(Duration.between(start, end).getNano());
    }

    @Override
    public Long getSize() {
        return cache.size();
    }

    @Override
    public CacheStatisticImpl getStatistic() {
        return statistic;
    }

}