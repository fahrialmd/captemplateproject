package com.customer.captemplateproject.service;

import java.util.List;
import java.util.Map;

public interface UploadService {

    public void uploadPurchaseOrderData(List<Map<Integer, String>> uploadData);

}
