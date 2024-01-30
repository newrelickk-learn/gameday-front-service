package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.List;

@Service
public class CatalogueService {

    @Autowired
    CatalogueClient client;

    public Mono<Product> get(String id) {
        Mono<Product> item = client.get(id);
        return item;
    }

    public Flux<Product> get(List<String> ids) {
        return Flux.fromIterable(ids).flatMap(this::get);
    }
}
