package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveInsuranceRequest {
    private Integer id;
    private Integer asnHeadId;
    private Integer poHeadId;
    private String transitInsuranceName;
    private String insuredWith;
    private String policyNumber;
    private String validTill;
    private Integer sumAssured;
    private String issuingOfficeId;
    private String issuingOfficeAddress;
    private String contactPerson;
    private String mobile;
    private String landline;

}
