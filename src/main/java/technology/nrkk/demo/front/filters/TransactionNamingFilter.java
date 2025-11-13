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
public class TransactionNamingFilter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionNamingFilter.class);
    private static final String ACTUATOR_ENDPOINT_PATTERN = "^(/actuator|/favicon|/static|/login|/health).*";

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path != null && method != null && !"/error".equals(path) && !path.startsWith("/40") && !path.startsWith("/50")){
            NewRelic.getAgent().getTransaction().isTransactionNameSet();
            boolean ignoreTx = path
                    .matches(ACTUATOR_ENDPOINT_PATTERN);
            if (ignoreTx) {
                NewRelic.ignoreTransaction();
            } else {
                String transactionName = String.format("%s (%s)", path, method);
                NewRelic.setTransactionName(null, transactionName);
            }
        }

        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String replacesPath = "/error".equals(path) ? (String) request.getAttribute("jakarta.servlet.error.request_uri") : path;
        String contentType = request.getContentType();
        int status = response.getStatus();
        int size = response.getBufferSize();
        long startTime = (long) request.getAttribute(START_TIME);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        if (status > 500) {
            logger.error("%s %s %s %d %d %d ms".formatted(method, replacesPath, contentType == null ? "-" : contentType, status, size, duration));
        } else {
            logger.info("%s %s %s %d %d %d ms".formatted(method, replacesPath, contentType == null ? "-" : contentType, status, size, duration));
        }
    }

}