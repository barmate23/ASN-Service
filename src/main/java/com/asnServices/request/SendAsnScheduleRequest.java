package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendAsnScheduleRequest {
    private String scheduleId;
    private String scheduleMonth;
    private String itemName;
    private String itemCode;
    private Integer year;
    private Integer totalQuantity;
    List<SendAsnScheduleLineRequest> sendAsnScheduleLineRequestList;
}