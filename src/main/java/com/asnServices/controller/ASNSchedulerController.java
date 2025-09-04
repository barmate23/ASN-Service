package com.asnServices.controller;


import com.asnServices.request.SendAsnScheduleRequest;
import com.asnServices.service.ASNSchedulerService;
import com.asnServices.utils.APIConstants;
import com.asnServices.response.ASNPOSupplierResponse;
import com.asnServices.response.ASNRequiredDate;
import com.asnServices.response.AsnScheduleItemResponse;
import com.asnServices.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;


@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME})
public class ASNSchedulerController {
    @Autowired
    private ASNSchedulerService asnSchedulerService;

    @GetMapping(APIConstants.GET_PPE_ITEM_LIST)
    public BaseResponse<AsnScheduleItemResponse> getPPCItemList() {
        return asnSchedulerService.getPPCItemList();

    }

    @GetMapping(APIConstants.GET_PPE_REQUIRED_DATE_TABLE)
    public BaseResponse<ASNRequiredDate> getRequiredDateTbl(@RequestParam Integer itemId, @RequestParam String month) {
        return asnSchedulerService.getRequiredDateTbl(itemId, month);

    }

    @GetMapping(APIConstants.GET_PO_AND_SUPPLIER)
    public BaseResponse<ASNPOSupplierResponse> getPoAndSupplier(@RequestParam Integer itemId) {
        return asnSchedulerService.getPoAndSupplier(itemId);

    }

    @PostMapping(APIConstants.SEND_ASN_SCHEDULE)
    public BaseResponse sendAsnSchedule(@RequestBody SendAsnScheduleRequest sendAsnScheduleRequest) {
        return asnSchedulerService.sendAsnSchedule(sendAsnScheduleRequest);
    }

    @GetMapping(APIConstants.GET_SCHEDULE_CODE)
    public BaseResponse getScheduleCode() {
        return asnSchedulerService.getScheduleCode();
    }

    @GetMapping(APIConstants.GET_SUPPLIER_SCHEDULE)
    public BaseResponse getSupplierSchedule(@RequestParam Integer itemId, @RequestParam String month) {
        return asnSchedulerService.getSupplierSchedule(itemId, month);
    }

    @GetMapping(APIConstants.GET_ASN_SUPPLIER_EXCEL)
    public BaseResponse getASNSupplierExcel(@RequestParam String month) {
        return asnSchedulerService.getASNSupplierExcel(month);
    }

    @GetMapping(APIConstants.GET_PPE_ITEM_FOR_SUPPLIER)
    public BaseResponse<AsnScheduleItemResponse> getItemForSupplier() {
        return asnSchedulerService.getItemForSupplier();
    }

    @GetMapping(APIConstants.GET_DATES_FOR_SUPPLIER)
    public BaseResponse<ASNRequiredDate> getDatesForSupplier(@PathVariable Integer itemScheduleId) {
        return asnSchedulerService.getDatesForSupplier(itemScheduleId);
    }

    @GetMapping(APIConstants.DOWNLOAD_ITEM_DATE_EXCEL)
    public ResponseEntity<InputStreamResource> downloadExcel(@RequestParam String month, @RequestParam int year) {
        try {
            ByteArrayInputStream in = asnSchedulerService.exportItemsToExcel(month, year);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=items.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

