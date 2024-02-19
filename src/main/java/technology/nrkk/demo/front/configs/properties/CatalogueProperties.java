package technology.nrkk.demo.front.configs.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("catalogue")
public class CatalogueProperties {
    private String url = "http://localhost:3000";
}
