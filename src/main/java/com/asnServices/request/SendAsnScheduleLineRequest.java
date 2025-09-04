package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendAsnScheduleLineRequest {
    private String requiredDate;
    private String requiredTime;
    private Integer requiredQuantity;
    private Integer ppeHeadId;
    private Integer supplierId;
    private Integer purchaseOrderHeadId;
}