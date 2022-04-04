package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.dao.PartyDao;
import com.increff.assure.dao.ProductDao;
import com.increff.assure.pojo.*;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.Party;
import com.increff.commons.Exception.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.increff.assure.spring.TestPojo.*;
import static org.junit.Assert.assertEquals;

public class ChannelListingServiceTest extends AbstractUnitTest {

    @Autowired
    private ChannelListingService channelListingService;

    @Autowired
    private ChannelListingDao dao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private PartyDao partyDao;
    @Autowired
    private ProductDao productDao;

    /* when all inputs are valid */
    @Test
    public void testAddChannelListingsValid1() throws ApiException {
        List<ChannelListingPojo> listBefore = dao.selectAll();

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("myntra", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(), "abcabc",
                channelPojo.getId(), partyPojoClient.getPartyId());
        List<ChannelListingPojo> listBack = channelListingService.addChannelListings(Arrays.asList(channelListingPojo));

        List<ChannelListingPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals("abcabc", listBack.get(0).getChannelSkuId());
        assertEquals(productPojo.getGlobalSkuId(), listBack.get(0).getGlobalSkuId());
        assertEquals(partyPojoClient.getPartyId(), listBack.get(0).getClientId());
    }
    /*  when some channelListings are already present in DB */
    @Test
    public void testAddChannelListingsValid2() throws ApiException {

        List<ChannelListingPojo> listBefore = dao.selectAll();

        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("myntra", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(), "abcabc",
                channelPojo.getId(), partyPojoClient.getPartyId());
        List<ChannelListingPojo> listBack = channelListingService.addChannelListings(Arrays.asList(channelListingPojo));

        List<ChannelListingPojo> listBack1 = channelListingService.addChannelListings(Arrays.asList(channelListingPojo));

        List<ChannelListingPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals("abcabc", listBack1.get(0).getChannelSkuId());
        assertEquals(productPojo.getGlobalSkuId(), listBack1.get(0).getGlobalSkuId());
        assertEquals(partyPojoClient.getPartyId(), listBack1.get(0).getClientId());

    }

    @Test
    public void testGetByClientIdChannelIdAndChannelSkuIds() throws ApiException {
        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("myntra", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(), "abcabc",
                channelPojo.getId(), partyPojoClient.getPartyId());
        dao.insert(channelListingPojo);

        List<String> channelSkuIds = Arrays.asList(channelListingPojo).stream().map(ChannelListingPojo::getChannelSkuId)
                .collect(Collectors.toList());

        List<ChannelListingPojo> list = channelListingService.getByClientIdChannelIdChannelSkuIds(
                partyPojoClient.getPartyId(), channelPojo.getId(), channelSkuIds);

        assertEquals(list.size(), 1);
        assertEquals(list.get(0).getChannelSkuId(), "abcabc");
        assertEquals(list.get(0).getGlobalSkuId(), productPojo.getGlobalSkuId());
        assertEquals(list.get(0).getClientId(), partyPojoClient.getPartyId());
        assertEquals(list.get(0).getChannelId(), channelPojo.getId());

    }

    @Test
    public void testGetByChannelIdAndGlobalSkuIds() throws ApiException {
        PartyPojo partyPojoClient = createMemberPojo("client1", Party.PartyType.CLIENT);
        PartyPojo partyPojoCustomer = createMemberPojo("cust1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojoClient);
        partyDao.insert(partyPojoCustomer);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojoClient.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("myntra", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingPojo channelListingPojo = createListing(productPojo.getGlobalSkuId(), "abcabc",
                channelPojo.getId(), partyPojoClient.getPartyId());
        dao.insert(channelListingPojo);

        List<Long> globalSkuIds = Arrays.asList(channelListingPojo).stream().map(ChannelListingPojo::getGlobalSkuId)
                .collect(Collectors.toList());

        Map<Long, String> map = channelListingService.getByChannelIdAndGlobalSkuIds(channelPojo.getId(),
                 globalSkuIds);

        assertEquals(map.size(), 1);
        assertEquals(map.get(map.keySet().stream().collect(Collectors.toList()).get(0)), "abcabc");
        assertEquals(map.keySet().stream().collect(Collectors.toList()).get(0), productPojo.getGlobalSkuId());

    }


}
