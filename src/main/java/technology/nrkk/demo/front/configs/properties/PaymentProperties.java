package technology.nrkk.demo.front.configs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("payment")
public class PaymentProperties {
    private String url = "http://payment";
} 