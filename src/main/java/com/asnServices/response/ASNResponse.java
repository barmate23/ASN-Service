package com.asnServices.response;

import com.asnServices.model.Organization;
import com.asnServices.model.PurchaseOrderHead;
import com.asnServices.model.PurchaseOrderLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ASNResponse {

    private List<PurchaseOrderLine> lineList;
    private PurchaseOrderHead purchaseOrderHead;
    private Organization organization;
}
