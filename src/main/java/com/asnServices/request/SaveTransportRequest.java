package com.asnServices.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveTransportRequest {
    private Integer asnHeadId;
    private Integer poHeadId;
    private String transporterName;
    private String shipMode;
    private String shipThrough;
    private String contactName;
    private String mobileNo;
    private String landlineNo;
    private Integer lorryReceiptNumber;
    private String dispatchDate;
    private String dispatchTime;
    private String expectedArrivalDate;
    private String expectedArrivalTime;
    private String vehicleType;
    private String vehicleNumber;
    private Double vehicleWeight;
    private Integer pucCertificateNumber;
    private String pucCertificateNumberValidTill;
    private String pucCenter;
    private String pucCenterId;
    private String driver;
    private String licenceNumber;
    private String licenceNumberValidTill;
    private String licenceType;
    private String licenceTypeDate;
}
