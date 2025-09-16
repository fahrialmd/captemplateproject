using {com.customer.captemplateproject as po} from '../db/schema';

service PurchaseOrderService {
    @odata.draft.enabled
    entity PurchaseOrders     as projection on po.PurchaseOrderHeader;

    @odata.draft.enabled
    entity PurchaseOrderItems as projection on po.PurchaseOrderItem;

    entity Vendors            as projection on po.Vendor;

    entity Materials          as projection on po.Material;
}
