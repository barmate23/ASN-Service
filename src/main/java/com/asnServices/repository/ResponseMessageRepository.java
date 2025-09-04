package com.asnServices.repository;


import com.asnServices.model.ResponseMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseMessageRepository extends JpaRepository<ResponseMessage, Integer> {
}
