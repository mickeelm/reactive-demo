package se.cygni.cts.burgertime.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.cygni.cts.burgertime.service.*;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestController
public class OrderController {

    final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    Mono<String> placeOrder() {
        return orderService.place();
    }

    @PostMapping(value = "/{id}/prepare", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> prepareOrder(@PathVariable Long id) {
        return orderService.prepare(id);
    }

    @PostMapping("/{id}/pick-up")
    Mono<String> pickUpOrder(@PathVariable Long id) {
        return orderService.pickUp(id);
    }

    @PostMapping(value = "/prune", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> pruneOrders() {
        return orderService.prune();
    }

    @ExceptionHandler(AlreadyPickedUpException.class)
    ResponseEntity<String> alreadyPickedUp() {
        return ResponseEntity.badRequest().body("Already picked up.");
    }

    @ExceptionHandler(AlreadyPreparedException.class)
    ResponseEntity<String> alreadyPrepared() {
        return ResponseEntity.badRequest().body("Already prepared.");
    }

    @ExceptionHandler(InsufficientFundsException.class)
    ResponseEntity<String> insufficientFunds() {
        return ResponseEntity.badRequest().body("Insufficient funds.");
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<Void> notFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(OrderNotReadyException.class)
    ResponseEntity<String> orderNotReady() {
        return ResponseEntity.badRequest().body("Order not ready.");
    }

    @ExceptionHandler(PaymentTimeoutException.class)
    ResponseEntity<Void> paymentTimeout() {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).build();
    }


}
