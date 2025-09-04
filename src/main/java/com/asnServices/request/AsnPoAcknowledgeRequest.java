package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AsnPoAcknowledgeRequest {
    private Integer poHeadId;
    private Integer asnHeadId;
    private Integer status;
    private List<AsnPoAcknowledgeLine> asnPoAcknowledgeLines;
}