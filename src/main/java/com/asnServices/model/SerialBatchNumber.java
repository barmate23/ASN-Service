package com.asnServices.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_serialBatchNumbers")
public class SerialBatchNumber {

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
    @JoinColumn(name = "purchaseOrderLineId", referencedColumnName = "id")
    private PurchaseOrderLine purchaseOrderLine;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "asnLineId", referencedColumnName = "id")
    private ASNLine asnLine;

    @Column(name = "serialBatchNumber", length = 30)
    private String serialBatchNumber;

    @Column(name = "manufacturingDate")
    private Date manufacturingDate;

    @Column(name = "expiryDate")
    private Date expiryDate;

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
