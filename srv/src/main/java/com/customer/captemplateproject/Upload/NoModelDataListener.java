package com.customer.captemplateproject.Upload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.sap.cds.ql.Upsert;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.com.customer.captemplateproject.PurchaseOrderItem;

import static cds.gen.purchaseorderservice.PurchaseOrderService_.PURCHASE_ORDER_ITEMS;

public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {

    private final PersistenceService db;
    private static final int BATCH_COUNT = 5;
    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    // Constructor
    public NoModelDataListener(PersistenceService db) {
        this.db = db;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    private void saveData() {
        for (Map<Integer, String> map : cachedDataList) {
            PurchaseOrderItem poitem = PurchaseOrderItem.create();

            for (Integer key : map.keySet()) {
                switch (key) {
                    case 0:
                        poitem.setHeaderId(map.get(key));
                        break;
                    case 1:
                        poitem.setItemNumber(map.get(key));
                        break;
                    case 2:
                        poitem.setMaterialId(map.get(key));
                        break;
                    case 3:
                        poitem.setDescription(map.get(key));
                        break;
                    case 4:
                        poitem.setQuantity(convertToBigDecimal(map.get(key)));
                        break;
                    case 5:
                        poitem.setUnit(map.get(key));
                        break;
                    case 6:
                        poitem.setNetPrice(convertToBigDecimal(map.get(key)));
                        break;
                    case 7:
                        poitem.setNetAmount(convertToBigDecimal(map.get(key)));
                        break;
                    case 8:
                        poitem.setDeliveryDate(LocalDate.parse(map.get(key)));
                        break;
                    case 9:
                        poitem.setNetAmount(convertToBigDecimal(map.get(key)));
                        break;
                    case 10:
                        poitem.setPlant(map.get(key));
                        break;
                }
            }
            db.run(Upsert.into(PURCHASE_ORDER_ITEMS).entry(poitem));
        }
    }

    private BigDecimal convertToBigDecimal(String input) {
        BigDecimal output = new BigDecimal(input);
        return output;
    }
}