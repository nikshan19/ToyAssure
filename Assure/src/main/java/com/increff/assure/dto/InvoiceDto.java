package com.increff.assure.dto;

import com.increff.assure.pojo.*;
import com.increff.assure.service.*;
import com.increff.assure.spring.ClientWrapper;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.InvoiceData;
import com.increff.commons.Data.InvoiceResponse;
import com.increff.commons.Exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.increff.assure.util.InvoiceUtil.*;
import static com.increff.commons.Util.ConvertUtil.convert;
import static com.increff.commons.Util.XmlUtil.generatePdf;

@Service
public class InvoiceDto {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private  PartyService partyService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private InvoiceService service;
    @Autowired
    private ClientWrapper clientWrapper;
    @Autowired
    private ChannelListingService channelListingService;


    //TODO change it based on invoice type
    @Transactional(rollbackOn = ApiException.class)
    public InvoiceResponse generateInvoice(Long orderId) throws Exception {
        OrderPojo orderPojo = orderService.getCheckOrder(orderId);
        ChannelPojo channelPojo = channelService.getCheckChannelById(orderPojo.getChannelId());

        if (orderPojo.getOrderStatus().equals(OrderStatus.CREATED)) {
            throw new ApiException("Order is not allocated");
        }
        if (orderPojo.getOrderStatus().equals(OrderStatus.FULFILLED)) {
            HashMap<InvoicePojo, byte[]> invoicePojoToByte = service.getcheckByOrderId(orderId);
            InvoicePojo pojo = invoicePojoToByte.keySet().stream().collect(Collectors.toList()).get(0);
            return getResponse(pojo, invoicePojoToByte.get(pojo), pojo.getInvoiceUrl());
        }
        if (channelPojo.getInvoiceType().equals(Invoice.InvoiceType.SELF)) {
                HashMap<String, byte[]> urlToByte = generatePdf(getInvoiceData(orderId));
            String invoiceUrl = urlToByte.keySet().stream().collect(Collectors.toList()).get(0);
                InvoicePojo invoicePojo = convertToInvoicePojo(orderId, invoiceUrl);
                return getResponse(service.add(invoicePojo), urlToByte.get(invoiceUrl), invoiceUrl);
        }
        return generateInvoiceInChannelApp(getChannelInvoiceData(orderId));

        }

        @Transactional(rollbackOn = ApiException.class)
        private InvoiceData getInvoiceData(Long orderId) throws Exception {
            OrderPojo orderPojo = orderService.getCheckOrder(orderId);
            List<OrderItemPojo> pojos = orderService.getItemsByOrderId(orderId);
            Map<Long, Long> globalSkuIdsToQty = pojos.stream().collect(Collectors.
                    toMap(val->val.getGlobalSkuId(), val->val.getOrderedQty()));

            inventoryService.updateFulfilledQty(globalSkuIdsToQty);
            orderService.updateFulfilledQty(orderId);

            List<Long> globalSkuIds = pojos.stream().
                    map(OrderItemPojo::getGlobalSkuId).collect(Collectors.toList());
            Map<Long, ProductPojo> globalSkuToProduct = productService.getByGlobalSkuIds(globalSkuIds);

            ChannelPojo channelPojo = channelService.getChannel(orderPojo.getChannelId());
            PartyPojo partyPojoClient = partyService.get(orderPojo.getClientId());
            PartyPojo partyPojoCustomer = partyService.get(orderPojo.getCustomerId());
            return convertToInvoiceData(channelPojo.getChannelName(),orderPojo.getChannelOrderId(),
                    partyPojoClient.getPartyName(), partyPojoCustomer.getPartyName(), pojos, globalSkuToProduct);
        }

    @Transactional(rollbackOn = ApiException.class)
    private ChannelInvoiceData getChannelInvoiceData(Long orderId) throws Exception {
        OrderPojo orderPojo = orderService.getCheckOrder(orderId);
        List<OrderItemPojo> pojos = orderService.getItemsByOrderId(orderId);
        Map<Long, Long> globalSkuIdsToQty = pojos.stream().collect(Collectors.
                toMap(val->val.getGlobalSkuId(), val->val.getOrderedQty()));

        inventoryService.updateFulfilledQty(globalSkuIdsToQty);
        orderService.updateFulfilledQty(orderId);

        List<Long> globalSkuIds = pojos.stream().
                map(OrderItemPojo::getGlobalSkuId).collect(Collectors.toList());
        Map<Long, ProductPojo> globalSkuToProduct = productService.getByGlobalSkuIds(globalSkuIds);

        ChannelPojo channelPojo = channelService.getChannel(orderPojo.getChannelId());
        PartyPojo partyPojoClient = partyService.get(orderPojo.getClientId());
        PartyPojo partyPojoCustomer = partyService.get(orderPojo.getCustomerId());

        Map<Long, String> globalSkuToChannelSku = channelListingService.getByChannelIdAndGlobalSkuIds(orderPojo.getChannelId(),
                globalSkuIds);

        return convertToChannelInvoiceData(channelPojo.getChannelName(), orderPojo.getChannelOrderId(),
                partyPojoClient.getPartyName(), partyPojoCustomer.getPartyName(), pojos, globalSkuToProduct,
                globalSkuToChannelSku);
    }



    private InvoiceResponse generateInvoiceInChannelApp(ChannelInvoiceData invoiceData) throws IOException {
        String url = clientWrapper.postForInvoiceInChannelApp(invoiceData);

        Path pdfPath = Paths.get(url);
        byte[] pdf = Files.readAllBytes(pdfPath);

        InvoicePojo invoicePojo = convertToInvoicePojo(invoiceData.getOrderId(), url);
        return getResponse(service.add(invoicePojo), pdf, url);
    }

    private InvoiceResponse getResponse(InvoicePojo pojo, byte[] b64Data, String url){
        InvoiceResponse response = convert(pojo, InvoiceResponse.class);
        response.setB64Data(b64Data);
        response.setInvoiceUrl(url);
        return response;
    }




    }






