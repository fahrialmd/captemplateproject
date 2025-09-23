package com.customer.captemplateproject.handlers;

import java.io.InputStream;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.customer.captemplateproject.Upload.NoModelDataListener;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.ParameterInfo;

import cds.gen.purchaseorderservice.Upload;
import cds.gen.purchaseorderservice.Upload_;

@Component
@ServiceName("PurchaseOrderService")
public class PurchaseOrderServiceHandler implements EventHandler {

    private final PersistenceService db;

    PurchaseOrderServiceHandler(PersistenceService db) {
        this.db = db;
    }

    @On(entity = Upload_.CDS_NAME, event = CqnService.EVENT_READ)
    public Upload getUploadSingleton() {
        return Upload.create();
    }

    @On
    public void handleExcelUpload(CdsUpdateEventContext context, Upload upload) {
        InputStream is = upload.getPurchaseOrderItems();
        if (is != null) {
            // Process Excel file using EasyExcel
            EasyExcel.read(is, new NoModelDataListener(db)).sheet().doRead();
        }
        context.setResult(Arrays.asList(upload));
    }
}