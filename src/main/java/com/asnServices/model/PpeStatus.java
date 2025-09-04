package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="tbl_PpeStatus")
public class PpeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String statusName;
    private String displayName;

}
