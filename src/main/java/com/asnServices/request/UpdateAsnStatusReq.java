package com.asnServices.request;

import lombok.Data;

@Data
public class UpdateAsnStatusReq {
    Integer asnId;
    Integer poId;
    Integer reasonId;
    Integer reasonDocumentId;
    String status;
    String urlLink;
}
