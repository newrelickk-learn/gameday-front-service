package technology.nrkk.demo.front.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.newrelic.api.agent.NewRelic;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import technology.nrkk.demo.front.newrelic.SampleLlmTokenCountCallback;
import technology.nrkk.demo.front.newrelic.BedrockTokenCountCache;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BedrockService {

    private final BedrockRuntimeAsyncClient bedrockClient;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${aws.bedrock.titan-embedding-model-id}")
    private String titanEmbeddingModelId;


    public BedrockService(
            @org.springframework.beans.factory.annotation.Value("${aws.region}") String region,
            @org.springframework.beans.factory.annotation.Value("${aws.credentials.access-key-id}") String awsAccessKeyId,
            @org.springframework.beans.factory.annotation.Value("${aws.credentials.secret-access-key}") String awsSecretAccessKey) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        this.bedrockClient = BedrockRuntimeAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.objectMapper = new ObjectMapper();
        NewRelic.getAgent().getAiMonitoring().setLlmTokenCountCallback(new SampleLlmTokenCountCallback());
    }

    public List<Float> getEmbedding(String text) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("inputText", text);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(titanEmbeddingModelId)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromByteArray(requestBody.toString().getBytes(StandardCharsets.UTF_8)))
                    .build();
            CompletableFuture<InvokeModelResponse> response = bedrockClient.invokeModel(request);
            String jsonString = response.get().body().asUtf8String();

            JsonNode rootNode = objectMapper.readTree(jsonString);
            BedrockTokenCountCache.setTokenCount(text, rootNode.get("inputTextTokenCount").asInt());
            JsonNode embeddingNode = rootNode.get("embedding");

            if (embeddingNode == null || !embeddingNode.isArray()) {
                throw new RuntimeException("Invalid Bedrock embedding response: 'embedding' field not found or not an array.");
            }

            List<Float> embeddingFloats = new java.util.ArrayList<>();
            for (JsonNode element : embeddingNode) { // 配列の要素をイテレート
                if (element.isNumber()) { // 数値であることを確認
                    embeddingFloats.add(element.floatValue()); // floatValue() で直接Floatに変換
                } else {
                    // エラーハンドリング: 数値以外の要素が含まれていた場合
                    System.err.println("Warning: Non-numeric element found in embedding array.");
                }
            }
            return embeddingFloats;

        } catch (Exception e) {
            System.err.println("General Error invoking Bedrock model: " + e.getMessage());
            throw new RuntimeException("Failed to process Bedrock embedding: " + e.getMessage(), e);
        }
    }

    public void shutdown() {
        if (bedrockClient != null) {
            bedrockClient.close();
            System.out.println("BedrockRuntimeClient shut down.");
        }
    }
}