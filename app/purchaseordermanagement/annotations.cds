using PurchaseOrderService as service from '../../srv/purchase-order-service';

annotate service.PurchaseOrders with @(
    UI.LineItem           : [
        {
            $Type: 'UI.DataField',
            Value: poNumber,
            Label: 'poNumber',
        },
        {
            $Type: 'UI.DataField',
            Value: poType,
            Label: 'poType',
        },
        {
            $Type: 'UI.DataField',
            Value: vendor.vendorNumber,
            Label: 'vendorNumber',
        },
        {
            $Type: 'UI.DataField',
            Value: companyCode,
            Label: 'companyCode',
        },
        {
            $Type: 'UI.DataField',
            Value: plant,
            Label: 'plant',
        },
        {
            $Type: 'UI.DataField',
            Value: documentDate,
            Label: 'documentDate',
        },
        {
            $Type: 'UI.DataField',
            Value: deliveryDate,
            Label: 'deliveryDate',
        },
        {
            $Type: 'UI.DataField',
            Value: totalAmount,
            Label: 'totalAmount',
        },
        {
            $Type: 'UI.DataField',
            Value: currency_code,
        },
        {
            $Type: 'UI.DataField',
            Value: deliveryStatus_code,
            Label: 'deliveryStatus_code',
        },
    ],
    UI.Facets             : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Details',
            ID    : 'Details',
            Target: '@UI.FieldGroup#Details',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'PO Items',
            ID    : 'POItems',
            Target: 'items/@UI.LineItem#POItems',
        },
    ],
    UI.FieldGroup #Details: {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: ID,
                Label: 'ID',
            },
            {
                $Type: 'UI.DataField',
                Value: poNumber,
                Label: 'poNumber',
            },
            {
                $Type: 'UI.DataField',
                Value: poType,
                Label: 'poType',
            },
            {
                $Type: 'UI.DataField',
                Value: vendor_ID,
                Label: 'vendorNumber',
            },
            {
                $Type: 'UI.DataField',
                Value: companyCode,
                Label: 'companyCode',
            },
            {
                $Type: 'UI.DataField',
                Value: plant,
                Label: 'plant',
            },
            {
                $Type: 'UI.DataField',
                Value: documentDate,
                Label: 'documentDate',
            },
            {
                $Type: 'UI.DataField',
                Value: deliveryDate,
                Label: 'deliveryDate',
            },
            {
                $Type: 'UI.DataField',
                Value: totalAmount,
                Label: 'totalAmount',
            },
            {
                $Type: 'UI.DataField',
                Value: currency_code,
            },
            {
                $Type: 'UI.DataField',
                Value: deliveryStatus_code,
                Label: 'deliveryStatus_code',
            },
            {
                $Type: 'UI.DataField',
                Value: createdAt,
            },
            {
                $Type: 'UI.DataField',
                Value: createdBy,
            },
            {
                $Type: 'UI.DataField',
                Value: modifiedAt,
            },
            {
                $Type: 'UI.DataField',
                Value: modifiedBy,
            },
        ],
    },
);

annotate service.PurchaseOrderItems with @(
    UI.LineItem #POItems      : [
        {
            $Type: 'UI.DataField',
            Value: itemNumber,
            Label: 'itemNumber',
        },
        {
            $Type: 'UI.DataField',
            Value: material.materialNumber,
            Label: 'materialNumber',
        },
        {
            $Type: 'UI.DataField',
            Value: description,
            Label: 'description',
        },
        {
            $Type: 'UI.DataField',
            Value: quantity,
            Label: 'quantity',
        },
        {
            $Type: 'UI.DataField',
            Value: unit,
            Label: 'unit',
        },
        {
            $Type: 'UI.DataField',
            Value: netPrice,
            Label: 'netPrice',
        },
        {
            $Type: 'UI.DataField',
            Value: netAmount,
            Label: 'netAmount',
        },
        {
            $Type: 'UI.DataField',
            Value: deliveryDate,
            Label: 'deliveryDate',
        },
        {
            $Type: 'UI.DataField',
            Value: plant,
            Label: 'plant',
        },
    ],
    UI.Facets                 : [{
        $Type : 'UI.ReferenceFacet',
        Label : 'Item Details',
        ID    : 'ItemDetails',
        Target: '@UI.FieldGroup#ItemDetails',
    }, ],
    UI.FieldGroup #ItemDetails: {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: itemNumber,
                Label: 'itemNumber',
            },
            {
                $Type: 'UI.DataField',
                Value: material_ID,
                Label: 'material_ID',
            },
            {
                $Type: 'UI.DataField',
                Value: description,
                Label: 'description',
            },
            {
                $Type: 'UI.DataField',
                Value: quantity,
                Label: 'quantity',
            },
            {
                $Type: 'UI.DataField',
                Value: unit,
                Label: 'unit',
            },
            {
                $Type: 'UI.DataField',
                Value: netPrice,
                Label: 'netPrice',
            },
            {
                $Type: 'UI.DataField',
                Value: netAmount,
                Label: 'netAmount',
            },
            {
                $Type: 'UI.DataField',
                Value: deliveryDate,
                Label: 'deliveryDate',
            },
            {
                $Type: 'UI.DataField',
                Value: header.plant,
                Label: 'plant',
            },
        ],
    },
);


// Vendor Value Help for PurchaseOrders
annotate service.PurchaseOrderItems with {
    material @(
        Common.ValueList               : {
            $Type         : 'Common.ValueListType',
            CollectionPath: 'Materials',
            Parameters    : [
                {
                    $Type            : 'Common.ValueListParameterInOut',
                    LocalDataProperty: material_ID,
                    ValueListProperty: 'ID',
                },
                {
                    $Type            : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty: 'materialNumber',
                },
                {
                    $Type            : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty: 'description',
                },
            ],
        },
        Common.ValueListWithFixedValues: false,
        Common.Text                    : material.materialNumber,
        Common.TextArrangement         : #TextFirst
    );
};

// Vendor Value Help for PurchaseOrders
annotate service.PurchaseOrders with {
    vendor @(
        Common.ValueList               : {
            $Type         : 'Common.ValueListType',
            CollectionPath: 'Vendors',
            Parameters    : [
                {
                    $Type            : 'Common.ValueListParameterInOut',
                    LocalDataProperty: vendor_ID,
                    ValueListProperty: 'ID',
                },
                {
                    $Type            : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty: 'vendorNumber',
                },
                {
                    $Type            : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty: 'name',
                },
            ],
        },
        Common.ValueListWithFixedValues: false,
        Common.Text                    : vendor.name,
        Common.TextArrangement         : #TextFirst
    );
};
