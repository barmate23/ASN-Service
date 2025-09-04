package com.asnServices.repository;

import com.asnServices.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Integer> {

Container findByItemIdAndIsDeletedAndSubOrganizationId(Integer itamId, boolean isDeleted, Integer subOrgId);

}
