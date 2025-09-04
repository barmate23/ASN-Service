package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ASNPOSupplierResponse {
    Integer supplierId;
    Integer poHeadId;
    String supplierCode;
    String supplierName;
    String purchaseOrderNumber;
    Integer purchaseOrderQuantity;
    Integer balanceQuantity;

}
