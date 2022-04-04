package com.increff.assure.spring;

import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.ChannelData;
import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.OrderData;
import com.increff.commons.Data.PartyData;
import com.increff.commons.Form.OrderSearchForm;
import com.increff.commons.Form.OrderWithChannelSkuIdForm;
import org.hibernate.criterion.Order;

public class TestPojo {


    public static OrderWithChannelSkuIdForm createOrderWithChannelSkuIdForm(String channelOrderId, Long channelId,
                                                                         Long customerId, Long clientId){
        OrderWithChannelSkuIdForm form = new OrderWithChannelSkuIdForm();
        form.setOrderItemWithChannelSkuIdList(null);
        form.setChannelOrderId(channelOrderId);
        form.setChannelId(channelId);
        form.setCustomerId(customerId);
        form.setClientId(clientId);
        return form;
    }
    public static OrderData createOrderData(Long clientId, Long customerId, String channelOrderId,
                                            OrderStatus os, Long channelId){
        OrderData data = new OrderData();
        data.setClientId(clientId);
        data.setCustomerId(customerId);
        data.setChannelOrderId(channelOrderId);
        data.setOrderStatus(os);
        data.setChannelId(channelId);
        data.setId(199L);
        return data;
    }

    public static ChannelInvoiceData createChannelInvoiceData(String clientName, String channelName, String customerName, String dateTime, Long channelId, String channelOrderId, Long orderId, Double total) {
        ChannelInvoiceData data=new ChannelInvoiceData();
        data.setOrderId(orderId);
        data.setTotal(total);
        data.setProductDataList(null);
        data.setChannelName(channelName);
        data.setClientName(clientName);
        data.setCustomerName(customerName);
        data.setGeneratedDateTime(dateTime);
        return data;
    }

    public static OrderSearchForm createSearchOrderForm(String startDate, String endDate, long channelId, String channelOrderId, OrderStatus status) {
        OrderSearchForm form = new OrderSearchForm();
        form.setChannelId(channelId);
        form.setChannelOrderId(channelOrderId);
        form.setEndDate(endDate);
        form.setStartDate(startDate);
        form.setOrderStatus(status);
        return form;
    }

    public static ChannelData createChannelData(Long id, String name, Invoice.InvoiceType invoiceType){
        ChannelData data = new ChannelData();
        data.setId(id);
        data.setChannelName(name);
        data.setInvoiceType(invoiceType);
        return data;
    }

    public static PartyData createPartyData(Long id, String partyName, Party.PartyType partyType){
        PartyData data = new PartyData();
        data.setPartyId(id);
        data.setPartyName(partyName);
        data.setPartyType(partyType);
        return data;
    }
}
