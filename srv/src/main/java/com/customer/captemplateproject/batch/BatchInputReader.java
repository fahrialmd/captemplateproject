package com.customer.captemplateproject.batch;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;

@Component
public class BatchInputReader implements ItemReader<Map<Integer, String>> {

    // Create iterator variable
    private Iterator<Map<Integer, String>> dataIterator;

    // Method to receives the uploaded Excel file as a stream of bytes
    public void setInputStream(InputStream inputStream) {
        // Create empty list to store all Excel rows
        List<Map<Integer, String>> data = ListUtils.newArrayList();
        // Read excel file using EasyExcel library
        readExcelFile(inputStream, data);
        // Create iterator
        this.dataIterator = data.iterator();
    }

    // Method for spring batch's "read"
    @Override
    public Map<Integer, String> read() {
        // Check if more row available next
        if (dataIterator != null && dataIterator.hasNext()) {
            // return next row
            return dataIterator.next();
        }
        // Return null as a sign of the end of data
        return null;
    }

    // Method to read excel file using EasyExcel
    private void readExcelFile(InputStream inputStream, List<Map<Integer, String>> data) {
        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
            // Method called by EasyExcel for every row in the Excel file
            @Override
            public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                // Append excel row into "data" variable
                data.add(rowData);
            }

            // Method called once when EasyExcel finishes reading the entire file
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // Do nothing for now
            }
        }).sheet().doRead();
    }
}