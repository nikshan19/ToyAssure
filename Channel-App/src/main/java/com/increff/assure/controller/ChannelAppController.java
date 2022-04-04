package com.increff.assure.controller;

import com.increff.assure.dto.ChannelAppDto;
import com.increff.commons.Data.*;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.OrderSearchForm;
import com.increff.commons.Form.OrderSearchFormChannelApp;
import com.increff.commons.Form.OrderWithChannelSkuIdForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api
@RestController
@RequestMapping("/api")
public class ChannelAppController {

    @Autowired
    private ChannelAppDto dto;

    @ApiOperation(value = "add order")
    @RequestMapping(path = "/orders", method= RequestMethod.POST)
    public void addOrder(@RequestBody OrderWithChannelSkuIdForm form) throws IOException, ApiException {
        dto.placeOrder(form);
    }


    @ApiOperation(value = "generate invoice")
    @RequestMapping(path = "/orders/invoice", method= RequestMethod.POST)
    public String generateInvoice(@RequestBody ChannelInvoiceData invoiceData) throws Exception {
        return dto.generateInvoice(invoiceData);
    }

    @ApiOperation(value = "search order")
    @RequestMapping(path = "/orders/search", method= RequestMethod.POST)
    public List<OrderData> searchOrder(@RequestBody OrderSearchForm form){
        return dto.searchOrder(form);
    }

    @ApiOperation(value = "gets all channels")
    @RequestMapping(path = "/channels", method= RequestMethod.GET)
    public List<ChannelData> getAllChannels(){
        return dto.getAllChannels();
    }

    @ApiOperation(value = "gets all parties")
    @RequestMapping(path = "/parties", method= RequestMethod.GET)
    public List<PartyData> getAllParties(){
        return dto.getAllParties();
    }


}
