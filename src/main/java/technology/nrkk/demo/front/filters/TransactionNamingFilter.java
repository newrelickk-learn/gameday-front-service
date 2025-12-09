package technology.nrkk.demo.front.filters;

import com.newrelic.api.agent.NewRelic;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionNamingFilter implements Filter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(TransactionNamingFilter.class);
    private static final String ACTUATOR_ENDPOINT_PATTERN = "^(/actuator|/favicon|/static|/login|/health).*";

    private static final String START_TIME = "startTime";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
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

        chain.doFilter(request, response);

        // After completion
        String replacesPath = "/error".equals(path) ? (String) request.getAttribute("jakarta.servlet.error.request_uri") : path;
        String contentType = request.getContentType();
        int status = response.getStatus();
        int size = response.getBufferSize();
        long endStartTime = (long) request.getAttribute(START_TIME);
        long endTime = System.currentTimeMillis();
        long duration = endTime - endStartTime;
        if (status > 500) {
            logger.error("%s %s %s %d %d %d ms".formatted(method, replacesPath, contentType == null ? "-" : contentType, status, size, duration));
        } else {
            logger.info("%s %s %s %d %d %d ms".formatted(method, replacesPath, contentType == null ? "-" : contentType, status, size, duration));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}