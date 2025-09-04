package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_SupplierItemMapper")
@Data
public class SupplierItemMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SupplierItemId")
    private Integer supplierItemId;

    @Column(name = "organizationId")
    private Integer organizationId;

    @Column(name = "subOrganizationId")
    private Integer subOrganizationId;
    @ManyToOne
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "LeadTime")
    private Integer leadTime;

    @Column(name = "IsDay")
    private Boolean isDay;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @Column(name = "createdBy")
    private Integer createdBy;


    @Column(name = "createdOn")
    private Date createdOn;

    @Column(name = "modifiedBy")
    private Integer modifiedBy;


    @Column(name = "modifiedOn")
    private Date modifiedOn;

    public int getLeadTimeInDays() {
        return isDay ? leadTime : (leadTime + 23) / 24; // Convert hours to days, rounding up
    }

}