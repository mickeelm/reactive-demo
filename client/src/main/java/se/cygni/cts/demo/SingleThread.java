package se.cygni.cts.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("single-thread")
public class SingleThread extends LatencyDemo {

    @Override
    protected int latchSize() {
        return 0;
    }

    @Override
    protected void execute() {
        for (int i = 0; i < 3; i++) {
            latencyCall();
        }
    }
}
