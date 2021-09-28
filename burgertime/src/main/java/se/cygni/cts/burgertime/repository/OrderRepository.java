package se.cygni.cts.burgertime.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import se.cygni.cts.burgertime.model.Order;
import se.cygni.cts.burgertime.model.Status;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Flux<Order> findByStatus(Status status);
}