package com.asnServices.repository;

import com.asnServices.model.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseStatusRepository extends JpaRepository<PurchaseStatus, Integer> {
    PurchaseStatus findByStatusNameAndStatusType(String statusName, String statusType);
    PurchaseStatus findByStatusNameIgnoreCase(String statusName);
}
