package com.asnServices.service;

import com.asnServices.response.ASNPOSupplierResponse;
import com.asnServices.response.ASNRequiredDate;
import com.asnServices.response.AsnScheduleItemResponse;
import com.asnServices.response.BaseResponse;
import com.asnServices.request.SendAsnScheduleRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ASNSchedulerService {
    BaseResponse<AsnScheduleItemResponse> getPPCItemList();

    BaseResponse<ASNRequiredDate> getRequiredDateTbl(Integer itemId, String month);

    BaseResponse<ASNPOSupplierResponse> getPoAndSupplier(Integer itemId);

    BaseResponse sendAsnSchedule(SendAsnScheduleRequest sendAsnScheduleRequest);

    BaseResponse getScheduleCode();

    BaseResponse getSupplierSchedule(Integer itemId, String month);

    BaseResponse getASNSupplierExcel(String month);

    BaseResponse<AsnScheduleItemResponse> getItemForSupplier();

    BaseResponse<ASNRequiredDate> getDatesForSupplier(Integer itemScheduleId);

    ByteArrayInputStream exportItemsToExcel(String month, int year) throws IOException;
}
