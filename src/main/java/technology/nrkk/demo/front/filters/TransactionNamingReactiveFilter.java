package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TransactionNamingReactiveFilter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionNamingReactiveFilter.class);
    private static final String ACTUATOR_ENDPOINT_PATTERN = "^(/actuator|/favicon|/static).*";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = request.getPathInfo();
        String method = request.getMethod();

        if (path != null && method != null && !path.startsWith("/static")){
            String transactionName = String.format("%s (%s)", path, method);
            NewRelic.setTransactionName(null, transactionName);
            logger.info(transactionName);
        } else {
            NewRelic.ignoreTransaction();
        }

        return true;
    }

}