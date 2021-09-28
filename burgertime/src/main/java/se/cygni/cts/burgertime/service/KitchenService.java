package se.cygni.cts.burgertime.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.cygni.cts.burgertime.model.Status;

import java.time.Duration;

import static se.cygni.cts.burgertime.model.Status.PREPARING;

@Service
public class KitchenService {

    /**
     * Simulates preparing a meal, sending Status updates along the way according to:
     * <p>
     * Immediately - PREPARING
     * After 2 sec - READY
     */
    public Flux<Status> prepareMeal() {
        return Flux.merge(Mono.just(PREPARING), Mono.just(Status.READY).delayElement(Duration.ofSeconds(2)));
    }

}
