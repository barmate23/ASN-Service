package com.asnServices.service;

import com.asnServices.model.PurchaseOrderHead;
import com.asnServices.model.SupplierDocument;
import com.asnServices.request.*;
import com.asnServices.response.ASNSupplierDropdownResponse;
import com.asnServices.response.AsnReceiptResponse;
import com.asnServices.response.AsnSupplierItemResponse;
import com.asnServices.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ASNSupplierService {

    BaseResponse<AsnSupplierItemResponse> getSupplierAsn();

    BaseResponse acknowledgePoAsn(AsnPoAcknowledgeRequest asnPoAcknowledgeRequest);

    BaseResponse<List<PurchaseOrderHead>> getSupplierPo();

    BaseResponse saveTransportDetails(SaveTransportRequest saveTransportRequest);

    BaseResponse saveDocumentDetails(List<SaveDocumentRequest> saveDocumentRequest);

    BaseResponse saveSerBatNumber(SaveSerialBatchNumberRequest saveSerialBatchNumberRequest);

    //BaseResponse<List<ASNLineStatusMaster>> getAsnLineStatus();

    BaseResponse saveASNLineSupplier(List<SaveAsnLineRequest> saveAsnLineRequestList);

    //BaseResponse<List<ASNReasonMaster>> getAsnReason();

    BaseResponse saveInsuranceDetails(SaveInsuranceRequest saveInsuranceRequest);

    public BaseResponse<ASNSupplierDropdownResponse> getSupplierDropdown();

    BaseResponse<AsnReceiptResponse> getAsnReceiptDet(Integer asnId, Integer poHeadId);

    ResponseEntity<byte[]> genarateExcel();

    BaseResponse generateSupBarcode(List<String> asnNumberList);

    BaseResponse updateAsnPrint(Integer asnId);

    BaseResponse updateAsnLineStatus(List<UpdateAsnLineStatus> asnId);

    BaseResponse getSerBatNumber(Integer asnLineId, Integer poLineId);

    byte[] downloadSerialBarcode(Integer asnLineId, Integer poLineId);

    BaseResponse saveInvoice(SaveInvoiceRequest saveInvoiceRequest);

    BaseResponse saveQCCertificate(List<SaveQCCertificateRequest> saveQCCertificateRequest);

    BaseResponse getPOASNLineDetails(Integer asnHeadId, Integer poHeadId);

    BaseResponse<SupplierDocument> savePODoc(MultipartFile file);

    BaseResponse<SupplierDocument> getPODoc(Integer documentId);

    byte[] getSerialBarcode(GenerateSerialBarcode generateSerialBarcode);
}
