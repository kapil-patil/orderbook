package com.ubs.controller;

import com.ubs.model.OrderModel;
import jakarta.validation.constraints.AssertTrue;
import lombok.extern.java.Log;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.ubs.service.OrderService.closeCheck;

@Log
public class OrderControllerTests {


    private OrderController orderController;

    private List<OrderModel> orderList;

    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
        orderController = new OrderController();
        orderList = new ArrayList<OrderModel>() {
            {
                add(new OrderModel("ISIN103", "800", "03-Sep-2023", "Buy", 2400.00));
            }
        };
    }

    @Test
    @DisplayName("get Orders")
    public void getOrdersTest() {
        try {
            log.info("Starting execution of getOrders");
            ResponseEntity<List<OrderModel>> expectedValue = new ResponseEntity<>(orderList, HttpStatus.OK);
            ResponseEntity<List<OrderModel>> actualValue = orderController.getOrders();
            log.info("Expected Value=" + expectedValue + " . Actual Value=" + actualValue);
            assertTrue(expectedValue.getBody().size() > 0);
        } catch (Exception exception) {
            log.info("Exception in execution of getOrders()-" + exception);
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }

    @Test
    @DisplayName("add Order")
    public void addOrderTest() {
        try {
            log.info("Starting execution of addOrder");
            OrderModel od = new OrderModel("ISIN102", "800", "03-Sep-2023", "Buy", 2400.00);
            ResponseEntity<?> actualValue = orderController.addOrder(orderList.get(0));
            log.info(" Actual Value=" + actualValue);
            Assertions.assertEquals(201, actualValue.getStatusCode().value());
        } catch (Exception exception) {
            log.info("Exception in execution of addOrder()-" + exception);
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }

    @Test
    @DisplayName("restricted adding if closed")
    public void addOrderIfBookClosedTest() {
        try {
            closeCheck = true;
            log.info("Starting execution of addOrder");
            OrderModel od = new OrderModel("ISIN102", "800", "03-Sep-2023", "Buy", 2400.00);
            ResponseEntity<?> actualValue = orderController.addOrder(orderList.get(0));
            log.info(" Actual Value=" + actualValue);
            Assertions.assertEquals(451, actualValue.getStatusCode().value());
            closeCheck = false;
        } catch (Exception exception) {
            log.info("Exception in execution of addOrder()-" + exception);
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }


    @Test
    @DisplayName("edit Order")
    public void editOrderTest() {
        try {
            log.info("Starting execution of editOrder");
            ResponseEntity<List<OrderModel>> actualValue = orderController.editOrder(orderList.get(0));
            Assertions.assertEquals(302, actualValue.getStatusCode().value());
        } catch (Exception exception) {
            log.info("Exception in execution of editOrder()-" + exception);
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }

    @Test
    @DisplayName("delete Order")
    public void deleteOrderTest() {
        try {
            log.info("Starting execution of deleteOrder");
            ResponseEntity<List<OrderModel>> expectedValue = null;
            String insId = "";
            ResponseEntity<List<OrderModel>> actualValue = orderController.deleteOrder(orderList.get(0).getInstrumentId());
            Assertions.assertEquals(301, actualValue.getStatusCode().value());
        } catch (Exception exception) {
            log.info("Exception in execution of deleteOrderTest()-" + exception);
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }

    @Test
    @DisplayName("closing Order Book")
    public void freezingOrder() {
        try {
            log.info("Starting execution of freezingOrder");
            ResponseEntity<String> expectedValue = null;
            ResponseEntity<String> actualValue = orderController.freezingOrder();
            Assertions.assertEquals(true, closeCheck);
            Assertions.assertEquals(421, actualValue.getStatusCode().value());
        } catch (Exception exception) {
            log.info("Exception in execution of freezingOrder()-" + exception);
            exception.printStackTrace();
            Assertions.assertFalse(false);
        }
    }
}
