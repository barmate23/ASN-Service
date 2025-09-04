package com.asnServices.repository;

import com.asnServices.model.StockBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockBalanceRepository extends JpaRepository<StockBalance,Integer> {
    List<StockBalance> findByIsDeletedAndSubOrganizationId(boolean isDeleted, Integer subOrgId);
}
