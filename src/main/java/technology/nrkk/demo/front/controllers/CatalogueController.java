package technology.nrkk.demo.front.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;
import technology.nrkk.demo.front.webclient.CatalogueClient;

@RestController
public class CatalogueController {

    @Autowired
    CatalogueClient client;

    @GetMapping(value={"/catalogue/items"}, produces = "application/json")
    public Mono<Product[]> home(@RequestParam String tags) {
        Mono<Product[]> items = client.search(tags);
        return items;
    }

    @GetMapping(value={"/catalogue/item/{id}/image"}, produces = "application/json")
    public Mono<Product> getProduct(@PathVariable("id") String id) {
        Mono<Product> item = client.get(id);
        return item;
    }

    @GetMapping(value={"/catalogue/tags"}, produces = "application/json")
    public Mono<Tags> getTags() {
        Mono<Tags> tags = client.getTags();
        return tags;
    }

}
