package technology.nrkk.demo.front.models;

import lombok.Data;

@Data
public class Product {
     String id;
     String name;
     String description;
     Double price;
     Integer count;
     String[] imageUrl;
}
