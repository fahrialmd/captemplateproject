package com.customer.captemplateproject.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.customer.captemplateproject.exception.BusinessException;
import com.customer.captemplateproject.service.UploadService;

import cds.gen.purchaseorderservice.PurchaseOrders;
import cds.gen.purchaseorderservice.PurchaseOrderItems;

@Service
public class UploadServiceImpl implements UploadService {

    private final GenericCqnService genericCqnService;

    public UploadServiceImpl(GenericCqnService genericCqnService) {
        this.genericCqnService = genericCqnService;
    }

    @Override
    public void uploadPurchaseOrderData(List<Map<Integer, String>> uploadData) {
        try {
            // Group rows by poNumber to separate headers from items
            Map<String, List<Map<Integer, String>>> groupedByPoNumber = uploadData.stream()
                    .collect(Collectors.groupingBy(row -> row.get(0))); // Group by poNumber (column 0)

            for (Map.Entry<String, List<Map<Integer, String>>> entry : groupedByPoNumber.entrySet()) {
                String poNumber = entry.getKey();
                List<Map<Integer, String>> rows = entry.getValue();
                processHeaderAndItems(poNumber, rows);
            }
        } catch (Exception e) {
            throw new BusinessException("Failed to upload purchase order data: " + e.getMessage(), e);
        }
    }

    @Override
    public void processPurchaseOrderWithItems(Map<String, Object> headerData, List<Map<String, Object>> itemsData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPurchaseOrderWithItems'");
    }

    private BigDecimal convertToBigDecimal(String input) {
        BigDecimal output = new BigDecimal(input);
        return output;
    }

    private void processHeaderAndItems(String poNumber, List<Map<Integer, String>> rows) {
        // Create header from first row (all rows should have same header data)
        Map<Integer, String> headerRow = rows.get(0);
        PurchaseOrders purchaseOrderData = PurchaseOrders.create();
        // Check if poNumber is exist in database
        purchaseOrderData = genericCqnService.checkAndGetPurchaseOrderById(poNumber);
        Boolean isHeaderEmpty = purchaseOrderData.isEmpty();
        if (isHeaderEmpty) {
            purchaseOrderData.setPoNumber(headerRow.get(0));
            purchaseOrderData.setPoType(headerRow.get(1));
            purchaseOrderData.setVendorId(headerRow.get(2));
            purchaseOrderData.setCompanyCode(headerRow.get(3));
            purchaseOrderData.setPlant(headerRow.get(4));
            purchaseOrderData.setDocumentDate(LocalDate.parse(headerRow.get(5)));
            purchaseOrderData.setDeliveryDate(LocalDate.parse(headerRow.get(6)));
            purchaseOrderData.setCurrencyCode(headerRow.get(7));
            purchaseOrderData.setTotalAmount(convertToBigDecimal(headerRow.get(8)));
            purchaseOrderData.setDeliveryStatusCode(headerRow.get(9));
        }
        // Populate the items
        List<PurchaseOrderItems> items = new ArrayList<>();
        for (Map<Integer, String> row : rows) {
            PurchaseOrderItems item = PurchaseOrderItems.create();
            item.setHeaderPoNumber(poNumber);
            item.setItemNumber(row.get(10));
            item.setMaterialId(row.get(11));
            item.setDescription(row.get(12));
            item.setQuantity(convertToBigDecimal(row.get(13)));
            item.setUnit(row.get(14));
            item.setNetPrice(convertToBigDecimal(row.get(15)));
            item.setNetAmount(convertToBigDecimal(row.get(16)));
            item.setDeliveryDate(LocalDate.parse(row.get(17)));
            item.setPlant(row.get(18));
            items.add(item);
        }
        purchaseOrderData.setItems(items);
        // Decision to insert data if there is no existing data or update it
        if (isHeaderEmpty) {
            genericCqnService.insertPurchaseOrderData(purchaseOrderData);
        } else {
            genericCqnService.insertPurchaseOrderItem(purchaseOrderData.getItems());
        }
    }

}
