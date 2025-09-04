package com.asnServices.repository;

import com.asnServices.model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceDetailsRepository extends JpaRepository<Insurance,Integer> {
    Insurance findByIsDeletedAndSubOrganizationIdAndAsnHeadId(boolean b, Integer orgId, Integer id);
    Insurance findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(boolean b, Integer orgId, Integer id);

    Insurance findByIdAndIsDeleted(Integer id, boolean b);
}
