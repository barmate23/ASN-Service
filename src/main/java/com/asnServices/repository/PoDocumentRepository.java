package com.asnServices.repository;



import com.asnServices.model.SupplierDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoDocumentRepository extends JpaRepository<SupplierDocument,Integer> {

}
