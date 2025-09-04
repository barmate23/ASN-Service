package com.asnServices.repository;

import com.asnServices.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportRepository extends JpaRepository<Transport,Integer> {
    Transport findByIsDeletedAndSubOrganizationIdAndAsnHeadId(boolean b, Integer orgId, Integer id);
    Transport findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(boolean b, Integer orgId, Integer id);

    Transport findByIdAndIsDeleted(Integer transporterID, boolean b);
}
