package com.asnServices.repository;

import com.asnServices.model.BuyerItemMapper;
import com.asnServices.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerItemMapperRepository extends JpaRepository<BuyerItemMapper, Integer> {
    List<BuyerItemMapper> findBySubOrganizationIdAndIsDeletedAndUserId(Integer subOrgId, boolean isDeleted, Integer itemId);
}
