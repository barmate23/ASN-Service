package com.asnServices.repository;

import com.asnServices.model.ASNLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsnLineRepository extends JpaRepository<ASNLine,Integer> {
    public List<ASNLine> findByAsnHeadIdIdAndSubOrganizationIdAndIsDeleted(Integer asnHeadId, Integer organizationId, boolean status);
    public List<ASNLine> findByStatusStatusNameAndSubOrganizationIdAndIsDeleted(String statusName, Integer organizationId, boolean status);
    public List<ASNLine> findByPurchaseOrderLineIdAndOrganizationIdAndIsDeleted(Integer asnHeadId, Integer organizationId, boolean status);
    public List<ASNLine> findBySubOrganizationIdAndAsnHeadIdIdAndIsDeleted(Integer subOrgId, Integer asnHeadId, boolean status);

    ASNLine findByIdAndSubOrganizationIdAndIsDeleted(Integer asnLineId, Integer subOrgId, boolean isDeleted);
    ASNLine findByAsnHeadIdIdAndItemIdAndSubOrganizationIdAndIsDeleted(Integer asnHeadId, Integer itemId, Integer subOrgId, boolean isDeleted);

    List<ASNLine> findByStatusStatusNameAndSubOrganizationIdAndIsDeletedAndAsnHeadIdSupplierId(String hold, Integer subOrgId, boolean b, Integer supplierId);
}
