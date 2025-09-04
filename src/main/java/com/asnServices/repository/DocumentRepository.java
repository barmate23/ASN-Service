package com.asnServices.repository;

import com.asnServices.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document,Integer> {

    public List<Document> findByIsDeletedAndSubOrganizationIdAndAsnHeadId(boolean b, Integer orgId, Integer asnId);
    public Document findByIsDeletedAndSubOrganizationIdAndId(boolean b, Integer orgId, Integer asnId);
    public List<Document> findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(boolean b, Integer orgId, Integer asnId);
}
