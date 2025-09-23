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

import com.customer.captemplateproject.batch.ExcelItemReader;
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

    private final JobLauncher asyncJobLauncher;
    private final Job excelUploadJob;
    private final ExcelItemReader excelItemReader;

    public PurchaseOrderServiceHandler(
            @Qualifier("asyncJobLauncher") JobLauncher asyncJobLauncher,
            Job excelUploadJob,
            ExcelItemReader excelItemReader) {
        this.asyncJobLauncher = asyncJobLauncher;
        this.excelUploadJob = excelUploadJob;
        this.excelItemReader = excelItemReader;
    }

    @On(entity = Upload_.CDS_NAME, event = CqnService.EVENT_READ)
    public Upload getUploadSingleton() {
        return Upload.create();
    }

    @On
    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleExcelUpload(CdsUpdateEventContext context, Upload upload) {
        InputStream inputStream = upload.getPurchaseOrderItems();

        if (inputStream != null) {
            try {
                // Set the input stream in the reader
                excelItemReader.setInputStream(inputStream);

                // Create job parameters
                Map<String, JobParameter<?>> paramMap = new HashMap<>();
                paramMap.put("timestamp", new JobParameter<>(System.currentTimeMillis(), Long.class));
                paramMap.put("fileName", new JobParameter<>("purchase-orders.xlsx", String.class));

                // Launch Transactionalthe batch job
                JobExecution jobExecution = asyncJobLauncher.run(excelUploadJob, new JobParameters(paramMap));

                // You can log the job execution ID or store it for tracking
                System.out.println("Batch job started with execution ID: " + jobExecution.getId());

            } catch (Exception e) {
                throw new RuntimeException("Failed to start batch job for Excel processing", e);
            }
        }

        context.setResult(Arrays.asList(upload));
    }
}