package com.asnServices.repository;

import com.asnServices.model.Supplier;
import com.asnServices.model.SupplierItemMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierItemMapperRepository extends JpaRepository<SupplierItemMapper,Integer> {

    SupplierItemMapper findBySubOrganizationIdAndIsDeletedAndSupplierIdAndItemId(Integer subOrgId, Boolean isDeleted, Integer supplierId, Integer itemId);

    List<SupplierItemMapper> findBySubOrganizationIdAndIsDeletedAndItemIdIn(Integer subOrgId, boolean b, List<Integer> itemIds);

    List<SupplierItemMapper> findBySubOrganizationIdAndIsDeletedAndSupplierIdAndItemIdIn(Integer subOrgId, boolean b, Integer supplierId, List<Integer> itemIds);
}
