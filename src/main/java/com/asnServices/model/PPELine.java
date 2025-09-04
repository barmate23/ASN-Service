package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;

import java.util.Date;

@Entity
@Data
@Table(name = "tbl_PPELine")
public class PPELine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "PPEHeadId")
    private PPEHead ppeHead;

    @ManyToOne
    @JoinColumn(name = "itemId", referencedColumnName = "id")
    private Item item;

    @Column(name = "requiredQuantity")
    private Integer requiredQuantity;

    //Changed type to Date
    @Column(name = "requiredBy")
    private Date requiredBy;

    @Column(name = "shortage")
    private Integer shortage;

    @Column(name = "inPipeline")
    private Integer inPipeline;

    @Column(name = "store", length = 30)
    private String store;

    @Column(name = "eta")
    private Integer eta;

    @Column(name = "status")
    private Integer status;

    //added new column
    @Column(name = "isScheduled")
    private Boolean isScheduled;

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