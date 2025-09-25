package com.customer.captemplateproject.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Service;

import cds.gen.purchaseorderservice.PurchaseOrderItems;
import cds.gen.purchaseorderservice.PurchaseOrders;

@Service
@StepScope
public class BatchProcessingService {
    // Map for storing cache of po data
    private final Map<String, PurchaseOrders> purchaseOrderDataCache = new ConcurrentHashMap<>();
    // Cqn service
    private final GenericCqnService genericCqnService;
    // Initial cache checking
    private boolean isCacheInitialized = false;

    public BatchProcessingService(GenericCqnService genericCqnService) {
        this.genericCqnService = genericCqnService;
    }

    /**
     * Main Logic of batch processing
     */
    public void processPurchaseOrderData(PurchaseOrders poData) {
        // Initialize cache
        initializeCache();
        // Cache PO data
        purchaseOrderDataCache.compute(poData.getPoNumber(), (key, existingPO) -> {
            // If there is no existing PO header data, create new also insert to DB
            if (existingPO == null) {
                genericCqnService.insertPurchaseOrderData(poData);
                return poData;
                // Update the items to cache if PO header found
                // also insert the items to DB
            } else {
                // Logic to only insert nonexistence items
                List<PurchaseOrderItems> existingItems = existingPO.getItems();
                List<PurchaseOrderItems> newItems = poData.getItems().stream()
                        .filter(item -> existingPO.getItems().stream()
                                .noneMatch(existing -> existing.getItemNumber().equals(item.getItemNumber())))
                        .toList();
                if (!newItems.isEmpty()) {
                    existingItems.addAll(newItems);
                    genericCqnService.insertPurchaseOrderItem(newItems);
                }
                return existingPO;
            }
        });
    }

    /**
     * Get all processed PO numbers in this step (useful for debugging)
     */
    public Set<String> getProcessedPONumbers() {
        return purchaseOrderDataCache.keySet();
    }

    /**
     * Manual reset - called by step listener or for testing
     */
    public void resetState() {
        purchaseOrderDataCache.clear();
    }

    /**
     * Initialize cache with existing PO numbers from database
     */
    private void initializeCache() {
        if (!isCacheInitialized) {
            List<PurchaseOrders> existingPOs = genericCqnService.initializePODataCaching();
            for (PurchaseOrders po : existingPOs) {
                purchaseOrderDataCache.put(po.getPoNumber(), po);
            }
            isCacheInitialized = true;
        }
    }

}
