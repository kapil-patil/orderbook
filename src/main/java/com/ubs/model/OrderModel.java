package com.ubs.model;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {

    private String instrumentId;
    private String Quantity;
    private String bookDate;
    private String type;
    private double Price;
}
