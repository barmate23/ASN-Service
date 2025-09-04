package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsnSupplierItemResponse {
    Integer asnHeadId;
    Integer poHeadId;
    String itemRequiredOnDate;
    String asnNumber;
    String purchaseOrderAsn;
    Integer remainingDays;
    Integer leadTime;
}
