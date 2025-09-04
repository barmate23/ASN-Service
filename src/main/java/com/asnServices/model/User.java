package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "UserId")
    private String userId;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @Column(name = "Department")
    private String department;

    @Column(name = "Designation")
    private String designation;

    @Column(name = "SupplierId")
    private Integer supplierId;

    @Column(name = "UserType")
    private String userType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "StartDate")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EndDate")
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DateOfBirth")
    private Date dateOfBirth;

    @Column(name = "Username")
    private String username;

    @Column(name = "EmployeeID")
    private String employeeId;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Password")
    private String password;

    @Column(name = "FirstPassword")
    private String firstPassword;

    @Column(name = "SecondPassword")
    private String secondPassword;

    @Column(name = "ThirdPassword")
    private String thirdPassword;

    @Column(name = "PwdExpireDate")
    private Long pwdExpireDate;

    @Column(name = "EmailId")
    private String emailId;

    @Column(name = "CountryCode")
    private String countryCode;

    @Column(name = "MobileNo")
    private String mobileNo;

    @Column(name = "LandLineNumber")
    private String landLineNumber;

    @Column(name = "Image")
    private String image;

    @Column(name = "ImagePath")
    private String imagePath;

    @Column(name = "UserLanguage")
    private String userLanguage;

    @Column(name = "BiometricID")
    private String biometricId;

    @Column(name = "IsEmailVerified")
    private Boolean isEmailVerified;

    @Column(name = "Otp")
    private Integer otp;

    @Column(name = "OtpExpDate")
    private Long otpExpDate;

    @Column(name = "IsFirstLogin")
    private Boolean isFirstLogin;

    @Column(name = "FreeField1")
    private String freeField1;

    @Column(name = "FreeField2")
    private Integer freeField2;

    @Column(name = "FreeField3")
    private String freeField3;

    @Column(name = "FreeField4")
    private Integer freeField4;

    @Column(name = "IsActive")
    private Boolean isActive;

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
