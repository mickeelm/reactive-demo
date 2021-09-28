package se.cygni.cts.burgertime;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import se.cygni.cts.burgertime.repository.OrderRepository;

@SpringBootApplication
@EnableR2dbcRepositories(basePackageClasses = OrderRepository.class)
public class BurgerTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BurgerTimeApplication.class, args);
    }

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        return initializer;
    }
}
