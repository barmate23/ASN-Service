package com.asnServices.service;

import com.asnServices.model.*;
import com.asnServices.response.*;
import com.asnServices.request.SaveAsnRequest;
import com.asnServices.request.UpdateAsnRequest;
import com.asnServices.request.UpdateAsnStatusReq;


import java.util.List;

public interface ASNBuyerService {
    public BaseResponse<PoNumberResponse> getPOList();

    public BaseResponse<ASNResponse> getASNLineList(Integer poId);

    BaseResponse deleteAsn(Integer asnId);

    BaseResponse saveAsnDet(List<SaveAsnRequest> saveRequestList);

    BaseResponse<ASNHead> getAsnHeadList(Integer poId);

    BaseResponse<Supplier> getSupplierList();

    BaseResponse<ASNDetResponse> getAsnDet(Integer id);

    BaseResponse updateAsnStatus(List<UpdateAsnStatusReq> asnStatusReqList);

    BaseResponse updateAsnDetails(List<UpdateAsnRequest> updateAsnRequest);

    BaseResponse<ItemDateResponse> getDateForItem();

    BaseResponse<ItemResponse> getAsnBuyerItem(Integer ppeHeadId);

    BaseResponse<ItemScheduleSupplier> getAsnSupplierPoDet(Integer itemId, Integer ppeHeadId);

    BaseResponse getAsnFilterList(String status, String startDate, String endDate);

    BaseResponse getReason(String reasonCategory);

    BaseResponse<Item> getBuyerItem();
}
