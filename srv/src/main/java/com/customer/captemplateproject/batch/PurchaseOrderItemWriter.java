package com.customer.captemplateproject.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.customer.captemplateproject.service.UploadService;

@Component
public class PurchaseOrderItemWriter implements ItemWriter<Map<Integer, String>> {

    private final UploadService uploadService;

    public PurchaseOrderItemWriter(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @Override
    public void write(Chunk<? extends Map<Integer, String>> chunk) throws Exception {
        // Convert the wildcard list to a concrete list
        List<Map<Integer, String>> items = new ArrayList<>(chunk.getItems());

        if (!items.isEmpty()) {
            // Use the existing upload service to process the data
            uploadService.uploadPurchaseOrderData(items);
        }
    }
}