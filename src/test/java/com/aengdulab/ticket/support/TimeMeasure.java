package com.aengdulab.ticket.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeMeasure {

    private static final Logger log = LoggerFactory.getLogger(TimeMeasure.class);

    public static void measureTime(Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();
        long endTime = System.currentTimeMillis();
        log.info("수행 시간 : {}ms", (endTime - startTime));
    }
}
