package com.asnServices.repository;

import com.asnServices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
User findByIsDeletedAndSupplierId(boolean isDeleted, Integer supplierId);
User findByIsDeletedAndId(boolean isDeleted, Integer id);
}
