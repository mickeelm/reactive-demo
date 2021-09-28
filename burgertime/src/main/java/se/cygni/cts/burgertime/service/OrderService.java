package se.cygni.cts.burgertime.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.cygni.cts.burgertime.model.Order;
import se.cygni.cts.burgertime.repository.OrderRepository;

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
        return Mono.empty();
    }

    public Flux<String> prepare(Long orderId) {
        return Flux.empty();
    }

    public Mono<String> pickUp(Long orderId) {
        return Mono.empty();
    }

    public Flux<String> prune() {
        return Flux.empty();
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
