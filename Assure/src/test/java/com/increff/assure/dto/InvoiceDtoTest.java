package com.increff.assure.dto;

import com.increff.assure.dao.*;
import com.increff.assure.pojo.*;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.InvoiceResponse;
import com.increff.commons.Exception.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.increff.assure.spring.TestPojo.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InvoiceDtoTest extends AbstractUnitTest {

    @Autowired
    private InvoiceDto dto;

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
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private InvoiceDao dao;


    /* when invoice type is self */
    @Test
    public void testGenerateInvoiceValid1() throws Exception {

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));//Collection.singleton list.

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo orderPojo = createOrderPojo(OrderStatus.ALLOCATED, channelPojo.getId(), "abcabc"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        orderDao.insert(orderPojo);
        OrderItemPojo orderItemPojo = createOrderItemPojo(orderPojo.getId(), productPojo.getGlobalSkuId(),
                20L, 20L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 100L, 20L
        , 0L);
        inventoryDao.insert(inventoryPojo);

        InvoiceResponse response = dto.generateInvoice(orderPojo.getId());

        assertEquals(orderPojo.getId(), response.getOrderId());
        assertEquals(true, response.getInvoiceUrl().length()>0);
        assertEquals(orderPojo.getOrderStatus(), OrderStatus.FULFILLED);
        assertEquals(Long.valueOf(20), orderItemPojo.getFulfilledQty());

    }

    /*  when order status is FULFILLED */
    @Test
    public void testGenerateInvoiceValid2() throws Exception {

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));//Collection.singleton list.

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo orderPojo = createOrderPojo(OrderStatus.ALLOCATED, channelPojo.getId(), "abcabc"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        orderDao.insert(orderPojo);
        OrderItemPojo orderItemPojo = createOrderItemPojo(orderPojo.getId(), productPojo.getGlobalSkuId(),
                20L, 20L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 100L, 20L
                , 0L);
        inventoryDao.insert(inventoryPojo);

        InvoiceResponse response = dto.generateInvoice(orderPojo.getId());
        InvoiceResponse response1 = dto.generateInvoice(orderPojo.getId());

        assertEquals(orderPojo.getId(), response.getOrderId());
        assertEquals(true, response.getInvoiceUrl().length()>0);
        assertEquals(orderPojo.getOrderStatus(), OrderStatus.FULFILLED);
        assertEquals(Long.valueOf(20), orderItemPojo.getFulfilledQty());

    }

    /* when order status is CREATED */
    @Test
    public void testGenerateInvoice() throws Exception {

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));//Collection.singleton list.

        BinSkuPojo binSkuPojo = createBinSkuPojo(100L, productPojo.getGlobalSkuId(), pojoList.get(0).getBinId());
        binSkuDao.insert(binSkuPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderPojo orderPojo = createOrderPojo(OrderStatus.CREATED, channelPojo.getId(), "abcabc"
                , partyPojoCustomer.getPartyId(), partyPojoClient.getPartyId());
        orderDao.insert(orderPojo);
        OrderItemPojo orderItemPojo = createOrderItemPojo(orderPojo.getId(), productPojo.getGlobalSkuId(),
                20L, 0L, 0L, 10.0);
        orderItemDao.insert(orderItemPojo);

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 100L, 0L
                , 0L);
        inventoryDao.insert(inventoryPojo);
        try {
            InvoiceResponse response = dto.generateInvoice(orderPojo.getId());
            fail();
        }catch (ApiException e){
            assertEquals("Order is not allocated", e.getMessage());
        }
    }




}
