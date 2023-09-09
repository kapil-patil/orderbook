package com.ubs.model;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {

    private String instrumentId;
    private Double Quantity;
    private String bookDate;
    private String type;
    private Double Price;
}
