package com.customer.captemplateproject.handlers;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.customer.captemplateproject.batch.BatchInputReader;
import com.customer.captemplateproject.service.UploadService;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.purchaseorderservice.Upload;
import cds.gen.purchaseorderservice.Upload_;

@Component
@ServiceName("PurchaseOrderService")
public class PurchaseOrderServiceHandler implements EventHandler {

    private final UploadService uploadService;

    public PurchaseOrderServiceHandler(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @On(entity = Upload_.CDS_NAME, event = CqnService.EVENT_READ)
    public Upload getUploadSingleton() {
        return Upload.create();
    }

    @On(entity = Upload_.CDS_NAME, event = CqnService.EVENT_UPDATE)
    public void handleExcelUpload(CdsUpdateEventContext context, Upload upload) {
        uploadService.processExcelBatchInput(upload);
        context.setResult(Arrays.asList(upload));
    }

}