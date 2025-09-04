package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveDocumentRequest {
    private Integer id;
    private Integer asnHeadId;
    private Integer poHeadId;
    private String documentName;
    private Integer documentId;
    private String documentDate;

}
