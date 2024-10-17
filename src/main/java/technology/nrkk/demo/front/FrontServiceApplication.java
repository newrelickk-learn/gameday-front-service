package technology.nrkk.demo.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FrontServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontServiceApplication.class, args);
    }

}
