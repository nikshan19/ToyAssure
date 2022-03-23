package com.increff.assure.spring;

import lombok.var;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {


            var factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(3000);
            factory.setReadTimeout(3000);//TODO 45sec

            return new RestTemplate(factory);
    }
}