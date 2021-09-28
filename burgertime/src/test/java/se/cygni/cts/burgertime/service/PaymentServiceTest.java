package se.cygni.cts.burgertime.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.test.StepVerifier;
import se.cygni.cts.burgertime.scenario.PaymentScenarioProvider;

import java.time.Duration;

import static org.mockito.Mockito.when;
import static se.cygni.cts.burgertime.scenario.PaymentScenario.*;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @MockBean
    PaymentScenarioProvider paymentScenarioProvider;

    @Test
    void fundedReturnsTrueAfter250ms() {
        when(paymentScenarioProvider.nextScenario()).thenReturn(FUNDED);
        StepVerifier.withVirtualTime(() -> paymentService.sufficientFunds())
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(250))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void insufficientFundsReturnsFalseAfter250ms() {
        when(paymentScenarioProvider.nextScenario()).thenReturn(INSUFFICIENT_FUNDS);
        StepVerifier.withVirtualTime(() -> paymentService.sufficientFunds())
                .expectSubscription()
                .expectNoEvent(Duration.ofMillis(250))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void timeoutReturnsTrueAfter2s() {
        when(paymentScenarioProvider.nextScenario()).thenReturn(TIMEOUT);
        StepVerifier.withVirtualTime(() -> paymentService.sufficientFunds())
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(2))
                .expectNext(true)
                .verifyComplete();
    }
}