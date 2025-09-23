package com.customer.captemplateproject.batch;

import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderItemProcessor implements ItemProcessor<Map<Integer, String>, Map<Integer, String>> {

    @Override
    public Map<Integer, String> process(Map<Integer, String> item) throws Exception {
        // Add any validation or transformation logic here
        // For now, just pass through the data

        // Example validation: check if required fields are present
        if (item.get(0) == null || item.get(0).trim().isEmpty()) {
            // Skip this item if PO Number is missing
            return null;
        }

        return item;
    }
}