package technology.nrkk.demo.front.controllers;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import technology.nrkk.demo.front.entities.User;
import technology.nrkk.demo.front.models.Product;
import technology.nrkk.demo.front.models.Tags;
import technology.nrkk.demo.front.services.UserService;
import technology.nrkk.demo.front.webclient.CatalogueClient;
import technology.nrkk.demo.front.webclient.NewRelicHeaders;

import java.security.Principal;
import java.util.Map;

@RestController
public class CatalogueController {

    @Autowired
    UserService userService;
    @Autowired
    CatalogueClient client;

    private final static Logger logger = LoggerFactory.getLogger(CatalogueController.class);
    @Trace(dispatcher = true)
    @GetMapping(value={"/catalogue/items"}, produces = "application/json")
    public Product[] home(Principal principal, @RequestParam String tags) throws CatalogueClient.CatalogueClientException {
        logger.info("items controller");
        User user = userService.getUserByPrincipal(principal);
        // Custom Headers class to collect traced headers
        NewRelicHeaders tracedHeaders = new NewRelicHeaders();
        // Collect New Relic traced headers
        NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(tracedHeaders);
        // Convert headers from custom Headers implementation to Spring's HttpHeaders
        Map<String, String> newRelicHeaders = tracedHeaders.getHeaderMap();
        logger.info("# of headers %d".formatted(newRelicHeaders.size()));
        for (Map.Entry<String, String> entry : newRelicHeaders.entrySet()) {
            logger.info("Add header %s : %s".formatted(entry.getKey(), entry.getValue()));
        }

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

    @Trace(dispatcher = true)
    @GetMapping(value={"/catalogue/tags"}, produces = "application/json")
    public Tags getTags(Principal principal) throws CatalogueClient.CatalogueClientException {
        userService.getUserByPrincipal(principal);
        return client.getTags();
    }

}
