namespace com.customer.captemplateproject;

using {
    Currency,
    managed,
    cuid
} from '@sap/cds/common';

entity PurchaseOrderHeader : managed, cuid {
    poNumber     : String(20);
    poType       : String(4);
    vendor       : Association to Vendor;
    vendorNumber : String(10);
    companyCode  : String(4);
    plant        : String(4);
    documentDate : Date;
    deliveryDate : Date;
    currency     : Currency;
    totalAmount  : Decimal(15, 2) @Semantics.amount.currencyCode: 'currency';
    status       : String(2);
    items        : Composition of many PurchaseOrderItem
                       on items.header = $self;
}

entity PurchaseOrderItem : managed, cuid {
    header         : Association to PurchaseOrderHeader;
    itemNumber     : String(5);
    material       : Association to Material;
    materialNumber : String(40);
    description    : String(80);
    quantity       : Decimal(13, 3);
    unit           : String(3);
    netPrice       : Decimal(11, 2) @Semantics.amount.currencyCode: 'header.currency';
    netAmount      : Decimal(13, 2) @Semantics.amount.currencyCode: 'header.currency';
    deliveryDate   : Date;
    plant          : String(4);
    status         : String(2);
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
