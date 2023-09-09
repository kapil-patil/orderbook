package com.ubs.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ubs.model.OrderModel;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log
public class OrderService {

    public static List<OrderModel> orderList = new ArrayList<OrderModel>() {
        {
            add(new OrderModel("ISIN103", 800.0, "03-Sep-2023", "Buy", 2400.0));
            add(new OrderModel("ISIN103", 300.0, "03-Sep-2023", "Sell", 900.0));
            add(new OrderModel("ISIN103", 700.0, "03-Sep-2023", "Sell", 2100.0));
            add(new OrderModel("ISIN100", 600.0, "03-Sep-2023", "Buy", 1200.0));
            add(new OrderModel("ISIN102", 300.0, "03-Sep-2023", "Sell", 1600.0));
        }
    };
    public static boolean closeCheck = false;

    public static List<OrderModel> executionOrderList = new ArrayList<OrderModel>();
    public static List<OrderModel> completeOrderList = new ArrayList<OrderModel>();

    public static Map<String, double[]> executionOrderMap = new HashMap<>();

    public static void startExecution() {

        Function<Double, Double> sellValue = s -> -s;
        Predicate<String> isSell = t -> t.equalsIgnoreCase("Sell");

        orderList.stream().forEach(
                olist -> {
                    if (executionOrderMap.get(olist.getInstrumentId() + "_" + olist.getBookDate()) == null) {
                        Double quantity = isSell.test(olist.getType()) ? sellValue.apply(olist.getQuantity()) : olist.getQuantity();
                        Double price = isSell.test(olist.getType()) ? sellValue.apply(olist.getPrice()) : olist.getPrice();
                        executionOrderMap.put(olist.getInstrumentId() + "_" + olist.getBookDate(), new double[]{quantity, price});
                    } else {
                        double[] bookedValues = executionOrderMap.get(olist.getInstrumentId() + "_" + olist.getBookDate());
                        Double quantity = isSell.test(olist.getType()) ? sellValue.apply(olist.getQuantity()) : olist.getQuantity();
                        Double price = isSell.test(olist.getType()) ? sellValue.apply(olist.getPrice()) : olist.getPrice();
                        executionOrderMap.put(olist.getInstrumentId() + "_" + olist.getBookDate(), new double[]{bookedValues[0] + quantity, bookedValues[1] + price});
                    }
                }
        );

        for (var entry : executionOrderMap.entrySet()) {
            double[] bookedValues = entry.getValue();
            String instType = bookedValues[0] < 0.0 ? "ask" : "offer";
            String[] InsidAndDate = entry.getKey().split("_");
            executionOrderList.add(new OrderModel(InsidAndDate[0], bookedValues[0] < 0.0 ? (-1) * (bookedValues[0]) : bookedValues[0], InsidAndDate[1], instType, bookedValues[1] < 0.0 ? (-1) * (bookedValues[1]) : bookedValues[1]));
        }

        for (OrderModel od : orderList) {
            List<OrderModel> executionOrder = executionOrderList.stream().filter(olist -> od.getInstrumentId().equalsIgnoreCase(olist.getInstrumentId())).collect(Collectors.toList());
            if (od.getPrice() < executionOrder.get(0).getPrice() && (od.getType().equalsIgnoreCase("Buy"))) {
                continue;
            } else if (od.getPrice() > executionOrder.get(0).getPrice() && (od.getType().equalsIgnoreCase("Sell"))) {
                continue;
            } else if (od.getPrice().equals(executionOrder.get(0).getPrice())) {
                continue;
            } else {
                completeOrderList.add(od);
            }
        }

        executionOrderList.addAll(completeOrderList);

        closeCheck = false;

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("OrderBook.pdf"));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        document.open();

        PdfPTable table = new PdfPTable(1);
        addTableHeader(table);
        addRows(table, executionOrderList);
        //addCustomRows(table);

        try {
            document.add(table);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.close();


    }

    private static void addRows(PdfPTable table, List<OrderModel> executionOrderList) {
        executionOrderList.stream().forEach(od -> table.addCell(od.toString()));

    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("instrument Details")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }


}
