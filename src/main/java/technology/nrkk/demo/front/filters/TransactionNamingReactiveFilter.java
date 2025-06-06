package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TransactionNamingReactiveFilter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionNamingReactiveFilter.class);
    private static final String ACTUATOR_ENDPOINT_PATTERN = "^(/actuator|/favicon|/static|/login|/health).*";
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path != null && method != null){
            boolean ignoreTx = path
                    .matches(ACTUATOR_ENDPOINT_PATTERN);
            if (ignoreTx) {
                NewRelic.ignoreTransaction();
            } else {
                String transactionName = String.format("%s (%s)", path, method);
                NewRelic.setTransactionName(null, transactionName);
            }
        }
    }

}