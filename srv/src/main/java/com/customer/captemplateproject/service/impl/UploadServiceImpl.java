package com.customer.captemplateproject.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.customer.captemplateproject.batch.BatchInputReader;
import com.customer.captemplateproject.exception.BusinessException;
import com.customer.captemplateproject.service.UploadService;

import cds.gen.purchaseorderservice.PurchaseOrders;
import cds.gen.purchaseorderservice.Upload;
import cds.gen.purchaseorderservice.PurchaseOrderItems;

@Service
public class UploadServiceImpl implements UploadService {

    private final GenericCqnService genericCqnService;
    private final JobLauncher asyncJobLauncher;
    private final Job excelUploadJob;
    private final BatchInputReader batchInputReader;

    public UploadServiceImpl(
            GenericCqnService genericCqnService,
            @Qualifier("asyncJobLauncher") JobLauncher asyncJobLauncher,
            Job excelUploadJob,
            BatchInputReader batchInputReader) {
        this.genericCqnService = genericCqnService;
        this.asyncJobLauncher = asyncJobLauncher;
        this.excelUploadJob = excelUploadJob;
        this.batchInputReader = batchInputReader;
    }

    @Override
    public void processExcelBatchInput(Upload upload) {
        InputStream inputStream = upload.getPurchaseOrderData();

        if (inputStream != null) {
            try {
                // Set the input stream in the reader
                batchInputReader.setInputStream(inputStream);

                // Create job parameters
                Map<String, JobParameter<?>> paramMap = new HashMap<>();
                paramMap.put("timestamp", new JobParameter<>(System.currentTimeMillis(), Long.class));
                paramMap.put("fileName", new JobParameter<>("purchase-orders.xlsx", String.class));

                // Launch the batch job
                JobExecution jobExecution = asyncJobLauncher.run(excelUploadJob, new JobParameters(paramMap));

                // You can log the job execution ID or store it for tracking
                System.out.println("Batch job started with execution ID: " + jobExecution.getId());

            } catch (Exception e) {
                throw new BusinessException("Failed to start batch job for Excel processing", e);
            }
        }
    }

    @Override
    public void processPurchaseOrderData(List<Map<Integer, String>> uploadData) {
        try {
            // Group rows by poNumber to separate headers from items
            Map<String, List<Map<Integer, String>>> groupedByPoNumber = uploadData.stream()
                    .collect(Collectors.groupingBy(row -> row.get(0)));
            // Check if poNumber is already exist
            String currentPONumber = "";
            // Loop the data
            for (Map.Entry<String, List<Map<Integer, String>>> entry : groupedByPoNumber.entrySet()) {
                // Get PO header number
                String poNumber = entry.getKey();
                // Get entire row data
                List<Map<Integer, String>> rows = entry.getValue();
                // Get header data from row
                Map<Integer, String> header = rows.get(0);
                // Create purchaseOrderData variable
                PurchaseOrders purchaseOrderData = PurchaseOrders.create();
                // Only fetch purchase order data when processing a different PO number
                if (currentPONumber.isEmpty() || !currentPONumber.equals(poNumber)) {
                    currentPONumber = poNumber;
                    purchaseOrderData = genericCqnService
                            .checkAndGetPurchaseOrderById(currentPONumber);
                }
                // Create new PO header data if there is no existing PO data
                Boolean isHeaderEmpty = purchaseOrderData.isEmpty();
                if (isHeaderEmpty) {
                    purchaseOrderData.setPoNumber(header.get(0));
                    purchaseOrderData.setPoType(header.get(1));
                    purchaseOrderData.setVendorId(header.get(2));
                    purchaseOrderData.setCompanyCode(header.get(3));
                    purchaseOrderData.setPlant(header.get(4));
                    purchaseOrderData.setDocumentDate(LocalDate.parse(header.get(5)));
                    purchaseOrderData.setDeliveryDate(LocalDate.parse(header.get(6)));
                    purchaseOrderData.setCurrencyCode(header.get(7));
                    purchaseOrderData.setTotalAmount(convertToBigDecimal(header.get(8)));
                    purchaseOrderData.setDeliveryStatusCode(header.get(9));
                }
                // Create new PO item data
                List<PurchaseOrderItems> purchaseOrderItemData = new ArrayList<>();
                for (Map<Integer, String> row : rows) {
                    PurchaseOrderItems item = PurchaseOrderItems.create();
                    item.setHeaderId(poNumber);
                    item.setItemNumber(row.get(10));
                    item.setMaterialId(row.get(11));
                    item.setDescription(row.get(12));
                    item.setQuantity(convertToBigDecimal(row.get(13)));
                    item.setUnit(row.get(14));
                    item.setNetPrice(convertToBigDecimal(row.get(15)));
                    item.setNetAmount(convertToBigDecimal(row.get(16)));
                    item.setDeliveryDate(LocalDate.parse(row.get(17)));
                    item.setPlant(row.get(18));
                    purchaseOrderItemData.add(item);
                }
                purchaseOrderData.setItems(purchaseOrderItemData);
                // Insert the row data into header entity or just update the item
                if (isHeaderEmpty) {
                    genericCqnService.insertPurchaseOrderData(purchaseOrderData);
                } else {
                    genericCqnService.insertPurchaseOrderItem(purchaseOrderData.getItems());
                }
            }
        } catch (Exception e) {
            throw new BusinessException("Failed to upload purchase order data: " + e.getMessage(), e);
        }
    }

    private BigDecimal convertToBigDecimal(String input) {
        BigDecimal output = new BigDecimal(input);
        return output;
    }
}
