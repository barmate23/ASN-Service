package com.asnServices.service;

import com.asnServices.EmailModule.EmailSender;
import com.asnServices.configuration.LoginUser;
import com.asnServices.model.*;
import com.asnServices.repository.*;
import com.asnServices.response.*;
import com.asnServices.request.SendAsnScheduleLineRequest;
import com.asnServices.request.SendAsnScheduleRequest;
import com.asnServices.utils.APIConstants;
import com.asnServices.utils.GlobalMessages;
import com.asnServices.utils.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class

ASNSchedulerServiceImpl implements ASNSchedulerService {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    @Autowired
    private LoginUser loginUser;
    @Autowired
    EmailSender emailSender;
    @Autowired
    BuyerItemMapperRepository buyerItemMapperRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private ItemScheduleRepository itemScheduleRepository;
    @Autowired
    private ItemScheduleSupplierRepository itemScheduleSupplierRepository;
    @Autowired
    private PPELineRepository ppeLineRepository;
    @Autowired
    private StockBalanceRepository stockBalanceRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PurchaseOrderLineRepository purchaseOrderLineRepository;
    @Autowired
    private PurchaseOrderHeadRepository purchaseOrderHeadRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    private Random random = new Random();

    private static final Map<String, Integer> monthMap = new HashMap<>();

    static {
        monthMap.put("January", 1);
        monthMap.put("February", 2);
        monthMap.put("March", 3);
        monthMap.put("April", 4);
        monthMap.put("May", 5);
        monthMap.put("June", 6);
        monthMap.put("July", 7);
        monthMap.put("August", 8);
        monthMap.put("September", 9);
        monthMap.put("October", 10);
        monthMap.put("November", 11);
        monthMap.put("December", 12);
    }

    @Override
    public BaseResponse<AsnScheduleItemResponse> getPPCItemList() {
        log.info(loginUser.getLogId() + " Get -> GET PPE Item List start --- ");
        BaseResponse<AsnScheduleItemResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> GET PPE Item List for organization : " + loginUser.getOrgId());
            List<BuyerItemMapper> buyerItemMappers = buyerItemMapperRepository.findBySubOrganizationIdAndIsDeletedAndUserId(loginUser.getSubOrgId(), false, loginUser.getUserId());
            List<Integer> itemIdList = buyerItemMappers.stream().map(v -> v.getItem().getId()).collect(Collectors.toList());

            List<PPELine> ppeLineList = ppeLineRepository.findByIsDeletedAndSubOrganizationIdAndPpeHeadPpeStatusStatusNameInAndItemIdInOrderByIdDesc(false, loginUser.getSubOrgId(), Arrays.asList("CREATED"), itemIdList);

            List<PPELine> ppeLines = new ArrayList<>();
            for (PPELine ppeLine : ppeLineList) {
                if (ppeLine.getIsScheduled() == null || !ppeLine.getIsScheduled()) {
                    ppeLines.add(ppeLine);
                }
            }

            Map<String, List<PPELine>> itemMonthMap = createMapByMonth(ppeLines);
            List<AsnScheduleItemResponse> asnScheduleItemResponseList = new ArrayList<>();
            itemMonthMap.forEach((month, objects) -> {
                Map<Integer, List<PPELine>> groupedByItemId = objects.stream()
                        .collect(Collectors.groupingBy(line -> line.getItem().getId()));
                groupedByItemId.forEach((itemId, ppeLnList) -> {
                    AsnScheduleItemResponse asnScheduleItemResponse = getAsnScheduleItemResponse(month, itemId, ppeLnList);
                    asnScheduleItemResponseList.add(asnScheduleItemResponse);
                });
            });
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100032);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(asnScheduleItemResponseList);
            baseResponse.setLogId(loginUser.getLogId());

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to Get PPE Item List --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100033);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET PPE Item List End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<ASNRequiredDate> getRequiredDateTbl(Integer itemId, String month) {
        log.info(loginUser.getLogId() + " Get -> GET REQUIRED DATE TABLE start --- ");
        BaseResponse<ASNRequiredDate> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> GET REQUIRED DATE TABLE  ORGANIZATION :: " + loginUser.getOrgId() + " Item Id :: " + itemId + " month :: " + monthMap.get(month));
            List<PPELine> ppeLineList = new ArrayList<>();
            List<PPELine> ppeLines = ppeLineRepository.findByIsDeletedAndSubOrganizationIdAndItemIdAndPpeHeadStartDate(loginUser.getSubOrgId(), itemId, monthMap.get(month));
            for (PPELine ppeLine : ppeLines) {
                if (ppeLine.getIsScheduled() == null) {
                    ppeLineList.add(ppeLine);
                } else {
                    if (!ppeLine.getIsScheduled()) {
                        ppeLineList.add(ppeLine);
                    }
                }
            }
            List<ASNRequiredDate> asnRequiredDateList = new ArrayList<>();
            for (PPELine ppeLine : ppeLineList) {
                ASNRequiredDate asnRequiredDate = new ASNRequiredDate();
                asnRequiredDate.setRequiredQuantity(ppeLine.getRequiredQuantity());
                asnRequiredDate.setDate(ppeLine.getPpeHead().getStartDate());
                asnRequiredDate.setTiming(ppeLine.getPpeHead().getStartTime());
                asnRequiredDate.setPpeHeadId(ppeLine.getPpeHead().getId());
                asnRequiredDateList.add(asnRequiredDate);
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100034);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(asnRequiredDateList);
            baseResponse.setLogId(loginUser.getLogId());
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to Get REQUIRED DATE TABLE --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100035);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET REQUIRED DATE TABLE End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse getSupplierSchedule(Integer itemId, String month) {
        log.info(loginUser.getLogId() + " Get -> GET SUPPLIER SCHEDULE start --- ");
        BaseResponse<SupplierScheduleResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> GET SUPPLIER SCHEDULE SUB ORGANIZATION :: " + loginUser.getOrgId() + " Item Id :: " + itemId + " month :: " + monthMap.get(month));
            SupplierScheduleResponse supplierScheduleResponse = new SupplierScheduleResponse();

            User user = userRepository.findById(loginUser.getUserId()).get();

            List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findByIsDeletedAndSubOrganizationIdAndItemScheduleItemIdAndItemScheduleScheduleMonthAndSupplierId(false, loginUser.getSubOrgId(), itemId, month, user.getSupplierId());
            Map<ItemSchedule, List<ItemScheduleSupplier>> itemScheduleListMap = itemScheduleSupplierList.stream().collect(Collectors.groupingBy(ItemScheduleSupplier::getItemSchedule));

            itemScheduleListMap.forEach((key, v) -> {
                supplierScheduleResponse.setItemSchedule(key);
                supplierScheduleResponse.setItemScheduleSupplierList(v);
            });

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100036);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(Arrays.asList(supplierScheduleResponse));
            baseResponse.setLogId(loginUser.getLogId());

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to GET SUPPLIER SCHEDULE --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100037);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET SUPPLIER SCHEDULE End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<ASNPOSupplierResponse> getPoAndSupplier(Integer itemId) {
        log.info(loginUser.getLogId() + " Get -> GET PURCHASE ORDER AND SUPPLIER START --- ");
        BaseResponse<ASNPOSupplierResponse> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " Get -> GET PURCHASE ORDER AND SUPPLIER ITEMID :: " + itemId);
            List<ASNPOSupplierResponse> asnpoSupplierResponseList = new ArrayList<>();
            List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrderLineRepository.findBySubOrganizationIdAndIsDeletedAndItemIdAndPurchaseOrderHeadDeliveryType(loginUser.getSubOrgId(), false, itemId, APIConstants.DELIVERY_TYPE_ASN);
            for (PurchaseOrderLine purchaseOrderLine : purchaseOrderLineList) {
                ASNPOSupplierResponse asnpoSupplierResponse = new ASNPOSupplierResponse();
                asnpoSupplierResponse.setPoHeadId(purchaseOrderLine.getPurchaseOrderHead().getId());
                asnpoSupplierResponse.setSupplierId(purchaseOrderLine.getPurchaseOrderHead().getSupplierId());
                Integer balanceQty = (purchaseOrderLine.getPurchaseOrderQuantity() - ((purchaseOrderLine.getRemainingQuantity() == null) ? 0 : purchaseOrderLine.getRemainingQuantity()));
                asnpoSupplierResponse.setBalanceQuantity((balanceQty > 0 ? balanceQty : 0));
                asnpoSupplierResponse.setPurchaseOrderQuantity(purchaseOrderLine.getPurchaseOrderQuantity());
                asnpoSupplierResponse.setPurchaseOrderNumber(purchaseOrderLine.getPurchaseOrderHead().getPurchaseOrderNumber());
                Supplier supplier = supplierRepository.findByIsDeletedAndId(false, purchaseOrderLine.getPurchaseOrderHead().getSupplierId());
                asnpoSupplierResponse.setSupplierCode(supplier.getSupplierId());
                asnpoSupplierResponse.setSupplierName(supplier.getSupplierName());
                asnpoSupplierResponseList.add(asnpoSupplierResponse);
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100038);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(asnpoSupplierResponseList);
            baseResponse.setLogId(loginUser.getLogId());

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to GET PURCHASE ORDER AND SUPPLIER  --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100039);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET PURCHASE ORDER AND SUPPLIER  End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<ItemSchedule> sendAsnSchedule(SendAsnScheduleRequest sendAsnScheduleRequest) {
        log.info(loginUser.getLogId() + " SAVE -> SEND ASN SCHEDULE start --- ");
        BaseResponse<ItemSchedule> baseResponse = new BaseResponse<>();
        try {
            log.info(loginUser.getLogId() + " SAVE -> SEND ASN SCHEDULE DB Call --- ");
            ItemSchedule itemSchedule = new ItemSchedule();
            itemSchedule.setOrganizationId(loginUser.getOrgId());
            itemSchedule.setSubOrganizationId(loginUser.getOrgId());
            itemSchedule.setScheduleId(sendAsnScheduleRequest.getScheduleId());
            itemSchedule.setScheduleMonth(sendAsnScheduleRequest.getScheduleMonth());
            itemSchedule.setYear(sendAsnScheduleRequest.getYear());
            itemSchedule.setTotalQuantity(sendAsnScheduleRequest.getTotalQuantity());
            if (sendAsnScheduleRequest.getItemCode().length() > 0 && sendAsnScheduleRequest.getItemName().length() > 0) {
                Item item = itemRepository.findByIsDeletedAndSubOrganizationIdAndNameAndItemId(false, loginUser.getSubOrgId(), sendAsnScheduleRequest.getItemName(), sendAsnScheduleRequest.getItemCode());
                itemSchedule.setItem(item);
            } else {
                log.error(loginUser.getLogId() + " SAVE -> Failed to SEND ASN SCHEDULE --- ");
                ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100040);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(null);
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setLogId(loginUser.getLogId());

                return baseResponse;
            }
            itemSchedule.setCreatedBy(loginUser.getUserId());
            itemSchedule.setCreatedOn(new Date());
            itemSchedule.setDeleted(false);
            itemScheduleRepository.save(itemSchedule);
            baseResponse = saveAsnScheduleLines(itemSchedule, sendAsnScheduleRequest);

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " SAVE -> Failed to SEND ASN SCHEDULE --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100042);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " SAVE -> SEND ASN SCHEDULE End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<String> getScheduleCode() {
        log.info(loginUser.getLogId() + " Get -> GET SCHEDULE CODE --- ");
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100043);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(Arrays.asList(generateScheduleCode()));
            baseResponse.setLogId(loginUser.getLogId());

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to GET SCHEDULE CODE --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100044);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET SCHEDULE CODE  End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<SupplierSchedulePrintResponse> getASNSupplierExcel(String month) {
        log.info(loginUser.getLogId() + " Get -> GET ASN Supplier Excel --- ");
        BaseResponse<SupplierSchedulePrintResponse> baseResponse = new BaseResponse<>();
        try {
            User user = userRepository.findById(loginUser.getUserId()).get();
            List<SupplierSchedulePrintResponse> supplierSchedulePrintResponseList = new ArrayList<>();
            List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findByIsDeletedAndSubOrganizationIdAndItemScheduleScheduleMonthAndSupplierIdOrderByIdDesc(false, loginUser.getSubOrgId(), month, user.getSupplierId());
            Map<Date, List<ItemScheduleSupplier>> itemScheduleListMap = itemScheduleSupplierList.stream().collect(Collectors.groupingBy(ItemScheduleSupplier::getRequiredDate));

            itemScheduleListMap.forEach((date, value) ->
                    value.forEach(itemScheduleSupplier -> {
                        SupplierSchedulePrintResponse supplierSchedulePrintResponse = new SupplierSchedulePrintResponse();
                        supplierSchedulePrintResponse.setMonth(month);
                        supplierSchedulePrintResponse.setItemCode(itemScheduleSupplier.getItemSchedule().getItem().getItemId());
                        supplierSchedulePrintResponse.setItemName(itemScheduleSupplier.getItemSchedule().getItem().getName());
                        supplierSchedulePrintResponse.setRequiredDate(date);
                        supplierSchedulePrintResponse.setRequiredQuantity(itemScheduleSupplier.getRequiredQuantity());
                        supplierSchedulePrintResponseList.add(supplierSchedulePrintResponse);
                    }));

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100045);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(supplierSchedulePrintResponseList);
            baseResponse.setLogId(loginUser.getLogId());

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to GET ASN Supplier Excel --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100046);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET ASN Supplier Excel End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<AsnScheduleItemResponse> getItemForSupplier() {
        log.info(loginUser.getLogId() + " Get -> GET PPE Item List start --- ");
        BaseResponse<AsnScheduleItemResponse> baseResponse = new BaseResponse<>();
        try {
            User supplier = userRepository.findByIsDeletedAndId(false, loginUser.getUserId());
            log.info(loginUser.getLogId() + " Get -> GET PPE Item List for organization : " + loginUser.getOrgId());
            List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findByIsDeletedAndSubOrganizationIdAndSupplierId(false, loginUser.getSubOrgId(), supplier.getSupplierId());
            Map<ItemSchedule, List<ItemScheduleSupplier>> itemScheduleMap = itemScheduleSupplierList.stream().collect(Collectors.groupingBy(ItemScheduleSupplier::getItemSchedule));
            List<AsnScheduleItemResponse> asnScheduleItemResponseList = new ArrayList<>();

            itemScheduleMap.forEach((k, v) -> {
                AsnScheduleItemResponse asnScheduleItemResponse = getAsnScheduleItemSupplierResponse(k, v);
                asnScheduleItemResponseList.add(asnScheduleItemResponse);
            });

            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100047);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(asnScheduleItemResponseList);
            baseResponse.setLogId(loginUser.getLogId());

        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to Get PPE Item List --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100048);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET PPE Item List End --- ");
        return baseResponse;
    }

    @Override
    public BaseResponse<ASNRequiredDate> getDatesForSupplier(Integer itemScheduleId) {
        log.info(loginUser.getLogId() + " Get -> GET DATES FOR SUPPLIER start --- ");
        BaseResponse<ASNRequiredDate> baseResponse = new BaseResponse<>();
        try {
            User supplier = userRepository.findByIsDeletedAndId(false, loginUser.getUserId());
            log.info(loginUser.getLogId() + " Get -> GET DATES FOR SUPPLIER for organization : " + loginUser.getOrgId());
            List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findByIsDeletedAndSubOrganizationIdAndSupplierIdAndItemScheduleId(false, loginUser.getSubOrgId(), supplier.getSupplierId(), itemScheduleId);
            List<ASNRequiredDate> asnRequiredDateList = new ArrayList<>();
            for (ItemScheduleSupplier itemScheduleSupplier : itemScheduleSupplierList) {
                ASNRequiredDate asnRequiredDate = new ASNRequiredDate();
                asnRequiredDate.setRequiredQuantity(itemScheduleSupplier.getRequiredQuantity());
                asnRequiredDate.setDate(itemScheduleSupplier.getRequiredDate());
                asnRequiredDate.setTiming(itemScheduleSupplier.getRequiredTime());
                asnRequiredDateList.add(asnRequiredDate);
            }
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100025);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setData(asnRequiredDateList);
            baseResponse.setLogId(loginUser.getLogId());
        } catch (Exception e) {
            log.error(loginUser.getLogId() + " Get -> Failed to GET DATES FOR SUPPLIER --- ");
            ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100049);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(null);
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setLogId(loginUser.getLogId());

            e.printStackTrace();
        }
        log.info(loginUser.getLogId() + " Get -> GET DATES FOR SUPPLIER End --- ");
        return baseResponse;
    }

    @Override
    public ByteArrayInputStream exportItemsToExcel(String month, int year) throws IOException {
        User supplier = userRepository.findByIsDeletedAndId(false, loginUser.getUserId());
        List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findByIsDeletedAndSubOrganizationIdAndItemScheduleScheduleMonthAndItemScheduleYearAndSupplierId(false, loginUser.getSubOrgId(), month, year, supplier.getSupplierId());

        Map<Date, List<ItemScheduleSupplier>> itemsGroupedByDate = new TreeMap<>(itemScheduleSupplierList.stream()
                .collect(Collectors.groupingBy(ItemScheduleSupplier::getRequiredDate)));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Items");

            // Create Header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Item Code");
            headerRow.createCell(2).setCellValue("Item Name");
            headerRow.createCell(3).setCellValue("Required Quantity");

            int rowIdx = 1;

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");

            for (Map.Entry<Date, List<ItemScheduleSupplier>> entry : itemsGroupedByDate.entrySet()) {
                Date date = entry.getKey();
                List<ItemScheduleSupplier> detailsList = entry.getValue();

                Row dateRow = sheet.createRow(rowIdx++);
                dateRow.createCell(0).setCellValue(formatter.format(date));

                for (ItemScheduleSupplier details : detailsList) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(1).setCellValue(details.getItemSchedule().getItem().getItemId());
                    row.createCell(2).setCellValue(details.getItemSchedule().getItem().getName());
                    row.createCell(3).setCellValue(details.getRequiredQuantity());
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void removeItemScheduleFromSupplier() {
        List<ItemScheduleSupplier> itemScheduleSupplierList = itemScheduleSupplierRepository.findAll();
        Map<ItemSchedule, List<ItemScheduleSupplier>> itemScheduleMap = itemScheduleSupplierList.stream().collect(Collectors.groupingBy(ItemScheduleSupplier::getItemSchedule));
        itemScheduleMap.forEach((k, v) -> {
            if (!CollectionUtils.isEmpty(v)) {
                ItemScheduleSupplier itemScheduleSupplier = v.stream().max(Comparator.comparing(ItemScheduleSupplier::getRequiredDate)).get();
                if (itemScheduleSupplier.getRequiredDate().after(new Date())) {
                    ItemSchedule itemSchedule = itemScheduleSupplier.getItemSchedule();
                    itemSchedule.setDeleted(true);
                    itemSchedule.setModifiedBy(loginUser.getUserId());
                    itemSchedule.setModifiedOn(new Date());
                    itemScheduleRepository.save(itemSchedule);
                }
            }
        });
    }

    private AsnScheduleItemResponse getAsnScheduleItemSupplierResponse(ItemSchedule itemSchedule, List<ItemScheduleSupplier> itemScheduleSupplierList) {
        AsnScheduleItemResponse asnScheduleItemResponse = new AsnScheduleItemResponse();
        User buyer = userRepository.findByIsDeletedAndId(false, itemSchedule.getCreatedBy());
        asnScheduleItemResponse.setItemId(itemSchedule.getItem().getId());
        asnScheduleItemResponse.setItemScheduleId(itemSchedule.getId());
        asnScheduleItemResponse.setItemCode(itemSchedule.getItem().getItemId());
        asnScheduleItemResponse.setItemName(itemSchedule.getItem().getName());
        asnScheduleItemResponse.setUnitOfMeasurement(itemSchedule.getItem().getUom());
        asnScheduleItemResponse.setYear(itemSchedule.getYear());
        asnScheduleItemResponse.setBuyer(buyer.getFirstName() + " " + buyer.getLastName());
        asnScheduleItemResponse.setScheduleId(itemSchedule.getScheduleId());
        asnScheduleItemResponse.setMonth(itemSchedule.getScheduleMonth());
        Integer sum = 0;
        for (ItemScheduleSupplier itemScheduleSupplier : itemScheduleSupplierList) {
            sum = sum + itemScheduleSupplier.getRequiredQuantity();
        }
        asnScheduleItemResponse.setTotalRequiredQuantity(sum);
        return asnScheduleItemResponse;
    }

    private BaseResponse<ItemSchedule> saveAsnScheduleLines(ItemSchedule itemSchedule, SendAsnScheduleRequest sendAsnScheduleRequest) throws ParseException {
        BaseResponse<ItemSchedule> baseResponse = new BaseResponse<>();
        List<ItemScheduleSupplier> itemScheduleSupplierList = new ArrayList<>();
        List<PPELine> ppeLineList = new ArrayList<>();
        Set<Supplier> supplierSet = new HashSet<>();
        for (SendAsnScheduleLineRequest sendAsnScheduleLineRequest : sendAsnScheduleRequest.getSendAsnScheduleLineRequestList()) {
            ItemScheduleSupplier itemScheduleSupplier = new ItemScheduleSupplier();
            itemScheduleSupplier.setOrganizationId(loginUser.getOrgId());
            itemScheduleSupplier.setSubOrganizationId(loginUser.getOrgId());
            itemScheduleSupplier.setItemSchedule(itemSchedule);
            itemScheduleSupplier.setSupplier(supplierRepository.findByIsDeletedAndId(false, sendAsnScheduleLineRequest.getSupplierId()));
            supplierSet.add(itemScheduleSupplier.getSupplier());
            itemScheduleSupplier.setPurchaseOrderHead(purchaseOrderHeadRepository.findByIdAndIsDeleted(sendAsnScheduleLineRequest.getPurchaseOrderHeadId(), false));

            itemScheduleSupplier.setRequiredDate(convertStringToDate(sendAsnScheduleLineRequest.getRequiredDate()));
            itemScheduleSupplier.setRequiredTime(convertStringToTime(sendAsnScheduleLineRequest.getRequiredTime()));
            itemScheduleSupplier.setRequiredQuantity(sendAsnScheduleLineRequest.getRequiredQuantity());
            itemScheduleSupplier.setCreatedBy(loginUser.getUserId());
            itemScheduleSupplier.setCreatedOn(new Date());
            itemScheduleSupplier.setIsDeleted(false);
            PPELine ppeLine = ppeLineRepository.findByIsDeletedAndSubOrganizationIdAndPpeHeadIdAndItemId(false, loginUser.getSubOrgId(), sendAsnScheduleLineRequest.getPpeHeadId(), itemSchedule.getItem().getId());
            ppeLine.setIsScheduled(true);
            ppeLineList.add(ppeLine);
            itemScheduleSupplierList.add(itemScheduleSupplier);
        }
        ppeLineRepository.saveAll(ppeLineList);
        itemScheduleSupplierRepository.saveAll(itemScheduleSupplierList);
        ResponseMessage responseMessage = GlobalMessages.getResponseMessages(ServiceConstants.ASN_ASNBS100041);
        baseResponse.setCode(responseMessage.getCode());
        baseResponse.setMessage(responseMessage.getMessage());
        baseResponse.setStatus(responseMessage.getStatus());
        baseResponse.setData(null);
        baseResponse.setLogId(loginUser.getLogId());

        return baseResponse;
    }

    private AsnScheduleItemResponse getAsnScheduleItemResponse(String month, Integer itemId, List<PPELine> ppeLines) {
        AsnScheduleItemResponse asnScheduleItemResponse = new AsnScheduleItemResponse();
        asnScheduleItemResponse.setItemId(itemId);
        asnScheduleItemResponse.setItemCode(ppeLines.get(0).getItem().getItemId());
        asnScheduleItemResponse.setItemName(ppeLines.get(0).getItem().getName());
        asnScheduleItemResponse.setUnitOfMeasurement(ppeLines.get(0).getItem().getUom());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ppeLines.get(0).getPpeHead().getStartDate());
        int year = calendar.get(Calendar.YEAR);
        asnScheduleItemResponse.setYear(year);
        asnScheduleItemResponse.setMonth(month);
        Integer sum = 0;
        for (PPELine ppeLine : ppeLines) {
            sum = sum + ppeLine.getRequiredQuantity();
        }
        asnScheduleItemResponse.setTotalRequiredQuantity(sum);
        return asnScheduleItemResponse;
    }

    private static Map<String, List<PPELine>> createMapByMonth(List<PPELine> ppeLineList) {
        Map<String, List<PPELine>> mapByMonth = new HashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        for (PPELine obj : ppeLineList) {
            String monthKey = monthFormat.format(obj.getPpeHead().getStartDate());
            mapByMonth.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(obj);
        }
        return mapByMonth;
    }

    public static Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dateString);
    }

    public static Time convertStringToTime(String timeString) {
        LocalTime localTime = LocalTime.parse(timeString);
        return Time.valueOf(localTime);
    }

    public String generateScheduleCode() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = this.random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(index));
        }

        return "SCH" + builder.toString();
    }

    public static String generateMessage(String recipientName, String scheduleId, String itemName) {
        return "Dear " + recipientName + ",\n\n" +
                "We are excited to inform you that Item for purchase order is Scheduled . Here are the details:\n\n" +

                "  - Schedule Id: " + scheduleId + "\n" +
                "  - Item Name: " + itemName + "\n" +
                "\n" +
                "If you have any questions or require further assistance, please feel free to reach out. We appreciate your cooperation.\n\n" +
                "Best regards,\n" +
                "Your Name\n" +
                "Your Position\n" +
                "Your Company/Organization\n" +
                "Contact Information";
    }

}
