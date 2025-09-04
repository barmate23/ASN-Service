package com.asnServices.repository;

import com.asnServices.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Integer> {
    public List<Supplier> findByIsDeleted(boolean status);
    public Supplier findByIsDeletedAndId(boolean status, Integer id);
    public List<Supplier> findByIsDeletedAndOrganizationId(boolean status, Integer orgId);
}
