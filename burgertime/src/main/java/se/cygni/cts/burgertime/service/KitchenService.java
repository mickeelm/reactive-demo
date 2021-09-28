package se.cygni.cts.burgertime.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import se.cygni.cts.burgertime.model.Status;

@Service
public class KitchenService {

    /**
     * Simulates preparing a meal, sending Status updates along the way according to:
     * <p>
     * Immediately - PREPARING
     * After 2 sec - READY
     */
    public Flux<Status> prepareMeal() {
        return Flux.empty();
    }

}
