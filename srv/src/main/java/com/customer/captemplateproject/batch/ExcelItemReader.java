package com.customer.captemplateproject.batch;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;

@Component
public class ExcelItemReader implements ItemReader<Map<Integer, String>> {

    private Iterator<Map<Integer, String>> dataIterator;

    public void setInputStream(InputStream inputStream) {
        List<Map<Integer, String>> data = ListUtils.newArrayList();

        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
            @Override
            public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                data.add(rowData);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // Analysis completed
            }
        }).sheet().doRead();

        this.dataIterator = data.iterator();
    }

    @Override
    public Map<Integer, String> read() {
        if (dataIterator != null && dataIterator.hasNext()) {
            return dataIterator.next();
        }
        return null; // End of data
    }
}