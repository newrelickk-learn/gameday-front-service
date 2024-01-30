package technology.nrkk.demo.front.webclient;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;

@Component
public class CatalogueClient {

    private final WebClient client;

    public CatalogueClient(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:3000").build();
    }

    public Mono<Product[]> search(String tags) {
        return this.client.get().uri("/catalogue?tags="+tags).accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Product[].class);
    }

    public Mono<Product> get(String id) {
        return this.client.get().uri("/catalogue/" + id).accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Product.class);
    }

    public Mono<Tags> getTags() {
        return this.client.get().uri("/tags").accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Tags.class);
    }
}
