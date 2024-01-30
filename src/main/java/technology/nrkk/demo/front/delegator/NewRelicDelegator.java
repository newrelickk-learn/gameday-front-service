package technology.nrkk.demo.front.delegator;

import com.newrelic.api.agent.NewRelic;

public class NewRelicDelegator {

    public String getBrowserTimingHeader() {
        String header = NewRelic.getBrowserTimingHeader();
        return header;
    }

    public String getBrowserTimingFooter() {
        return NewRelic.getBrowserTimingFooter();
    }
}
