package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_insurance")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderHeadId", referencedColumnName = "PurchaseOrderID")
    private PurchaseOrderHead purchaseOrderHead;

    @ManyToOne
    @JoinColumn(name = "asnHeadId", referencedColumnName = "id")
    private ASNHead asnHead;

    @Column(name = "transitInsurance", length = 30)
    private String transitInsurance;

    @Column(name = "insuredWith", length = 30)
    private String insuredWith;

    @Column(name = "policyNumber", length = 30)
    private String policyNumber;

    @Column(name = "issuingOfficeId", length = 30)
    private String issuingOfficeId;

    @Column(name = "issuingOfficeAddress", length = 30)
    private String issuingOfficeAddress;

    @Column(name = "contactPerson", length = 30)
    private String contactPerson;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "landline")
    private String landline;

    @Column(name = "validTill")
    private Date validTill;

    @Column(name = "sumAssured")
    private Integer sumAssured;

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
