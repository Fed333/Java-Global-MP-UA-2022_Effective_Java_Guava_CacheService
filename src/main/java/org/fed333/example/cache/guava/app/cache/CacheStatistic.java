package org.fed333.example.cache.guava.app.cache;

public interface CacheStatistic {

    Long incrementEvictions();

    Double calcAveragePuttingTime(long newTime);

    Double getAveragePuttingTime();

    Long getEvictions();

}
