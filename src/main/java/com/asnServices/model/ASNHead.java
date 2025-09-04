package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_ASNHead")
public class ASNHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "deliveryDate")
    private Date deliveryDate;

    @Column(name = "requiredOnDate")
    private Date requiredOnDate;

    @Column(name = "deliveryTime")
    private LocalTime deliveryTime;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderHeadId", referencedColumnName = "id")
    private PurchaseOrderHead purchaseOrderHead;

    @Column(name = "AsnNumber")
    private String asnNumber;

    @ManyToOne
    @JoinColumn(name = "supplierId", referencedColumnName = "id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "PurchaseStatusId")
    private PurchaseStatus purchaseStatus;

    //add new column
    @ManyToOne
    private Reason reason;

    //add new column
    @Column(name = "ReasonDocumentId")
    private Integer reasonDocumentId;

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

    @Transient
    private Integer accepted;

    @Transient
    private Integer rejected;
}
