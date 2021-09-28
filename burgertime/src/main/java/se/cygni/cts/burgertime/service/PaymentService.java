package se.cygni.cts.burgertime.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import se.cygni.cts.burgertime.scenario.PaymentScenarioProvider;

import java.time.Duration;

@Service
public class PaymentService {

    final PaymentScenarioProvider scenarioProvider;

    public PaymentService(PaymentScenarioProvider scenarioProvider) {
        this.scenarioProvider = scenarioProvider;
    }

    /**
     * Simulates a paying experience by acting on a scenario given by the PaymentScenarioProvider. Note that there
     * is no amount involved.
     * <p>
     * FUNDED - will return true, with a 250ms delay
     * INSUFFICIENT_FUNDS - will return false, with a 250ms delay
     * TIMEOUT - will return true (which should be irrelevant when used in a timeout scenario), with a 2s delay.
     */
    Mono<Boolean> sufficientFunds() {
        return switch (scenarioProvider.nextScenario()) {
            case FUNDED -> Mono.just(true).delayElement(Duration.ofMillis(250));
            case INSUFFICIENT_FUNDS -> Mono.just(false).delayElement(Duration.ofMillis(250));
            case TIMEOUT -> Mono.just(true).delayElement(Duration.ofSeconds(2));
        };
    }
}
