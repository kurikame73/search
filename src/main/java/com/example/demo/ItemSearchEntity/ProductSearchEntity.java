package com.example.demo.ItemSearchEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "items")
public class ProductSearchEntity {

    private Long id;  // 상품 ID

    @Field(type = FieldType.Text, analyzer = "standard")
    private String itemName;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Integer)
    private Integer stockQuantity;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Integer)
    private Integer sales;

    @Field(type = FieldType.Boolean)
    private Boolean isOnPromotion;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchKeywords;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private List<String> detailImages;
}
