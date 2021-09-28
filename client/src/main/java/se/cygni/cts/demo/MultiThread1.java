package se.cygni.cts.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
@Profile("multi-thread-1")
public class MultiThread1 extends LatencyDemo {

    @Override
    protected int latchSize() {
        return 20;
    }

    @Override
    protected void execute() {
        var executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            executorService.execute(() -> {
                latencyCall();
                countDownLatch.countDown();
            });
        }
    }
}
