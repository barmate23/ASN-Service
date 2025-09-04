package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_ItemSchedule")
@Data
public class ItemSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "scheduleId")
    private String scheduleId;

    @Column(name = "schedulemonth")
    private String scheduleMonth;

    @ManyToOne
    @JoinColumn(name = "ItemId")
    private Item item;

    @Column(name = "totalQuantity")
    private Integer totalQuantity;

    @Column(name = "Year")
    private Integer year;

    @Column(name = "IsDeleted")
    private boolean isDeleted;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "ModifiedOn")
    private Date modifiedOn;

}

