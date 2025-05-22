package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IgnoreTransactionsReactiveFilter implements HandlerInterceptor {

    private static final String ACTUATOR_ENDPOINT_PATTERN = "^(/actuator|/favicon|/static).*";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean ignoreTx = request.getPathInfo()
                .matches(ACTUATOR_ENDPOINT_PATTERN);

        if (ignoreTx){
            NewRelic.ignoreTransaction();
        }
        return true;
    }

}
