package com.asnServices.repository;

import com.asnServices.model.PurchaseOrderHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderHeadRepository extends JpaRepository<PurchaseOrderHead,Integer> {

    List<PurchaseOrderHead> findByOrganizationIdAndPurchaseOrderNumber(Integer organizationId, String purchaseOrderNumber);
    List<PurchaseOrderHead> findByOrganizationIdAndIsDeleted(Integer organizationId, boolean status);
    PurchaseOrderHead findByIdAndIsDeleted(Integer Id, boolean status);
    PurchaseOrderHead findBySubOrganizationIdAndIdAndIsDeleted(Integer subOrgId, Integer Id, boolean status);

    List<PurchaseOrderHead> findBySupplierIdAndSubOrganizationIdAndIsDeletedAndDeliveryTypeAndStatusId(Integer userId, Integer orgId, boolean b, String deliveryType, Integer statusId);
}
