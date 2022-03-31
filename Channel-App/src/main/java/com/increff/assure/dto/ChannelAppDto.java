package com.increff.assure.dto;
//
//import com.increff.channelapp.spring.RestTemplateUrls;
import com.increff.assure.spring.ApplicationProperties;
import com.increff.assure.spring.ClientWrapper;
import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.InvoiceData;
import com.increff.commons.Data.OrderData;
import com.increff.commons.Form.OrderSearchForm;
import com.increff.commons.Form.OrderSearchFormChannelApp;
import com.increff.commons.Form.OrderWithChannelSkuIdForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.increff.assure.util.ChannelXmlUtil.generatePdf;


@Service
public class ChannelAppDto {

    @Autowired
    private ClientWrapper clientWrapper;

    public void placeOrder(OrderWithChannelSkuIdForm form) {
        clientWrapper.postForOrderInAssure(form);
    }
    public String generateInvoice(ChannelInvoiceData invoiceData) throws Exception {
        return generatePdf(invoiceData);

    }
    public List<OrderData> searchOrder(OrderSearchForm form){
        return clientWrapper.postForOrderSearch(form);
    }



}
