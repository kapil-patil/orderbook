package com.ubs.controller;

import com.ubs.model.OrderModel;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.ubs.service.OrderService.orderList;
import static com.ubs.service.OrderService.closeCheck;

@RestController
@Log
public class OrderController {
    @GetMapping("/getAllOrders")
    public ResponseEntity<List<OrderModel>> getOrders() {
        log.info("returning order book");
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> addOrder(@RequestBody OrderModel order) {
        if (!closeCheck) {
            orderList.add(order);
            log.info("order added successfully");
            return new ResponseEntity<>(orderList, HttpStatus.CREATED);
        }
        log.info("order closed, can be modified after completion");
        return new ResponseEntity<>("Order Book is closed, new order will open after completion", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
    }

    @PutMapping("/updateOrder")
    public ResponseEntity<List<OrderModel>> editOrder(@RequestBody OrderModel order) {
        List<OrderModel> existingOrder = orderList.stream().filter(olist -> olist.getInstrumentId().equalsIgnoreCase(order.getInstrumentId())).collect(Collectors.toList());
        if (!existingOrder.isEmpty()) {
            int existingBucket = orderList.indexOf(existingOrder.get(0));
            orderList.set(existingBucket, order);
            log.info("order updated sccuessfully");
            return new ResponseEntity<>(orderList, HttpStatus.FOUND);
        } else {
            log.info("order not found to update");
            return new ResponseEntity<>(orderList, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteOrder/{insId}")
    public ResponseEntity<List<OrderModel>> deleteOrder(@PathVariable String insId) {

        List<OrderModel> existingOrder = orderList.stream().filter(olist -> olist.getInstrumentId().equalsIgnoreCase(insId)).collect(Collectors.toList());
        if (!existingOrder.isEmpty()) {
            int existingBucket = orderList.indexOf(existingOrder.get(0));
            orderList.remove(existingBucket);
            log.info("order deleted sccuessfully");
            return new ResponseEntity<>(orderList, HttpStatus.MOVED_PERMANENTLY);
        } else {
            log.info("order not found for deletion");
            return new ResponseEntity<>(orderList, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/closeOrder")
    public ResponseEntity<String> freezingOrder() {
        closeCheck = true;
        return new ResponseEntity<>("closing order , new order can not be accepted", HttpStatus.DESTINATION_LOCKED);
        // TBD execution

    }

}
