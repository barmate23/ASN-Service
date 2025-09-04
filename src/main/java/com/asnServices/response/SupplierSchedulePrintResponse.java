package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSchedulePrintResponse {
   private Date requiredDate;
   private String itemCode;
   private String itemName;
   private Integer requiredQuantity;
   private String month;

}
