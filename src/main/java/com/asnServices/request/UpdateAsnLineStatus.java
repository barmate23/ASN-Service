package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateAsnLineStatus {
    private Integer asnLineId;
    private Integer poLineId;
    private Integer reasonId;
    private Integer reasonFileId;
    private String status;
}
