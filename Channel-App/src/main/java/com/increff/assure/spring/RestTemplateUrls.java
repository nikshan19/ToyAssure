package com.increff.assure.spring;

import org.springframework.beans.factory.annotation.Autowired;

public class RestTemplateUrls {

    @Autowired
    ApplicationProperties properties;

    public String getAssureOrderUri(){
        return properties.getServerUri()+"orders";
    }

    public String getAssureSearchOrderUri(){return properties.getServerUri()+"orders/search";}

    public String getAssureAllChannelsUri(){
        return properties.getServerUri()+"channels";
    }

    public String getAssureAllPartiesUri(){
        return properties.getServerUri()+"parties";
    }
}
