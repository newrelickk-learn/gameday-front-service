package technology.nrkk.demo.front.models;
import lombok.Data;

@Data
public class SearchResponse {
    private Product[] products;
    private String description;
}