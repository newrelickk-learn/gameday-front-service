package technology.nrkk.demo.front.webclient;

import com.newrelic.api.agent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import technology.nrkk.demo.front.configs.properties.CatalogueProperties;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;

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
        String sizeOption = "&size=100";
        if (size.length > 0 && size[0] != null) {
            sizeOption = "&size=%d".formatted(size[0]);
        }
        String url = ("%s/catalogue?tags=%s&user=uid_%s"+sizeOption).formatted(this.properties.getUrl(), tags, userId);
        logger.info("Try access to {}", url);
        try {
            ResponseEntity<Product[]> response = this.restTemplate.getForEntity(url, Product[].class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw handleServerError(url, e);
        } catch (ResourceAccessException e) {
            throw handleConnectionError(url, e);
        } catch (RestClientException e) {
            throw handleParseError(url, e);
        } finally {
            segment.end();
        }
    }

    @Trace
    public Product get(String id, User user) throws CatalogueClientException {
        String userId = (user != null) ? user.getId().toString() : "";
        String url = "%s/catalogue/%s?user=uid_%s".formatted(this.properties.getUrl(), id, userId);
        logger.info("Try access to {}", url);
        try {
            ResponseEntity<Product> response = this.restTemplate.getForEntity(url, Product.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw handleServerError(url, e);
        } catch (ResourceAccessException e) {
            throw handleConnectionError(url, e);
        } catch (RestClientException e) {
            throw handleParseError(url, e);
        }
    }

    @Trace
    public Tags getTags() throws CatalogueClientException {
        String url = "%s/tags".formatted(this.properties.getUrl());
        logger.info("Try access to {}", url);
        try {
            ResponseEntity<Tags> response = this.restTemplate.getForEntity(url, Tags.class);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            throw handleServerError(url, e);
        } catch (ResourceAccessException e) {
            throw handleConnectionError(url, e);
        } catch (RestClientException e) {
            throw handleParseError(url, e);
        }
    }

    private CatalogueServerErrorException handleServerError(String url, HttpStatusCodeException e) {
        String message = "Catalogue returned %s for '%s': %s".formatted(e.getStatusCode(), url, e.getResponseBodyAsString());
        logger.error(message, e);
        return new CatalogueServerErrorException(message, e);
    }

    private CatalogueConnectionException handleConnectionError(String url, ResourceAccessException e) {
        String message = "Could not connect to catalogue at '%s'".formatted(url);
        logger.error(message, e);
        return new CatalogueConnectionException(message, e);
    }

    private CatalogueResponseParseException handleParseError(String url, RestClientException e) {
        String message = "Failed to parse catalogue response from '%s'".formatted(url);
        logger.error(message, e);
        return new CatalogueResponseParseException(message, e);
    }

    public static class CatalogueClientException extends Exception {
        public CatalogueClientException(String message, Exception e) {
            super(message, e);
        }
    }

    public static class CatalogueServerErrorException extends CatalogueClientException {
        public CatalogueServerErrorException(String message, Exception e) {
            super(message, e);
        }
    }

    public static class CatalogueConnectionException extends CatalogueClientException {
        public CatalogueConnectionException(String message, Exception e) {
            super(message, e);
        }
    }

    public static class CatalogueResponseParseException extends CatalogueClientException {
        public CatalogueResponseParseException(String message, Exception e) {
            super(message, e);
        }
    }
}
