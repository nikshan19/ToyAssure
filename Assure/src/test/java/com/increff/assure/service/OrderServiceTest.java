package com.increff.assure.service;

import com.increff.assure.dao.*;
import com.increff.assure.pojo.*;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.assure.spring.OrderSearchProperties;
import com.increff.assure.spring.TestPojo;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Constants.Party;
import com.increff.commons.Exception.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.increff.assure.spring.TestPojo.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrderServiceTest extends AbstractUnitTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PartyDao partyDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BinDao binDao;
    @Autowired
    private BinSkuDao binSkuDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ChannelListingDao channelListingDao;
    @Autowired
    private OrderDao dao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private InventoryDao inventoryDao;


    @Test
    public void testAddOrder() throws ApiException {
        List<OrderPojo> listBefore = dao.selectAll();

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());

        orderService.addOrder(pojo);
        List<OrderPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());

    }


    @Test
    public void testInsertOrderItemPojo() throws ApiException {

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());

        dao.insert(pojo);

        List<OrderItemPojo> listBefore = orderItemDao.selectAll();

        OrderItemPojo orderItemPojo = createOrderItemPojo(pojo.getId(), productPojo.getGlobalSkuId(), 10L,
                0L, 0L, 10.0);
        orderService.insertOrderItem(orderItemPojo);
        List<OrderItemPojo> listAfter = orderItemDao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());

    }



    @Test
    public void testGetByChannelIdAndChannelOrderId() throws ApiException {

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());

        dao.insert(pojo);



        assertEquals(pojo.getId(), orderService.getByChannelIdAndChannelOrderId(channelPojo.getId(),
                "ididid").getId());
        assertEquals(OrderStatus.CREATED, orderService.getByChannelIdAndChannelOrderId(channelPojo.getId(),
                "ididid").getOrderStatus());

    }

    @Test
    public void testAllocateOrder() throws ApiException {
        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());

        dao.insert(pojo);

        OrderItemPojo orderItemPojo = createOrderItemPojo(pojo.getId(), productPojo.getGlobalSkuId(), 10L,
                0L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(),
                100L, 0L, 0L);
        inventoryDao.insert(inventoryPojo);

        orderService.allocateOrder(orderItemPojo.getId(), 10L);

        assertEquals(Long.valueOf(10), orderItemPojo.getAllocatedQty());

    }

    @Test
    public void testChangeStatusToAllocated() throws ApiException {
        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());

        dao.insert(pojo);
        orderService.changeStatusToAllocated(pojo.getId());

        assertEquals(OrderStatus.ALLOCATED, pojo.getOrderStatus());

    }

    @Test
    public void testGetItemsByOrderId(){

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        dao.insert(pojo);

        OrderItemPojo orderItemPojo = createOrderItemPojo(pojo.getId(), productPojo.getGlobalSkuId(), 10L,
                0L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        List<OrderItemPojo> orderItemPojoList = orderService.getItemsByOrderId(pojo.getId());

        assertEquals(orderItemPojoList.size(), 1);
        assertEquals(orderItemPojoList.get(0).getOrderedQty(), Long.valueOf(10));
        assertEquals(orderItemPojoList.get(0).getOrderId(), pojo.getId());
        assertEquals(orderItemPojoList.get(0).getAllocatedQty(), orderItemPojo.getAllocatedQty());
        assertEquals(orderItemPojoList.get(0).getFulfilledQty(), Long.valueOf(0));


    }

    @Test
    public void testUpdateFulfilledQty(){
        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        dao.insert(pojo);

        OrderItemPojo orderItemPojo = createOrderItemPojo(pojo.getId(), productPojo.getGlobalSkuId(), 10L,
                10L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        orderService.updateFulfilledQty(pojo.getId());

        assertEquals(orderItemPojo.getOrderedQty(), Long.valueOf(10));
        assertEquals(orderItemPojo.getAllocatedQty(), Long.valueOf(0));
        assertEquals(orderItemPojo.getFulfilledQty(), Long.valueOf(10));
        assertEquals(pojo.getOrderStatus(), OrderStatus.FULFILLED);

    }

    @Test
    public void testSearch(){

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        dao.insert(pojo);

        OrderItemPojo orderItemPojo = createOrderItemPojo(pojo.getId(), productPojo.getGlobalSkuId(), 10L,
                10L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);


        OrderSearchProperties properties = createOrderSearchProperties(OrderStatus.CREATED,
                ZonedDateTime.now().minusDays(1L), ZonedDateTime.now().plusDays(1L), pojo.getChannelOrderId(), channelPojo.getId());

        List<OrderPojo> list = orderService.searchOrder(properties);

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getOrderStatus(), OrderStatus.CREATED);
        assertEquals(list.get(0).getId(), pojo.getId());
        assertEquals(list.get(0).getChannelOrderId(), pojo.getChannelOrderId());
        assertEquals(list.get(0).getChannelId(), pojo.getChannelId());
        assertEquals(list.get(0).getClientId(), pojo.getClientId());
        assertEquals(list.get(0).getCustomerId(), pojo.getCustomerId());
    }


    @Test
    public void testSearchWithoutDates(){

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo pojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "ididid"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        dao.insert(pojo);

        OrderItemPojo orderItemPojo = createOrderItemPojo(pojo.getId(), productPojo.getGlobalSkuId(), 10L,
                10L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        OrderSearchProperties properties = createOrderSearchProperties(OrderStatus.CREATED,
                null, null, pojo.getChannelOrderId(), channelPojo.getId());

        List<OrderPojo> list = orderService.searchOrderWithoutDates(properties);

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getOrderStatus(), OrderStatus.CREATED);
        assertEquals(list.get(0).getId(), pojo.getId());
        assertEquals(list.get(0).getChannelOrderId(), pojo.getChannelOrderId());
        assertEquals(list.get(0).getChannelId(), pojo.getChannelId());
        assertEquals(list.get(0).getClientId(), pojo.getClientId());
        assertEquals(list.get(0).getCustomerId(), pojo.getCustomerId());
    }

    @Test
    public void testGetCheckOrder() throws ApiException {
        try {
            OrderPojo pojo = orderService.getCheckOrder(176L);
            fail();
        }catch(ApiException e){
            assertEquals("Order doesn't exist with ID: "+176L, e.getMessage());
        }
    }



}
