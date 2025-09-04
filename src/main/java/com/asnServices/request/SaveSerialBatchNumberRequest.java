package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveSerialBatchNumberRequest {
private Integer poLineId;
private Integer asnLineId;
List<SerialBatchNumberRequest> serialBatchNumberList;
}
