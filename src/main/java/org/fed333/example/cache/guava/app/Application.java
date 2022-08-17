package org.fed333.example.cache.guava.app;

import lombok.extern.slf4j.Slf4j;
import org.fed333.example.cache.guava.app.cache.CustomCache;
import org.fed333.example.cache.guava.app.entry.CacheValue;
import org.omg.CORBA.portable.CustomValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    }


    private static void consoleTesting(ConfigurableApplicationContext context) throws InterruptedException {
        CustomCache<String, CacheValue> cache = context.getBean(CustomCache.class);

//        cache.put("Cache entry1");

        Long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String key = new StringBuilder("Test string").append(i).toString();
            cache.put(key);
            cache.get(key);
        }

        Long end = System.nanoTime();
        long duration = (end-start)/1000;

        log.info("size: {}", cache.getSize());
        log.info("avg duration:\n \t{} nanos\n \t{} micros\n \t{} millis", duration, TimeUnit.NANOSECONDS.toMicros(duration), TimeUnit.NANOSECONDS.toMillis(duration));
        log.info("statistic avg duration: \n \t{} nanos", cache.getStatistic().getAveragePuttingTime());

        cache.put("Custom test string1");
        log.info("Test string {}", Optional.ofNullable(cache.get("Custom test string1")).map(CacheValue::toString).orElse("null"));
        Thread.sleep(5000);

        for (int i = 0; i < 1000; i++) {
            String key = new StringBuilder("Test string").append(i).toString();
            log.info("[Thread {}], {} is {}", Thread.currentThread(), key, Optional.ofNullable(cache.get(key)).map(o->"Presence").orElse("Missing"));
        }

        log.info("Test string {}", Optional.ofNullable(cache.get("Custom test string1")).map(CacheValue::toString).orElse("null"));

        log.info("Put \"Test eviction\", size: {}", cache.getSize());

        log.info("size: {}, number of evictions: {}", cache.getSize(), cache.getStatistic().getEvictions());
        log.info("end");
    }

}
