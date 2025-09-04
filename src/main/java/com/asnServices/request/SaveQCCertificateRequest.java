package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveQCCertificateRequest {
    Integer poLineId;
    Integer asnLineId;
    Integer itemId;
    Integer qcCertificateId;
    Integer qcNumber;
    String qcDate;
}
