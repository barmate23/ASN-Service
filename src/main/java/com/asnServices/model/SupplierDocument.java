package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_supplier_Document")
@Data
public class SupplierDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private byte[] file;
}
