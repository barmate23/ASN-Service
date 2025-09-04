package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "tbl_ResponseMessage")
public class ResponseMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Message")
    private String message;

    @Column(name = "code")
    private Integer code;

    @Column(name = "status")
    private Integer status;

    @Column(name = "key")
    private String key;

    @Column(name = "FreeField1")
    private String freeField1;

    @Column(name = "FreeField2")
    private String freeField2;

    @Column(name = "FreeField3")
    private String freeField3;

    @Column(name = "FreeField4")
    private String freeField4;

    @Column(name = "FreeField5")
    private String freeField5;

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
