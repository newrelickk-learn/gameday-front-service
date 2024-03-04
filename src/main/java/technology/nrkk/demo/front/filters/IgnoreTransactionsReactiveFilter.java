package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IgnoreTransactionsReactiveFilter implements WebFilter {

    private static final String ACTUATOR_ENDPOINT_PATTERN = "^(/actuator|/favicon|/static).*";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        boolean ignoreTx = exchange.getRequest().getPath().value()
            .matches(ACTUATOR_ENDPOINT_PATTERN);


        if (ignoreTx){
            NewRelic.ignoreTransaction();
        }


        return chain.filter(exchange);
    }
}
