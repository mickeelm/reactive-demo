package se.cygni.cts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import se.cygni.cts.demo.Demo;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws InterruptedException {
        var applicationContext = new SpringApplicationBuilder(ClientApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        var demoBean = applicationContext.getBean(Demo.class);
        demoBean.demo();
        System.exit(SpringApplication.exit(applicationContext));
    }

}
