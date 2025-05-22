package technology.nrkk.demo.front.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.List;

@Service
public class CatalogueService {

    @Autowired
    CatalogueClient client;

    public Product get(String id, User user) throws CatalogueClient.CatalogueClientException {
        return client.get(id, user);
    }

    public List<Product> get(List<String> ids, User user) {
        return ids.stream().map(id -> {
            try {
                return this.get(id, user);
            } catch (CatalogueClient.CatalogueClientException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
