package com.asnServices.repository;

import com.asnServices.model.ASNHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsnHeadRepository extends JpaRepository<ASNHead, Integer> {
    public List<ASNHead> findByPurchaseOrderHeadIdAndSubOrganizationIdAndIsDeleted(Integer poId, Integer organizationId, boolean status);

    public List<ASNHead> findBySupplierIdAndSubOrganizationIdAndIsDeletedAndPurchaseStatusStatusName(Integer poId, Integer organizationId, boolean isDeleted, String status);

    public ASNHead findByIdAndSubOrganizationIdAndIsDeleted(Integer id, Integer organizationId, boolean status);

    List<ASNHead> findBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameIn(Integer organizationId, boolean isDeleted, List<String> status);
    List<ASNHead> findBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameInAndSupplierId(Integer subOrgId, boolean b, List<String> asList, Integer supplierId);

    List<ASNHead> findBySubOrganizationIdAndIsDeletedAndPurchaseStatusId(Integer organizationId, boolean isDeleted, Integer statuId);

    @Query("SELECT COUNT(a) " +
            "FROM ASNHead a " +
            "JOIN a.purchaseStatus ps " +
            "WHERE a.subOrganizationId = :subOrganizationId " +
            "AND a.isDeleted = :isDeleted " +
            "AND ps.statusName NOT IN :statusList " +
            "AND FUNCTION('YEAR', a.createdOn) = :year " +
            "AND FUNCTION('MONTH', a.createdOn) = :month")
    Long countBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameNotInAndCreatedOnYearAndCreatedOnMonth(
            @Param("subOrganizationId") Integer subOrganizationId,
            @Param("isDeleted") Boolean isDeleted,
            @Param("statusList") List<String> statusList,
            @Param("year") Integer year,
            @Param("month") Integer month);

    List<ASNHead> findBySubOrganizationIdAndIsDeletedAndPurchaseStatusIdAndSupplierId(Integer subOrgId, boolean b, Object o, Integer supplierId);
}
