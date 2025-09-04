package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_qcCertificate")
public class QCCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderLineId", referencedColumnName = "id")
    private PurchaseOrderLine purchaseOrderLine;

    @ManyToOne
    @JoinColumn(name = "asnLineId", referencedColumnName = "id")
    private ASNLine asnLine;

    @ManyToOne
    @JoinColumn(name = "ItemId", referencedColumnName = "id")
    private Item item;

    private Integer qcDocumentId;

    @Column(name = "qcNumber")
    private Integer qcNumber;

    @Column(name = "qcDate")
    private Date qcDate;

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
