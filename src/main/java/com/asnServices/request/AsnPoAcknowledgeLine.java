package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AsnPoAcknowledgeLine {
    private Integer poLineId;
    private Integer asnLineId;
    private Integer containerNumber;
    private  Integer statusId;
}