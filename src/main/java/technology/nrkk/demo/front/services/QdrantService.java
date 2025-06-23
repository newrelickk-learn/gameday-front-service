package technology.nrkk.demo.front.services;

import com.google.common.util.concurrent.ListenableFuture;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Collections.CreateCollection;
import io.qdrant.client.grpc.Collections.CollectionExists;
import io.qdrant.client.grpc.JsonWithInt.Value;
import io.qdrant.client.grpc.Points;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

@Service
public class QdrantService {

    private final QdrantClient qdrantClient;

    @org.springframework.beans.factory.annotation.Value("${qdrant.collection-name}")
    private String collectionName;

    private static final int VECTOR_DIMENSION = 1024;

    public QdrantService(@org.springframework.beans.factory.annotation.Value("${qdrant.host}") String host, @org.springframework.beans.factory.annotation.Value("${qdrant.port}") int port) {
        this.qdrantClient = new QdrantClient(QdrantGrpcClient.newBuilder(host, port, false).build());
    }

    /**
     * コレクションが存在しない場合は作成します。
     * @return コレクションが作成された場合はtrue、既に存在する場合はfalse
     * @throws RuntimeException Qdrantアクセス中にエラーが発生した場合
     */
    public boolean createCollectionIfNotExists() {
        try {
            ListenableFuture<Boolean> existsAsync = this.qdrantClient.collectionExistsAsync(collectionName);
            Boolean exists = existsAsync.get();
            // コレクションが存在しない場合は作成
            if (!exists) {
                System.out.println("Collection " + collectionName + " does not exist. Creating...");

                // CreateCollectionRequest をビルダで構築
                CreateCollection createCollectionRequest = CreateCollection.newBuilder()
                        .setCollectionName(collectionName)
                        .setVectorsConfig(
                                Collections.VectorsConfig.newBuilder()
                                        .setParams(Collections.VectorParams.newBuilder().setDistance(Collections.Distance.Cosine).setSize(VECTOR_DIMENSION))
                                        .build()
                        )
                        .setHnswConfig( // HnswConfigDiff ではなく HnswConfig を使用
                                Collections.HnswConfigDiff.newBuilder()
                                        .setM(16)
                                        .setEfConstruct(100)
                                        .build()
                        )
                        .setQuantizationConfig( // QuantizationConfig を使用
                                Collections.QuantizationConfig.newBuilder()
                                        .setScalar(Collections.ScalarQuantization.newBuilder()
                                                .setTypeValue(Collections.QuantizationType.Int8.getNumber())
                                                .setQuantile(0.99f)
                                                .setAlwaysRam(true)
                                                .build()).build())
                        .build();

                qdrantClient.createCollectionAsync(createCollectionRequest).get();
                return true;
            } else {
                System.out.println("Collection " + collectionName + " already exists.");
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error checking/creating collection: " + e.getMessage(), e);
        }
    }

    /**
     * 商品データをQdrantにインサートします。
     * @param products Qdrantに格納する商品データとメタデータのリスト
     * @return 処理が成功した場合はtrue、それ以外はfalse
     * @throws RuntimeException Qdrantアクセス中にエラーが発生した場合
     */
    public boolean upsertProducts(List<ProductDataForQdrant> products) {


        List<Points.PointStruct> points = products.stream()
                .map(product -> {
                    Map<String, Value> payloadMap = product.getPayload().entrySet().stream()
                            .collect(Collectors.toMap(
                                    e -> e.getKey(),
                                    e -> {
                                        Object value = e.getValue();
                                        if (value instanceof String) return Value.newBuilder().setStringValue((String) value).build();
                                        if (value instanceof Double) return Value.newBuilder().setDoubleValue((Double) value).build();
                                        if (value instanceof Integer) return Value.newBuilder().setIntegerValue((Integer) value).build();
                                        if (value instanceof Boolean) return Value.newBuilder().setBoolValue((Boolean) value).build();
                                        return value(String.valueOf(value));
                                    }
                            ));
                    return Points.PointStruct.newBuilder()
                            .setId(Points.PointId.newBuilder().setUuid(UUID.randomUUID().toString()).build())
                            .setVectors(vectors(product.getVector()))
                            .putAllPayload(payloadMap)
                            .build();
                })
                .collect(Collectors.toList());

        // UpsertPointsRequest をビルダで構築
        Points.UpsertPoints upsertPointsRequest = Points.UpsertPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllPoints(points)
                .setWait(true) // wait=true で結果を待つ
                .build();

        try {
            return qdrantClient.upsertAsync(upsertPointsRequest).get().getInitializationErrorString().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error upserting points: " + e.getMessage(), e);
        }
    }

    /**
     * ベクトルとフィルタ条件に基づいてQdrantを検索します。
     *
     * @param vector 検索クエリのベクトル
     * @param limit 取得する結果の最大数
     * @param categoryFilter カテゴリによる絞り込み (Optional)
     * @param minPrice 価格の下限 (Optional)
     * @param maxPrice 価格の上限 (Optional)
     * @return 検索結果のリスト (商品IDとスコア)
     * @throws RuntimeException Qdrantアクセス中にエラーが発生した場合
     */
    public List<SearchResult> searchProducts(List<Float> vector, int limit,
                                             String categoryFilter) { // MaxPriceの引数名も修正 (大文字始まり)
        Points.SearchPoints.Builder searchPointsBuilder = Points.SearchPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllVector(vector)
                .setLimit(limit)
                .setWithPayload(Points.WithPayloadSelector.newBuilder().setEnable(true))
                .setWithVectors(Points.WithVectorsSelector.newBuilder().setEnable(false));

        Points.Filter.Builder filterBuilder = Points.Filter.newBuilder();
        boolean hasFilter = false;

        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            filterBuilder.addMust(Points.Condition.newBuilder().setField(
                            Points.FieldCondition.newBuilder()
                                    .setKey("category")
                                    .setMatch(Points.Match.newBuilder().setText(categoryFilter).build()) // .build() を追加
                                    .build()) // .build() を追加
                    .build()); // .build() を追加
            hasFilter = true;
        }

        if (hasFilter) {
            searchPointsBuilder.setFilter(filterBuilder.build()); // .build() を追加
        }

        try {
            return qdrantClient.searchAsync(searchPointsBuilder.build()).get().stream()
                    .map(searchedPoint -> {
                        String id = searchedPoint.getId().getUuid();
                        double score = searchedPoint.getScore();
                        return new SearchResult(id, score, searchedPoint.getPayloadMap());
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error searching points: " + e.getMessage(), e);
        }
    }

    // ProductDataForQdrant および SearchResult は変更なし
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDataForQdrant {
        private String id;
        private List<Float> vector;
        private Map<String, Object> payload;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResult {
        private String id;
        private double score;
        private Map<String, Value> payload;
    }

    // アプリケーション終了時にクライアントをシャットダウン
    public void shutdown() {
        if (qdrantClient != null) {
            qdrantClient.close();
            System.out.println("QdrantClient shut down.");
        }
    }
}