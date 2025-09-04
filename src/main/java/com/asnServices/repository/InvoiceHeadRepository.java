package com.asnServices.repository;

import com.asnServices.model.InvoiceHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceHeadRepository extends JpaRepository<InvoiceHead,Integer> {
        List<InvoiceHead> findBySubOrganizationIdAndAsnHeadIdAndIsDeleted(Integer subOrgId, Integer asnHeadId, boolean isDeleted);
        InvoiceHead findBySubOrganizationIdAndIsDeletedAndId(Integer subOrgId, boolean isDeleted, Integer id);
        List<InvoiceHead> findBySubOrganizationIdAndInvoiceNumberAndIsDeleted(Integer subOrgId, Integer invoiceNumber, boolean isDeleted);
        List<InvoiceHead> findBySubOrganizationIdAndPurchaseOrderHeadIdAndIsDeleted(Integer subOrgId, Integer asnHeadId, boolean isDeleted);
}
