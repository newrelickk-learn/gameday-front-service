package technology.nrkk.demo.front.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.SearchRequest;
import technology.nrkk.demo.front.models.SearchResponse;
import technology.nrkk.demo.front.models.Tags;
import technology.nrkk.demo.front.services.BedrockService;
import technology.nrkk.demo.front.services.QdrantService;
import technology.nrkk.demo.front.services.UserService;
import technology.nrkk.demo.front.utils.ProductSummaryPromptUtil;
import technology.nrkk.demo.front.webclient.CatalogueClient;
import technology.nrkk.demo.front.webclient.NewRelicHeaders;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class CatalogueController {

    @Autowired
    UserService userService;
    @Autowired
    CatalogueClient client;
    @Autowired
    BedrockService bedrockService;
    @Autowired
    QdrantService qdrantService;

    private final static Logger logger = LoggerFactory.getLogger(CatalogueController.class);
    @GetMapping(value={"/catalogue/items"}, produces = "application/json")
    public Product[] home(Principal principal, @RequestParam String tags) throws CatalogueClient.CatalogueClientException {
        logger.info("items controller");
        User user = userService.getUserByPrincipal(principal);
        return client.search(tags, user);
    }

    @Trace(dispatcher = true)
    @GetMapping(value={"/catalogue/item/{id}/image"}, produces = "application/json")
    public Product getImage(Principal principal, @PathVariable("id") String id) throws CatalogueClient.CatalogueClientException {
        User user = userService.getUserByPrincipal(principal);
        return client.get(id, user);
    }

    @Trace(dispatcher = true)
    @GetMapping(value={"/catalogue/item/{id}"}, produces = "application/json")
    public Product getProduct(Principal principal, @PathVariable("id") String id) throws CatalogueClient.CatalogueClientException {
        User user = userService.getUserByPrincipal(principal);
        return client.get(id, user);
    }

    @GetMapping(value={"/catalogue/tags"}, produces = "application/json")
    public Tags getTags(Principal principal) throws CatalogueClient.CatalogueClientException {
        userService.getUserByPrincipal(principal);
        return client.getTags();
    }

    @PostMapping(value={"/catalogue/search"}, produces = "application/json")
    public SearchResponse search(Principal principal, @RequestBody SearchRequest searchRequest) throws CatalogueClient.CatalogueClientException, ExecutionException, InterruptedException, JsonProcessingException {
        User user = userService.getUserByPrincipal(principal);
        NewRelic.addCustomParameter("bedrockSearch", true);
        List<Float> vectors = bedrockService.getEmbedding(searchRequest.getQuery());
        List<QdrantService.SearchResult> result = qdrantService.searchProducts(vectors, 10, null);
        List<Product> productList = result.stream().map(QdrantService.SearchResult::getPayload).map(payload -> {
            try {
                String id = payload.get("sockId").getStringValue();
                return client.get(id, user);
            } catch (CatalogueClient.CatalogueClientException e) {
                logger.error("Error fetching product with ID: " + payload.get("sockId").getStringValue(), e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
        Product[] products = productList.subList(0, Math.min(4, productList.size() - 1)).toArray(Product[]::new);
        SearchResponse response = new SearchResponse();
        response.setProducts(products);
        response.setDescription(bedrockService.getDescription(ProductSummaryPromptUtil.getProductSummaryPrompt(searchRequest.getQuery(), products), Objects.equals(user.getRank(), "GoldMember") ? "Premium" : "Normal"));
        return response;

    }

}
