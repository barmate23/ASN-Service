package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerialBatchNumberRequest {
    private String serialBatchNumber;
    private String manufacturingDate;
    private String expiryDate;

}
