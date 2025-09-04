package com.asnServices.repository;

import com.asnServices.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization,Integer> {
    Organization findByIsDeletedAndId(boolean isDeleted, Integer id);
}
