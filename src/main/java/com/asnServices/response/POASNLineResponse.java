package com.asnServices.response;

import com.asnServices.model.ASNHead;
import com.asnServices.model.ASNLine;
import com.asnServices.model.PurchaseOrderHead;
import com.asnServices.model.PurchaseOrderLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POASNLineResponse {
    private ASNHead asnHead;
    private List<ASNLine> asnLineList;
    private PurchaseOrderHead purchaseOrderHead;
    private List<PurchaseOrderLine> purchaseOrderLineList;
    private String buyer;
    private String contactNumber;
    private String organization;
    private String address;
}
