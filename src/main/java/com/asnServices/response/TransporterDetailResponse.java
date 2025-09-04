package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransporterDetailResponse {

    private Integer transporterId;

    private String shipMode;

    private String shipThrough;


    private String contactName;

    private String mobileNo;

    private String landlineNo;

    private String lrNumber;

    private Date dispatchDate;

    private LocalTime dispatchTime;

    private Date expectedArrivalDate;

    private LocalTime expectedArrivalTime;

    private String vehicleType;

    private String vehicleNumber;

    private Double vehicleWeight;








    private String pucCertificateNumber;

    private String licenceNumber;

    private Date licenceNumberValidTill;

    private String licenceType;

    private Date licenceTypeValidTill;

}
