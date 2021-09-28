package se.cygni.cts.demo;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

public abstract class LatencyDemo extends Demo {

    final PoolingHttpClientConnectionManager mgr;
    final CloseableHttpClient httpClient;

    {
        mgr = new PoolingHttpClientConnectionManager();
        mgr.setDefaultMaxPerRoute(50);
        httpClient = HttpClients.custom().setConnectionManager(mgr).build();
    }

    protected void latencyCall() {
        try {
            var httpResponse = httpClient.execute(new HttpGet("http://localhost:9988"));
            var response = new String(httpResponse.getEntity().getContent().readAllBytes());
            logger.info("[{}]", response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
