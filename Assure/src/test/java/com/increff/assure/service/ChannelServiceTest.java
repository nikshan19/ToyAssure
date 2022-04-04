package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.assure.spring.TestPojo;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Exception.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.increff.assure.spring.TestPojo.createChannelPojo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChannelServiceTest extends AbstractUnitTest {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelDao dao;

    /* when all inputs are valid*/
    @Test
    public void testAddChannelValid1() throws ApiException {

        List<ChannelPojo> list_before = dao.selectAll();
        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelService.addChannel(channelPojo);
        List<ChannelPojo> list_after = dao.selectAll();

        assertEquals(list_before.size()+1, list_after.size());
        assertEquals("c1", dao.select(list_after.get(list_after.size()-1).getId()).getChannelName());
    }

    /* when all inputs are valid and are in Upper case as well and have white spaces*/

    @Test
    public void testAddChannelValid2() throws ApiException {

        List<ChannelPojo> list_before = dao.selectAll();
        ChannelPojo channelPojo = createChannelPojo("    C1    ", Invoice.InvoiceType.CHANNEL);
        channelService.addChannel(channelPojo);
        List<ChannelPojo> list_after = dao.selectAll();

        assertEquals(list_before.size()+1, list_after.size());
        assertEquals("c1", dao.select(list_after.get(list_after.size()-1).getId()).getChannelName());
    }

    @Test
    public void testAddChannelInvalid1() throws ApiException {

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelService.addChannel(channelPojo);

        ChannelPojo channelPojo1 = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        try{
            channelService.addChannel(channelPojo1);
            fail();
        }catch (ApiException e){
            assertEquals("Channel with Name: \""+"c1"+"\" already exists", e.getMessage());
        }
    }

    @Test
    public void testGetChannel() throws ApiException {

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelService.addChannel(channelPojo);
        List<ChannelPojo> list_after = dao.selectAll();

        assertEquals(list_after.get(list_after.size()-1).getChannelName(), channelPojo.getChannelName());
    }

    @Test
    public void testGetAllChannels() throws ApiException {

        List<ChannelPojo> list_before = dao.selectAll();
        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        dao.insert(channelPojo);
        List<ChannelPojo> list_after = channelService.getAllChannels();

        assertEquals(list_before.size()+1, list_after.size());
    }

    /*  when all inputs are valid */
    @Test
    public void testGetCheckByNameValid() throws ApiException {

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        dao.insert(channelPojo);
        List<ChannelPojo> list_after = dao.selectAll();

        assertEquals(channelPojo.getChannelName(), channelService.getCheckChannelByName(channelPojo.getChannelName())
                .getChannelName());
        assertEquals(channelPojo.getInvoiceType(), channelService.getCheckChannelByName(channelPojo.getChannelName())
                .getInvoiceType());
    }
    /*  when channel with given name does not exist in DB*/
    @Test
    public void testGetCheckByNameInvalid1(){

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        dao.insert(channelPojo);
        try {
            ChannelPojo pojo = channelService.getCheckChannelByName("abcabc");
            fail();
        }catch (ApiException e){
            assertEquals("Channel with name: "+"abcabc"+" doesn't exist", e.getMessage());
        }

    }

    /*  when all inputs are valid */
    @Test
    public void testGetCheckByIdValid() throws ApiException {

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        dao.insert(channelPojo);

        assertEquals(channelPojo.getChannelName(), channelService.getCheckChannelById(channelPojo.getId())
                .getChannelName());
        assertEquals(channelPojo.getInvoiceType(), channelService.getCheckChannelById(channelPojo.getId())
                .getInvoiceType());
    }

    /* when channel with given ID does not exist in db */
    @Test
    public void testGetCheckByIdInvalid1() throws ApiException {

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        dao.insert(channelPojo);

        try{
            channelService.getCheckChannelById(9L);
            fail();
        }catch (ApiException e){
            assertEquals("Channel doesn't exist with id: "+9L, e.getMessage());
        }
    }




}
