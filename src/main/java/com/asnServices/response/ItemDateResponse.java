package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDateResponse {
   Integer ppeHeadId;
   Date requiredDate;
   Integer remainingDays;
   Integer highestLeadTime;
   Integer shortestLeadTime;
}
