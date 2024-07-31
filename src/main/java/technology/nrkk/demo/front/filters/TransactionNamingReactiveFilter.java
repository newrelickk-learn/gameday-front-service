package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TransactionNamingReactiveFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionNamingReactiveFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();


        if (path != null && method != null && !path.startsWith("/static")){
            String transactionName = String.format("%s (%s)", path, method);
            NewRelic.setTransactionName(null, transactionName);
            logger.info(transactionName);
        } else {
            NewRelic.ignoreTransaction();
        }


        return chain.filter(exchange);
    }
}
