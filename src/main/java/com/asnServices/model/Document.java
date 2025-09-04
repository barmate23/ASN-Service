package com.asnServices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "purchaseOrderHeadId", referencedColumnName = "id")
    private PurchaseOrderHead purchaseOrderHead;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "asnHeadId", referencedColumnName = "id")
    private ASNHead asnHead;

    @Column(name = "documentName", length = 30)
    private String documentName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "documentDate")
    private Date documentDate;

    @Column(name = "documentId")
    private Integer documentId;

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
