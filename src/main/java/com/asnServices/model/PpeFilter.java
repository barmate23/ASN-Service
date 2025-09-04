package com.asnServices.model;


import lombok.Data;

import javax.persistence.*;
@Entity
@Data
@Table(name="tbl_PpeFilter")
public class PpeFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String statusName;
    private Boolean isDeleted;
    private Integer sequence;
}
