package se.cygni.cts.burgertime.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

import static se.cygni.cts.burgertime.model.Status.*;

@Table("order_")
public class Order {

    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private final Status status;

    private Order() {
        this.status = Status.NEW;
    }

    @PersistenceConstructor
    private Order(Long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public static Order newOrder() {
        return new Order();
    }

    public Order preparing() {
        return new Order(this.id, PREPARING);
    }

    public Order ready() {
        return new Order(this.id, READY);
    }

    public Order pickedUp() {
        return new Order(this.id, PICKED_UP);
    }

    public Order updateStatus(Status status) {
        return new Order(this.id, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId()) && getStatus() == order.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}

