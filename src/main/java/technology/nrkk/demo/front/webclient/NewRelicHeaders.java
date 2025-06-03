package technology.nrkk.demo.front.webclient;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRelicHeaders implements Headers {
    private Map<String, String> headerMap = new HashMap<>();

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
        headerMap.put(name, value);
    }

    @Override
    public void addHeader(String s, String s1) {
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

    @Override
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}
