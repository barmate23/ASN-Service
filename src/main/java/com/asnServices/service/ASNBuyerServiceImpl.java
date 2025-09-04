package com.asnServices.service;

import com.asnServices.EmailModule.EmailSender;
import com.asnServices.configuration.LoginUser;
import com.asnServices.model.*;
import com.asnServices.repository.*;
import com.asnServices.response.*;
import com.asnServices.request.AsnBuyerKey;
import com.asnServices.request.SaveAsnRequest;
import com.asnServices.request.UpdateAsnRequest;
import com.asnServices.request.UpdateAsnStatusReq;
import com.asnServices.utils.APIConstants;
import com.asnServices.utils.BarcodeGenerator;
import com.asnServices.utils.GlobalMessages;
import com.asnServices.utils.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.text.ParseException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ASNBuyerServiceImpl implements ASNBuyerService {
    @Autowired
    AsnHeadRepository asnHeadRepository;
    @Autowired
    ReasonRepository reasonRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    SupplierItemMapperRepository supplierItemMapperRepository;

    @Autowired
    BuyerItemMapperRepository buyerItemMapperRepository;

    @Autowired
    private StockBalanceRepository stockBalanceRepository;

    @Autowired
    PurchaseStatusRepository purchaseStatusRepository;
    @Autowired
    PurchaseOrderHeadRepository purchaseOrderHeadRepository;
    @Autowired
    AsnLineRepository asnLineRepository;
    @Autowired
    PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Autowired
    EmailSender emailSender;

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    private PPELineRepository ppeLineRepository;
    @Autowired
    private PPEHeadRepository ppeHeadRepository;

    @Autowired
    private ItemScheduleRepository itemScheduleRepository;

    @Autowired
    private ItemScheduleSupplierRepository itemScheduleSupplierRepository;
    @Autowired
    BarcodeGenerator barcodeGenerator;
    @Autowired
    private LoginUser loginUser;
    private static final String DEFAULT_TIME_FORMAT_PATTERN = "HH:mm";

    @Override
    public BaseResponse<ItemDateResponse> getDateForItem() {
        log.info(ServiceConstants.GET_DATE_FOR_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET DATE FOR ITEM Started ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ItemDateResponse> baseResponse = new BaseResponse<>();
        try {
            List<ItemDateResponse> itemDateResponseList = new ArrayList<>();
            log.info(ServiceConstants.GET_DATE_FOR_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET DATE FOR ITEM DB CALL ::  sub organization Id : " + loginUser.getSubOrgId());
            List<PPEHead> ppeHeadList = ppeHeadRepository.findBySubOrganizationIdAndIsDeletedAndPpeStatusStatusNameIn(loginUser.getSubOrgId(), false, Arrays.asList("CONFIRM", "EXECUTE"));
            List<BuyerItemMapper> buyerItemMappers = buyerItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndUserId(loginUser.getSubOrgId(), false, loginUser.getUserId());
            List<Integer> itemIdList = buyerItemMappers.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());

            for (PPEHead ppeHead : ppeHeadList) {
                List<PPELine> ppeLineList = ppeLineRepository.findByIsDeletedAndSubOrganizationIdAndPpeHeadIdAndItemIdIn(false, loginUser.getSubOrgId(), ppeHead.getId(), itemIdList);
                if (!ppeLineList.isEmpty() && shortProductItem(ppeLineList)) {
                    ItemDateResponse itemDateResponse = new ItemDateResponse();
                    int highestLeadTime = findMaxNumberDays(ppeLineList);
                    int shortestLeadTime = findMinNumberDays(ppeLineList);
                    itemDateResponse.setPpeHeadId(ppeHead.getId());
                    itemDateResponse.setRequiredDate(ppeHead.getStartDate());
                    itemDateResponse.setRemainingDays(BarcodeGenerator.getRemainingDays(ppeHead.getStartDate()));
                    itemDateResponse.setShortestLeadTime(shortestLeadTime);
                    itemDateResponse.setHighestLeadTime(highestLeadTime);
                    itemDateResponseList.add(itemDateResponse);
                }
            }
            itemDateResponseList.sort(Comparator.comparingInt(item -> Math.abs(item.getRemainingDays() - item.getHighestLeadTime())));
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100001);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(itemDateResponseList);
            log.info(ServiceConstants.GET_DATE_FOR_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), "GET DATE FOR ITEM Successfully");
        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100002);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(responseMessage.getStatus());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_DATE_FOR_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " Failed to GET DATE FOR ITEM TIME" + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_DATE_FOR_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET DATE FOR ITEM End " + (endTime - startTime));
        return baseResponse;
    }

    private boolean shortProductItem(List<PPELine> ppeLineList) {
        boolean isShort = false;
        List<StockBalance> stockBalanceList = stockBalanceRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
        Map<Integer, StockBalance> stockBalanceMap = stockBalanceList.stream().collect(Collectors.toMap(k -> k.getItemId().getId(), v -> v));
        for (PPELine ppeLine : ppeLineList) {
            StockBalance stockBalance = stockBalanceMap.get(ppeLine.getItem().getId());
            if (stockBalance != null && checkBalanceQty(ppeLine, stockBalance)) {
                isShort = true;
            }
        }
        return isShort;
    }

    @Override
    public BaseResponse<ItemResponse> getAsnBuyerItem(Integer ppeHeadId) {
        log.info(ServiceConstants.GET_ASN_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN BUYER ITEM Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ItemResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_ASN_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN BUYER ITEM Organization Id : " + loginUser.getSubOrgId() + "PPEHeadId :: " + ppeHeadId);
            List<ItemResponse> itemResponseList = new ArrayList<>();
            List<BuyerItemMapper> buyerItemMappers = buyerItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndUserId(loginUser.getSubOrgId(), false, loginUser.getUserId());
            List<Integer> itemIdList = buyerItemMappers.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());

            List<PPELine> ppeLineList = ppeLineRepository.findByIsDeletedAndSubOrganizationIdAndPpeHeadIdAndItemIdIn(false, loginUser.getSubOrgId(), ppeHeadId, itemIdList);

            List<StockBalance> stockBalanceList = stockBalanceRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            Map<Integer, StockBalance> stockBalanceMap = stockBalanceList.stream().collect(Collectors.toMap(k -> k.getItemId().getId(), v -> v));

            for (PPELine ppeLine : ppeLineList) {
                StockBalance stockBalance = stockBalanceMap.get(ppeLine.getItem().getId());
                if (stockBalance != null && checkBalanceQty(ppeLine, stockBalance)) {
                    ItemResponse itemResponse = new ItemResponse();
                    itemResponse.setId(ppeLine.getItem().getId());
                    itemResponse.setItemCode(ppeLine.getItem().getItemId());
                    itemResponse.setItemName(ppeLine.getItem().getName());
                    itemResponseList.add(itemResponse);
                }
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100003);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(itemResponseList);
            log.info(ServiceConstants.GET_ASN_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), "GET ASN BUYER ITEM Successfully ");

        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100004);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_ASN_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to GET ASN BUYER ITEM --- " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_ASN_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN BUYER ITEM End " + (endTime - startTime));

        return baseResponse;
    }


    @Override
    public BaseResponse<ItemScheduleSupplier> getAsnSupplierPoDet(Integer itemId, Integer ppeHeadId) {
        log.info(ServiceConstants.GET_ASN_SUPPLIER_PO_LOG, loginUser.getLogId(), loginUser.getUserId(), " ASN SUPPLIER AND PURCHASE ORDER DETAILS Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ItemScheduleSupplier> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_ASN_SUPPLIER_PO_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN SUPPLIER AND PURCHASE ORDER DETAILS Organization Id: " + loginUser.getSubOrgId() + " Item Id :: " + itemId + " PPEHeadId " + ppeHeadId);
            PPEHead ppeHead = ppeHeadRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, ppeHeadId);
            List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findByRequiredDateAndItemScheduleItemId(ppeHead.getStartDate(), itemId);
            for (ItemScheduleSupplier itemScheduleSupplier : itemScheduleSupplierList) {
                PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndItemIdAndPurchaseOrderHeadId(loginUser.getSubOrgId(), false, itemScheduleSupplier.getItemSchedule().getItem().getId(), itemScheduleSupplier.getPurchaseOrderHead().getId());
                itemScheduleSupplier.setIsDay(purchaseOrderLine.getIsDay());
                itemScheduleSupplier.setLeadTime(purchaseOrderLine.getLeadTime());
                itemScheduleSupplier.setPurchaseOrderQuantity(purchaseOrderLine.getPurchaseOrderQuantity());
                itemScheduleSupplier.setSubTotalRs(purchaseOrderLine.getSubTotalRs());
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100005);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(itemScheduleSupplierList);
            log.info(ServiceConstants.GET_ASN_SUPPLIER_PO_LOG, loginUser.getLogId(), loginUser.getUserId(), "GET ASN SUPPLIER AND PURCHASE ORDER DETAILS Successfully ");

        } catch (Exception e) {

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100006);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(responseMessage.getStatus());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_ASN_SUPPLIER_PO_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to GET ASN SUPPLIER AND PURCHASE ORDER DETAILS ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_ASN_SUPPLIER_PO_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN SUPPLIER AND PURCHASE ORDER DETAILS END--- " + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<ASNHead> getAsnFilterList(String status, String startDate, String endDate) {
        log.info(ServiceConstants.GET_ASN_FILTER_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), " ASN FILTER LIST Started  --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ASNHead> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_ASN_FILTER_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "ASN FILTER LIST " + loginUser.getSubOrgId());

            log.info(loginUser.getLogId() + " GET ->  ASN FILTER LIST ");
            boolean isSupplier = false;
            List<ASNHead> asnHeadList = null;
            User user = userRepository.findByIsDeletedAndId(false, loginUser.getUserId());
            if (user.getSupplierId() != null) {
                isSupplier = true;
            }
            if (isSupplier) {
                asnHeadList = asnHeadRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameInAndSupplierId(loginUser.getSubOrgId(), false, Arrays.asList(status), user.getSupplierId());
            } else {
                asnHeadList = asnHeadRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameIn(loginUser.getSubOrgId(), false, Arrays.asList(status));
            }

            if (status.equalsIgnoreCase("Acknowledge")) {
                processAcknowledgeStatus(asnHeadList);
            } else if (status.equalsIgnoreCase("NotConfirm")) {
                asnHeadList = findNotConfirmedAsnHeads(user.getSupplierId(), isSupplier);
            } else if (status.equalsIgnoreCase("HCBSupplier")) {
                asnHeadList = findHcbSupplierAsnHeads(user.getSupplierId(), isSupplier);
            } else if (status.equalsIgnoreCase("HItemBSupplier")) {
                asnHeadList = findHItemBSupplierAsnHeads(user.getSupplierId(), isSupplier);
            }

            if (isDateRangeValid(startDate, endDate)) {
                asnHeadList = filterDataByDateRange(asnHeadList, stringToDate(startDate), stringToDate(endDate));
            }

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100007);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(asnHeadList);
            log.info(ServiceConstants.GET_ASN_FILTER_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "GET ASN FILTER LIST Successfully");

        } catch (Exception e) {

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100008);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setStatus(responseMessage.getStatus());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_ASN_FILTER_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to GET ASN FILTER LIST  ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_ASN_FILTER_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN FILTER LIST End --- " + (endTime - startTime));
        return baseResponse;
    }


    private void processAcknowledgeStatus(List<ASNHead> asnHeadList) {
        for (ASNHead asnHead : asnHeadList) {
            int accepted = 0;
            int rejected = 0;
            List<ASNLine> asnLineList = asnLineRepository.findByAsnHeadIdIdAndSubOrganizationIdAndIsDeleted(asnHead.getId(), loginUser.getSubOrgId(), false);
            for (ASNLine asnLine : asnLineList) {
                if (asnLine.getStatus().getStatusName().equalsIgnoreCase("Acknowledge")) {
                    accepted++;
                } else {
                    rejected++;
                }
            }
            asnHead.setAccepted(accepted);
            asnHead.setRejected(rejected);
        }
    }

    private List<ASNHead> findNotConfirmedAsnHeads(Integer supplierId, boolean isSupplier) {
        if (isSupplier) {
            return asnHeadRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseStatusIdAndSupplierId(loginUser.getSubOrgId(), false, null, supplierId);

        } else {
            return asnHeadRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseStatusId(loginUser.getSubOrgId(), false, null);
        }
    }

    private List<ASNHead> findHcbSupplierAsnHeads(Integer supplierId, boolean isSupplier) {
        if (isSupplier) {
            return asnHeadRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameInAndSupplierId(loginUser.getSubOrgId(), false, Arrays.asList("HoldS", "CancelS"), supplierId);
        } else {
            return asnHeadRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseStatusStatusNameIn(loginUser.getSubOrgId(), false, Arrays.asList("HoldS", "CancelS"));
        }
    }

    private List<ASNHead> findHItemBSupplierAsnHeads(Integer supplierId, boolean isSupplier) {
        List<ASNLine> asnLineList = null;
        if (isSupplier) {
            asnLineList = asnLineRepository.findByStatusStatusNameAndSubOrganizationIdAndIsDeletedAndAsnHeadIdSupplierId("Hold", loginUser.getSubOrgId(), false, supplierId);
        } else {
            asnLineList = asnLineRepository.findByStatusStatusNameAndSubOrganizationIdAndIsDeleted("Hold", loginUser.getSubOrgId(), false);
        }
        Set<ASNHead> asnHeads = asnLineList.stream().map(ASNLine::getAsnHeadId).collect(Collectors.toSet());
        return new ArrayList<>(asnHeads);
    }

    private boolean isDateRangeValid(String startDate, String endDate) {
        return startDate.length() > 0 && !startDate.equalsIgnoreCase("null") && endDate.length() > 0 && !endDate.equalsIgnoreCase("null");
    }

    @Override
    public BaseResponse<Reason> getReason(String reasonCategory) {
        log.info(ServiceConstants.GET_REASON_LOG, loginUser.getLogId(), loginUser.getUserId(), " Get Reason Started  --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<Reason> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_REASON_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get Reason :: Reason category :  " + reasonCategory);
            List<Reason> reasonList = reasonRepository.findByIsDeletedAndReasonCategory(false, reasonCategory);

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100009);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(reasonList);
            baseResponse.setLogId(loginUser.getLogId());

            log.info(ServiceConstants.GET_REASON_LOG, loginUser.getLogId(), loginUser.getUserId(), "GET ASN FILTER LIST Successfully");

        } catch (Exception e) {

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100010);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_REASON_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to GET ASN FILTER LIST  ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_REASON_LOG, loginUser.getLogId(), loginUser.getUserId(), " GET ASN FILTER LIST End --- " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Item> getBuyerItem() {
        log.info(ServiceConstants.GET_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " Get Buyer Item Started  --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<Item> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get Buyer Item buyer Id :: " + loginUser.getUserId());
            List<BuyerItemMapper> buyerItemMappers = buyerItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndUserId(loginUser.getSubOrgId(), false, loginUser.getUserId());

            List<Item> itemList = buyerItemMappers.stream().map(v -> v.getItem()).collect(Collectors.toList());
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100009);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(itemList);
            baseResponse.setLogId(loginUser.getLogId());

            log.info(ServiceConstants.GET_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get Buyer Item LIST Successfully");

        } catch (Exception e) {

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100010);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to Get Buyer Item LIST  ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_BUYER_ITEM_LOG, loginUser.getLogId(), loginUser.getUserId(), " Get Buyer Item LIST End --- " + (endTime - startTime));
        return baseResponse;    }

    @Override
    public BaseResponse<PoNumberResponse> getPOList() {
        long startTime = System.currentTimeMillis();
        BaseResponse<PoNumberResponse> baseResponse = new BaseResponse<>();
        try {

            List<PurchaseOrderHead> purchaseOrderList = purchaseOrderHeadRepository.findByOrganizationIdAndIsDeleted(loginUser.getOrgId(), false);
            List<PoNumberResponse> poNumberResponseList = new ArrayList<>();



            baseResponse.setCode(1);
            baseResponse.setMessage("Get Purchase Order Number List Successfully");
            baseResponse.setStatus(200);
            baseResponse.setData(poNumberResponseList);

        } catch (Exception e) {
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get Purchase Order Data ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - ASNBuyerService - getPOList - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), "  Failed to Get PO HEAD LIST  ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - ASNBuyerService - getPOList - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), "  Purchase Order Head List End --- " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<ASNResponse> getASNLineList(Integer poId) {
        log.info(ServiceConstants.GET_ASN_LINE_LOG, loginUser.getLogId(), loginUser.getUserId(), "Gate Line List Started  --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ASNResponse> baseResponse = new BaseResponse<>();
        try {
            ASNResponse asnResponse = new ASNResponse();
            log.info(ServiceConstants.GET_ASN_LINE_LOG, loginUser.getLogId(), loginUser.getUserId(), "PO Line DB Call  " + loginUser.getSubOrgId());
            List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndPurchaseOrderHeadId(loginUser.getOrgId(), false, poId);
            purchaseOrderLineList.forEach(line -> {
                Integer sum = 0;
                List<ASNLine> asnLineList = asnLineRepository.findByPurchaseOrderLineIdAndOrganizationIdAndIsDeleted(line.getId(), loginUser.getOrgId(), false);
                if (!CollectionUtils.isEmpty(asnLineList)) {
                    for (ASNLine asnLine : asnLineList) {
                        sum = sum + asnLine.getRequiredQuantity();
                    }
                }
            });

            if (!CollectionUtils.isEmpty(purchaseOrderLineList)) {
                asnResponse.setLineList(purchaseOrderLineList);
                asnResponse.setOrganization(organizationRepository.findById(loginUser.getOrgId()).get());
                baseResponse.setCode(1);
                baseResponse.setMessage("Get Gate Line Details Successfully");
                baseResponse.setStatus(200);
                baseResponse.setData(Arrays.asList(asnResponse));
                log.info(ServiceConstants.GET_ASN_LINE_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get Gate Line Details Successfully ");

            } else {
                baseResponse.setCode(0);
                baseResponse.setMessage("No Data Present in PO Line ");
                baseResponse.setData(null);
                baseResponse.setStatus(500);
            }
        } catch (Exception e) {
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get Gate Line Details ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_ASN_LINE_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to Get Gate Line Details   ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_ASN_LINE_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Gate Line List End--- " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse saveAsnDet(List<SaveAsnRequest> saveRequestList) {
        log.info(ServiceConstants.SAVE_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Save ASN Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<SaveAsnResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.SAVE_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Save Asn numbers Get Call" + loginUser.getSubOrgId());
            baseResponse = saveAsn(saveRequestList);

            log.info(ServiceConstants.SAVE_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Asn numbers Generated Successfully");

        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100019);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.SAVE_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "  Failed to Save ASN   ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.SAVE_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "   Asn Data End --- " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<String> deleteAsn(Integer asnId) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {

            ASNHead asnHead = asnHeadRepository.findById(asnId).get();
            asnHead.setIsDeleted(true);
            asnHeadRepository.save(asnHead);
            baseResponse.setCode(1);
            baseResponse.setMessage("ASN Deleted Successfully");
            baseResponse.setStatus(200);
            baseResponse.setData(null);

        } catch (Exception e) {
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Delete Asn ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            e.printStackTrace();
        }
        return baseResponse;
    }

    private BaseResponse<SaveAsnResponse> saveAsn(List<SaveAsnRequest> saveRequestList) {
        BaseResponse<SaveAsnResponse> baseResponse = new BaseResponse<>();
        List<SaveAsnResponse> saveAsnResponseList = new ArrayList<>();
        Map<AsnBuyerKey, List<SaveAsnRequest>> groupByPoIdLeadDateTime = saveRequestList.stream()
                .collect(Collectors.groupingBy(po -> new AsnBuyerKey(po.getPoHeadId(), po.getDeliveryDate())));

        for (Map.Entry<AsnBuyerKey, List<SaveAsnRequest>> entry : groupByPoIdLeadDateTime.entrySet()) {
            ASNHead asnHead = new ASNHead();
            List<ASNLine> asnLineList = new ArrayList<>();
            asnHead.setOrganizationId(loginUser.getOrgId());
            asnHead.setSubOrganizationId(loginUser.getOrgId());
            PurchaseOrderHead purchaseOrderHead = purchaseOrderHeadRepository.findByIdAndIsDeleted(entry.getKey().getPoId(), false);
            asnHead.setPurchaseOrderHead(purchaseOrderHead);
            asnHead.setDeliveryDate(stringToDate(entry.getKey().getScheduleDate()));
            asnHead.setDeliveryTime(stringToLocalTime(entry.getValue().get(0).getDeliveryTime()));
            asnHead.setRequiredOnDate(stringToDate(entry.getValue().get(0).getRequiredOnDate()));
            asnHead.setSupplier(supplierRepository.findByIsDeletedAndId(false, purchaseOrderHead.getSupplierId()));
            asnHead.setPurchaseStatus(null);
            asnHead.setIsDeleted(false);
            asnHead.setCreatedBy(loginUser.getUserId());
            asnHead.setCreatedOn(new Date());
            for (SaveAsnRequest saveAsnRequest : entry.getValue()) {
                ASNLine asnLine = new ASNLine();
                asnLine.setOrganizationId(loginUser.getOrgId());
                asnLine.setSubOrganizationId(loginUser.getOrgId());
                asnLine.setAsnHeadId(asnHead);
                PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndItemIdAndPurchaseOrderHeadId(loginUser.getOrgId(), false, saveAsnRequest.getItemId(), saveAsnRequest.getPoHeadId());
                asnLine.setItem(purchaseOrderLine.getItem());
                asnLine.setPurchaseOrderLine(purchaseOrderLine);
                asnLine.setAllocatedQuantity(saveAsnRequest.getAllocatedQty());
                ItemScheduleSupplier itemScheduleSupplier = itemScheduleSupplierRepository.findById(saveAsnRequest.getItemScheduleSupplierId()).get();
                asnLine.setItemScheduleSupplier(itemScheduleSupplier);
                asnLine.setRequiredQuantity(itemScheduleSupplier.getRequiredQuantity());
                asnLine.setBalanceQuantity(itemScheduleSupplier.getRequiredQuantity() - saveAsnRequest.getAllocatedQty());
                asnLine.setStatus(null);
                asnLine.setIsDeleted(false);
                asnLine.setCreatedBy(loginUser.getUserId());
                asnLine.setCreatedOn(new Date());
                asnLineList.add(asnLine);
            }
            asnHeadRepository.save(asnHead);
            asnLineRepository.saveAll(asnLineList);
            Integer ppeHeadId = saveRequestList.get(0).getPpeHeadId();
            PPEHead ppeHead = ppeHeadRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, ppeHeadId);
            ppeHead.setIsAsnCreated(true);
            ppeHeadRepository.save(ppeHead);
            saveAsnResponseList.add(new SaveAsnResponse(asnHead.getId(), asnHead.getAsnNumber(), asnHead.getSupplier().getSupplierId(), asnHead.getSupplier().getSupplierName(), asnHead.getPurchaseOrderHead().getPurchaseOrderNumber(), asnHead.getPurchaseOrderHead().getPurchaseOrderDate().toString()));
        }
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100018);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(saveAsnResponseList);
        baseResponse.setLogId(loginUser.getLogId());

        return baseResponse;
    }

    @Override
    public BaseResponse<ASNHead> getAsnHeadList(Integer poId) {
        log.info(ServiceConstants.GET_ASN_HEAD_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "ASN HEAD LIST Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ASNHead> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_ASN_HEAD_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "ASN HEAD LIST DB Call " + loginUser.getSubOrgId() + "poId::" + poId);
            List<ASNHead> asnHeadList = asnHeadRepository.findByPurchaseOrderHeadIdAndSubOrganizationIdAndIsDeleted(poId, loginUser.getSubOrgId(), false);
            baseResponse.setCode(1);
            baseResponse.setMessage("Get ASN List Successfully");
            baseResponse.setStatus(200);
            baseResponse.setData(asnHeadList);
            log.info(ServiceConstants.GET_ASN_HEAD_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get ASN List Successfully ");

        } catch (Exception e) {
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get Purchase Order Data ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_ASN_HEAD_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), "   Failed to Get ASN HEAD LIST---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_ASN_HEAD_LIST_LOG, loginUser.getLogId(), loginUser.getUserId(), " ASN HEAD LIST End  --- " + (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<Supplier> getSupplierList() {
        log.info(ServiceConstants.GET_SUPPLIER_LOG, loginUser.getLogId(), loginUser.getUserId(), "SUPPLIER LIST Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<Supplier> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_SUPPLIER_LOG, loginUser.getLogId(), loginUser.getUserId(), "SUPPLIER LIST DB Call " + loginUser.getSubOrgId());
            List<Supplier> supplierList = supplierRepository.findByIsDeletedAndOrganizationId(false, loginUser.getOrgId());
            baseResponse.setCode(1);
            baseResponse.setMessage("Get Supplier List Successfully");
            baseResponse.setStatus(200);
            baseResponse.setData(supplierList);
            log.info(ServiceConstants.GET_SUPPLIER_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get Supplier List Successfully ");

        } catch (Exception e) {
            baseResponse.setCode(0);
            baseResponse.setMessage("Failed to Get SUPPLIER Data ");
            baseResponse.setData(null);
            baseResponse.setStatus(500);
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_SUPPLIER_LOG, loginUser.getLogId(), loginUser.getUserId(), " Failed to Get SUPPLIER LIST ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_SUPPLIER_LOG, loginUser.getLogId(), loginUser.getUserId(), " SUPPLIER LIST End --- " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<ASNDetResponse> getAsnDet(Integer id) {
        log.info(ServiceConstants.GET_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Asn Details By Id Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<ASNDetResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.GET_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Asn Details By Id DB Call" + loginUser.getSubOrgId() + "Id::" + id);
            ASNDetResponse asnDetResponse = new ASNDetResponse();
            ASNHead asnHead = asnHeadRepository.findByIdAndSubOrganizationIdAndIsDeleted(id, loginUser.getOrgId(), false);
            List<ASNLine> asnLineList = asnLineRepository.findBySubOrganizationIdAndAsnHeadIdIdAndIsDeleted(loginUser.getOrgId(), id, false);


            asnDetResponse.setAsnHead(asnHead);
            asnDetResponse.setAsnLineList(asnLineList);
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100026);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(Arrays.asList(asnDetResponse));
            baseResponse.setLogId(loginUser.getLogId());
            log.info(ServiceConstants.GET_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Get Asn Details By Id Successfully");

        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100027);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.GET_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), "Failed to Get  Asn Details By Id  ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.GET_ASN_DET_LOG, loginUser.getLogId(), loginUser.getUserId(), " SUPPLIER LIST End--- " + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<String> updateAsnStatus(List<UpdateAsnStatusReq> asnStatusReqList) {
        log.info(ServiceConstants.UPDATE_ASN_STATUS_LOG, loginUser.getLogId(), loginUser.getUserId(), "update ASN status Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.UPDATE_ASN_STATUS_LOG, loginUser.getLogId(), loginUser.getUserId(), "update ASN status Call" + loginUser.getSubOrgId() + "AsnStatusReqList::" + asnStatusReqList);
            List<ASNHead> asnHeadList = new ArrayList<>();
            List<PurchaseOrderHead> purchaseOrderHeadList = new ArrayList<>();

            asnStatusReqList.forEach(updateAsnStatusReq -> {
                if (updateAsnStatusReq.getAsnId() != null) {
                    processAsnStatus(updateAsnStatusReq, asnHeadList);
                } else if (updateAsnStatusReq.getPoId() != null) {
                    processPurchaseOrderStatus(updateAsnStatusReq, purchaseOrderHeadList);
                }
            });

            saveAllEntities(purchaseOrderHeadList, asnHeadList);

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100028);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(null);
            baseResponse.setLogId(loginUser.getLogId());
            log.info(ServiceConstants.UPDATE_ASN_STATUS_LOG, loginUser.getLogId(), loginUser.getUserId(), "update ASN status Successfully");

        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100029);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.UPDATE_ASN_STATUS_LOG, loginUser.getLogId(), loginUser.getUserId(), "Failed to update ASN status ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.UPDATE_ASN_STATUS_LOG, loginUser.getLogId(), loginUser.getUserId(), " update ASN status End --- " + (endTime - startTime));
        return baseResponse;
    }

    private void processAsnStatus(UpdateAsnStatusReq updateAsnStatusReq, List<ASNHead> asnHeadList) {
        ASNHead asnHead = asnHeadRepository.findById(updateAsnStatusReq.getAsnId()).orElse(null);
        if (asnHead == null) return;
        asnHead.setPurchaseStatus(purchaseStatusRepository.findByStatusNameAndStatusType(updateAsnStatusReq.getStatus(), "ASN Head"));
        asnHead.setReasonDocumentId(updateAsnStatusReq.getReasonDocumentId());
        if (updateAsnStatusReq.getStatus().equalsIgnoreCase(APIConstants.POST)) {
            handlePostStatus(updateAsnStatusReq, asnHead);
        } else if (Arrays.asList(APIConstants.HOLD, APIConstants.CANCEL).contains(updateAsnStatusReq.getStatus())) {
            handleHoldOrCancelStatus(updateAsnStatusReq, asnHead);
        }
        asnHeadList.add(asnHead);
    }

    private void handlePostStatus(UpdateAsnStatusReq updateAsnStatusReq, ASNHead asnHead) {
        if (StringUtils.isEmpty(asnHead.getAsnNumber())) {
            asnHead.setAsnNumber(barcodeGenerator.getAsnNumber(asnHead.getSupplier()));
        }
        List<ASNLine> asnLineList = asnLineRepository.findByAsnHeadIdIdAndSubOrganizationIdAndIsDeleted(asnHead.getId(), loginUser.getSubOrgId(), false);
        for (ASNLine asnLine : asnLineList) {
            PurchaseOrderLine purchaseOrderLine = asnLine.getPurchaseOrderLine();
            updatePurchaseOrderLine(asnLine, purchaseOrderLine, false);
        }
        sendEmailToSupplier(asnHead, updateAsnStatusReq.getUrlLink());
    }

    private void handleHoldOrCancelStatus(UpdateAsnStatusReq updateAsnStatusReq, ASNHead asnHead) {
        asnHead.setReason(reasonRepository.findByIsDeletedAndId(false, updateAsnStatusReq.getReasonId()));
        if (updateAsnStatusReq.getStatus().equalsIgnoreCase(APIConstants.CANCEL)) {
            List<ASNLine> asnLineList = asnLineRepository.findByAsnHeadIdIdAndSubOrganizationIdAndIsDeleted(asnHead.getId(), loginUser.getSubOrgId(), false);
            for (ASNLine asnLine : asnLineList) {
                PurchaseOrderLine purchaseOrderLine = asnLine.getPurchaseOrderLine();
                updatePurchaseOrderLine(asnLine, purchaseOrderLine, true);
            }
        }
    }

    private void updatePurchaseOrderLine(ASNLine asnLine, PurchaseOrderLine purchaseOrderLine, boolean isCancel) {
        if (purchaseOrderLine.getSubTotalRs() != null && asnLine.getItem().getItemUnitRate() != null) {
            int poQty = isCancel ? purchaseOrderLine.getPurchaseOrderQuantity() + asnLine.getAllocatedQuantity()
                    : purchaseOrderLine.getPurchaseOrderQuantity() - asnLine.getAllocatedQuantity();
            int subTotAmt = (int) (isCancel ? purchaseOrderLine.getSubTotalRs() + (asnLine.getInvoiceQuantity() * asnLine.getItem().getItemUnitRate())
                    : purchaseOrderLine.getSubTotalRs() - (asnLine.getAllocatedQuantity() * asnLine.getItem().getItemUnitRate()));
            purchaseOrderLine.setPurchaseOrderQuantity(poQty);
            purchaseOrderLine.setSubTotalRs(subTotAmt);
            purchaseOrderLineRepository.save(purchaseOrderLine);
        }
    }

    private void processPurchaseOrderStatus(UpdateAsnStatusReq updateAsnStatusReq, List<PurchaseOrderHead> purchaseOrderHeadList) {
        PurchaseOrderHead purchaseOrderHead = purchaseOrderHeadRepository.findByIdAndIsDeleted(updateAsnStatusReq.getPoId(), false);
        if (purchaseOrderHead != null) {
            purchaseOrderHead.setStatus(purchaseStatusRepository.findByStatusNameAndStatusType(updateAsnStatusReq.getStatus(), "PO Head"));
            purchaseOrderHeadList.add(purchaseOrderHead);
        }
    }

    private void saveAllEntities(List<PurchaseOrderHead> purchaseOrderHeadList, List<ASNHead> asnHeadList) {
        if (!purchaseOrderHeadList.isEmpty()) {
            purchaseOrderHeadRepository.saveAll(purchaseOrderHeadList);
        }
        if (!asnHeadList.isEmpty()) {
            asnHeadRepository.saveAll(asnHeadList);
        }
    }

    private void sendEmailToSupplier(ASNHead asnHead, String urlLink) {
        User user = userRepository.findByIsDeletedAndSupplierId(false, asnHead.getSupplier().getId());
        Organization organization = organizationRepository.findByIsDeletedAndId(false, user.getOrganizationId());
        Email email = new Email();
        email.setSubOrganizationId(loginUser.getSubOrgId());
        email.setAttemptCount(0);
        email.setIsSend(false);
        email.setType("ASNPOST");
        email.setLoginUrl(urlLink);
        email.setSubjectLine("ASN Generated - Access Details");
        email.setToUser(user.getId());
        email.setSubType(asnHead.getId());
        email.setSubTypeName(organization.getOrganizationName());
        emailRepository.save(email);
    }

    @Override
    public BaseResponse<String> updateAsnDetails(List<UpdateAsnRequest> updateAsnRequest) {
        log.info(ServiceConstants.UPDATE_ASN_LOG, loginUser.getLogId(), loginUser.getUserId(), "update ASN status Started --- ");
        long startTime = System.currentTimeMillis();
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            log.info(ServiceConstants.UPDATE_ASN_DETAILS_LOG, loginUser.getLogId(), loginUser.getUserId(), "update ASN status DB Call" + loginUser.getSubOrgId() + "UpdateAsnRequest:" + updateAsnRequest);
            updateAsnDet(updateAsnRequest);
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100030);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(null);
            baseResponse.setLogId(loginUser.getLogId());

            log.info(ServiceConstants.UPDATE_ASN_LOG, loginUser.getLogId(), loginUser.getUserId(), "update ASN status Successfully");

        } catch (Exception e) {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100031);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            long endTime = System.currentTimeMillis();
            log.error(ServiceConstants.UPDATE_ASN_LOG, loginUser.getLogId(), loginUser.getUserId(), "Failed to update ASN status  ---  " + (endTime - startTime));
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info(ServiceConstants.UPDATE_ASN_LOG, loginUser.getLogId(), loginUser.getUserId(), " update ASN status End --- " + (endTime - startTime));
        return baseResponse;
    }


    private void updateAsnDet(List<UpdateAsnRequest> updateAsnRequestList) {
        List<ASNLine> asnLineList = new ArrayList<>();
        for (UpdateAsnRequest updateAsnRequest : updateAsnRequestList) {
            ASNLine asnLine = asnLineRepository.findByIdAndSubOrganizationIdAndIsDeleted(updateAsnRequest.getAsnLineId(), loginUser.getSubOrgId(), false);
            int allocatedQty = asnLine.getAllocatedQuantity() - updateAsnRequest.getAllocatedQty();
            asnLine.setAllocatedQuantity(updateAsnRequest.getAllocatedQty());
            PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findByIdAndSubOrganizationIdAndIsDeleted(asnLine.getPurchaseOrderLine().getId(), loginUser.getSubOrgId(), false);
            if (allocatedQty > 0) {
                purchaseOrderLine.setPurchaseOrderQuantity(purchaseOrderLine.getPurchaseOrderQuantity() + allocatedQty);
            } else {
                purchaseOrderLine.setPurchaseOrderQuantity(purchaseOrderLine.getPurchaseOrderQuantity() + allocatedQty);
            }
            asnLineList.add(asnLine);
        }
        asnLineRepository.saveAll(asnLineList);
    }

    public static Date stringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalTime stringToLocalTime(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT_PATTERN);
        return LocalTime.parse(timeString, formatter);
    }

    public int findMaxNumberDays(List<PPELine> ppeLineList) {
        Long remainingDays = calculateDaysBetween(ppeLineList.get(0).getPpeHead().getStartDate());
        List<Integer> itemIds = ppeLineList.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());
        List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndItemIdIn(loginUser.getSubOrgId(), false, itemIds);

        Map<Integer, List<PurchaseOrderLine>> groupedByItem = purchaseOrderLineList.stream()
                .collect(Collectors.groupingBy(purchaseOrderLine -> purchaseOrderLine.getItem().getId()));

        // Filter items whose lead time is smaller than remaining days and find the max lead time for each item
        Map<Integer, Integer> maxLeadTimes = new HashMap<>();
        for (Map.Entry<Integer, List<PurchaseOrderLine>> entry : groupedByItem.entrySet()) {
            Integer itemId = entry.getKey();
            List<PurchaseOrderLine> suppliers = entry.getValue();
            int maxLeadTime = suppliers.stream()
                    .filter(item -> item.getLeadTimeInDays() < remainingDays)
                    .mapToInt(PurchaseOrderLine::getLeadTimeInDays)
                    .max()
                    .orElse(0);
            maxLeadTimes.put(itemId, maxLeadTime);
        }

        int overallMaxLeadTime = maxLeadTimes.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        return overallMaxLeadTime + 1;
    }

    public int findMinNumberDays(List<PPELine> ppeLineList) {
        List<Integer> itemIds = ppeLineList.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());
        List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndItemIdIn(loginUser.getSubOrgId(), false, itemIds);

        int overallMinLeadTime = purchaseOrderLineList.stream()
                .mapToInt(PurchaseOrderLine::getLeadTimeInDays)
                .min()
                .orElse(0);

        return overallMinLeadTime + 1;
    }

    public static long calculateDaysBetween(Date startDate) {
        LocalDate start = LocalDate.now();
        LocalDate end = convertToLocalDate(startDate);
        return ChronoUnit.DAYS.between(start, end);
    }

    private static LocalDate convertToLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    public static String generateMessage(String recipientName, String asnNumber, String shipmentDate, String asnAccessLink) {
        return "Dear " + recipientName + ",\n\n" +
                "We are excited to inform you that an Advanced Shipping Notice (ASN) has been generated for your upcoming shipment. Here are the details:\n\n" +
                "ASN Details:\n" +
                "  - ASN Number: " + asnNumber + "\n" +
                "  - Shipment Date: " + shipmentDate + "\n" +
                "\n" +
                "Access Link:\n" +
                "Click here to access the ASN details: " + asnAccessLink + "\n\n" +
                "If you have any questions or require further assistance, please feel free to reach out. We appreciate your cooperation.\n\n" +
                "Best regards,\n" +
                "Your Name\n" +
                "Your Position\n" +
                "Your Company/Organization\n" +
                "Contact Information";
    }

    public List<ASNHead> filterDataByDateRange(List<ASNHead> asnHeadList, Date startDate, Date endDate) {
        return asnHeadList.stream().filter(asnHead -> (asnHead.getPurchaseOrderHead().getPurchaseOrderDate().equals(startDate) || asnHead.getPurchaseOrderHead().getPurchaseOrderDate().after(startDate) && (asnHead.getPurchaseOrderHead().getPurchaseOrderDate().before(endDate) || asnHead.getPurchaseOrderHead().getPurchaseOrderDate().equals(endDate)))).collect(Collectors.toList());
    }

    private static boolean checkBalanceQty(PPELine ppeLine, StockBalance stockBalance) {
        return stockBalance.getBalanceQuantity() < ppeLine.getRequiredQuantity();
    }
}
