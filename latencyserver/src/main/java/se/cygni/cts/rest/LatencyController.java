package se.cygni.cts.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LatencyController {

    final Logger logger = LoggerFactory.getLogger(LatencyController.class);

    @GetMapping("/")
    public String delayedHello() throws Exception {
        logger.info("Call received. Sleeping 2s.");
        Thread.sleep(2000);
        logger.info("Done sleeping.");
        return "Hi from " + Thread.currentThread().getName();
    }

}
