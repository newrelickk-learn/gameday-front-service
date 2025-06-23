package technology.nrkk.demo.front.webclient;

import com.newrelic.api.agent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import technology.nrkk.demo.front.configs.properties.CatalogueProperties;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;
import java.util.Map;

@Component
public class CatalogueClient {

    private final RestTemplate restTemplate;
    private final CatalogueProperties properties;

    protected final static Logger logger = LoggerFactory.getLogger(CatalogueClient.class);

    @Autowired
    public CatalogueClient(RestTemplateBuilder builder, CatalogueProperties properties) {
        this.properties = properties;
        this.restTemplate = builder
                .additionalInterceptors((request, body, execution) -> {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return execution.execute(request, body);
                })
                .build();
    }

    public Product[] search(String tags, User user, Integer... size) throws CatalogueClientException {
        Segment segment = NewRelic.getAgent().getTransaction().startSegment("CatalogueClient.search");
        String userId = (user != null) ? user.getId().toString() : "";
        try {
            String sizeOption = "&size=100";
            if (size.length > 0 && size[0] != null) {
                sizeOption = "&size=%d".formatted(size[0]);
            }
            ResponseEntity<Product[]> response = this.restTemplate.getForEntity(("%s/catalogue?tags=%s&user=uid_%s"+sizeOption).formatted(this.properties.getUrl(), tags, userId), Product[].class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new CatalogueClientException("'/catalogue?tags=" + tags + "' does not work correctly", e);
        } finally {
            segment.end();
        }
    }

    @Trace
    public Product get(String id, User user) throws CatalogueClientException {
        String userId = (user != null) ? user.getId().toString() : "";
        logger.info("Try access to /catalogue/?user=uid_%s" + id);
        try {
            ResponseEntity<Product> response = this.restTemplate.getForEntity("%s/catalogue/%s?user=uid_%s".formatted(this.properties.getUrl(), id, userId), Product.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new CatalogueClientException("'/catalogue/" + id + "' does not work correctly", e);
        }
    }

    @Trace
    public Tags getTags() throws CatalogueClientException {
        logger.info("Try access to /tags/");
        try {
            ResponseEntity<Tags> response = this.restTemplate.getForEntity("%s/tags".formatted(this.properties.getUrl()), Tags.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new CatalogueClientException("'/tags' does not work correctly", e);
        }
    }

    public class CatalogueClientException extends Exception {
        public CatalogueClientException(String message, Exception e) {
            super(message, e);
        }


    }
}
