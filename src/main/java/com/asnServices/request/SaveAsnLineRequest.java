package com.asnServices.request;

import lombok.Data;

@Data
public class SaveAsnLineRequest {
    private Integer poLineId;
    private Integer shipQty;
    private String packing;
    private Integer noOfContainer;
    private Integer statusId;
    private Integer reasonId;
}
