package technology.nrkk.demo.front.models;
import lombok.Data;

@Data
public class SearchRequest {
    private String query;
    private String categoryFilter; // カテゴリによる絞り込み
    private Double minPrice;
    private Double maxPrice;
    private int limit = 10; // 取得件数
}