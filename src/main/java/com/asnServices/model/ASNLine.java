package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_ASNOrderLine")
public class ASNLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    private ASNHead asnHeadId;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "PurchaseOrderLineId")
    private PurchaseOrderLine purchaseOrderLine;

    @ManyToOne
    @JoinColumn(name = "ItemScheduleSupplierId")
    private ItemScheduleSupplier itemScheduleSupplier;

    @ManyToOne
    @JoinColumn(name = "ItemId")
    private Item item;

    @Column(name = "RequiredQuentity")
    private Integer requiredQuantity;

    @Column(name = "AllocatedQuentity")
    private Integer allocatedQuantity;

    @Column(name = "balanceQuantity")
    private Integer balanceQuantity;

    @Column(name = "InvoiceQuantity")
    private Integer invoiceQuantity;

    @Column(name = "numberOfContainer")
    private Integer numberOfContainer;

    @Column(name = "mode")
    private Integer mode;

    //add new column
    @ManyToOne
    private Reason reason;

    //add new column
    @Column(name = "ReasonDocumentId")
    private Integer reasonDocumentId;

    @ManyToOne
    @JoinColumn(name = "StatusId")
    private PurchaseStatus status;

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
