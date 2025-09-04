package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveAsnRequest {
    private Integer poHeadId;
    private Integer ppeHeadId;
    private Integer supplierId;
    private Integer itemId;
    private Integer itemScheduleSupplierId;
    private Integer allocatedQty;
    private String requiredOnDate;
    private String deliveryDate;
    private String deliveryTime;
}
