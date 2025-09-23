package com.customer.captemplateproject.Upload;

import java.util.List;
import java.util.Map;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.customer.captemplateproject.service.UploadService;

public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    private final UploadService uploadService;
    private static final int BATCH_COUNT = 5;
    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    // Constructor
    public NoModelDataListener(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            uploadService.uploadPurchaseOrderData(cachedDataList);
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        uploadService.uploadPurchaseOrderData(cachedDataList);
    }

}