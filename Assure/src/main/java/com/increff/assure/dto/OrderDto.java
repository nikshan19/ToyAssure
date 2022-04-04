package com.increff.assure.dto;

import com.increff.assure.pojo.*;
import com.increff.assure.service.*;
import com.increff.assure.spring.OrderSearchProperties;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Data.ErrorData;
import com.increff.commons.Data.OrderData;
import com.increff.commons.Data.OrderDetailsData;
import com.increff.commons.Data.OrderItemData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.assure.util.NormalizeUtil.normalizeClientSkus;
import static com.increff.assure.util.NormalizeUtil.normalizeOrderItemWithClientSkuIdForm;
import static com.increff.commons.Util.ConvertUtil.convert;
import static com.increff.commons.Util.ValidationUtil.*;

@Service
public class OrderDto extends AbstractDto {

    @Autowired
    private OrderService service;
    @Autowired
    private PartyService partyService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private BinService binService;
    @Autowired
    private ChannelListingService channelListingService;


    public OrderData addOrderUsingClientSkuIds(OrderWithClientSkuIdForm orderWithClientSkuIdForm) throws ApiException{
        validateUploadOrderForm(orderWithClientSkuIdForm);
        checkClientIdAndCustomerIdAndChannelOrderId(orderWithClientSkuIdForm);
        normalizeOrderItemWithClientSkuIdForm(orderWithClientSkuIdForm.getFormList());

        List<String> clientSkuIds = orderWithClientSkuIdForm.getFormList().stream().
                map(OrderItemWithClientSkuId::getClientSkuId).collect(Collectors.toList());
        Map<String, Long> clientSkuToGlobalSku= getCheckClientSkuId(orderWithClientSkuIdForm.getClientId(), clientSkuIds);
        Map<String, Long> clientSkuToQty = orderWithClientSkuIdForm.getFormList().stream().collect(Collectors.
                toMap(value->value.getClientSkuId(), value->value.getQty()));
        Map<String, Double> clientSkuToPrice = orderWithClientSkuIdForm.getFormList().stream().collect(Collectors.
                toMap(value->value.getClientSkuId(), value->value.getSellingPricePerUnit()));

        OrderPojo orderPojo = convertUploadOrderForm(orderWithClientSkuIdForm);
        OrderPojo p = service.addOrder(orderPojo);
        addOrderItems(p.getId(), clientSkuToGlobalSku, clientSkuToQty, clientSkuToPrice);
        return convert(p, OrderData.class);
    }

    public OrderData addOrderUsingChannelSkuIds(OrderWithChannelSkuIdForm form) throws ApiException {
        validateOrderByChannelApiForm(form);
        checkClientIdAndCustomerIdAndChannelOrderId(form);
        List<String> channelSkuIds = form.getOrderItemWithChannelSkuIdList().stream()
                .map(OrderItemWithChannelSkuId::getChannelSkuId).collect(Collectors.toList());
        Map<String, Long> channelSkuToGlobalSku = getCheckChannelSkuId(form.getClientId(),form.getChannelId(), channelSkuIds);
        Map<String, Long> channelSkuToQty = form.getOrderItemWithChannelSkuIdList().stream().collect(Collectors.
                toMap(value->value.getChannelSkuId(), value->value.getQty()));
        Map<String, Double> channelSkuToPrice = form.getOrderItemWithChannelSkuIdList().stream().collect(Collectors.
                toMap(value->value.getChannelSkuId(), value->value.getSellingPricePerUnit()));

        OrderPojo orderPojo = convert(form, OrderPojo.class);
        orderPojo.setOrderStatus(OrderStatus.CREATED);
        OrderPojo p = service.addOrder(orderPojo);
        addOrderItems(p.getId(), channelSkuToGlobalSku, channelSkuToQty, channelSkuToPrice);
        return convert(p, OrderData.class);
    }


    @Transactional(rollbackOn = ApiException.class)
    public void allocateOrder(Long id) throws ApiException {
        OrderPojo orderPojo = service.getCheckOrder(id);
        if(!orderPojo.getOrderStatus().equals(OrderStatus.CREATED)){
            throw new ApiException("Order already allocated");
        }
            List<OrderItemPojo> orderItemPojoList = service.getItemsByOrderId(id);
            for (OrderItemPojo orderItemPojo : orderItemPojoList) {
                Long qtyToAllocate = inventoryService.updateAvailableAndAllocatedQty(
                        orderItemPojo.getOrderedQty() - orderItemPojo.getAllocatedQty(),
                        orderItemPojo.getGlobalSkuId());
                InventoryPojo inventoryPojo = inventoryService.getCheckGlobalSkuId(orderItemPojo.getGlobalSkuId());
                service.allocateOrder(orderItemPojo.getId(), qtyToAllocate);
                binService.updateBinSkuQty(inventoryPojo.getAllocatedQty(), orderItemPojo.getGlobalSkuId());
            }
            checkAndAllocateOrder(id);


    }

    public List<OrderDetailsData> getOrderItems(Long orderId){
        List<OrderItemPojo> pojoList = service.getItemsByOrderId(orderId);
        List<Long> globalsSkuIds = pojoList.stream().map(OrderItemPojo::getGlobalSkuId).collect(Collectors.toList());
        Map<Long, ProductPojo> GlobalSkuToProductPojo = productService.getByGlobalSkuIds(globalsSkuIds);
        List<OrderDetailsData> dataList = new ArrayList<>();

        for(OrderItemPojo p : pojoList){
            OrderDetailsData d = convert(p, OrderDetailsData.class);
            d.setClientSkuId(GlobalSkuToProductPojo.get(p.getGlobalSkuId()).getClientSkuId());
            d.setBrandId(GlobalSkuToProductPojo.get(p.getGlobalSkuId()).getBrandId());
            dataList.add(d);
        }
        return dataList;
    }


    public List<OrderData> searchByOrderId(Long orderId) throws ApiException {
        OrderData data  = convert(service.getCheckOrder(orderId), OrderData.class);
        List<OrderData> dataList = new ArrayList<>();
        dataList.add(data);
        return dataList;
    }

    public List<OrderData> searchOrder(OrderSearchForm form) throws ApiException {
        checkEmptyFields(form);
        if((form.getStartDate()!=null)^(form.getEndDate()!=null)){
            throw new ApiException("Both Start Date and End Date must be present");
        }
        if(form.getStartDate()!=null && form.getEndDate()!=null){
            List<ZonedDateTime> dates = checkAndConvertDates(form.getStartDate(), form.getEndDate());
            OrderSearchProperties properties = convertToProperties(dates, form);
            List<OrderPojo> pojoList = service.searchOrder(properties);
            return convert(pojoList, OrderData.class);
        }

        OrderSearchProperties properties = convertWithoutDates(form);
        List<OrderPojo> pojoList = service.searchOrderWithoutDates(properties);
        return convert(pojoList, OrderData.class);

    }

    private void checkAndAllocateOrder(Long id){
        List<OrderItemPojo> orderItemPojoList = service.getItemsByOrderId(id);
        for(OrderItemPojo orderItemPojo:orderItemPojoList){
            if(orderItemPojo.getOrderedQty()!=orderItemPojo.getAllocatedQty()){
                return;
            }
        }
        service.changeStatusToAllocated(id);
    }

    private void checkEmptyFields(OrderSearchForm form){
        form.setEndDate(form.getEndDate().isEmpty()?null: form.getEndDate());
        form.setStartDate(form.getStartDate().isEmpty()?null:form.getStartDate());
        form.setChannelOrderId(form.getChannelOrderId().isEmpty()?null:form.getChannelOrderId());
    }

    private OrderSearchProperties convertToProperties(List<ZonedDateTime> dates, OrderSearchForm form){
        OrderSearchProperties properties = new OrderSearchProperties();
        properties.setChannelId(form.getChannelId());
        properties.setOrderStatus(form.getOrderStatus());
        properties.setChannelOrderId(form.getChannelOrderId());
        properties.setStartDate(dates.get(0));
        properties.setEndDate(dates.get(1));
        return properties;
    }

    private List<ZonedDateTime> checkAndConvertDates(String startDate, String endDate) throws ApiException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZoneId timeZone = ZoneId.systemDefault();
        ZonedDateTime startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay().atZone(timeZone);
        ZonedDateTime endDateTime = LocalDate.parse(endDate, formatter).atStartOfDay().plusDays(1).atZone(timeZone);
        checkDatesLimit(startDateTime, endDateTime);
        List<ZonedDateTime> list = new ArrayList<>();
        list.add(startDateTime);
        list.add(endDateTime);
        return list;
    }

    private OrderSearchProperties convertWithoutDates(OrderSearchForm form){
        OrderSearchProperties properties = new OrderSearchProperties();
        properties.setChannelId(form.getChannelId());
        properties.setChannelOrderId(form.getChannelOrderId());
        properties.setOrderStatus(form.getOrderStatus());
        return properties;
    }

    private OrderPojo convertUploadOrderForm(OrderWithClientSkuIdForm orderWithClientSkuIdForm) throws ApiException {
        Long channelId = channelService.getCheckChannelByName("internal").getId();
        checkDuplicateChannelIdAndChannelOrderId(channelId, orderWithClientSkuIdForm.getChannelOrderId());
        OrderPojo orderPojo = convert(orderWithClientSkuIdForm, OrderPojo.class);
        orderPojo.setChannelId(channelId);
        orderPojo.setOrderStatus(OrderStatus.CREATED);
        return orderPojo;
    }

    private void validateChannelOrderId(String channelOrderId) throws ApiException {
        if(channelOrderId.trim().isEmpty()){
            throw new ApiException("Channel Order ID must not be empty");
        }
    }

    private void checkClientIdAndCustomerIdAndChannelOrderId(OrderWithClientSkuIdForm form) throws ApiException {
        partyService.getCheck(form.getClientId());
        partyService.getCheck(form.getCustomerId());
        ChannelPojo channelPojo = channelService.getCheckChannelByName("internal");
        OrderPojo orderPojo = service.getByChannelIdAndChannelOrderId(channelPojo.getId(), form.getChannelOrderId());
        if(Objects.nonNull(orderPojo)){
            throw new ApiException("Channel Order ID: "+form.getChannelOrderId()+" already exists");
        }

    }

    private void checkClientIdAndCustomerIdAndChannelOrderId(OrderWithChannelSkuIdForm form) throws ApiException {
        partyService.getCheck(form.getClientId());
        partyService.getCheck(form.getCustomerId());
        OrderPojo orderPojo = service.getByChannelIdAndChannelOrderId(form.getChannelId(), form.getChannelOrderId());
        if(Objects.nonNull(orderPojo)){
            throw new ApiException("Channel Order ID: "+form.getChannelOrderId()+" already exists");
        }

    }


    private Map<String, Long> getCheckClientSkuId(Long clientId, List<String> clientSkuIds) throws ApiException {
        List<ProductPojo> pojoList = productService.getByClientSkuIdsAndClientId(clientId, clientSkuIds);
        List<String> finalClientSkuIds = pojoList.stream().map(ProductPojo::getClientSkuId).collect(Collectors.toList());
        List<String> differences = clientSkuIds.stream().filter(element -> !finalClientSkuIds.contains(element))
                .collect(Collectors.toList());
        if(differences.size()>0){
            throw new ApiException(ErrorData.convert("Client SKU ID(s) not found: ", differences));
        }
        return  pojoList.stream().collect(Collectors.toMap(value ->value.getClientSkuId(), value->value.getGlobalSkuId()));

    }

    private Map<String, Long> getCheckChannelSkuId(Long clientId, Long channelId, List<String> channelSkuIds) throws ApiException {
        List<ChannelListingPojo> pojoList = channelListingService.getByClientIdChannelIdChannelSkuIds(clientId,
                channelId, channelSkuIds);
        List<String> finalChannelSkuIds = pojoList.stream().map(ChannelListingPojo::getChannelSkuId)
                .collect(Collectors.toList());
        List<String> differences = channelSkuIds.stream().filter(element -> !finalChannelSkuIds.contains(element))
                .collect(Collectors.toList());
        if(differences.size()>0){
            throw new ApiException(ErrorData.convert("Channel SKU ID(s) not found: ", differences));
        }
        return pojoList.stream().collect(Collectors.toMap(value ->value.getChannelSkuId(), value->value.getGlobalSkuId()));
    }

    private void validateUploadOrderForm(OrderWithClientSkuIdForm orderWithClientSkuIdForm) throws ApiException {
        formValidation(orderWithClientSkuIdForm);
        validateChannelOrderId(orderWithClientSkuIdForm.getChannelOrderId());
        List<String> clientSkuIds = orderWithClientSkuIdForm.getFormList().stream().map(OrderItemWithClientSkuId::getClientSkuId)
                .collect(Collectors.toList());
        clientSkuIds = normalizeClientSkus(clientSkuIds);
        validateEmptyFields(clientSkuIds, "Client SKU ID(s)");
        checkDuplicates(clientSkuIds, "Client SKU ID(s)");

    }

    private void validateOrderByChannelApiForm(OrderWithChannelSkuIdForm form) throws ApiException {
        formValidation(form);
        validateChannelOrderId(form.getChannelOrderId());
       List<String> channelSkuIds = form.getOrderItemWithChannelSkuIdList().stream().
               map(OrderItemWithChannelSkuId::getChannelSkuId).collect(Collectors.toList());
       validateEmptyFields(channelSkuIds, "Channel SKU ID(s)");
       checkDuplicates(channelSkuIds, "Channel SKU ID(s)");

    }

    private void checkDuplicateChannelIdAndChannelOrderId(Long channelId, String channelOrderId) throws ApiException {
        OrderPojo orderPojo = service.getByChannelIdAndChannelOrderId(channelId, channelOrderId);
        if(Objects.nonNull(orderPojo)){
            throw  new ApiException("Order already exists with channel ID: "+channelId+" and channel Order ID: "
                    +channelOrderId);
        }
    }

    private void addOrderItems(Long orderId, Map<String, Long> productsMap, Map<String, Long> productQuantitiesMap,
                               Map<String, Double> sellingPrice){
        for(String skuId: productsMap.keySet()){
            OrderItemPojo orderItemPojo = new OrderItemPojo();
            orderItemPojo.setGlobalSkuId(productsMap.get(skuId));
            orderItemPojo.setOrderedQty(productQuantitiesMap.get(skuId));
            orderItemPojo.setAllocatedQty(0L);
            orderItemPojo.setFulfilledQty(0L);
            orderItemPojo.setOrderId(orderId);
            orderItemPojo.setSellingPricePerUnit(sellingPrice.get(skuId));
            service.insertOrderItem(orderItemPojo);
        }
    }








}



