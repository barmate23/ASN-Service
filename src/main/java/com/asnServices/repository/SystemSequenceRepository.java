package com.asnServices.repository;

import com.asnServices.model.Dock;
import com.asnServices.model.SystemSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSequenceRepository extends JpaRepository<SystemSequence, Integer> {
//    Dock findBy(boolean status, Integer orgId, String dockId);

}
