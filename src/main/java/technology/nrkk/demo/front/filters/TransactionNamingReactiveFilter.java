package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();


        if (path != null && method != null){
            String transactionName = String.format("%s (%s)", path, method);
            NewRelic.setTransactionName(null, transactionName);
        }


        return chain.filter(exchange);
    }
}
