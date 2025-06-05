package technology.nrkk.demo.front.webclient;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class NewRelicHeaders implements Headers {
    private final Map<String, String> headerMap = new HashMap<>();

    protected final static Logger logger = LoggerFactory.getLogger(NewRelicHeaders.class);

    @Override
    public HeaderType getHeaderType() {
        return HeaderType.HTTP;
    }

    @Override
    public String getHeader(String s) {
        return this.headerMap.get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return List.of();
    }

    @Override
    public void setHeader(String name, String value) {
        logger.info("Set header %s : %s".formatted(name, value));
        headerMap.put(name, value);
    }

    @Override
    public void addHeader(String s, String s1) {
        logger.info("Add header %s : %s".formatted(s, s1));
        this.headerMap.put(s, s1);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return List.of();
    }

    @Override
    public boolean containsHeader(String s) {
        return this.headerMap.containsKey(s);
    }

}
