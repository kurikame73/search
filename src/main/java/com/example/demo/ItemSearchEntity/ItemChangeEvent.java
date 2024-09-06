package com.example.demo.ItemSearchEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemChangeEvent {
    private Long itemId;
    private String itemName;
    private Integer itemPrice;
    private String status;

    // 추가할 필드 예시
    private String categoryName;
    private String brand;


}
