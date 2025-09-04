package com.asnServices.model;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_Reason")
@Data
public class Reason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ReasonName", length = 30)
    private String reasonName;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "Sequence")
    private Integer sequence;

    @Column(name = "reasonId")
    private String reasonId;

    @Column(name = "rejectedReason")
    private String rejectedReason;

    @Column(name = "reasonCategory")
    private String reasonCategory;

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
