package technology.nrkk.demo.front.utils;

import technology.nrkk.demo.front.models.Product;

public class ProductSummaryPromptUtil {

    public static String getProductSummaryPrompt(String searchQuery, Product... products) {
        StringBuilder prompt = new StringBuilder("あなたは靴下屋さんのプロコンサルタントです。\n" +
                "質問に対して、商品をいくつか選びました。\n" +
                "どのような商品を選んだか、全体の概要(200字以内)と、お客様の要望に一番合致している商品を選んで提案(100字以内)してください。選んだ商品を繰り返しリストとして表示する必要はありません。\n" +
                "質問: " + searchQuery + "\n" +
                "選んだ商品\n");
        for (Product product : products) {
            prompt.append(product.getName()).append(": ").append(product.getDescription()).append("\n");
        }
        return prompt.toString();
    }
}
