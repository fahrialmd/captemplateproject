namespace com.customer.captemplateproject;

using {
                           Currency,
                           managed,
                           cuid,
    sap.common.CodeList as CodeList
} from '@sap/cds/common';

entity PurchaseOrderHeader : managed, cuid {
    poNumber       : String(20);
    poType         : String(4);
    vendor         : Association to Vendor;
    companyCode    : String(4);
    plant          : String(4);
    documentDate   : Date;
    deliveryDate   : Date;
    currency       : Currency;
    totalAmount    : Decimal(15, 2) @Semantics.amount.currencyCode: 'currency';
    deliveryStatus : Association to DeliveryStatus;
    items          : Composition of many PurchaseOrderItem
                         on items.header = $self;
}

entity PurchaseOrderItem : managed {
    key header       : Association to PurchaseOrderHeader;
    key itemNumber   : String(5)      @mandatory;
        material     : Association to Material;
        description  : String(80);
        quantity     : Decimal(13, 3);
        unit         : String(3);
        netPrice     : Decimal(11, 2) @Semantics.amount.currencyCode: 'header.currency  ';
        netAmount    : Decimal(13, 2) @Semantics.amount.currencyCode: 'header.currency';
        deliveryDate : Date;
        plant        : String(4);
}

entity Vendor : managed, cuid {
    vendorNumber   : String(10);
    name           : String(80);
    currency       : Currency;
    purchaseOrders : Association to many PurchaseOrderHeader
                         on purchaseOrders.vendor = $self;
}

entity Material : managed, cuid {
    materialNumber : String(40);
    description    : String(80);
    baseUnit       : String(3);
    currency       : Currency;
    standardPrice  : Decimal(11, 2) @Semantics.amount.currencyCode: 'currency';
    poItems        : Association to many PurchaseOrderItem
                         on poItems.material = $self;
}

entity DeliveryStatus : CodeList {
    key code : String(1) enum {
            Pending = 'P';
            Shipped = 'S';
            Delivered = 'D';
        } default 'P';
}

@cds.persistence.skip
entity Upload @odata.singleton {
    PurchaseOrderData : LargeBinary @Core.MediaType: 'application/octet-stream';
}

@cds.persistence.skip
entity job_instance {
    key JOB_INSTANCE_ID : Int64;
        VERSION         : Int64;
        JOB_NAME        : String(100);
        JOB_KEY         : String(36);
        to_Executions   : Association to many job_execution
                              on JOB_INSTANCE_ID = to_Executions.JOB_INSTANCE_ID;
}

@cds.persistence.skip
entity job_execution {
    key JOB_EXECUTION_ID  : Int64;
        VERSION           : Int64;
        JOB_INSTANCE_ID   : Int64;
        CREATE_TIME       : Timestamp;
        START_TIME        : Timestamp;
        END_TIME          : Timestamp;
        STATUS            : String(10);
        EXIT_CODE         : String(2500);
        EXIT_MESSAGE      : String(2500);
        LAST_UPDATED      : Timestamp;

        to_Instance       : Association to one job_instance
                                on JOB_INSTANCE_ID = to_Instance.JOB_INSTANCE_ID;

        to_Context        : Association to one job_execution_context
                                on JOB_EXECUTION_ID = to_Context.JOB_EXECUTION_ID;

        to_Params         : Association to many job_execution_params
                                on JOB_EXECUTION_ID = to_Params.JOB_EXECUTION_ID;

        to_StepExecutions : Association to many step_execution
                                on JOB_EXECUTION_ID = to_StepExecutions.JOB_EXECUTION_ID;
}

@cds.persistence.skip
entity job_execution_context {
    key JOB_EXECUTION_ID   : Integer64;
        SHORT_CONTEXT      : String(2500);
        SERIALIZED_CONTEXT : LargeString;

        to_Executions      : Association to job_execution
                                 on to_Executions.JOB_EXECUTION_ID = JOB_EXECUTION_ID;
}

@cds.persistence.skip
entity job_execution_params {
    key JOB_EXECUTION_ID : Integer64;
    key PARAMETER_NAME   : String(100);
        PARAMETER_TYPE   : String(100);
        PARAMETER_VALUE  : String(2500);
        IDENTIFYING      : String(1);

        to_Executions    : Association to job_execution
                               on to_Executions.JOB_EXECUTION_ID = JOB_EXECUTION_ID;
}

@cds.persistence.skip
entity step_execution {
    key STEP_EXECUTION_ID       : Integer64;
        VERSION                 : Integer64;
        STEP_NAME               : String(100);
        JOB_EXECUTION_ID        : Integer64;
        CREATE_TIME             : Timestamp;
        START_TIME              : Timestamp;
        END_TIME                : Timestamp;
        STATUS                  : String(10);
        COMMIT_COUNT            : Integer64;
        READ_COUNT              : Integer64;
        FILTER_COUNT            : Integer64;
        WRITE_COUNT             : Integer64;
        READ_SKIP_COUNT         : Integer64;
        WRITE_SKIP_COUNT        : Integer64;
        PROCESS_SKIP_COUNT      : Integer64;
        ROLLBACK_COUNT          : Integer64;
        EXIT_CODE               : String(2500);
        EXIT_MESSAGE            : String(2500);
        LAST_UPDATED            : Timestamp;

        to_Executions           : Association to job_execution
                                      on to_Executions.JOB_EXECUTION_ID = JOB_EXECUTION_ID;
        to_stepExecutionContext : Association to step_execution_context
                                      on to_stepExecutionContext.STEP_EXECUTION_ID = STEP_EXECUTION_ID;
}

@cds.persistence.skip
entity step_execution_context {
    key STEP_EXECUTION_ID  : Integer64;
        SHORT_CONTEXT      : String(2500);
        SERIALIZED_CONTEXT : LargeString;

        to_stepExecution   : Association to step_execution
                                 on to_stepExecution.STEP_EXECUTION_ID = STEP_EXECUTION_ID;
}
