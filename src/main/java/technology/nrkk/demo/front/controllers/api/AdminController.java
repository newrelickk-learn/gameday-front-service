package technology.nrkk.demo.front.controllers.api;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.services.BedrockService;
import technology.nrkk.demo.front.services.QdrantService;
import technology.nrkk.demo.front.webclient.CatalogueClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    BedrockService bedrockService;

    @Autowired
    QdrantService qdrantService;

    @Autowired
    CatalogueClient client;

    protected final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Trace(dispatcher = true)
    //@RequestMapping(value ="/admin/initiate")
    public ResponseEntity<Void> get() throws CatalogueClient.CatalogueClientException {
        qdrantService.createCollectionIfNotExists();
        Product[] products = client.search("", null, 1000);
        Map<String, List<Float>> vectorMap = new HashMap<>();
        List<Product> productList = List.of(products);
        NewRelic.addCustomParameter("productCount", productList.size());
        List<QdrantService.ProductDataForQdrant> productDataForQdrantList = productList.stream().map(product -> {
            String text = product.getDescription();
            List<Float> vectors = vectorMap.get(text);
            if (vectors == null) {
                vectors = bedrockService.getEmbedding(text);
                vectorMap.put(text, vectors);
            }
            return new QdrantService.ProductDataForQdrant(product.getId(), vectors, Map.of("description", text));
        }).toList();
        NewRelic.addCustomParameter("uniqueTextCount", vectorMap.size());
        qdrantService.upsertProducts(productDataForQdrantList);
        logger.info("Initiate API called");
        return ResponseEntity.ok().build();
    }

    @Trace(dispatcher = true)
    //@RequestMapping(value ="/admin/test")
    public ResponseEntity<Void> test() throws CatalogueClient.CatalogueClientException {
        List<Float> vectors = bedrockService.getEmbedding("薄い男性用のしっかりした靴下が欲しい");
        List<QdrantService.SearchResult> result = qdrantService.searchProducts(vectors, 10, null);
        logger.info(result.get(0).getId());
        return ResponseEntity.ok().build();
    }
}
