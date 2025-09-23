package com.customer.captemplateproject.service;

import java.util.List;
import java.util.Map;

public interface UploadService {

    void uploadPurchaseOrderData(List<Map<Integer, String>> uploadData);

    void processPurchaseOrderWithItems(Map<String, Object> headerData, List<Map<String, Object>> itemsData);

}
