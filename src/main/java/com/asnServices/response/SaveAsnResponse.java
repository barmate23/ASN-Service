package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAsnResponse {
private Integer id;
private String asnNumber;
private String supplierCode;
private String supplierName;
private String purchaseOrder;
private String purchaseOrderDate;
}
