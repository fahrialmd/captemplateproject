package com.customer.captemplateproject.config;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.customer.captemplateproject.batch.ExcelItemReader;
import com.customer.captemplateproject.batch.PurchaseOrderItemProcessor;
import com.customer.captemplateproject.batch.PurchaseOrderItemWriter;

@Configuration
public class BatchConfiguration {

    @Bean
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Step excelProcessingStep(
            JobRepository jobRepository,
            @Qualifier("tx-db") PlatformTransactionManager transactionManager,
            ItemReader<Map<Integer, String>> reader,
            ItemProcessor<Map<Integer, String>, Map<Integer, String>> processor,
            ItemWriter<Map<Integer, String>> writer) {

        return new StepBuilder("excelProcessingStep", jobRepository)
                .<Map<Integer, String>, Map<Integer, String>>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job excelUploadJob(JobRepository jobRepository, Step excelProcessingStep) {
        return new JobBuilder("excelUploadJob", jobRepository)
                .start(excelProcessingStep)
                .build();
    }
}