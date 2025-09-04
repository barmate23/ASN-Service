package com.asnServices.response;

import com.asnServices.model.ItemSchedule;
import com.asnServices.model.ItemScheduleSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierScheduleResponse {
    private ItemSchedule itemSchedule;
    private List<ItemScheduleSupplier> itemScheduleSupplierList;
}
