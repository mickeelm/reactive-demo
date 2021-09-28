package se.cygni.cts.burgertime.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Duration;

import static se.cygni.cts.burgertime.model.Status.PREPARING;
import static se.cygni.cts.burgertime.model.Status.READY;

@SpringBootTest
class KitchenServiceTest {

    @Autowired
    KitchenService kitchenService;

    @Test
    void preparingThenReadyAfterTwoSec() {
        StepVerifier.withVirtualTime(() -> kitchenService.prepareMeal())
                .expectSubscription()
                .expectNext(PREPARING)
                .expectNoEvent(Duration.ofSeconds(2))
                .expectNext(READY)
                .verifyComplete();
    }

}