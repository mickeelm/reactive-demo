package se.cygni.cts.demo;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

@Component
@Profile("reactive")
public class Reactive extends LatencyDemo {

    @Override
    protected int latchSize() {
        return 200;
    }

    @Override
    protected void execute() {
        WebClient client = forcedSingleThreadWebClient();

        for (int i = 0; i < 200; i++) {
            client.get()
                    .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                    .doOnNext(str -> countDownLatch.countDown())
                    .subscribe(logger::info);
        }
    }

    /**
     * Don't do this at home, kids!
     * <p>
     * This one forces WebClient/Netty to only run on a single thread. In reality, there's really no need to put on
     * restraints like this. It's just for demo purposes in order to show that we can handle all requests efficiently
     * even with only one thread.
     * But yeah, don't do this, let WebClient/Netty optimize as it sees fit.
     */
    private WebClient forcedSingleThreadWebClient() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        factory.afterPropertiesSet();
        factory.setLoopResources(useNative -> nioEventLoopGroup);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(factory, Function.identity());
        return WebClient.builder()
                .baseUrl("http://localhost:9988")
                .clientConnector(connector)
                .build();
    }
}
