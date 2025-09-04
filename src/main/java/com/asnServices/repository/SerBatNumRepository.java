package com.asnServices.repository;

import com.asnServices.model.SerialBatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerBatNumRepository extends JpaRepository<SerialBatchNumber,Integer> {
    List<SerialBatchNumber> findBySubOrganizationIdAndPurchaseOrderLineIdAndIsDeleted(Integer subOrgId, Integer purchaseOrderLineId, Boolean isDeleted);
    List<SerialBatchNumber> findBySubOrganizationIdAndAsnLineIdAndIsDeleted(Integer subOrgId, Integer asnLineId, Boolean isDeleted);
}
