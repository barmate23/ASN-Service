package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "tbl_Organization")
@Data
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "OrganizationName")
    private String organizationName;

    @Column(name = "OrgRegNumber")
    private String orgRegNumber;

    @Column(name = "PanCardNumber")
    private String panCardNumber;

    @Column(name = "GstinNumber")
    private String gstinNumber;

    @Column(name = "PostCode")
    private Integer postCode;

    @Column(name = "District")
    private String district;

    @Column(name = "SubDistrict")
    private String subDistrict;

    @Column(name = "State")
    private String state;

    @Column(name = "Country")
    private String country;

    @Column(name = "CountryCode")
    private Integer countryCode;

    @Column(name = "OfficeNumber")
    private String officeNumber;

    @Column(name = "City")
    private String city;

    @Column(name = "Town")
    private String town;

    @Column(name = "Village")
    private String village;

    @Column(name = "Address1")
    private String address1;

    @Column(name = "Address2")
    private String address2;

    @Column(name = "Building")
    private String building;

    @Column(name = "Street")
    private String street;

    @Column(name = "LandMark")
    private String landMark;

    @Column(name = "Locality")
    private String locality;

    @Column(name = "SubLocality")
    private String subLocality;

    @Column(name = "AreaCode")
    private String areaCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StartDate")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EndDate")
    private Date endDate;

    @Column(name = "MobileNumber")
    private String mobileNumber;

    @Column(name = "LandlineNumber")
    private String landlineNumber;

    @Column(name = "IsSubOrg")
    private Boolean isSubOrg;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "IsAdminPresent")
    private Boolean isAdminPresent;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreatedOn")
    private Date createdOn;

    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ModifiedOn")
    private Date modifiedOn;

}
