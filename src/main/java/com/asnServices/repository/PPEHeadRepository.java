package com.asnServices.repository;

import com.asnServices.model.PPEHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PPEHeadRepository extends JpaRepository<PPEHead, Integer> {
    List<PPEHead> findBySubOrganizationIdAndIsDeletedAndPpeStatusStatusNameInAndIsAsnCreatedNot(
            Integer subOrgId, boolean isDeleted, List<String> statusList, boolean isAsnCreated);

    @Query("SELECT p FROM PPEHead p JOIN p.ppeStatus ps WHERE p.subOrganizationId = :subOrgId " +
            "AND p.isDeleted = :isDeleted AND ps.statusName IN :statusList " +
            "AND (p.isAsnCreated IS NULL OR p.isAsnCreated = false)")
    List<PPEHead> findBySubOrganizationIdAndIsDeletedAndPpeStatusStatusNameIn(
            @Param("subOrgId") Integer subOrgId,
            @Param("isDeleted") boolean isDeleted,
            @Param("statusList") List<String> statusList);



    PPEHead findBySubOrganizationIdAndIsDeletedAndId(Integer subOrgId, boolean isDeleted, Integer id);
}
