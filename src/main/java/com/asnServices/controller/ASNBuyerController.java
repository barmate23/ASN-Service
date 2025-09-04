package com.asnServices.controller;


import com.asnServices.model.*;
import com.asnServices.response.*;
import com.asnServices.service.ASNBuyerService;
import com.asnServices.request.SaveAsnRequest;
import com.asnServices.request.UpdateAsnRequest;
import com.asnServices.request.UpdateAsnStatusReq;
import com.asnServices.utils.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME})
public class ASNBuyerController {
    @Autowired
    private ASNBuyerService asnBuyerService;

    @GetMapping(APIConstants.GET_DATE_FOR_ITEM)
    public BaseResponse<ItemDateResponse> getDateForItem() {
        return asnBuyerService.getDateForItem();
    }

    @GetMapping(APIConstants.GET_ITEM_LIST_FOR_ASN_BUYER)
    public BaseResponse<ItemResponse> getAsnBuyerItem(@PathVariable Integer ppeHeadId) {
        return asnBuyerService.getAsnBuyerItem(ppeHeadId);
    }

    @GetMapping(APIConstants.GET_ASN_SUPPLIER_PO_DETAILS)
    public BaseResponse<ItemScheduleSupplier> getAsnSupplierPoDet(@RequestParam Integer itemId, @RequestParam Integer ppeHeadId) {
        return asnBuyerService.getAsnSupplierPoDet(itemId, ppeHeadId);
    }

    @PostMapping(APIConstants.SAVE_ASN_DET)
    public BaseResponse saveAsnDet(@RequestBody List<SaveAsnRequest> saveRequestList) {
        return asnBuyerService.saveAsnDet(saveRequestList);
    }

    @GetMapping(APIConstants.GET_ASN_FILTER_LIST)
    public BaseResponse saveAsnDet(@RequestParam String status, @RequestParam String startDate, @RequestParam String endDate) {
        return asnBuyerService.getAsnFilterList(status, startDate, endDate);
    }

    @GetMapping(APIConstants.GET_ASN_DET)
    public BaseResponse<ASNDetResponse> getAsnDet(@PathVariable Integer id) {
        return asnBuyerService.getAsnDet(id);
    }

    @PostMapping(APIConstants.UPDATE_ASN_STATUS)
    public BaseResponse updateAsnStatus(@RequestBody List<UpdateAsnStatusReq> asnStatusReqList) {
        return asnBuyerService.updateAsnStatus(asnStatusReqList);
    }

    @GetMapping(APIConstants.GET_REASON)
    public BaseResponse getAsnList(@RequestParam String reasonCategory) {
        return asnBuyerService.getReason(reasonCategory);

    }

    @DeleteMapping(APIConstants.DELETE_ASN)
    public BaseResponse updateAsnStatus(@PathVariable Integer asnId) {
        return asnBuyerService.deleteAsn(asnId);

    }

    @PostMapping(APIConstants.UPDATE_ASN_DETAILS)
    public BaseResponse updateAsnDetails(@RequestBody List<UpdateAsnRequest> updateAsnRequest) {
        return asnBuyerService.updateAsnDetails(updateAsnRequest);
    }

    @GetMapping(APIConstants.GET_PO_LIST)
    public BaseResponse<PoNumberResponse> getPOList() {
        return asnBuyerService.getPOList();
    }

    @GetMapping(APIConstants.GET_PO_DET)
    public BaseResponse<ASNResponse> getGateLineList(@PathVariable Integer poId) {
        return asnBuyerService.getASNLineList(poId);
    }

    @GetMapping(APIConstants.GET_ASN_BY_POID)
    public BaseResponse<ASNHead> getAsnList(@PathVariable Integer poId) {
        return asnBuyerService.getAsnHeadList(poId);

    }

    @GetMapping(APIConstants.GET_SUPPLIER_LIST)
    public BaseResponse<Supplier> getSupplierList() {
        return asnBuyerService.getSupplierList();
    }

    @GetMapping(APIConstants.GET_BUYER_ITEM)
    public BaseResponse<Item> getBuyerItemMapper() {
        return asnBuyerService.getBuyerItem();
    }

}

