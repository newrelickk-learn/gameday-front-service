package technology.nrkk.demo.front.newrelic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BedrockTokenCountCache {
    private static final Cache<String, Integer> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // 最終書き込みから10分後に削除
            .maximumSize(1000) // 最大1000エントリ
            .weakValues() // ★値がどこからも強く参照されなくなったらGC可能になる
            .concurrencyLevel(4) // 同時書き込みスレッドのヒント
            .build();

    /**
     * Sets the token count for the current thread.
     *
     * @param tokenCount The token count to set.
     */
    public static void setTokenCount(String text, int tokenCount) {
        if (cache.getIfPresent(text) == null) {
            cache.put(text, tokenCount);
        }
    }

    /**
     * Gets the token count for the current thread.
     *
     * @return The token count.
     */
    public static int getTokenCount(String text) throws ExecutionException {
        Integer count = cache.getIfPresent(text);
        if (count == null) {
            return 0;
        }
        return count;
    }

}
