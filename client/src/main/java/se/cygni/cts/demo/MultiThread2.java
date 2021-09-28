package se.cygni.cts.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
@Profile("multi-thread-2")
public class MultiThread2 extends LatencyDemo {

    @Override
    protected int latchSize() {
        return 20;
    }

    @Override
    protected void execute() {
        var executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            executorService.execute(() -> {
                latencyCall();
                countDownLatch.countDown();
            });
        }

    }
}
