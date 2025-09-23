package com.customer.captemplateproject.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sap.cds.ql.Select;

import cds.gen.purchaseorderservice.PurchaseOrderItems;
import cds.gen.purchaseorderservice.PurchaseOrderItems_;
import cds.gen.purchaseorderservice.PurchaseOrderService;
import cds.gen.purchaseorderservice.PurchaseOrders;
import cds.gen.purchaseorderservice.PurchaseOrders_;

/**
 * Service for handling CQN operations. You should write flat method for
 * specific SQL, e.g. insertMaterial( String material, String XXXX){
 * Insert.into(Material_.class).entries(XXX); entityService.insert(XXX); }
 * 1. Call EntityService to commit SQL
 * 2. Use flat and fix variable as parameters, Do not use any generic type or
 * Collection/Map.
 */
@Service
public class GenericCqnService {

    private final PurchaseOrderService purchaseOrderService;

    private final EntityService entityService;

    public GenericCqnService(
            PurchaseOrderService purchaseOrderService,
            EntityService entityService) {
        this.purchaseOrderService = purchaseOrderService;
        this.entityService = entityService;
    }

    public PurchaseOrders getPurchaseOrderById(String id) {
        var select = Select.from(PurchaseOrders_.class).where(m -> m.ID().eq(id));
        return entityService.selectSingle(purchaseOrderService, select, PurchaseOrders.class,
                "MaterialHeader data not found: " + id);
    }

    public PurchaseOrders checkAndGetPurchaseOrderById(String id) {
        var select = Select.from(PurchaseOrders_.class).where(m -> m.ID().eq(id));
        PurchaseOrders result = PurchaseOrders.create();
        try {
            result = entityService.selectSingle(purchaseOrderService, select, PurchaseOrders.class,
                    "MaterialHeader data not found: " + id);
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public void insertPurchaseOrderData(PurchaseOrders data) {
        entityService.insert(purchaseOrderService, null, PurchaseOrders_.class, data, true);
    }

    public void insertPurchaseOrderItem(List<PurchaseOrderItems> data) {
        entityService.bulkInsert(purchaseOrderService, null, PurchaseOrderItems_.class, data, true);
    }

}