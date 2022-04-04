package com.increff.assure.util;

import com.increff.assure.pojo.InvoicePojo;
import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.pojo.OrderPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.commons.Data.*;
import com.increff.commons.Util.ConvertUtil;
import com.increff.commons.Util.XmlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.increff.commons.Util.ConvertUtil.convert;

public class InvoiceUtil {

    public static InvoiceData convertToInvoiceData(String channelName,String channelOrderId, String clientName,
                                                   String customerName, List<OrderItemPojo> orderItemPojos, Map<Long,
            ProductPojo> globalSkuToProduct) throws Exception {
        List<InvoiceProductData> invoiceProductDataList = new ArrayList<>();

        Double total = 0D;
        for(OrderItemPojo oip: orderItemPojos){
            ProductPojo p = globalSkuToProduct.get(oip.getGlobalSkuId());
            InvoiceProductData ipd = ConvertUtil.convert(p, InvoiceProductData.class);
            ipd.setQty(oip.getOrderedQty());
            ipd.setClientSkuId(p.getClientSkuId());
            ipd.setSellingPricePerUnit(oip.getSellingPricePerUnit());
            invoiceProductDataList.add(ipd);
            total+=oip.getSellingPricePerUnit()*oip.getOrderedQty();
        }

        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setOrderId(orderItemPojos.get(0).getOrderId());
        invoiceData.setChannelOrderId(channelOrderId);
        invoiceData.setChannelName(channelName);
        invoiceData.setClientName(clientName);
        invoiceData.setCustomerName(customerName);
        invoiceData.setGeneratedDateTime(XmlUtil.getDateTime());
        invoiceData.setInvoiceProductDataList(invoiceProductDataList);
        invoiceData.setTotal(total);
        return invoiceData;
    }

    public static ChannelInvoiceData convertToChannelInvoiceData(String channelName, String channelOrderId,
                                                                 String clientName, String customerName,
                                                   List<OrderItemPojo> orderItemPojos, Map<Long,
            ProductPojo> globalSkuToProduct, Map<Long, String> globalSkuToChannelSku) throws Exception {
        List<ChannelInvoiceProductData> channelInvoiceProductDataList = new ArrayList<>();

        Double total = 0D;
        for(OrderItemPojo oip: orderItemPojos){
            ProductPojo p = globalSkuToProduct.get(oip.getGlobalSkuId());
            ChannelInvoiceProductData ipd = ConvertUtil.convert(p, ChannelInvoiceProductData.class);
            ipd.setQty(oip.getOrderedQty());
            ipd.setChannelSkuId(globalSkuToChannelSku.get(oip.getGlobalSkuId()));
            ipd.setSellingPricePerUnit(oip.getSellingPricePerUnit());
            channelInvoiceProductDataList.add(ipd);
            total+=oip.getSellingPricePerUnit()*oip.getOrderedQty();
        }

        ChannelInvoiceData invoiceData = new ChannelInvoiceData();
        invoiceData.setOrderId(orderItemPojos.get(0).getOrderId());
        invoiceData.setChannelOrderId(channelOrderId);
        invoiceData.setChannelName(channelName);
        invoiceData.setClientName(clientName);
        invoiceData.setCustomerName(customerName);
        invoiceData.setGeneratedDateTime(XmlUtil.getDateTime());
        invoiceData.setProductDataList(channelInvoiceProductDataList);
        invoiceData.setTotal(total);
        return invoiceData;
    }

    public static InvoicePojo convertToInvoicePojo(Long orderId, String invoiceUrl){
        InvoicePojo invoicePojo = new InvoicePojo();
        invoicePojo.setOrderId(orderId);
        invoicePojo.setInvoiceUrl(invoiceUrl);
        return invoicePojo;
    }

    public static InvoiceResponse convertToInvoiceResponse(InvoicePojo invoicePojo){
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setId(invoicePojo.getId());
        invoiceResponse.setOrderId(invoicePojo.getOrderId());
        return  invoiceResponse;
    }






//
//// return Invoice response class
////
//    public static void createPdfResponse(byte[] bytes) throws IOException {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();
//        response.setContentType("application/pdf");
//        response.setContentLength(bytes.length);
//        response.getOutputStream().write(bytes);
//        response.getOutputStream().flush();
//    }
}
