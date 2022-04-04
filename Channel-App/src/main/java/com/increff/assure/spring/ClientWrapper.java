package com.increff.assure.spring;

import com.increff.assure.spring.RestTemplateUrls;
import com.increff.commons.Data.ChannelData;
import com.increff.commons.Data.InvoiceData;
import com.increff.commons.Data.OrderData;
import com.increff.commons.Data.PartyData;
import com.increff.commons.Form.OrderSearchForm;
import com.increff.commons.Form.OrderSearchFormChannelApp;
import com.increff.commons.Form.OrderWithChannelSkuIdForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ClientWrapper {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateUrls restTemplateUrls;

    public OrderData postForOrderInAssure(OrderWithChannelSkuIdForm form){
        return restTemplate.postForObject(restTemplateUrls.getAssureOrderUri(), form, OrderData.class);
    }

    public List<OrderData> postForOrderSearch(OrderSearchForm form){
        return restTemplate.postForObject(restTemplateUrls.getAssureSearchOrderUri(), form, List.class);
    }

    public List<ChannelData> getForChannels(){
        return restTemplate.getForObject(restTemplateUrls.getAssureAllChannelsUri(), List.class);
    }

    public List<PartyData> getAllParties(){
        return restTemplate.getForObject(restTemplateUrls.getAssureAllPartiesUri(), List.class);
    }
}
