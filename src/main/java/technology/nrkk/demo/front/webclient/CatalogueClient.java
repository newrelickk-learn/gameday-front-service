package technology.nrkk.demo.front.webclient;

import com.newrelic.api.agent.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.configs.properties.CatalogueProperties;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.handlers.GlobalErrorWebExceptionHandler;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;

@Component
public class CatalogueClient {

    private final WebClient client;

    private final CatalogueProperties properties;

    protected final static Logger logger = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);

    @Autowired
    public CatalogueClient(WebClient.Builder builder, CatalogueProperties properties) {
        this.properties = properties;
        this.client = builder.baseUrl(properties.getUrl()).build();
    }

    @Trace
    public Mono<Product[]> search(String tags, User user) {
        logger.info("Try access to /catalogue?tags=%s&user=%s".formatted(tags, user.getId()));
        return this.client.get().uri("/catalogue?tags=%s&user=%s".formatted(tags, user.getId())).accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Product[].class)
            .onErrorResume(
                WebClientResponseException.class,
                ex -> {
                    return Mono.error(new CatalogueClientException("'/catalogue?tags=" + tags + "' does not work correctly"));
                });
    }

    @Trace
    public Mono<Product> get(String id) {
        logger.info("Try access to /catalogue/" + id);
        return this.client.get().uri("/catalogue/" + id).accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Product.class)
            .onErrorResume(
                WebClientResponseException.class,
                ex -> {
                    return Mono.error(new CatalogueClientException("'/catalogue/" + id + "' does not work correctly"));
                });
    }

    @Trace
    public Mono<Tags> getTags() {
        logger.info("Try access to /tags/");
        return this.client.get().uri("/tags").accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Tags.class)
            .onErrorResume(
                WebClientResponseException.class,
                ex -> {
                    return Mono.error(new CatalogueClientException("'/tags' does not work correctly"));
                });
    }

    public class CatalogueClientException extends Exception {
        public CatalogueClientException(String message) {
            super(message);
        }


    }
}
