package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_invoiceHead")
public class InvoiceHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderHeadId", referencedColumnName = "id")
    private PurchaseOrderHead purchaseOrderHead;

    @ManyToOne
    @JoinColumn(name = "asnHeadId", referencedColumnName = "id")
    private ASNHead asnHead;

    @Column(name = "invoiceNumber")
    private Integer invoiceNumber;

    @Column(name = "invoiceDate")
    private Date invoiceDate;

    //saved Id directly insted of object
    @Column(name = "invoiceDocumentId")
    private Integer invoiceDocumentId;

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
