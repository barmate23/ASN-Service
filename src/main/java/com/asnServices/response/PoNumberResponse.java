package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PoNumberResponse {
    String poNumber;
    Integer poId;
}
