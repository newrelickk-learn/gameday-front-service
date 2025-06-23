package technology.nrkk.demo.front.newrelic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.LlmTokenCountCallback;

import java.util.concurrent.ExecutionException;

public class SampleLlmTokenCountCallback implements LlmTokenCountCallback {

    @Override
    public int calculateLlmTokenCount(String model, String content) {
        return (int) Math.floor(content.length() * 1.25); // 真面目に実装したいが今の所出来なさそうなので仕方なくマジックナンバーでの実装です
    }

}