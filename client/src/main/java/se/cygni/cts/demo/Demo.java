package se.cygni.cts.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class Demo {

    protected final CountDownLatch countDownLatch = new CountDownLatch(latchSize());
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StopWatch stopWatch = new StopWatch();

    protected abstract int latchSize();

    protected abstract void execute();

    public void demo() throws InterruptedException {
        stopWatch.start();
        execute();
        if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("I waited for 10 seconds, this can't be right!");
        }
        stopWatch.stop();
        logger.info("Exec time: {} s.", stopWatch.getTotalTimeSeconds());
    }
}
