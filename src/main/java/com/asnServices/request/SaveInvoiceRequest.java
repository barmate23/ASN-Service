package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveInvoiceRequest {
    private Integer id;
    private Integer asnHeadId;
    private Integer poHeadId;
    private Integer invoiceNumber;
    private String date;
    private Integer invoiceDocumentId;
    private List<SaveInvoiceLineRequest> saveInvoiceLineRequest;

}
