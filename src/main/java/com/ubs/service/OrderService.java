package com.ubs.service;

import com.ubs.model.OrderModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    public static List<OrderModel> orderList = new ArrayList<OrderModel>(){
        {
            add(new OrderModel("ISIN103", "800", "03-Sep-2023", "Buy", 2400.00));
        }
    };
    public static boolean closeCheck = false;


}
