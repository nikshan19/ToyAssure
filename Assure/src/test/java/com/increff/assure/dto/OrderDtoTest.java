package com.increff.assure.dto;

import com.increff.assure.dao.*;
import com.increff.assure.pojo.*;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.OrderData;
import com.increff.commons.Data.OrderDetailsData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.increff.assure.spring.TestPojo.*;
import static com.increff.commons.Constants.ConstantNames.MAX_DAYS_DIFFERENCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private OrderDto dto;

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

    /*  when all inputs are valid*/
    @Test
    public void testAddOrderUsingClientSkuIdsValid1() throws ApiException {

        List<OrderPojo> listBefore = dao.selectAll();

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);

        List<OrderPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(form.getChannelOrderId(), dao.select(listAfter.get(listAfter.size()-1).getId()).getChannelOrderId());
        assertEquals(OrderStatus.CREATED, dao.select(listAfter.get(listAfter.size()-1).getId()).getOrderStatus());



    }

    /* when client SkuId is in upper case and have white spaces*/
    @Test
    public void testAddOrderUsingClientSkuIdValid2() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId("  CSKiUd1      "
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);

        List<OrderPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(form.getChannelOrderId(), dao.select(listAfter.get(listAfter.size()-1).getId()).getChannelOrderId());
        assertEquals(OrderStatus.CREATED, dao.select(listAfter.get(listAfter.size()-1).getId()).getOrderStatus());

    }
    /* when clientId or Customer Ids are invalid*/
    @Test
    public void testAddOrderUsingClientSkuIdInvalid1() throws ApiException {


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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(7777777L, "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        try {
            dto.addOrderUsingClientSkuIds(form);
            fail();
        }catch (ApiException e){
            assertEquals("Party with given id: "+ 7777777L+ " doesn't exist", e.getMessage());
        }



    }

    /* when channelOrderId is already present in DB*/
    @Test
    public void testAddOrderUsingClientSkuIdsInvalid2() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());

        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);
        try {
            dto.addOrderUsingClientSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Channel Order ID: "+form.getChannelOrderId()+" already exists", e.getMessage());
        }

    }


    /*  when channelOrderId is empty*/
    @Test
    public void testAddOrderUsingClientSkuIdsInvalid3() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "   "
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));


        try {
            dto.addOrderUsingClientSkuIds(form);
            fail();
        }catch (ApiException e){
            assertEquals("Channel Order ID must not be empty", e.getMessage());
        }

    }

    /* when client SKU ID(s) are empty*/
    @Test
    public void testAddOrderUsingClientSkuIdInvalid4() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId("    "
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        try {
            dto.addOrderUsingClientSkuIds(form);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Client SKU ID(s) at indexes: "+" ["+1+"]", e.getMessage());
        }



    }

    /* when duplicate ClientSkuIds are present in a CSV*/
    @Test
    public void testAddOrderUsingClientSkuIdsInvalid5() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1, orderItems1}));


        try {
            dto.addOrderUsingClientSkuIds(form);
            fail();
        }catch (ApiException e){
            assertEquals("Duplicate Client SKU ID(s): "+" [\""+orderItems1.getClientSkuId()+"\"]", e.getMessage());
        }

    }


    /*  when client SKU ID(s) are invalid*/
    @Test
    public void testAddOrderUsingClientSkuIdsInvalid6() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId("hdgsfhfhgdggdggdgdgdgdgdgdggdg"
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        try {
            dto.addOrderUsingClientSkuIds(form);
            fail();
        }catch (ApiException e){
            assertEquals("Client SKU ID(s) not found: "+" [\""+"hdgsfhfhgdggdggdgdgdgdgdgdggdg"+"\"]", e.getMessage());
        }


        }



        @Test
        public void testAllocateOrderValid1() throws ApiException {

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

            InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 100L,
                    0L, 0L);
            inventoryDao.insert(inventoryPojo);

            ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
            channelDao.insert(channelPojo);

            OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                    , 40L, productPojo.getProductMrp());
            OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                    , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

            dto.addOrderUsingClientSkuIds(form);

            List<OrderPojo> listAfter = dao.selectAll();

            dto.allocateOrder(listAfter.get(listAfter.size()-1).getId());

            assertEquals(OrderStatus.ALLOCATED, dao.select(listAfter.get(listAfter.size()-1).getId()).getOrderStatus());
            List<OrderItemPojo> orderItems = orderItemDao.selectByOrderId(listAfter.get(listAfter.size()-1).getId());
            assertEquals(Long.valueOf(40), orderItemDao.select(orderItems.get(orderItems.size()-1).getId()).getAllocatedQty());
    }

/* when Order is already allocated*/
    @Test
    public void testAllocateOrderInvalid1() throws ApiException {

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

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 100L,
                0L, 0L);
        inventoryDao.insert(inventoryPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);

        List<OrderPojo> listAfter = dao.selectAll();

        dto.allocateOrder(listAfter.get(listAfter.size()-1).getId());

        try{
            dto.allocateOrder(listAfter.get(listAfter.size()-1).getId());
            fail();
        }catch(ApiException e){
            assertEquals("Order already allocated", e.getMessage());
        }
    }

    /*  when available qty is less than ordered qty*/
    @Test
    public void testAllocateInvalid3() throws ApiException {
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

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 30L,
                0L, 0L);
        inventoryDao.insert(inventoryPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);

        List<OrderPojo> listAfter = dao.selectAll();

        dto.allocateOrder(listAfter.get(listAfter.size()-1).getId());

        assertEquals(OrderStatus.CREATED, dao.select(listAfter.get(listAfter.size()-1).getId()).getOrderStatus());
        List<OrderItemPojo> orderItems = orderItemDao.selectByOrderId(listAfter.get(listAfter.size()-1).getId());
        assertEquals(Long.valueOf(30), orderItemDao.select(orderItems.get(orderItems.size()-1).getId()).getAllocatedQty());

    }


    /*  when all inputs are valid*/
    @Test
    public void testAddOrderUsingChannelSkuIdsValid1() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId(channelListingPojo.getChannelSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                channelListingPojo.getChannelSkuId(), Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        dto.addOrderUsingChannelSkuIds(form);

        List<OrderPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(form.getChannelOrderId(), dao.select(listAfter.get(listAfter.size()-1).getId()).getChannelOrderId());
        assertEquals(OrderStatus.CREATED, dao.select(listAfter.get(listAfter.size()-1).getId()).getOrderStatus());



    }

    /* when channel SkuId is in upper case and have white spaces*/
    @Test
    public void testAddOrderUsingChannelSkuIdInvalid1() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId("1CHskuid   "
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                channelListingPojo.getChannelSkuId(), Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Channel SKU ID(s) not found: "+" [\""+"1CHskuid   "+"\"]", e.getMessage());
        }

    }

    /* when clientId or Customer Ids are invalid*/
    @Test
    public void testAddOrderUsingChannelSkuIdInvalid2() throws ApiException {
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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId(channelListingPojo.getChannelSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                channelListingPojo.getChannelSkuId(), Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , 70707070707L
                , partyPojoCustomer.getPartyId());


        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch (ApiException e){
            assertEquals("Party with given id: "+70707070707L+ " doesn't exist", e.getMessage());
        }



    }

    /*  when channel Order iD already exist in DB */
    @Test
    public void testAddOrderUsingChannelSkuIdsInvalid3() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId(channelListingPojo.getChannelSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                channelListingPojo.getChannelSkuId(), Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        dto.addOrderUsingChannelSkuIds(form);
        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Channel Order ID: "+form.getChannelOrderId()+" already exists", e.getMessage());
        }

    }

    /*  when channel Order is Empty */
    @Test
    public void testAddOrderUsingChannelSkuIdsInvalid4() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId(channelListingPojo.getChannelSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                "   ", Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Channel Order ID must not be empty", e.getMessage());
        }

    }

    /*  when channel SKU ID is Empty */
    @Test
    public void testAddOrderUsingChannelSkuIdsInvalid5() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId("  "
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                "111111", Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Empty Channel SKU ID(s) at indexes: "+" ["+1+"]", e.getMessage());
        }

    }

    /*  when channel SKU ID is duplicate in CSV */
    @Test
    public void testAddOrderUsingChannelSkuIdsInvalid6() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId(channelListingPojo.getChannelSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                "111111", Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1, orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Duplicate Channel SKU ID(s): "+" [\""+orderItems1.getChannelSkuId()+"\"]",
                    e.getMessage());
        }

    }


    /*  when channel SKU ID(s) are invalid */
    @Test
    public void testAddOrderUsingChannelSkuIdsInvalid7() throws ApiException {

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

        ChannelPojo channelPojo = createChannelPojo("1channel", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(),"1chskuid"
                , channelPojo.getId(), partyPojoClient.getPartyId());
        channelListingDao.insert(channelListingPojo);
        OrderItemWithChannelSkuId orderItems1 = createOrderItemWithChannelSkuId("babaabab"
                , 40L, productPojo.getProductMrp());
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm(channelPojo.getId(),
                "111111", Arrays.asList(new OrderItemWithChannelSkuId[]{orderItems1})
                , partyPojoClient.getPartyId()
                , partyPojoCustomer.getPartyId());

        try {
            dto.addOrderUsingChannelSkuIds(form);
            fail();
        }catch(ApiException e){
            assertEquals("Channel SKU ID(s) not found: "+" [\""+"babaabab"+"\"]", e.getMessage());
        }

    }


    @Test
    public void testSearchById() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);

        List<OrderData> data  = dto.searchByOrderId(dao.selectAll().get(dao.selectAll().size()-1).getId());

        assertEquals("abcbca", data.get(0).getChannelOrderId());

    }

    /* when all inputs are valid*/
    @Test
    public void testSearch() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);
        OrderSearchForm form1 = createOrderSearchForm("", "", null, "abcbca", OrderStatus.CREATED);
        List<OrderData> data = dto.searchOrder(form1);

        assertEquals(form.getChannelOrderId(), data.get(0).getChannelOrderId());

    }

    /* either start date or end date is not present*/
    @Test
    public void testSearchInvalid1() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);
        OrderSearchForm form1 = createOrderSearchForm("", "2022-04-01", null, "", OrderStatus.CREATED);
        try {
            List<OrderData> data = dto.searchOrder(form1);
            fail();
        }catch (ApiException e){
            assertEquals("Both Start Date and End Date must be present", e.getMessage());
        }

    }

    /* difference between start date and end date is greater than 30 days*/
    @Test
    public void testSearchInvalid2() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);
        OrderSearchForm form1 = createOrderSearchForm("2022-02-01", "2022-04-01", null, "", OrderStatus.CREATED);
        try {
            List<OrderData> data = dto.searchOrder(form1);
            fail();
        }catch (ApiException e){
            assertEquals("Difference between Start Date and End Date must not exceed "+ MAX_DAYS_DIFFERENCE, e.getMessage());
        }

    }

    /* start date is after end date */
    @Test
    public void testSearchInvalid3() throws ApiException {

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

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);
        OrderSearchForm form1 = createOrderSearchForm("2022-05-01", "2022-04-01", null, "", OrderStatus.CREATED);
        try {
            List<OrderData> data = dto.searchOrder(form1);
            fail();
        }catch (ApiException e){
            assertEquals("Start Date should be before End Date", e.getMessage());
        }

    }


    @Test
    public void testGetOrderItems() throws ApiException {

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

        InventoryPojo inventoryPojo = createInventoryPojo(productPojo.getGlobalSkuId(), 100L,
                0L, 0L);
        inventoryDao.insert(inventoryPojo);

        ChannelPojo channelPojo = createChannelPojo("internal", Invoice.InvoiceType.SELF);
        channelDao.insert(channelPojo);

        OrderItemWithClientSkuId orderItems1 = createOrderItemWithClientSkuId(productPojo.getClientSkuId()
                , 40L, productPojo.getProductMrp());
        OrderWithClientSkuIdForm form = createOrderWithClientSkuIdForm(partyPojoClient.getPartyId(), "abcbca"
                , partyPojoCustomer.getPartyId(), Arrays.asList(new OrderItemWithClientSkuId[]{orderItems1}));

        dto.addOrderUsingClientSkuIds(form);
        List<OrderDetailsData>  detailsData = dto.getOrderItems(dao.selectAll().get(dao.selectAll().size()-1).getId());

        assertEquals(productPojo.getClientSkuId(), detailsData.get(0).getClientSkuId());
        assertEquals(orderItems1.getQty(), detailsData.get(0).getOrderedQty());


    }








}
