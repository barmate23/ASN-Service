package com.asnServices.repository;

import com.asnServices.model.ItemSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemScheduleRepository extends JpaRepository<ItemSchedule, Integer> {

}
