package se.cygni.cts.burgertime.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import se.cygni.cts.burgertime.model.Order;
import se.cygni.cts.burgertime.repository.OrderRepository;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static se.cygni.cts.burgertime.model.Status.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @MockBean
    KitchenService kitchenService;

    @MockBean
    PaymentService paymentService;

    @MockBean
    OrderRepository orderRepository;

    Order order;
    Long orderId;

    @BeforeEach
    void setup() throws Exception {
        order = newOrderWithId();
        orderId = order.getId();
    }

    @Test
    void fundedOrderSavesInDbAndReturnsOrderNo() {
        when(orderRepository.save(Order.newOrder())).thenReturn(Mono.just(order));
        when(paymentService.sufficientFunds()).thenReturn(Mono.just(true));

        StepVerifier.create(orderService.place())
                .expectSubscription()
                .then(() -> verify(paymentService).sufficientFunds())
                .then(() -> verify(orderRepository).save(Order.newOrder()))
                .expectNext("Your order no: " + order.getId())
                .verifyComplete();
    }

    @Test
    void nonFundedOrderReturnsErrorWithInsufficientFundsException() {
        when(paymentService.sufficientFunds()).thenReturn(Mono.just(false));

        StepVerifier.create(orderService.place())
                .expectSubscription()
                .then(() -> verify(paymentService).sufficientFunds())
                .then(() -> verifyNoInteractions(orderRepository))
                .expectError(InsufficientFundsException.class)
                .verify();
    }

    /**
     * This test uses an explicit VirtualTimeScheduler, as we are dealing with several time-dependent publishers
     * (orderService.place() needs to decide when to signal an error due to a timeout against
     * paymentService.sufficientFunds()). We must make sure that both use virtual time, and that we are in control.
     */
    @Test
    void delayedReplyAtLeast1SecFromPaymentServiceReturnsErrorWithPaymentTimeoutException() {
        VirtualTimeScheduler vts = VirtualTimeScheduler.create();
        when(paymentService.sufficientFunds()).thenReturn(Mono.just(true).delayElement(Duration.ofSeconds(1), vts));

        StepVerifier.withVirtualTime(() -> orderService.place(), () -> vts, 1)
                .expectSubscription()
                .then(() -> verify(paymentService).sufficientFunds())
                .expectNoEvent(Duration.ofSeconds(1))
                .then(() -> verifyNoInteractions(orderRepository))
                .expectError(PaymentTimeoutException.class)
                .verify();
    }

    @Test
    void prepareShouldReturnErrorSignalWithOrderNotFoundExceptionWhenNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());
        StepVerifier.create(orderService.prepare(orderId))
                .expectSubscription()
                .then(() -> verify(orderRepository).findById(orderId))
                .expectError(OrderNotFoundException.class)
                .verify();
    }

    @Test
    void prepareShouldReturnErrorSignalWithAlreadyPreparedExceptionIfNotNew() {
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order.ready()));
        StepVerifier.create(orderService.prepare(orderId))
                .expectSubscription()
                .then(() -> verify(orderRepository).findById(orderId))
                .expectError(AlreadyPreparedException.class)
                .verify();
    }

    @Test
    void prepareShouldUpdateAccordingToUpdatesFromKitchenServiceAndMessage() {
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
        when(orderRepository.save(order.preparing())).thenReturn(Mono.just(order.preparing()));
        when(orderRepository.save(order.ready())).thenReturn(Mono.just(order.ready()));
        when(kitchenService.prepareMeal()).thenReturn(Flux.just(PREPARING, READY));

        StepVerifier.create(orderService.prepare(orderId))
                .expectSubscription()
                .then(() -> verify(orderRepository).findById(orderId))
                .then(() -> verify(kitchenService).prepareMeal())
                .then(() -> verify(orderRepository).save(order.preparing()))
                .then(() -> verify(orderRepository).save(order.ready()))
                .expectNext("Order no " + orderId + " is being prepared...")
                .expectNext("Order no " + orderId + " is ready.")
                .verifyComplete();

    }

    @Test
    void pickUpShouldUpdateStatusAndMessage() {
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order.ready()));
        when(orderRepository.save(order.pickedUp())).thenReturn(Mono.just(order.pickedUp()));
        StepVerifier.create(orderService.pickUp(orderId))
                .expectSubscription()
                .then(() -> verify(orderRepository).save(order.pickedUp()))
                .expectNext("Enjoy order no " + orderId)
                .verifyComplete();
    }

    @Test
    void pickUpShouldReturnErrorWithOrderNotFoundExceptionWhenNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Mono.empty());
        StepVerifier.create(orderService.pickUp(orderId))
                .expectSubscription()
                .then(() -> verify(orderRepository).findById(orderId))
                .expectError(OrderNotFoundException.class)
                .verify();
    }

    @Test
    void pruneDeletesAllInStatusPickedUpAndMessagesOrderNo() throws Exception {
        Order firstOrder = newOrderWithId().pickedUp();
        Order secondOrder = newOrderWithId().pickedUp();
        when(orderRepository.findByStatus(PICKED_UP)).thenReturn(Flux.just(firstOrder, secondOrder));
        when(orderRepository.delete(firstOrder)).thenReturn(Mono.empty());
        when(orderRepository.delete(secondOrder)).thenReturn(Mono.empty());

        StepVerifier.create(orderService.prune())
                .expectSubscription()
                .then(() -> verify(orderRepository).findByStatus(PICKED_UP))
                .then(() -> verify(orderRepository).delete(firstOrder))
                .then(() -> verify(orderRepository).delete(secondOrder))
                .expectNext("Order no " + firstOrder.getId() + " pruned")
                .expectNext("Order no " + secondOrder.getId() + " pruned")
                .verifyComplete();
    }

    @Test
    void pruneInformsWhenThereAreNoOrdersToPrune() {
        when(orderRepository.findByStatus(PICKED_UP)).thenReturn(Flux.empty());

        StepVerifier.create(orderService.prune())
                .expectSubscription()
                .then(() -> verify(orderRepository).findByStatus(PICKED_UP))
                .expectNext("No orders to prune.")
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("illegalPickUpStates")
    void pickUpShouldReturnErrorWhenIllegalStatus(Order order, Class<? extends RuntimeException> exceptionClass) {
        when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
        StepVerifier.create(orderService.pickUp(orderId))
                .expectSubscription()
                .expectError(exceptionClass)
                .verify();
    }

    Stream<Arguments> illegalPickUpStates() {
        return Stream.of(
                Arguments.of(order, OrderNotReadyException.class),
                Arguments.of(order.preparing(), OrderNotReadyException.class),
                Arguments.of(order.pickedUp(), AlreadyPickedUpException.class)
        );
    }

    private Order newOrderWithId() throws NoSuchFieldException, IllegalAccessException {
        var order = Order.newOrder();
        Field id = Order.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(order, new Random().nextLong());
        return order;
    }
}