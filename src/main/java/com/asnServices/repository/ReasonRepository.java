package com.asnServices.repository;


import com.asnServices.model.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonRepository extends JpaRepository<Reason,Integer> {
    List<Reason> findByIsDeletedAndReasonCategory(boolean isDeleted, String reasonCategory);
    Reason findByIsDeletedAndId(boolean isDeleted, Integer id);
}
