package se.cygni.cts.burgertime.scenario;

import org.springframework.stereotype.Component;

import java.util.Random;

import static se.cygni.cts.burgertime.scenario.PaymentScenario.*;

@Component
public class PaymentScenarioProvider {

    private final Random random = new Random();

    /**
     * Randomly generates payment scenarios *roughly* according to this distribution:
     * <p>
     * FUNDED - 80%
     * INSUFFICIENT_FUNDS - 10%
     * TIMEOUT - 10%
     */
    public PaymentScenario nextScenario() {
        return switch (random.nextInt(10)) {
            case 0 -> INSUFFICIENT_FUNDS;
            case 1 -> TIMEOUT;
            default -> FUNDED;
        };
    }
}
