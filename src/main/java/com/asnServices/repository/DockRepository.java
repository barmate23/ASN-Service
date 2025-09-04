package com.asnServices.repository;

import com.asnServices.model.Dock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DockRepository extends JpaRepository<Dock, Integer> {
    Dock findByIsDeletedAndSubOrganizationIdAndDockId(boolean status, Integer orgId, String dockId);

}
