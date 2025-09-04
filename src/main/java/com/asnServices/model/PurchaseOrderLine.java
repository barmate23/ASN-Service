package com.asnServices.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "tbl_PurchaseOrderLine")
public class PurchaseOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "PurchaseOrderLineID")
    private String purchaseOrderLineId;

    @Column(name = "OrganizationId")
    private Integer organizationId;

    @Column(name = "SubOrganizationId")
    private Integer subOrganizationId;

    @ManyToOne
    @JoinColumn(name = "PurchaseOrderHeadID")
    private PurchaseOrderHead purchaseOrderHead;

    @Column(name = "LineNumber")
    private Integer lineNumber;

    @ManyToOne
    @JoinColumn(name = "ItemID")
    private Item item;

    @Column(name = "UoM", length = 255)
    private String uom;

    @Column(name = "Currency", length = 255)
    private String currency;

    @Column(name = "UnitPrice")
    private Float unitPrice;

    @Column(name = "numberOfContainer")
    private Integer numberOfContainer;

    @Column(name = "PurchaseOrderQuantity")
    private Integer purchaseOrderQuantity;

    @Column(name = "SubTotalRs")
    private Integer subTotalRs;

    @Column(name = "StateGSTPercentage")
    private Float stateGSTPercentage;

    @Column(name = "StateGSTAmount")
    private Float stateGSTAmount;

    @Column(name = "CentralGSTPercentage")
    private Float centralGSTPercentage;

    @Column(name = "centralGSTAmount")
    private Float centralGSTAmount;

    @Column(name = "InterStateGSTPercentage")
    private Float interStateGSTPercentage;

    @Column(name = "InterStateGSTAmount")
    private Float interStateGSTAmount;

    @Column(name = "TotalAmountRs")
    private Float totalAmountRs;

    @Column(name = "LeadTime")
    private Integer leadTime;

    @Column(name = "IsDay")
    private Boolean isDay;

    @ManyToOne
    @JoinColumn(name = "StatusId")
    private PurchaseStatus status;

    @Column(name = "RemainingQuantity")
    private Integer remainingQuantity;

    @Column(name = "RemainingAmount")
    private Integer remainingAmount;

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

    public int getLeadTimeInDays() {
        return isDay ? leadTime : (leadTime + 23) / 24; // Convert hours to days, rounding up
    }

}
