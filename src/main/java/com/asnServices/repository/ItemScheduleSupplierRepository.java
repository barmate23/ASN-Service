package com.asnServices.repository;

import com.asnServices.model.ItemScheduleSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ItemScheduleSupplierRepository extends JpaRepository<ItemScheduleSupplier, Integer> {
    List<ItemScheduleSupplier> findByRequiredDateAndItemScheduleItemId(Date requiredDate, Integer itemId);

    List<ItemScheduleSupplier> findByIsDeletedAndSubOrganizationIdAndItemScheduleItemIdAndItemScheduleScheduleMonthAndSupplierId(boolean b, Integer subOrgId, Integer itemId, String month, Integer supplierId);
    List<ItemScheduleSupplier> findByIsDeletedAndSubOrganizationIdAndSupplierId(boolean b, Integer subOrgId, Integer supplierId);
    List<ItemScheduleSupplier> findByIsDeletedAndSubOrganizationIdAndSupplierIdAndItemScheduleId(boolean b, Integer subOrgId, Integer supplierId, Integer itemScheduleId);
    List<ItemScheduleSupplier> findByIsDeletedAndSubOrganizationIdAndItemScheduleScheduleMonthAndSupplierIdOrderByIdDesc(boolean b, Integer subOrgId, String month, Integer supplierId);
    List<ItemScheduleSupplier> findByIsDeletedAndSubOrganizationIdAndItemScheduleScheduleMonthAndItemScheduleYearAndSupplierId(boolean b, Integer subOrgId, String month, Integer year, Integer supplierId);
}
