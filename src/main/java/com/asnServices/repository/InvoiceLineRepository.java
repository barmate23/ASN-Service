package com.asnServices.repository;

import com.asnServices.model.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine,Integer> {

}
