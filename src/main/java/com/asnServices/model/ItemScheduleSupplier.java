package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_ItemScheduleSupplier")
public class ItemScheduleSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "itemScheduleId")
    private ItemSchedule itemSchedule;

    @ManyToOne
    @JoinColumn(name = "ppeHeadId")
    private PPEHead ppeHead;

    @Column(name = "requiredDate")
    private Date requiredDate;

    @Column(name = "requiredTime")
    private Time requiredTime;

    @Column(name = "requiredQuantity")
    private Integer requiredQuantity;

    @ManyToOne
    @JoinColumn(name = "SupplierId")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "purchaseOrderHeadId")
    private PurchaseOrderHead purchaseOrderHead;

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
    private Integer purchaseOrderQuantity;

    @Transient
    private Integer subTotalRs;

    @Transient
    private Boolean isDay;

    @Transient
    private Integer leadTime;
}

