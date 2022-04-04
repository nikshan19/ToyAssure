package com.increff.assure.spring;


import com.increff.assure.pojo.*;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.InvoiceResponse;
import com.increff.commons.Form.*;
import io.swagger.models.auth.In;
import org.hibernate.criterion.Order;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestPojo {


        public static PartyPojo createMemberPojo(String name, Party.PartyType type) {
            PartyPojo member = new PartyPojo();
            member.setPartyName(name);
            member.setPartyType(type);
            return member;
        }

        public static BinSkuPojo createBinSkuPojo(Long qty, Long globalSku, Long binId) {
            BinSkuPojo pojo = new BinSkuPojo();
            pojo.setQty(qty);
            pojo.setGlobalSkuId(globalSku);
            pojo.setBinId(binId);
            return pojo;
        }

        public static OrderPojo createOrderPojo(OrderStatus status, Long channelId, String channelOrderId, Long customerId, Long clientId) {
            OrderPojo pojo = new OrderPojo();
            pojo.setOrderStatus(status);
            pojo.setChannelId(channelId);
            pojo.setChannelOrderId(channelOrderId);
            pojo.setCustomerId(customerId);
            pojo.setClientId(clientId);
            return pojo;
        }

        public static ChannelListingPojo createListing(Long globalSkuId, String channelSkuId, Long channelId, Long clientId) {
            ChannelListingPojo pojo = new ChannelListingPojo();
            pojo.setGlobalSkuId(globalSkuId);
            pojo.setChannelSkuId(channelSkuId);
            pojo.setChannelId(channelId);
            pojo.setClientId(clientId);
            return pojo;
        }

        public static InventoryPojo createInventoryPojo(Long globalSkuId, Long qty) {
            InventoryPojo pojo = new InventoryPojo();
            pojo.setGlobalSkuId(globalSkuId);
            pojo.setAllocatedQty(0L);
            pojo.setFulfilledQty(0L);
            pojo.setAvailableQty(qty);
            return pojo;
        }

        public static ChannelPojo createChannelPojo(String name, Invoice.InvoiceType type) {
            ChannelPojo pojo = new ChannelPojo();
            pojo.setInvoiceType(type);
            pojo.setChannelName(name);
            return pojo;
        }

        public static UploadBinSkuForm createUploadBinSkuForm(Long clientId, List<BinSkuForm> binSkuForms){
            UploadBinSkuForm form = new UploadBinSkuForm();
            form.setClientId(clientId);
            form.setFormList(binSkuForms);
            return form;
        }

        public static BinSkuForm createBinSkuForm(Long binId, String clientSkuId, Long qty) {
            BinSkuForm  form = new BinSkuForm ();
            form.setBinId(binId);
            form.setClientSkuId(clientSkuId);
            form.setQty(qty);
            return form;
        }

        public static UpdateBinSkuForm createUpdateBinSkuForm(Long qty){
            UpdateBinSkuForm form = new UpdateBinSkuForm();
            form.setQty(qty);
            return form;
        }

        public static List<BinPojo> createBinPojo(Long qty){
            List<BinPojo> bp = new ArrayList<>();
            for(int i = 0; i<qty;i++){
                bp.add(new BinPojo());
            }
            return bp;
        }

        public static ChannelForm createChannelForm(String name, Invoice.InvoiceType type) {
            ChannelForm form = new ChannelForm();
            form.setInvoiceType(type);
            form.setChannelName(name);
            return form;
        }

        public static UploadChannelListingForm createUploadChannelListingForm(Long clientId, Long channelId,
                                                                 List<ChannelListingForm> list) {
            UploadChannelListingForm form = new UploadChannelListingForm();
            form.setChannelId(channelId);
            form.setChannelListings(list);
            form.setClientId(clientId);
            return form;
        }

        public static ChannelListingForm createChannelListingForm(String channelSkuId, String clientSkuId){
            ChannelListingForm form  = new ChannelListingForm();
            form.setChannelSkuId(channelSkuId);
            form.setClientSkuId(clientSkuId);
            return form;
        }




        public static PartyForm createMemberForm(String name, Party.PartyType type) {
            PartyForm form = new PartyForm();
            form.setPartyName(name);
            form.setPartyType(type);
            return form;
        }

        public static ProductForm createProductForm(String name, String brandId, String clientSkuId, String desc, Double mrp) {
            ProductForm form = new ProductForm();
            form.setBrandId(brandId);
            form.setDescription(desc);
            form.setClientSkuId(clientSkuId);
            form.setProductMrp(mrp);
            form.setProductName(name);
            return form;
        }

        public static UploadProductForm createUploadProductForm(Long clientId, List<ProductForm> forms){
            UploadProductForm form = new UploadProductForm();
            form.setFormList(forms);
            form.setClientId(clientId);
            return form;
        }

        public static UpdateProductForm createUpdateProductForm(String brandId, Double mrp, String description
                , String productName){
            UpdateProductForm form = new UpdateProductForm();
            form.setProductName(productName);
            form.setProductMrp(mrp);
            form.setBrandId(brandId);
            form.setDescription(description);
            return form;

        }

        public static ProductPojo createProductPojo(String name, String brandId, String clientSkuId, String desc, Double mrp, Long clientId) {
            ProductPojo pojo = new ProductPojo();
            pojo.setProductName(name);
            pojo.setProductMrp(mrp);
            pojo.setBrandId(brandId);
            pojo.setDescription(desc);
            pojo.setClientId(clientId);
            pojo.setClientSkuId(clientSkuId);
            return pojo;
        }

        public static OrderItemWithClientSkuId createOrderItemWithClientSkuId(String clientSkuId, Long qty
                , Double sellingPrice){

            OrderItemWithClientSkuId form = new OrderItemWithClientSkuId();
            form.setClientSkuId(clientSkuId);
            form.setQty(qty);
            form.setSellingPricePerUnit(sellingPrice);
            return form;
        }

        public static OrderItemPojo createOrderItemPojo(Long orderId, Long globalSkuId, Long orderedQty
                , Long allocatedQty, Long fulfilledQty, Double sellingPrice){
            OrderItemPojo pojo = new OrderItemPojo();
            pojo.setOrderId(orderId);
            pojo.setGlobalSkuId(globalSkuId);
            pojo.setOrderedQty(orderedQty);
            pojo.setAllocatedQty(allocatedQty);
            pojo.setFulfilledQty(fulfilledQty);
            pojo.setSellingPricePerUnit(sellingPrice);
            return pojo;
        }

        public static InventoryPojo createInventoryPojo(Long globalSkuId, Long availabaleQty, Long allocatedQty
                , Long fulfilledQty){
            InventoryPojo pojo = new InventoryPojo();
            pojo.setGlobalSkuId(globalSkuId);
            pojo.setAvailableQty(availabaleQty);
            pojo.setAllocatedQty(allocatedQty);
            pojo.setFulfilledQty(fulfilledQty);
            return pojo;
        }

        public static OrderWithClientSkuIdForm createOrderWithClientSkuIdForm(Long clientId, String channelOrderId
                , Long customerId, List<OrderItemWithClientSkuId> orderItems){
            OrderWithClientSkuIdForm form = new OrderWithClientSkuIdForm();
            form.setFormList(orderItems);
            form.setClientId(clientId);
            form.setCustomerId(customerId);
            form.setChannelOrderId(channelOrderId);
            return form;
        }

        public static OrderItemWithChannelSkuId createOrderItemWithChannelSkuId(String channelSkuId, Long qty
                    , Double sellingPrice){
            OrderItemWithChannelSkuId form = new OrderItemWithChannelSkuId();
            form.setChannelSkuId(channelSkuId);
            form.setQty(qty);
            form.setSellingPricePerUnit(sellingPrice);
            return form;
        }

        public static OrderWithChannelSkuIdForm createOrderWithChannelSkuIdForm(Long channelId, String channelOrderId
        , List<OrderItemWithChannelSkuId> orderItems, Long clientId, Long customerId){
            OrderWithChannelSkuIdForm form = new OrderWithChannelSkuIdForm();
            form.setChannelId(channelId);
            form.setChannelOrderId(channelOrderId);
            form.setOrderItemWithChannelSkuIdList(orderItems);
            form.setClientId(clientId);
            form.setCustomerId(customerId);
            return form;
        }

        public static OrderSearchForm createOrderSearchForm(String startDate, String endDate, Long channelId,
                                                            String channelOrderId, OrderStatus orderStatus){

            OrderSearchForm form = new OrderSearchForm();
            form.setStartDate(startDate);
            form.setEndDate(endDate);
            form.setChannelId(channelId);
            form.setChannelOrderId(channelOrderId);
            form.setOrderStatus(orderStatus);
            return form;
        }

        public static ChannelInvoiceData createChannelInvoiceData(String channelName, Long orderId,
                                                                  String clientName, String customerName,
                                                                  Double total){

            ChannelInvoiceData data = new ChannelInvoiceData();
            data.setProductDataList(null);
            data.setChannelName(channelName);
            data.setOrderId(orderId);
            data.setClientName(clientName);
            data.setTotal(total);
            data.setCustomerName(customerName);
            return data;
        }

        public static OrderSearchProperties createOrderSearchProperties(OrderStatus orderStatus, ZonedDateTime startDate,
                                                                        ZonedDateTime endDate, String channelOrderId,
                                                                        Long channelId){
            OrderSearchProperties properties = new OrderSearchProperties();
            properties.setOrderStatus(orderStatus);
            properties.setStartDate(startDate);
            properties.setEndDate(endDate);
            properties.setChannelOrderId(channelOrderId);
            properties.setChannelId(channelId);
            return properties;
        }






}
