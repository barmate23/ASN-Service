package com.asnServices.response;

import com.asnServices.request.SerialBatchNumberRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialBatchValidResponse {
   private List<SerialBatchNumberRequest> invalidSerialBatchNumberList;
   private Integer totalSerialBatchNumbers;

}
