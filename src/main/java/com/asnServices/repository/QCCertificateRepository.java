package com.asnServices.repository;

import com.asnServices.model.QCCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QCCertificateRepository extends JpaRepository<QCCertificate,Integer> {
    List<QCCertificate> findBySubOrganizationIdAndAsnLineAsnHeadIdIdAndIsDeleted(Integer subOrgId, Integer asnHeadId, boolean isDeleted);
    List<QCCertificate> findBySubOrganizationIdAndPurchaseOrderLinePurchaseOrderHeadIdAndIsDeleted(Integer subOrgId, Integer asnHeadId, boolean isDeleted);

}