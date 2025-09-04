package com.asnServices.controller;


import com.asnServices.model.SupplierDocument;
import com.asnServices.model.PurchaseOrderHead;
import com.asnServices.request.*;
import com.asnServices.service.ASNSupplierService;
import com.asnServices.utils.APIConstants;
import com.asnServices.response.ASNSupplierDropdownResponse;
import com.asnServices.response.AsnReceiptResponse;
import com.asnServices.response.AsnSupplierItemResponse;
import com.asnServices.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@Slf4j
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME})
public class
ASNSupplierController {
    @Autowired
    private ASNSupplierService asnSupplierService;

    @GetMapping(APIConstants.GET_ASN_SUPPLIER)
    public BaseResponse<AsnSupplierItemResponse> getASNList() {
        return asnSupplierService.getSupplierAsn();
    }

    @PostMapping(APIConstants.ACKNOWLEDGE_PO_ASN_LINE)
    public BaseResponse acknowledgePoAsn(@RequestBody AsnPoAcknowledgeRequest asnPoAcknowledgeRequest) {
        return asnSupplierService.acknowledgePoAsn(asnPoAcknowledgeRequest);
    }

    @PostMapping(APIConstants.SAVE_SER_BATCH_NO)
    public BaseResponse saveSerBatNumber(@RequestBody SaveSerialBatchNumberRequest saveSerialBatchNumberRequest) {
        return asnSupplierService.saveSerBatNumber(saveSerialBatchNumberRequest);
    }

    @GetMapping(APIConstants.GET_SERIAL_BATCH_NUMBER)
    public BaseResponse getSerBatNumber(@RequestParam Integer asnLineId, @RequestParam Integer poLineId) {
        return asnSupplierService.getSerBatNumber(asnLineId, poLineId);
    }

    @GetMapping(APIConstants.GET_PO_ASN_LINE_DET)
    public BaseResponse getPOASNLineDetails(@RequestParam Integer asnHeadId, @RequestParam Integer poHeadId) {
        return asnSupplierService.getPOASNLineDetails(asnHeadId, poHeadId);
    }

    @GetMapping(APIConstants.GENERATE_SUP_BARCODE)
    public BaseResponse downloadSerExcel(@RequestParam List<String> barcodeNumberList) {
        return asnSupplierService.generateSupBarcode(barcodeNumberList);
    }

    @GetMapping(APIConstants.DOWNLOAD_SERIAL_BARCODE)
    public byte[] downloadSerialBarcode(@RequestParam Integer asnLineId, @RequestParam Integer poLineId) {
        return asnSupplierService.downloadSerialBarcode(asnLineId, poLineId);
    }

    @PostMapping(APIConstants.SAVE_INSURANCE_DET)
    public BaseResponse saveInvoice(@RequestBody SaveInsuranceRequest saveInsuranceRequest) {
        return asnSupplierService.saveInsuranceDetails(saveInsuranceRequest);
    }

    @PostMapping(APIConstants.SAVE_INVOICE)
    public BaseResponse saveInvoice(@RequestBody SaveInvoiceRequest saveInvoiceRequest) {
        return asnSupplierService.saveInvoice(saveInvoiceRequest);
    }

    @PostMapping(APIConstants.SAVE_QC_CERTIFICATE)
    public BaseResponse saveQCCertificate(@RequestBody List<SaveQCCertificateRequest> saveQCCertificateRequest) {
        return asnSupplierService.saveQCCertificate(saveQCCertificateRequest);
    }

    @PostMapping(APIConstants.SAVE_DOCUMENT_DET)
    public BaseResponse saveDocDet(@RequestBody List<SaveDocumentRequest> saveDocumentRequest) {
        return asnSupplierService.saveDocumentDetails(saveDocumentRequest);
    }

    @PostMapping(APIConstants.SAVE_TRANSPORT_DET)
    public BaseResponse saveTransportDet(@RequestBody SaveTransportRequest saveTransportRequest) {
        return asnSupplierService.saveTransportDetails(saveTransportRequest);
    }

    @GetMapping(APIConstants.GET_ASN_REC_DET)
    public BaseResponse<AsnReceiptResponse> getAsnReceiptDet(@RequestParam Integer asnHeadId, @RequestParam Integer poHeadId) {
        return asnSupplierService.getAsnReceiptDet(asnHeadId, poHeadId);
    }

    @PostMapping(APIConstants.SAVE_DOCUMENT)
    public BaseResponse<SupplierDocument> savePODoc(@RequestParam MultipartFile file) {
        return asnSupplierService.savePODoc(file);
    }

    @GetMapping(APIConstants.GET_DOCUMENT)
    public BaseResponse<SupplierDocument> getPODoc(@PathVariable Integer documentId) {
        return asnSupplierService.getPODoc(documentId);
    }

    /*
    @GetMapping(APIConstants.GET_ASN_LINE_STATUS)
    public BaseResponse<List<ASNLineStatusMaster>> getAsnLineStatus() {
        BaseResponse<List<ASNLineStatusMaster>> baseResponse = new BaseResponse();
        baseResponse = asnSupplierService.getAsnLineStatus();
        return baseResponse;
    }

    @GetMapping(APIConstants.GET_ASN_REASON)
    public BaseResponse<List<ASNReasonMaster>> getAsnReason() {
        BaseResponse<List<ASNReasonMaster>> baseResponse = new BaseResponse();
        baseResponse = asnSupplierService.getAsnReason();
        return baseResponse;
    }
    */


    @GetMapping(APIConstants.GET_ASN_SUPPLIER_DROPDOWN)
    public BaseResponse<ASNSupplierDropdownResponse> getShipThru() {
        return asnSupplierService.getSupplierDropdown();
    }

    @GetMapping(APIConstants.GENERATE_EXCEL)
    public ResponseEntity<byte[]> downloadSerExcel() {
        return asnSupplierService.genarateExcel();
    }


    @PostMapping(APIConstants.UPDATE_PRINT_STATUS)
    public BaseResponse updateAsnPrint(@PathVariable Integer asnId) {
        return asnSupplierService.updateAsnPrint(asnId);
    }

    @PostMapping(APIConstants.UPDATE_LINE_STATUS)
    public BaseResponse updateAsnLineStatus(@RequestBody List<UpdateAsnLineStatus> asnLineStatus) {
        return asnSupplierService.updateAsnLineStatus(asnLineStatus);
    }

    @PostMapping(APIConstants.SAVE_ASN_LINE_SUPPLIER)
    public BaseResponse saveASNLineSupplier(@RequestBody List<SaveAsnLineRequest> saveAsnLineRequestList) {
        return asnSupplierService.saveASNLineSupplier(saveAsnLineRequestList);
    }

    @GetMapping(APIConstants.GET_PO_SUPPLIER)
    public BaseResponse<List<PurchaseOrderHead>> getPOList() {
        return asnSupplierService.getSupplierPo();

    }

    @PostMapping(APIConstants.GET_SERIAL_BARCODE)
    public ResponseEntity<byte[]> getSerialBarcode(@RequestBody GenerateSerialBarcode generateSerialBarcode) {
        byte[] pdfFile =  asnSupplierService.getSerialBarcode(generateSerialBarcode);
        if(pdfFile != null){
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfFile);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

}

