package com.customer.captemplateproject.config;

import javax.sql.DataSource;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchDataSourceConfiguration {

    @Bean
    public JobRepository jobRepository(
            @Qualifier("ds-db") DataSource dataSource,
            @Qualifier("tx-db") PlatformTransactionManager transactionManager) throws Exception {

        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setTablePrefix("BATCH_"); // This matches your HANA table prefix

        // Optional: Set isolation level if needed
        // factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");

        factory.afterPropertiesSet();
        return factory.getObject();
    }
}