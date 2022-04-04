package com.increff.assure.spring;

import com.increff.assure.spring.RestTemplateUrls;
import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.InvoiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClientWrapper {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateUrls restTemplateUrls;

    public String postForInvoiceInChannelApp(ChannelInvoiceData data){
        return restTemplate.postForObject(restTemplateUrls.getChannelAppInvoiceUri(), data, String.class);
    }
}
