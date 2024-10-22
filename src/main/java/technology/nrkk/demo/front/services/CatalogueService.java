package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.List;

@Service
public class CatalogueService {

    @Autowired
    CatalogueClient client;

    public Mono<Product> get(String id, User user) {
        Mono<Product> item = client.get(id, user);
        return item;
    }

    public Flux<Product> get(List<String> ids, User user) {
        return Flux.fromIterable(ids).flatMap(id -> this.get(id, user));
    }
}
