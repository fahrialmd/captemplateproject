package com.customer.captemplateproject.service;

import java.util.List;
import java.util.Map;

import cds.gen.purchaseorderservice.Upload;

public interface UploadService {

    public void processExcelBatchInput(Upload upload);

    public void processPurchaseOrderData(List<Map<Integer, String>> uploadData);
}
