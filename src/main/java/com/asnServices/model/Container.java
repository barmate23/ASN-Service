package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "tbl_Container")
@Data
public class Container {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "Code")
    private String code;

    @Column(name = "Type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "DimensionUOM")
    private String dimensionUOM;

    @Column(name = "Width")
    private Float width;

    @Column(name = "Height")
    private Float height;

    @Column(name = "Length")
    private Float length;

    @Column(name = "Circumference")
    private Float circumference;

    @Column(name = "Weight")
    private Float weight;

    @Column(name = "ItemQty")
    private Integer itemQty;

    @Column(name = "minimumOrderQty")
    private Integer minimumOrderQty;

    @Column(name = "IsActive")
    private Boolean isActive;

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

}