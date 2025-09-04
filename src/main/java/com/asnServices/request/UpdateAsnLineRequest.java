package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAsnLineRequest {
    private Integer id;
    private Integer asnHead;
    private Integer poLine;
    private String scheduleDate;
    private String scheduleTime;
    private Integer scheduleQty;
    private Integer statusId;
}
