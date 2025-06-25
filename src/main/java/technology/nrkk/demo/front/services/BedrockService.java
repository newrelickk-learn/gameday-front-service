package technology.nrkk.demo.front.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.newrelic.api.agent.NewRelic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;
import technology.nrkk.demo.front.newrelic.BedrockTokenCountCache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BedrockService {

    private final BedrockRuntimeAsyncClient bedrockClient;
    private final ObjectMapper objectMapper;
    Random random = new Random();

    @org.springframework.beans.factory.annotation.Value("${aws.bedrock.embedding-model-id}")
    private String embeddingModelId;
    @org.springframework.beans.factory.annotation.Value("${aws.bedrock.model-id}")
    private String modelId;

    private final static Logger logger = LoggerFactory.getLogger(BedrockService.class);
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
        //NewRelic.getAgent().getAiMonitoring().setLlmTokenCountCallback(new SampleLlmTokenCountCallback());
    }

    public List<Float> getEmbedding(String text) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("inputText", text);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(embeddingModelId)
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

    public String getDescription(String prompt, String mode) {
        NewRelic.addCustomParameter("bedrockMode", mode);
        if (Objects.equals(mode, "Premium")) {
            int randomInt = random.nextInt(6);
            return switch (randomInt) {
                case 0 -> getDescriptionWithClaudeLegacy(prompt);
                case 1, 2 -> getDescriptionWithClaudeHaiku3(prompt);
                default -> throw new RuntimeException("We cannot call the Bedrock");
            };
        } else {
            int randomInt = random.nextInt(5);
            return switch (randomInt) {
                case 0, 1, 2 -> getDescriptionWithTitan(prompt);
                default -> throw new RuntimeException("We cannot call the Bedrock");
            };
        }
    }
    public String getDescriptionWithNova(String prompt) {
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        logger.info("Using Bedrock model: " + modelId);
        NewRelic.addCustomParameter("bedrockModelId", modelId);

        ObjectNode requestBody = this.objectMapper.createObjectNode();
        ObjectNode messageNode = objectMapper.createObjectNode();
        messageNode.put("role", ConversationRole.USER.toString());
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("text", prompt);
        messageNode.putIfAbsent("content", this.objectMapper.valueToTree(List.of(contentNode)));
        requestBody.putIfAbsent("messages", this.objectMapper.valueToTree(List.of(messageNode)));

        SdkBytes body;
        try {
            body = SdkBytes.fromUtf8String(this.objectMapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("リクエストボディのシリアライズに失敗しました: " + e.getMessage(), e);
        }
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        NewRelic.addCustomParameter("bedrockUsed", true);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(body)
                .build();

            try {
                CompletableFuture<InvokeModelResponse> response = bedrockClient.invokeModel(request);
                JsonNode jsonResponse = this.objectMapper.readTree(response.get().body().asString(StandardCharsets.UTF_8));

                if (jsonResponse.has("output")
                        && jsonResponse.get("output").has("message")
                        && jsonResponse.get("output").get("message").has("content")
                        && jsonResponse.get("output").get("message").get("content").isArray()) {
                    logger.info("Response from Bedrock model: " + modelId + "completed successfully.");
                    return jsonResponse.get("output").get("message").get("content").get(0).get("text").asText();
                } else {
                    throw new RuntimeException("レスポンスに 'output.message.content' が見つからないか、形式が不正です: " + jsonResponse.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException("レスポンスボディのデシリアライズに失敗しました: " + e.getMessage(), e);
            } catch (Exception e) {
                // その他のBedrock呼び出しエラー
                throw new RuntimeException(String.format("モデル'%s'の呼び出し中にエラーが発生しました: %s", modelId, e.getMessage()), e);
            }

    }

    public String getDescriptionWithTitan(String prompt) {
        var modelId = "amazon.titan-text-express-v1";
        logger.info("Using Bedrock model: " + modelId);
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        ObjectNode requestBody = this.objectMapper.createObjectNode();
        requestBody.put("inputText", prompt);

        SdkBytes body;
        try {
            body = SdkBytes.fromUtf8String(this.objectMapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("リクエストボディのシリアライズに失敗しました: " + e.getMessage(), e);
        }
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        NewRelic.addCustomParameter("bedrockUsed", true);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(body)
                .build();
        try {
            CompletableFuture<InvokeModelResponse> response = bedrockClient.invokeModel(request);
            JsonNode jsonResponse = this.objectMapper.readTree(response.get().body().asString(StandardCharsets.UTF_8));

            if (jsonResponse.has("results")
                    && jsonResponse.get("results").isArray()) {
                logger.info("Response from Bedrock model: " + modelId + "completed successfully.");
                return jsonResponse.get("results").get(0).get("outputText").asText();
            } else {
                throw new RuntimeException("レスポンスに 'results.0.outputText' が見つからないか、形式が不正です: " + jsonResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("レスポンスボディのデシリアライズに失敗しました: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("モデル'%s'の呼び出し中にエラーが発生しました: %s", modelId, e.getMessage()), e);
        }

    }

    public String getDescriptionWithClaudeHaiku3(String prompt) {
        var modelId = "anthropic.claude-3-haiku-20240307-v1:0";
        logger.info("Using Bedrock model: " + modelId);
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        ObjectNode requestBody = this.objectMapper.createObjectNode();
        requestBody.put("anthropic_version", "bedrock-2023-05-31");
        requestBody.put("max_tokens", 512); // 最大トークン数を設定
        ObjectNode messageNode = objectMapper.createObjectNode();
        messageNode.put("role", ConversationRole.USER.toString());
        messageNode.put("content", prompt);
        requestBody.putIfAbsent("messages", this.objectMapper.valueToTree(List.of(messageNode)));

        SdkBytes body;
        try {
            body = SdkBytes.fromUtf8String(this.objectMapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("リクエストボディのシリアライズに失敗しました: " + e.getMessage(), e);
        }
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        NewRelic.addCustomParameter("bedrockUsed", true);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(body)
                .build();

        try {
            CompletableFuture<InvokeModelResponse> response = bedrockClient.invokeModel(request);
            JsonNode jsonResponse = this.objectMapper.readTree(response.get().body().asString(StandardCharsets.UTF_8));

            if (jsonResponse.has("content")
                    && jsonResponse.get("content").isArray()) {
                logger.info("Response from Bedrock model: " + modelId + "completed successfully.");
                return jsonResponse.get("content").get(0).get("text").asText();
            } else {
                throw new RuntimeException("レスポンスに 'results.0.outputText' が見つからないか、形式が不正です: " + jsonResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("レスポンスボディのデシリアライズに失敗しました: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("モデル'%s'の呼び出し中にエラーが発生しました: %s", modelId, e.getMessage()), e);
        }

    }
    public String getDescriptionWithClaudeLegacy(String prompt) {
        var modelId = "anthropic.claude-instant-v1";
        logger.info("Using Bedrock model: " + modelId);
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        ObjectNode requestBody = this.objectMapper.createObjectNode();
        requestBody.put("anthropic_version", "bedrock-2023-05-31");
        requestBody.put("max_tokens_to_sample", 512); // 最大トークン数を設定
        ObjectNode messageNode = objectMapper.createObjectNode();
        messageNode.put("role", ConversationRole.USER.toString());
        messageNode.put("content", "\n\nHuman: %s".formatted(prompt));
        requestBody.put("prompt", "\n\nHuman: %s\n\nAssistant:".formatted(prompt));

        //requestBody.putIfAbsent("messages", this.objectMapper.valueToTree(List.of(messageNode)));

        SdkBytes body;
        try {
            body = SdkBytes.fromUtf8String(this.objectMapper.writeValueAsString(requestBody));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("リクエストボディのシリアライズに失敗しました: " + e.getMessage(), e);
        }
        NewRelic.addCustomParameter("bedrockModelId", modelId);
        NewRelic.addCustomParameter("bedrockUsed", true);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(body)
                .build();

        try {
            CompletableFuture<InvokeModelResponse> response = bedrockClient.invokeModel(request);
            JsonNode jsonResponse = this.objectMapper.readTree(response.get().body().asString(StandardCharsets.UTF_8));

            if (jsonResponse.has("completion")) {
                logger.info("Response from Bedrock model: " + modelId + "completed successfully.");
                return jsonResponse.get("completion").asText();
            } else {
                throw new RuntimeException("レスポンスに 'results.0.outputText' が見つからないか、形式が不正です: " + jsonResponse.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("レスポンスボディのデシリアライズに失敗しました: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("モデル'%s'の呼び出し中にエラーが発生しました: %s", modelId, e.getMessage()), e);
        }

    }
}