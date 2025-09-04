package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_transport")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "organizationId")
    private Integer organizationId;

    @Column(name = "subOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderHeadId", referencedColumnName = "id")
    private PurchaseOrderHead purchaseOrderHead;

    @ManyToOne
    @JoinColumn(name = "asnHeadId", referencedColumnName = "id")
    private ASNHead asnHead;

    @Column(name = "shipMode", length = 30)
    private String shipMode;

    @Column(name = "shipThrough", length = 30)
    private String shipThrough;

    @Column(name = "transporter", length = 30)
    private String transporter;

    @Column(name = "contactPerson", length = 30)
    private String contactPerson;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "landline")
    private String landline;

    @Column(name = "lorryReceipt")
    private Integer lorryReceipt;

    @Column(name = "vehicleType", length = 30)
    private String vehicleType;

    @Column(name = "vehicleNumber", length = 30)
    private String vehicleNumber;

    @Column(name = "vehicleWeight")
    private Double vehicleWeight;

    @Column(name = "pucCertificateNumber")
    private Integer pucCertificateNumber;

    @Column(name = "pucCertificateValidTill")
    private Date pucCertificateValidTill;

    @Column(name = "pucCenter", length = 30)
    private String pucCenter;

    @Column(name = "pucCenterId", length = 30)
    private String pucCenterId;

    @Column(name = "driver", length = 30)
    private String driver;

    @Column(name = "licenseNumber", length = 30)
    private String licenseNumber;

    @Column(name = "licenseType", length = 30)
    private String licenseType;

    @Column(name = "licenseNumberValidTill")
    private Date licenseNumberValidTill;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "ModifiedOn")
    private Date modifiedOn;
}
