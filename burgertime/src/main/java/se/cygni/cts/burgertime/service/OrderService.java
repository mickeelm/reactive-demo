package se.cygni.cts.burgertime.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.cygni.cts.burgertime.model.Order;
import se.cygni.cts.burgertime.repository.OrderRepository;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static se.cygni.cts.burgertime.model.Status.NEW;
import static se.cygni.cts.burgertime.model.Status.PICKED_UP;

@Service
public class OrderService {

    final OrderRepository orderRepository;
    final PaymentService paymentService;
    final KitchenService kitchenService;

    public OrderService(OrderRepository orderRepository, PaymentService paymentService, KitchenService kitchenService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.kitchenService = kitchenService;
    }

    public Mono<String> place() {
        return paymentService.sufficientFunds()
                .timeout(Duration.ofSeconds(1))
                .flatMap(funded ->
                        funded ? orderRepository.save(Order.newOrder()).map(this::msgFromStatusUpdate)
                                : Mono.error(InsufficientFundsException::new)
                )
                .onErrorMap(TimeoutException.class, t -> new PaymentTimeoutException());
    }

    public Flux<String> prepare(Long orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(OrderNotFoundException::new))
                .filter(order -> order.getStatus() == NEW)
                .switchIfEmpty(Mono.error(AlreadyPreparedException::new))
                .flatMapMany(order -> kitchenService.prepareMeal()
                        .map(order::updateStatus)
                        .flatMap(orderRepository::save)
                )
                .map(this::msgFromStatusUpdate);
    }

    public Mono<String> pickUp(Long orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(OrderNotFoundException::new))
                .flatMap(order -> switch (order.getStatus()) {
                    case NEW, PREPARING -> Mono.error(OrderNotReadyException::new);
                    case PICKED_UP -> Mono.error(AlreadyPickedUpException::new);
                    case READY -> Mono.just(order);
                })
                .map(Order::pickedUp)
                .flatMap(orderRepository::save)
                .map(this::msgFromStatusUpdate);
    }

    public Flux<String> prune() {
        return orderRepository.findByStatus(PICKED_UP)
                .flatMap(order -> orderRepository.delete(order).thenReturn(order.getId()))
                .map(this::pruneMessage)
                .defaultIfEmpty("No orders to prune.");
    }

    private String pruneMessage(Long orderId) {
        return String.format("Order no %d pruned", orderId);
    }

    private String msgFromStatusUpdate(Order order) {
        return switch (order.getStatus()) {
            case NEW -> String.format("Your order no: %d", order.getId());
            case PREPARING -> String.format("Order no %d is being prepared...", order.getId());
            case READY -> String.format("Order no %d is ready.", order.getId());
            case PICKED_UP -> String.format("Enjoy order no %d", order.getId());
        };
    }
}
