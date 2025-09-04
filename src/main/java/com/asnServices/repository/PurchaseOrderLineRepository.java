package com.asnServices.repository;

import com.asnServices.model.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine,Integer> {

    PurchaseOrderLine findByIdAndSubOrganizationIdAndIsDeleted(Integer id, Integer subOrgId, boolean isDeleted);
    List<PurchaseOrderLine> findBySubOrganizationIdAndIsDeletedAndPurchaseOrderHeadId(Integer orgId, Boolean status, Integer purchaseOrderHeaderId);
    List<PurchaseOrderLine> findBySubOrganizationIdAndIsDeletedAndItemIdAndPurchaseOrderHeadDeliveryType(Integer orgId, Boolean status, Integer itemId, String deliveryType);
    PurchaseOrderLine findBySubOrganizationIdAndIsDeletedAndItemIdAndPurchaseOrderHeadId(Integer orgId, Boolean status, Integer itemId, Integer poHeadId);
    List<PurchaseOrderLine> findByOrganizationIdAndIsDeletedAndPurchaseOrderHeadIsDeletedAndItemIdIn(Integer orgId, Boolean status, Boolean headStatus , List<Integer> itemId);
    List<PurchaseOrderLine> findByIsDeletedAndPurchaseOrderHeadIdIn(Boolean status, List<Integer> purchaseOrderHeaderIdList);

    List<PurchaseOrderLine> findBySubOrganizationIdAndIsDeletedAndItemIdIn(Integer subOrgId, boolean b, List<Integer> itemIds);
}
