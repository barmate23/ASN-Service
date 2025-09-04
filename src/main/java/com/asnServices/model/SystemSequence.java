package com.asnServices.model;

import javax.persistence.*;

@Entity
@Table(name = "tbl_SystemSequence")
public class SystemSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "counter")
    private Integer counter;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @Column(name = "sequenceType")
    private String sequenceType;
}
