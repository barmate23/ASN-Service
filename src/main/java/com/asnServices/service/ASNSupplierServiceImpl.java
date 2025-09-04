package com.asnServices.service;

import com.asnServices.configuration.LoginUser;
import com.asnServices.model.*;
import com.asnServices.repository.*;
import com.asnServices.request.*;
import com.asnServices.response.*;
import com.asnServices.utils.BarcodeGenerator;
import com.asnServices.utils.GlobalMessages;
import com.asnServices.utils.ServiceConstants;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ASNSupplierServiceImpl implements ASNSupplierService {
    @Autowired
    AsnHeadRepository asnHeadRepository;
    @Autowired
    AsnLineRepository asnLineRepository;
    @Autowired
    ReasonRepository reasonRepository;
    @Autowired
    QCCertificateRepository qcCertificateRepository;
    @Autowired
    SupplierItemMapperRepository supplierItemMapperRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    PoDocumentRepository poDocumentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    InvoiceHeadRepository invoiceHeadRepository;
    @Autowired
    InvoiceLineRepository invoiceLineRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    TransportRepository transportDetailsRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    InsuranceDetailsRepository insuranceDetailsRepository;
    @Autowired
    SerBatNumRepository serBatNumRepository;
    @Autowired
    PurchaseOrderHeadRepository purchaseOrderHeadRepository;
    @Autowired
    PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Autowired
    PurchaseStatusRepository purchaseStatusRepository;
    @Autowired
    private LoginUser loginUser;
    private static final String DEFAULT_TIME_FORMAT_PATTERN = "HH:mm";

    @Override
    public BaseResponse<AsnSupplierItemResponse> getSupplierAsn() {
        log.info(loginUser.getLogId() + " Get -> Asn And Purchase Order List Started --- ");
        BaseResponse<AsnSupplierItemResponse> baseResponse = new BaseResponse<>();
        try {
            User user = userRepository.findById(loginUser.getUserId()).get();
            log.info(loginUser.getLogId() + " Get -> Asn Head List With Sub Organization Id :: " + loginUser.getSubOrgId() + " supplier Id :: " + user.getSupplierId());
            List<AsnSupplierItemResponse> asnSupplierItemResponseList = new ArrayList<>();
            List<ASNHead> asnHeadList = asnHeadRepository.findBySupplierIdAndSubOrganizationIdAndIsDeletedAndPurchaseStatusStatusName(user.getSupplierId(), loginUser.getSubOrgId(), false, "Post");
            log.info(loginUser.getLogId() + " Get -> Purchase Order Head List With Organization Id :: " + loginUser.getOrgId() + " supplier Id :: " + user.getSupplierId());
            List<PurchaseOrderHead> purchaseOrderHeadList = purchaseOrderHeadRepository.findBySupplierIdAndSubOrganizationIdAndIsDeletedAndDeliveryTypeAndStatusId(user.getSupplierId(), loginUser.getSubOrgId(), false, "PO", null);
            if (!asnHeadList.isEmpty()) {
                for (ASNHead asnHead : asnHeadList) {
                    AsnSupplierItemResponse asnSupplierItemResponse = new AsnSupplierItemResponse();
                    asnSupplierItemResponse.setAsnHeadId(asnHead.getId());
                    asnSupplierItemResponse.setAsnNumber(String.valueOf(asnHead.getAsnNumber()));
                    asnSupplierItemResponse.setItemRequiredOnDate(String.valueOf(asnHead.getRequiredOnDate()));
                    asnSupplierItemResponse.setRemainingDays(BarcodeGenerator.getRemainingDays(asnHead.getRequiredOnDate()));
                    asnSupplierItemResponse.setPoHeadId(null);
                    asnSupplierItemResponse.setPurchaseOrderAsn("ASN");
                    List<ASNLine> asnLineList = asnLineRepository.findBySubOrganizationIdAndAsnHeadIdIdAndIsDeleted(loginUser.getSubOrgId(), asnHead.getId(), false);
                    asnSupplierItemResponse.setLeadTime(findMaxNumberDaysAsn(asnLineList));
                    asnSupplierItemResponseList.add(asnSupplierItemResponse);
                }
            }
            if (!purchaseOrderHeadList.isEmpty()) {
                for (PurchaseOrderHead purchaseOrderHead : purchaseOrderHeadList) {
                    AsnSupplierItemResponse asnSupplierItemResponse = new AsnSupplierItemResponse();
                    asnSupplierItemResponse.setAsnHeadId(null);
                    asnSupplierItemResponse.setAsnNumber(purchaseOrderHead.getPurchaseOrderNumber());
                    asnSupplierItemResponse.setItemRequiredOnDate(String.valueOf(purchaseOrderHead.getDeliverByDate()));
                    asnSupplierItemResponse.setPoHeadId(purchaseOrderHead.getId());
                    asnSupplierItemResponse.setPurchaseOrderAsn(ServiceConstants.PURCHASE_ORDER);
                    asnSupplierItemResponse.setRemainingDays(BarcodeGenerator.getRemainingDays(purchaseOrderHead.getDeliverByDate()));
                    List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseOrderHeadId(loginUser.getSubOrgId(), false, purchaseOrderHead.getId());
                    asnSupplierItemResponse.setLeadTime(findMaxNumberDaysPurchase(purchaseOrderLineList));
                    asnSupplierItemResponseList.add(asnSupplierItemResponse);
                }
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100050);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(asnSupplierItemResponseList);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to Asn And Purchase Order List ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100051);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> Asn And Purchase Order List End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse acknowledgePoAsn(AsnPoAcknowledgeRequest asnPoAcknowledgeRequest) {
        log.info(loginUser.getLogId() + " Get -> Asn And Purchase Order List Started --- ");
        Map<Integer, String> lineStatusMap = new HashMap<>();
        lineStatusMap.put(1, "Acknowledge");
        lineStatusMap.put(2, "Reject");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            if (asnPoAcknowledgeRequest.getAsnHeadId() != null) {
                ASNHead asnHead = asnHeadRepository.findById(asnPoAcknowledgeRequest.getAsnHeadId()).get();
                asnHead.setPurchaseStatus(purchaseStatusRepository.findByStatusNameAndStatusType("Acknowledge", "ASN Head"));
                asnHeadRepository.save(asnHead);
            } else if (asnPoAcknowledgeRequest.getPoHeadId() != null) {
                PurchaseOrderHead purchaseOrderHead = purchaseOrderHeadRepository.findById(asnPoAcknowledgeRequest.getPoHeadId()).get();
                purchaseOrderHead.setStatus(purchaseStatusRepository.findByStatusNameAndStatusType("Acknowledge", "PO Head"));
                purchaseOrderHeadRepository.save(purchaseOrderHead);
            }
            for (AsnPoAcknowledgeLine poAcknowledgeLine : asnPoAcknowledgeRequest.getAsnPoAcknowledgeLines()) {
                if (poAcknowledgeLine.getAsnLineId() != null && poAcknowledgeLine.getAsnLineId() > 0) {
                    ASNLine asnLine = asnLineRepository.findById(poAcknowledgeLine.getAsnLineId()).get();
                    asnLine.setStatus(purchaseStatusRepository.findByStatusNameAndStatusType(lineStatusMap.get(poAcknowledgeLine.getStatusId()), ServiceConstants.ASN_LINE));
                    /*
                    Container container = containerRepository.findByItemIdAndIsDeletedAndSubOrganizationId(asnLine.getItem().getId(), false, loginUser.getSubOrgId());
                    double totalContainer = Math.ceil((double)asnLine.getAllocatedQuantity() / container.getItemQty());
                    asnLine.setNumberOfContainer((int) totalContainer);
                     */
                    asnLineRepository.save(asnLine);
                } else if (poAcknowledgeLine.getPoLineId() != null && poAcknowledgeLine.getPoLineId() > 0) {
                    PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findById(poAcknowledgeLine.getPoLineId()).get();
                    purchaseOrderLine.setStatus(purchaseStatusRepository.findByStatusNameAndStatusType(lineStatusMap.get(poAcknowledgeLine.getStatusId()), ServiceConstants.PO_LINE));
                    /*
                    Container container = containerRepository.findByItemIdAndIsDeletedAndSubOrganizationId(purchaseOrderLine.getItem().getId(), false, loginUser.getSubOrgId());
                    double totalContainer = Math.ceil((double)purchaseOrderLine.getPurchaseOrderQuantity() / container.getItemQty());
                    purchaseOrderLine.setNumberOfContainer((int) totalContainer);
                     */
                    purchaseOrderLineRepository.save(purchaseOrderLine);
                }
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100052);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to Acknowledge ASN --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100053);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> Asn And Purchase Order List End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse getPOASNLineDetails(Integer asnHeadId, Integer poHeadId) {
        log.info(loginUser.getLogId() + " Get -> Asn And Purchase Order Line details Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        POASNLineResponse poasnLineResponse = new POASNLineResponse();
        try {
            if (asnHeadId != null && asnHeadId > 0) {
                ASNHead asnHead = asnHeadRepository.findByIdAndSubOrganizationIdAndIsDeleted(asnHeadId, loginUser.getSubOrgId(), false);
                List<ASNLine> asnLineList = asnLineRepository.findBySubOrganizationIdAndAsnHeadIdIdAndIsDeleted(loginUser.getSubOrgId(), asnHeadId, false);
                poasnLineResponse.setAsnHead(asnHead);
                poasnLineResponse.setAsnLineList(asnLineList);
                User buyer = userRepository.findByIsDeletedAndId(false, asnHead.getCreatedBy());
                Organization organization = organizationRepository.findByIsDeletedAndId(false, asnHead.getSubOrganizationId());
                poasnLineResponse.setBuyer(buyer.getFirstName() + " " + buyer.getLastName());
                poasnLineResponse.setContactNumber(buyer.getMobileNo());
                poasnLineResponse.setOrganization(organization.getOrganizationName());
                poasnLineResponse.setAddress(organization.getAddress1());
            }
            if (poHeadId != null && poHeadId > 0) {
                PurchaseOrderHead purchaseOrderHead = purchaseOrderHeadRepository.findBySubOrganizationIdAndIdAndIsDeleted(loginUser.getSubOrgId(), poHeadId, false);
                List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseOrderHeadId(loginUser.getSubOrgId(), false, poHeadId);
                poasnLineResponse.setPurchaseOrderHead(purchaseOrderHead);
                poasnLineResponse.setPurchaseOrderLineList(purchaseOrderLineList);
                Organization organization = organizationRepository.findByIsDeletedAndId(false, purchaseOrderHead.getSubOrganizationId());
                poasnLineResponse.setOrganization(organization.getOrganizationName());
                poasnLineResponse.setAddress(organization.getAddress1());
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100054);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(Arrays.asList(poasnLineResponse));
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to Asn And Purchase Order Line details --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100055);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> Asn And Purchase Order Line details End --- ");
        return baseResponse;
    }


    @Override
    public BaseResponse<SupplierDocument> savePODoc(MultipartFile file) {
        long startTime = System.currentTimeMillis();
        log.info(loginUser.getLogId() + " UPLOAD PO DOC START");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            SupplierDocument poDocument = new SupplierDocument();
            poDocument.setFile(file.getBytes());
            poDocumentRepository.save(poDocument);
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100056);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setData(Arrays.asList(poDocument));
            baseResponse.setMessage(responseMessage.getMessage());
            log.info(loginUser.getLogId() + " SUCCESSFULLY UPLOADED PO DOC ");
        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100057);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setMessage(responseMessage.getMessage());
            long endTime = System.currentTimeMillis();
            log.error(String.valueOf(loginUser.getLogId() + " ERROR OCCURS AT WHILE UPLOADING PO DOC EXECUTED TIME " + (endTime - startTime)), e);
        }
        long endTime = System.currentTimeMillis();
        log.info(String.valueOf(loginUser.getLogId() + " UPLOAD PO DOC EXECUTED  EXECUTED TIME " + (endTime - startTime)));
        return baseResponse;
    }

    @Override
    public BaseResponse<SupplierDocument> getPODoc(Integer docId) {
        long startTime = System.currentTimeMillis();
        log.info(loginUser.getLogId() + " GET PO DOC START");
        BaseResponse<SupplierDocument> baseResponse = new BaseResponse<>();
        try {
            SupplierDocument poDocument = poDocumentRepository.findById(docId).get();
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100058);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setData(Arrays.asList(poDocument));
            baseResponse.setMessage(responseMessage.getMessage());
            log.info(loginUser.getLogId() + " SUCCESSFULLY FETCHED PO DOC ");
        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100059);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setMessage(responseMessage.getMessage());
            long endTime = System.currentTimeMillis();
            log.error(String.valueOf(loginUser.getLogId() + " ERROR OCCURS AT WHILE FETCHING PO DOC  EXECUTED TIME " + (endTime - startTime)), e);
        }
        long endTime = System.currentTimeMillis();
        log.info(String.valueOf(loginUser.getLogId() + " GET PO DOC EXECUTED  EXECUTED TIME " + (endTime - startTime)));

        return baseResponse;
    }

    @Override
    public byte[] getSerialBarcode(GenerateSerialBarcode generateSerialBarcode) {
        try {
            long startTime = System.currentTimeMillis();
            log.info(String.valueOf(loginUser.getLogId() + " DOWNLOAD DOCKS BARCODE PDF "));
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            for (String locId : generateSerialBarcode.getSerialNumberList()) {
                byte[] barcodeImageBytes = BarcodeGenerator.generateBarcode(locId);
                com.itextpdf.text.Image locationImage = com.itextpdf.text.Image.getInstance(barcodeImageBytes);
                document.add(locationImage);
            }
            document.close();
            long endTime = System.currentTimeMillis();
            log.info(String.valueOf(loginUser.getLogId() + "SUCCESSFULLY DOWNLOAD DOCKS BARCODE PDF TIME" + (endTime - startTime)));
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.info(String.valueOf(loginUser.getLogId() + "FAILED DOWNLOAD DOCK BARCODE PDF"), e);
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public BaseResponse<List<PurchaseOrderHead>> getSupplierPo() {
        log.info(loginUser.getLogId() + " Get -> PO Head List Started --- ");
        BaseResponse<List<PurchaseOrderHead>> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> PO Head List DB Call --- ");
            baseResponse.setCode(1);
            baseResponse.setMessage("Get PO Head List Successfully");
            baseResponse.setStatus(200);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to get PO Head List --- ");
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get PO Head List ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> PO Head List End --- ");
        return baseResponse;
    }


    @Override
    public BaseResponse saveSerBatNumber(SaveSerialBatchNumberRequest saveSerialBatchNumberRequest) {
        log.info(loginUser.getLogId() + " Save -> serial batch number Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Save -> serial batch number DB Call --- ");
            baseResponse = saveSerBatNo(saveSerialBatchNumberRequest);

        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100062);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> serial batch number End --- ");
        return baseResponse;
    }



    @Override
    public BaseResponse saveASNLineSupplier(List<SaveAsnLineRequest> saveAsnLineRequestList) {
        log.info(loginUser.getLogId() + " Save -> Asn Line List Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            List<ASNLine> asnLineList = new ArrayList<>();
            for (SaveAsnLineRequest saveAsnLineRequest : saveAsnLineRequestList) {
                ASNLine asnLine = asnLineRepository.findById(saveAsnLineRequest.getPoLineId()).get();

                asnLineList.add(asnLine);
            }
            log.info(loginUser.getLogId() + " Save -> Asn Line List DB Call --- ");
            asnLineRepository.saveAll(asnLineList);
            baseResponse.setCode(1);
            baseResponse.setMessage("Save Asn Line List Successfully");
            baseResponse.setStatus(200);
            baseResponse.setData(null);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Save -> Failed to get Asn Line List --- ");
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Save Asn Line List");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> Asn Line List End --- ");
        return baseResponse;
    }

    private BaseResponse saveSerBatNo(SaveSerialBatchNumberRequest saveSerialBatchNumberRequest) {
        BaseResponse baseResponse = new BaseResponse<>();
        List<SerialBatchNumber> serialBatchNumberList = new ArrayList<>();
        List<SerialBatchNumber> serialBatchNumbers = null;
        Integer serialBatchNumberSize = 0;
        Integer allocatedSize = 0;
        List<SerialBatchNumberRequest> invalidSerialBatchNumberList = new ArrayList<>();
        PurchaseOrderLine purchaseOrderLine = null;
        SerialBatchValidResponse serialBatchValidResponse = new SerialBatchValidResponse();
        ASNLine asnLine = null;
        if (saveSerialBatchNumberRequest.getPoLineId() != null) {
            purchaseOrderLine = purchaseOrderLineRepository.findByIdAndSubOrganizationIdAndIsDeleted(saveSerialBatchNumberRequest.getPoLineId(), loginUser.getSubOrgId(), false);
            serialBatchNumbers = serBatNumRepository.findBySubOrganizationIdAndPurchaseOrderLineIdAndIsDeleted(loginUser.getSubOrgId(), saveSerialBatchNumberRequest.getPoLineId(), false);
            allocatedSize = purchaseOrderLine.getPurchaseOrderQuantity();
        } else if (saveSerialBatchNumberRequest.getAsnLineId() != null) {
            asnLine = asnLineRepository.findByIdAndSubOrganizationIdAndIsDeleted(saveSerialBatchNumberRequest.getAsnLineId(), loginUser.getSubOrgId(), false);
            serialBatchNumbers = serBatNumRepository.findBySubOrganizationIdAndAsnLineIdAndIsDeleted(loginUser.getSubOrgId(), saveSerialBatchNumberRequest.getAsnLineId(), false);
            allocatedSize = asnLine.getAllocatedQuantity();
        }
        serialBatchNumberSize = serialBatchNumbers.size();
        Map<String, SerialBatchNumber> serialBatchNumberMap = serialBatchNumbers.stream().collect(Collectors.toMap(k -> k.getSerialBatchNumber(), v -> v));
        serialBatchValidResponse.setTotalSerialBatchNumbers(serialBatchNumbers.size());
        for (SerialBatchNumberRequest serialBatchNumberRequest : saveSerialBatchNumberRequest.getSerialBatchNumberList()) {
            if (serialBatchNumberMap.containsKey(serialBatchNumberRequest.getSerialBatchNumber())) {
                invalidSerialBatchNumberList.add(serialBatchNumberRequest);
            } else {
                SerialBatchNumber serialBatchNumber = new SerialBatchNumber();
                serialBatchNumber.setPurchaseOrderLine(purchaseOrderLine);
                serialBatchNumber.setAsnLine(asnLine);
                serialBatchNumber.setOrganizationId(loginUser.getOrgId());
                serialBatchNumber.setSubOrganizationId(loginUser.getSubOrgId());
                serialBatchNumber.setSerialBatchNumber(serialBatchNumberRequest.getSerialBatchNumber());
                serialBatchNumber.setManufacturingDate(stringToDate(serialBatchNumberRequest.getManufacturingDate()));
                serialBatchNumber.setExpiryDate(stringToDate(serialBatchNumberRequest.getExpiryDate()));
                serialBatchNumber.setIsDeleted(false);
                serialBatchNumber.setCreatedBy(loginUser.getUserId());
                serialBatchNumber.setCreatedOn(new Date());
                if (serialBatchNumberSize >= allocatedSize) {
                    baseResponse.setCode(0);
                    baseResponse.setMessage("Number of serial number exceeds allocated quantity");
                    baseResponse.setStatus(500);
                    baseResponse.setData(null);
                    return baseResponse;
                } else {
                    serialBatchNumberSize++;
                    serialBatchNumberList.add(serialBatchNumber);
                }
            }
        }
        serialBatchValidResponse.setInvalidSerialBatchNumberList(invalidSerialBatchNumberList);
        if (!CollectionUtils.isEmpty(invalidSerialBatchNumberList)) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100065);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(Arrays.asList(serialBatchValidResponse));
            return baseResponse;
        } else {
            serialBatchValidResponse.setTotalSerialBatchNumbers(serialBatchNumbers.size() + serialBatchNumberList.size());
            serBatNumRepository.saveAll(serialBatchNumberList);
        }
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100066);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(Arrays.asList(serialBatchValidResponse));
        return baseResponse;
    }

    @Override
    public BaseResponse saveTransportDetails(SaveTransportRequest saveTransportRequest) {
        log.info(loginUser.getLogId() + " Save -> transport Details Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Save -> transport Details DB Call --- ");
            baseResponse = saveTransDet(saveTransportRequest);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Save -> Failed to save transport Details  --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100067);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> transport Details End --- ");
        return baseResponse;
    }

    private BaseResponse saveTransDet(SaveTransportRequest saveTransportRequest) {
        BaseResponse baseResponse = new BaseResponse<>();
        Transport transportDetails = new Transport();
        transportDetails.setOrganizationId(loginUser.getOrgId());
        transportDetails.setSubOrganizationId(loginUser.getSubOrgId());
        if (saveTransportRequest.getAsnHeadId() != null) {
            Transport transport = transportDetailsRepository.findByIsDeletedAndSubOrganizationIdAndAsnHeadId(false, loginUser.getSubOrgId(), saveTransportRequest.getAsnHeadId());
            if (transport == null) {
                transportDetails.setAsnHead(asnHeadRepository.findById(saveTransportRequest.getAsnHeadId()).get());
            } else {
                ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100068);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(null);
                baseResponse.setStatus(responseMessage.getStatus());
                return baseResponse;
            }

        } else if (saveTransportRequest.getPoHeadId() != null) {
            Transport transport = transportDetailsRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), saveTransportRequest.getPoHeadId());
            if (transport == null) {
                transportDetails.setPurchaseOrderHead(purchaseOrderHeadRepository.findById(saveTransportRequest.getPoHeadId()).get());
            } else {
                ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100069);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(null);
                baseResponse.setStatus(responseMessage.getStatus());
                return baseResponse;
            }
        }
        transportDetails.setShipMode(saveTransportRequest.getShipMode());
        transportDetails.setShipThrough(saveTransportRequest.getShipThrough());
        transportDetails.setContactPerson(saveTransportRequest.getContactName());
        transportDetails.setTransporter(saveTransportRequest.getTransporterName());
        transportDetails.setMobile(saveTransportRequest.getMobileNo());
        transportDetails.setLandline(saveTransportRequest.getLandlineNo());
        transportDetails.setLorryReceipt(saveTransportRequest.getLorryReceiptNumber());

        if (saveTransportRequest.getPucCertificateNumberValidTill() != null) {
            transportDetails.setPucCertificateValidTill(stringToDate(saveTransportRequest.getPucCertificateNumberValidTill()));
        }
        if (saveTransportRequest.getLicenceNumberValidTill() != null) {
            transportDetails.setLicenseNumberValidTill(stringToDate(saveTransportRequest.getLicenceNumberValidTill()));
        }

        transportDetails.setVehicleType(saveTransportRequest.getVehicleType());
        transportDetails.setVehicleNumber(saveTransportRequest.getVehicleNumber());
        transportDetails.setVehicleWeight(saveTransportRequest.getVehicleWeight());
        transportDetails.setPucCertificateNumber(saveTransportRequest.getPucCertificateNumber());
        transportDetails.setPucCenter(saveTransportRequest.getPucCenter());
        transportDetails.setPucCenterId(saveTransportRequest.getPucCenterId());
        transportDetails.setDriver(saveTransportRequest.getDriver());
        transportDetails.setLicenseNumber(saveTransportRequest.getLicenceNumber());
        transportDetails.setLicenseType(saveTransportRequest.getLicenceType());
        transportDetails.setIsDeleted(false);
        transportDetails.setCreatedBy(loginUser.getUserId());
        transportDetails.setCreatedOn(new Date());
        transportDetailsRepository.save(transportDetails);
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100070);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(null);
        return baseResponse;
    }

    @Override
    public BaseResponse saveInsuranceDetails(SaveInsuranceRequest saveInsuranceRequest) {
        log.info(loginUser.getLogId() + " Save -> Insurance Details Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Save -> Insurance Details DB Call --- ");
            baseResponse = saveInsurance(saveInsuranceRequest);

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Save -> Failed to save Insurance Details  --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100071);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> Insurance Details End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse saveInvoice(SaveInvoiceRequest saveInvoiceRequest) {
        log.info(loginUser.getLogId() + " Save -> Invoice Details Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Save -> Invoice Details DB Call --- ");
            baseResponse = saveInvoiceDetails(saveInvoiceRequest);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Save -> Failed to save Invoice Details  --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100072);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> Invoice Details End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse saveQCCertificate(List<SaveQCCertificateRequest> saveQCCertificateRequestList) {
        log.info(loginUser.getLogId() + " Save -> QC Certificate details Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Save -> QC Certificate DB Call --- ");
            baseResponse = saveQCDet(saveQCCertificateRequestList);

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Save -> Failed to save QC Certificate details --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100073);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> QC Certificate details End --- ");
        return baseResponse;
    }

    private BaseResponse saveQCDet(List<SaveQCCertificateRequest> saveQCCertificateRequestList) {
        BaseResponse baseResponse = new BaseResponse<>();
        List<QCCertificate> qcCertificateList = new ArrayList<>();
        for (SaveQCCertificateRequest saveQCCertificateRequest : saveQCCertificateRequestList) {
            QCCertificate qcCertificate = new QCCertificate();
            qcCertificate.setSubOrganizationId(loginUser.getSubOrgId());
            qcCertificate.setOrganizationId(loginUser.getOrgId());
            if (saveQCCertificateRequest.getAsnLineId() != null) {
                qcCertificate.setAsnLine(asnLineRepository.findById(saveQCCertificateRequest.getAsnLineId()).get());
            } else if (saveQCCertificateRequest.getPoLineId() != null) {
                qcCertificate.setPurchaseOrderLine(purchaseOrderLineRepository.findById(saveQCCertificateRequest.getPoLineId()).get());
            }
            if (saveQCCertificateRequest.getItemId() != null) {
                qcCertificate.setItem(itemRepository.findById(saveQCCertificateRequest.getItemId()).get());
            } else {
                ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100074);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(null);
                baseResponse.setStatus(responseMessage.getStatus());
                return baseResponse;
            }
            qcCertificate.setQcDocumentId(saveQCCertificateRequest.getQcCertificateId());
            qcCertificate.setQcNumber(saveQCCertificateRequest.getQcNumber());
            qcCertificate.setQcDate(stringToDate(saveQCCertificateRequest.getQcDate()));
            qcCertificate.setIsDeleted(false);
            qcCertificate.setCreatedBy(loginUser.getUserId());
            qcCertificate.setCreatedOn(new Date());
            qcCertificateList.add(qcCertificate);
        }
        qcCertificateRepository.saveAll(qcCertificateList);
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100075);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(null);
        return baseResponse;
    }

    private BaseResponse saveInvoiceDetails(SaveInvoiceRequest saveInvoiceRequest) {
        BaseResponse baseResponse = new BaseResponse<>();
        InvoiceHead invoiceHead = null;
        boolean isAsn = false;
        List<InvoiceLine> invoiceLineList = new ArrayList<>();
        if (saveInvoiceRequest.getId() != null) {
            invoiceHead = invoiceHeadRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, saveInvoiceRequest.getId());
        } else {
            invoiceHead = new InvoiceHead();
            List<InvoiceHead> invoiceHeadList = invoiceHeadRepository.findBySubOrganizationIdAndInvoiceNumberAndIsDeleted(loginUser.getSubOrgId(), saveInvoiceRequest.getInvoiceNumber(), false);
            if (!CollectionUtils.isEmpty(invoiceHeadList)) {
                baseResponse.setCode(0);
                baseResponse.setMessage("Invoice Number is already used ");
                baseResponse.setData(null);
                baseResponse.setStatus(500);
                return baseResponse;
            }
            invoiceHead.setCreatedBy(loginUser.getUserId());
            invoiceHead.setCreatedOn(new Date());
        }

        invoiceHead.setSubOrganizationId(loginUser.getSubOrgId());
        invoiceHead.setOrganizationId(loginUser.getOrgId());
        if (saveInvoiceRequest.getAsnHeadId() != null) {
            invoiceHead.setAsnHead(asnHeadRepository.findById(saveInvoiceRequest.getAsnHeadId()).get());
            isAsn = true;
        } else if (saveInvoiceRequest.getPoHeadId() != null) {
            invoiceHead.setPurchaseOrderHead(purchaseOrderHeadRepository.findById(saveInvoiceRequest.getPoHeadId()).get());
        }
        if (saveInvoiceRequest.getInvoiceDocumentId() != null) {
            invoiceHead.setInvoiceDocumentId(saveInvoiceRequest.getInvoiceDocumentId());
        } else {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100076);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            return baseResponse;
        }
        invoiceHead.setInvoiceNumber(saveInvoiceRequest.getInvoiceNumber());
        invoiceHead.setInvoiceDate(stringToDate(saveInvoiceRequest.getDate()));
        invoiceHead.setIsDeleted(false);
        invoiceHead.setModifiedBy(loginUser.getUserId());
        invoiceHead.setModifiedOn(new Date());
        invoiceHeadRepository.save(invoiceHead);
        for (SaveInvoiceLineRequest saveInvoiceLineRequest : saveInvoiceRequest.getSaveInvoiceLineRequest()) {
            InvoiceLine invoiceLine = new InvoiceLine();
            Item item = itemRepository.findByIsDeletedAndSubOrganizationIdAndNameAndItemId(false, loginUser.getSubOrgId(), saveInvoiceLineRequest.getItemName(), saveInvoiceLineRequest.getItemCode());
            invoiceLine.setOrganizationId(loginUser.getOrgId());
            invoiceLine.setSubOrganizationId(loginUser.getSubOrgId());
            invoiceLine.setInvoiceHead(invoiceHead);
            invoiceLine.setInvoiceQuantity(saveInvoiceLineRequest.getInvoiceItemQuantity());
            invoiceLine.setItem(item);
            invoiceLine.setIsDeleted(false);
            invoiceLine.setCreatedBy(loginUser.getUserId());
            invoiceLine.setCreatedOn(new Date());
            if(isAsn) {
                ASNLine asnLine = asnLineRepository.findByAsnHeadIdIdAndItemIdAndSubOrganizationIdAndIsDeleted(saveInvoiceRequest.getAsnHeadId(), item.getId(), loginUser.getSubOrgId(), false);
                Container container = containerRepository.findByItemIdAndIsDeletedAndSubOrganizationId(asnLine.getItem().getId(), false, loginUser.getSubOrgId());
                double totalContainer = Math.ceil((double)saveInvoiceLineRequest.getInvoiceItemQuantity() / container.getItemQty());
                asnLine.setNumberOfContainer((int) totalContainer);
                asnLineRepository.save(asnLine);
            } else {
                PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndItemIdAndPurchaseOrderHeadId(loginUser.getSubOrgId(), false, item.getId(), saveInvoiceRequest.getPoHeadId());
                Container container = containerRepository.findByItemIdAndIsDeletedAndSubOrganizationId(purchaseOrderLine.getItem().getId(), false, loginUser.getSubOrgId());
                double totalContainer = Math.ceil((double)saveInvoiceLineRequest.getInvoiceItemQuantity() / container.getItemQty());
                purchaseOrderLine.setNumberOfContainer((int) totalContainer);
                purchaseOrderLineRepository.save(purchaseOrderLine);
            }
            invoiceLineList.add(invoiceLine);
        }
        invoiceLineRepository.saveAll(invoiceLineList);
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100077);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(null);
        return baseResponse;
    }

    private BaseResponse<Insurance> saveInsurance(SaveInsuranceRequest saveInsuranceRequest) {
        BaseResponse<Insurance> baseResponse = new BaseResponse<>();
        Insurance insurance = null;
        if (saveInsuranceRequest.getId() != null) {
            insurance = insuranceDetailsRepository.findByIdAndIsDeleted(saveInsuranceRequest.getId(), false);
        } else {
            insurance = new Insurance();
            if (saveInsuranceRequest.getAsnHeadId() != null) {
                Insurance insuranceDetails1 = insuranceDetailsRepository.findByIsDeletedAndSubOrganizationIdAndAsnHeadId(false, loginUser.getSubOrgId(), saveInsuranceRequest.getAsnHeadId());
                if (insuranceDetails1 == null) {
                    insurance.setAsnHead(asnHeadRepository.findById(saveInsuranceRequest.getAsnHeadId()).get());
                } else {
                    ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100078);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(null);
                    baseResponse.setStatus(responseMessage.getStatus());
                    return baseResponse;
                }
            } else if (saveInsuranceRequest.getPoHeadId() != null) {
                Insurance insuranceDetails1 = insuranceDetailsRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), saveInsuranceRequest.getPoHeadId());
                if (insuranceDetails1 == null) {
                    insurance.setPurchaseOrderHead(purchaseOrderHeadRepository.findById(saveInsuranceRequest.getPoHeadId()).get());
                } else {
                    ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100079);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setData(null);
                    baseResponse.setStatus(responseMessage.getStatus());
                    return baseResponse;
                }
            }
        }

        insurance.setOrganizationId(loginUser.getOrgId());
        insurance.setSubOrganizationId(loginUser.getSubOrgId());
        insurance.setInsuredWith(saveInsuranceRequest.getInsuredWith());
        insurance.setPolicyNumber(saveInsuranceRequest.getPolicyNumber());
        insurance.setIssuingOfficeId(saveInsuranceRequest.getIssuingOfficeId());
        insurance.setIssuingOfficeAddress(saveInsuranceRequest.getIssuingOfficeAddress());
        insurance.setTransitInsurance(saveInsuranceRequest.getTransitInsuranceName());
        insurance.setValidTill(stringToDate(saveInsuranceRequest.getValidTill()));
        insurance.setContactPerson(saveInsuranceRequest.getContactPerson());
        insurance.setMobile(saveInsuranceRequest.getMobile());
        insurance.setLandline(saveInsuranceRequest.getLandline());
        insurance.setIsDeleted(false);
        insurance.setCreatedBy(loginUser.getUserId());
        insurance.setCreatedOn(new Date());
        insuranceDetailsRepository.save(insurance);

        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100080);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(Arrays.asList(insurance));
        return baseResponse;
    }

    @Override
    public BaseResponse saveDocumentDetails(List<SaveDocumentRequest> saveDocumentRequest) {
        log.info(loginUser.getLogId() + " Save -> Document Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Save -> Document DB Call --- ");
            baseResponse = saveDocDet(saveDocumentRequest);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Save -> Failed to Save Document --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100081);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Save -> Document End --- ");
        return baseResponse;
    }

    private BaseResponse saveDocDet(List<SaveDocumentRequest> saveDocumentRequestList) {
        BaseResponse baseResponse = new BaseResponse<>();
        List<Document> documentList = new ArrayList<>();
        for (SaveDocumentRequest saveDocumentRequest : saveDocumentRequestList) {
            Document document = null;
            if (saveDocumentRequest.getId() != null) {
                document = documentRepository.findByIsDeletedAndSubOrganizationIdAndId(false, loginUser.getSubOrgId(), saveDocumentRequest.getId());
            } else {
                document = new Document();
                document.setCreatedBy(loginUser.getUserId());
                document.setCreatedOn(new Date());

            }
            document.setOrganizationId(loginUser.getOrgId());
            document.setSubOrganizationId(loginUser.getSubOrgId());
            if (saveDocumentRequest.getAsnHeadId() != null) {
                document.setAsnHead(asnHeadRepository.findById(saveDocumentRequest.getAsnHeadId()).get());
            } else if (saveDocumentRequest.getPoHeadId() != null) {
                document.setPurchaseOrderHead(purchaseOrderHeadRepository.findById(saveDocumentRequest.getPoHeadId()).get());
            }
            if (saveDocumentRequest.getDocumentId() != null) {
                document.setDocumentId(saveDocumentRequest.getDocumentId());
            } else {
                ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100082);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(null);
                baseResponse.setStatus(responseMessage.getStatus());
                return baseResponse;
            }
            document.setDocumentDate(stringToDate(saveDocumentRequest.getDocumentDate()));
            document.setDocumentName(saveDocumentRequest.getDocumentName());
            document.setIsDeleted(false);
            documentList.add(document);
        }
        documentRepository.saveAll(documentList);
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100083);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(documentList);
        return baseResponse;
    }

    @Override
    public BaseResponse<ASNSupplierDropdownResponse> getSupplierDropdown() {
        log.info(loginUser.getLogId() + " Get -> Asn Supplier dropdown Started --- ");
        BaseResponse<ASNSupplierDropdownResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> Asn Supplier dropdown DB Call --- ");
            baseResponse.setCode(1);
            baseResponse.setMessage("Get ASN Supplier Dropdown");
            baseResponse.setStatus(200);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to get Asn Supplier dropdown --- ");
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get Asn Supplier dropdown ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> ASN Supplier Dropdown End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<AsnReceiptResponse> getAsnReceiptDet(Integer asnId, Integer poHeadId) {
        log.info(loginUser.getLogId() + " Get -> ASN Receipt Details Started --- ");
        BaseResponse<AsnReceiptResponse> baseResponse = new BaseResponse<>();
        try {
            AsnReceiptResponse asnReceiptResponse = new AsnReceiptResponse();
            log.info(loginUser.getLogId() + " Get -> ASN Receipt Details Sub Organization :: " + loginUser.getSubOrgId() + " asn Head Id :: " + asnId + " PO HeadId :: " + poHeadId);
            if (asnId != null && asnId > 0) {
                ASNHead asnHead = asnHeadRepository.findByIdAndSubOrganizationIdAndIsDeleted(asnId, loginUser.getSubOrgId(), false);
                List<ASNLine> asnLineList = asnLineRepository.findByAsnHeadIdIdAndSubOrganizationIdAndIsDeleted(asnId, loginUser.getSubOrgId(), false);
                Transport transportDetails = transportDetailsRepository.findByIsDeletedAndSubOrganizationIdAndAsnHeadId(false, loginUser.getSubOrgId(), asnId);
                Insurance insuranceDetails = insuranceDetailsRepository.findByIsDeletedAndSubOrganizationIdAndAsnHeadId(false, loginUser.getSubOrgId(), asnId);
                List<Document> documentList = documentRepository.findByIsDeletedAndSubOrganizationIdAndAsnHeadId(false, loginUser.getSubOrgId(), asnId);
                List<InvoiceHead> invoiceHeadList = invoiceHeadRepository.findBySubOrganizationIdAndAsnHeadIdAndIsDeleted(loginUser.getSubOrgId(), asnId, false);
                List<QCCertificate> qcCertificateList = qcCertificateRepository.findBySubOrganizationIdAndAsnLineAsnHeadIdIdAndIsDeleted(loginUser.getSubOrgId(), asnId, false);
                SupplierItemMapper supplierItemMapper = supplierItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndSupplierIdAndItemId(loginUser.getSubOrgId(), false, asnHead.getSupplier().getId(), asnLineList.get(0).getItem().getId());
                asnReceiptResponse.setLeadTime(supplierItemMapper.getLeadTime());
                asnReceiptResponse.setIsDay(supplierItemMapper.getIsDay());
                asnReceiptResponse.setAsnHead(asnHead);
                asnReceiptResponse.setAsnLineList(asnLineList);
                asnReceiptResponse.setBuyer(userRepository.findById(asnHead.getCreatedBy()).get());
                asnReceiptResponse.setTransportDetails(transportDetails);
                asnReceiptResponse.setInsuranceDetails(insuranceDetails);
                asnReceiptResponse.setInvoiceHeadList(invoiceHeadList);
                asnReceiptResponse.setQcCertificateList(qcCertificateList);
                asnReceiptResponse.setDocumentList(documentList);
            } else if (poHeadId != null && poHeadId > 0) {
                PurchaseOrderHead poHead = purchaseOrderHeadRepository.findBySubOrganizationIdAndIdAndIsDeleted(loginUser.getSubOrgId(), poHeadId, false);
                List<PurchaseOrderLine> poLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseOrderHeadId(loginUser.getSubOrgId(), false, poHeadId);
                Transport transportDetails = transportDetailsRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), poHeadId);
                Insurance insuranceDetails = insuranceDetailsRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), poHeadId);
                List<Document> documentList = documentRepository.findByIsDeletedAndSubOrganizationIdAndPurchaseOrderHeadId(false, loginUser.getSubOrgId(), poHeadId);
                List<InvoiceHead> invoiceHeadList = invoiceHeadRepository.findBySubOrganizationIdAndPurchaseOrderHeadIdAndIsDeleted(loginUser.getSubOrgId(), poHeadId, false);
                List<QCCertificate> qcCertificateList = qcCertificateRepository.findBySubOrganizationIdAndPurchaseOrderLinePurchaseOrderHeadIdAndIsDeleted(loginUser.getSubOrgId(), poHeadId, false);

                SupplierItemMapper supplierItemMapper = supplierItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndSupplierIdAndItemId(loginUser.getSubOrgId(), false, poHead.getSupplierId(), poLineList.get(0).getItem().getId());
                asnReceiptResponse.setLeadTime(supplierItemMapper.getLeadTime());
                asnReceiptResponse.setIsDay(supplierItemMapper.getIsDay());
                asnReceiptResponse.setPoHead(poHead);
                asnReceiptResponse.setPoLineList(poLineList);
                asnReceiptResponse.setTransportDetails(transportDetails);
                asnReceiptResponse.setInsuranceDetails(insuranceDetails);
                asnReceiptResponse.setInvoiceHeadList(invoiceHeadList);
                asnReceiptResponse.setQcCertificateList(qcCertificateList);
                asnReceiptResponse.setDocumentList(documentList);
            }

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100086);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(Arrays.asList(asnReceiptResponse));
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to get ASN Receipt Details --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100087);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> ASN Receipt Details  End --- ");
        return baseResponse;
    }

    @Override
    public ResponseEntity<byte[]> genarateExcel() {
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("SerialBatch");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Serial Number", "Manufacture Date", "Expiry Date"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Create an in-memory output stream for the Excel data
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            header.setContentDispositionFormData("attachment", "ProductData.xlsx");
            header.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(header)
                    .body(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BaseResponse<String> generateSupBarcode(List<String> asnNumberList) {
        try {
            BaseResponse<String> baseResponse = new BaseResponse();
            List<String> barcodeList = new ArrayList<>();
            for (String asnNumber : asnNumberList) {
                byte[] barcode = BarcodeGenerator.generateBarcode(asnNumber);
                String base64String = Base64.getEncoder().encodeToString(barcode);
                barcodeList.add(base64String);
            }
            baseResponse.setCode(1);
            baseResponse.setMessage("Get ASN Receipt Details");
            baseResponse.setStatus(200);
            baseResponse.setData(barcodeList);
            return baseResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BaseResponse updateAsnPrint(Integer asnId) {
        log.info(loginUser.getLogId() + " Get -> ASN Receipt Details Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> ASN Receipt Details DB Call --- ");

            ASNHead asnHead = null;

            if (asnHead != null) {
                asnHeadRepository.save(asnHead);
                baseResponse.setCode(1);
                baseResponse.setMessage("Get ASN Receipt Details");
                baseResponse.setStatus(200);
                baseResponse.setData(null);
            } else {
                baseResponse.setCode(0);
                baseResponse.setMessage("Asn is already printed please contact admin to give print permission");
                baseResponse.setData(null);
                baseResponse.setStatus(500);
            }
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to get ASN Receipt Details --- ");
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get ASN Receipt Details ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> ASN Receipt Details  End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse updateAsnLineStatus(List<UpdateAsnLineStatus> asnLineStatusList) {
        log.info(loginUser.getLogId() + " UPDATE -> ASN LINE STATUS UPDATE STARTED --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " UPDATE -> ASN LINE STATUS DB CALL --- ");
            List<ASNLine> asnLineList = new ArrayList<>();
            List<PurchaseOrderLine> purchaseOrderLineList = new ArrayList<>();
            for (UpdateAsnLineStatus updateAsnLineStatus : asnLineStatusList) {
                if (updateAsnLineStatus.getAsnLineId() != null) {
                    ASNLine asnLine = asnLineRepository.findByIdAndSubOrganizationIdAndIsDeleted(updateAsnLineStatus.getAsnLineId(), loginUser.getSubOrgId(), false);
                    asnLine.setStatus(purchaseStatusRepository.findByStatusNameAndStatusType(updateAsnLineStatus.getStatus(), "ASN Line"));
                    if (updateAsnLineStatus.getReasonId() != null) {
                        asnLine.setReason(reasonRepository.findByIsDeletedAndId(false, updateAsnLineStatus.getReasonId()));
                    } else if (updateAsnLineStatus.getReasonFileId() != null) {
                        asnLine.setReasonDocumentId(updateAsnLineStatus.getReasonFileId());
                    }
                    asnLineList.add(asnLine);
                } else if (updateAsnLineStatus.getPoLineId() != null) {
                    PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findById(updateAsnLineStatus.getPoLineId()).get();
                    purchaseOrderLine.setStatus(purchaseStatusRepository.findByStatusNameAndStatusType(updateAsnLineStatus.getStatus(), "PO Line"));
                    purchaseOrderLineList.add(purchaseOrderLine);
                }
            }
            if (!CollectionUtils.isEmpty(asnLineList)) {
                asnLineRepository.saveAll(asnLineList);
            } else if (!CollectionUtils.isEmpty(purchaseOrderLineList)) {
                purchaseOrderLineRepository.saveAll(purchaseOrderLineList);
            }
            baseResponse.setCode(1);
            baseResponse.setMessage("UPDATED ASN LINE STATUS SUCCESSFULLY ");
            baseResponse.setData(null);
            baseResponse.setStatus(200);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " UPDATE -> Failed to UPDATE ASN LINE STATUS --- ");
            baseResponse.setCode(0);
            baseResponse.setMessage("FAILED TO UPDATE ASN LINE STATUS ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " UPDATE -> UPDATE ASN LINE STATUS END --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse getSerBatNumber(Integer asnLineId, Integer poLineId) {
        log.info(loginUser.getLogId() + " Get -> Serial Batch Number Started --- ");
        BaseResponse<SerialBatchNumber> baseResponse = new BaseResponse<>();
        try {
            List<SerialBatchNumber> serialBatchNumberList = null;
            if (asnLineId != null) {
                log.info(loginUser.getLogId() + " Get -> SERIAL BATCH NUMBER DB Call With SUB ORGANIZATION  ID :: " + loginUser.getSubOrgId() + " Asn Line Id :: " + asnLineId);
                serialBatchNumberList = serBatNumRepository.findBySubOrganizationIdAndAsnLineIdAndIsDeleted(loginUser.getSubOrgId(), asnLineId, false);
            } else if (poLineId != null) {
                log.info(loginUser.getLogId() + " Get -> SERIAL BATCH NUMBER DB Call With SUB ORGANIZATION  ID :: " + loginUser.getSubOrgId() + " Purchase Order Line Id :: " + poLineId);
                serialBatchNumberList = serBatNumRepository.findBySubOrganizationIdAndPurchaseOrderLineIdAndIsDeleted(loginUser.getSubOrgId(), poLineId, false);
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100094);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(serialBatchNumberList);
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to get Serial Batch Numbers --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100095);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> Serial Batch Numbers End --- ");
        return baseResponse;
    }

    @Override
    public byte[] downloadSerialBarcode(Integer asnLineId, Integer poLineId) {
        log.info(loginUser.getLogId() + " Download -> Serial Batch Number Started --- ");
        BaseResponse baseResponse = new BaseResponse<>();
        byte[] file = null;
        try {
            List<SerialBatchNumber> serialBatchNumberList = null;
            if (asnLineId != null) {
                log.info(loginUser.getLogId() + " Download -> SERIAL BATCH NUMBER DB Call With SUB ORGANIZATION  ID :: " + loginUser.getOrgId() + " Asn Line Id :: " + asnLineId);
                serialBatchNumberList = serBatNumRepository.findBySubOrganizationIdAndAsnLineIdAndIsDeleted(loginUser.getSubOrgId(), asnLineId, false);
            } else if (poLineId != null) {
                log.info(loginUser.getLogId() + " Download -> SERIAL BATCH NUMBER DB Call With SUB ORGANIZATION  ID :: " + loginUser.getOrgId() + " Purchase Order Line Id :: " + poLineId);
                serialBatchNumberList = serBatNumRepository.findBySubOrganizationIdAndPurchaseOrderLineIdAndIsDeleted(loginUser.getSubOrgId(), poLineId, false);
            }
            file = BarcodeGenerator.generatePdfWithBarcodes(serialBatchNumberList);

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to download serial batch number --- ");
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to download serial batch number ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        return file;
    }

    public static Date stringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle parsing errors
        }
    }

    public static LocalTime stringToLocalTime(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT_PATTERN);
        return LocalTime.parse(timeString, formatter);
    }

    public static Time stringToSqlTime(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT_PATTERN);
        LocalTime localTime = LocalTime.parse(timeString, formatter);
        return Time.valueOf(localTime);
    }

    public int findMaxNumberDaysAsn(List<ASNLine> asnLineList) {
        List<Integer> itemIds = asnLineList.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());
        List<SupplierItemMapper> supplierItemMapperList = supplierItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndSupplierIdAndItemIdIn(loginUser.getSubOrgId(), false, asnLineList.get(0).getItemScheduleSupplier().getSupplier().getId(), itemIds);

        int overallMaxLeadTime = supplierItemMapperList.stream()
                .mapToInt(SupplierItemMapper::getLeadTimeInDays)
                .max()
                .orElse(0);

        return overallMaxLeadTime + 1;
    }

    public int findMaxNumberDaysPurchase(List<PurchaseOrderLine> purchaseOrderLineList) {
        List<Integer> itemIds = purchaseOrderLineList.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());
        List<SupplierItemMapper> supplierItemMapperList = supplierItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndSupplierIdAndItemIdIn(loginUser.getSubOrgId(), false, purchaseOrderLineList.get(0).getPurchaseOrderHead().getSupplierId(), itemIds);

        int overallMaxLeadTime = supplierItemMapperList.stream()
                .mapToInt(SupplierItemMapper::getLeadTimeInDays)
                .max()
                .orElse(0);

        return overallMaxLeadTime + 1;
    }
}
