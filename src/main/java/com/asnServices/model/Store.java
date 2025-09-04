package com.asnServices.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_Store")
@Data
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "StoreId")
    private String storeId;
    @Column(name = "StoreName")
    private String storeName;

    @Column(name = "StoreAddress")
    private String storeAddress;

    @Column(name = "StoreManagerName")
    private String storeManagerName;

    @Column(name = "ContactNumber")
    private String contactNumber;

    @Column(name = "EmailId")
    private String emailId;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "ErpStoreId")
    private String erpStoreId;

    @Column(name = "FreeField1")
    private Integer freeField1;

    @Column(name = "FreeField2")
    private String freeField2;

    @Column(name = "FreeField3")
    private Float freeField3;

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

