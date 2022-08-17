package org.fed333.example.cache.guava.app.cache.test.configuration;

import org.fed333.example.cache.guava.app.cache.CustomCache;
import org.fed333.example.cache.guava.app.cache.impl.CacheStatisticImpl;
import org.fed333.example.cache.guava.app.cache.impl.GuavaCustomCache;
import org.fed333.example.cache.guava.app.entry.CacheValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestConfig {


    @Bean
    @Scope("prototype")
    @Primary
    public CustomCache<String, CacheValue> testCache() {
        return new GuavaCustomCache(new CacheStatisticImpl());
    }


}
