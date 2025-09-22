sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'purchaseordermanagement/test/integration/FirstJourney',
		'purchaseordermanagement/test/integration/pages/PurchaseOrdersList',
		'purchaseordermanagement/test/integration/pages/PurchaseOrdersObjectPage',
		'purchaseordermanagement/test/integration/pages/PurchaseOrderItemsObjectPage'
    ],
    function(JourneyRunner, opaJourney, PurchaseOrdersList, PurchaseOrdersObjectPage, PurchaseOrderItemsObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('purchaseordermanagement') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onThePurchaseOrdersList: PurchaseOrdersList,
					onThePurchaseOrdersObjectPage: PurchaseOrdersObjectPage,
					onThePurchaseOrderItemsObjectPage: PurchaseOrderItemsObjectPage
                }
            },
            opaJourney.run
        );
    }
);