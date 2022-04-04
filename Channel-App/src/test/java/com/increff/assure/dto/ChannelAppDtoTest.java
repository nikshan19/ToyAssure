package com.increff.assure.dto;

import com.increff.assure.spring.AbstractUnitTest;
import com.increff.assure.spring.ApplicationProperties;
import com.increff.assure.spring.ClientWrapper;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.ChannelData;
import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.OrderData;
import com.increff.commons.Data.PartyData;
import com.increff.commons.Form.OrderSearchForm;
import com.increff.commons.Form.OrderWithChannelSkuIdForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.increff.assure.spring.TestPojo.*;
import static org.junit.Assert.assertEquals;

public class ChannelAppDtoTest extends AbstractUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private ClientWrapper clientWrapper;

    @InjectMocks
    private ChannelAppDto dto;
    private ChannelData[] channelList = new ChannelData[1];



    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPlaceOrder()
    {
        OrderWithChannelSkuIdForm form = createOrderWithChannelSkuIdForm("abc",1L, 2L,
                3L);
        Mockito.when(clientWrapper.postForOrderInAssure(form)).thenReturn(createOrderData(3L, 2L,
                "abc", OrderStatus.CREATED, 1L));
        OrderData data = dto.placeOrder(form);

        assertEquals("abc", data.getChannelOrderId());
        assertEquals(Long.valueOf(1), data.getChannelId());
        //assert all the fields in order data.
        //assert no of calls, form(arg captcha) with which we call assure.

    }

    @Test
    public void testGetInvoice() throws Exception {
        ChannelInvoiceData data = createChannelInvoiceData("puma", "flipkart", "nikshan",
                "2022-04-01", 1L, "ghjgjg", 1090909L, 100.0);
        String s = dto.generateInvoice(data);

        assertEquals(s.length()>0, true);
    }

    @Test
    public void testSearch(){
        OrderData data = createOrderData(1L, 2L, "abc", OrderStatus.FULFILLED, 4L);
        OrderSearchForm form = createSearchOrderForm("2022-02-01", "2022-02-01", 1L,
                "abc", OrderStatus.FULFILLED);

        Mockito.when(clientWrapper.postForOrderSearch(form)).thenReturn(Arrays.asList(new OrderData[]{data}));
        List<OrderData> finalData = dto.searchOrder(form);

        assertEquals(Long.valueOf(1), finalData.get(0).getClientId());
        assertEquals(Long.valueOf(2), finalData.get(0).getCustomerId());
        assertEquals(Long.valueOf(4), finalData.get(0).getChannelId());
        assertEquals(OrderStatus.FULFILLED, finalData.get(0).getOrderStatus());

    }

    @Test
    public void testGetAllChannels(){
        ChannelData data = createChannelData(101L, "Rare", Invoice.InvoiceType.CHANNEL);
        Mockito.when(clientWrapper.getForChannels()).thenReturn(Arrays.asList(data));

        List<ChannelData> finalData = dto.getAllChannels();

        assertEquals(Long.valueOf(101), finalData.get(0).getId());
        assertEquals("Rare", finalData.get(0).getChannelName());
        assertEquals(Invoice.InvoiceType.CHANNEL, finalData.get(0).getInvoiceType());
    }

    @Test
    public void testGetAllParties(){
        PartyData data = createPartyData(101L, "Adidas", Party.PartyType.CLIENT);
        Mockito.when(clientWrapper.getAllParties()).thenReturn(Arrays.asList(data));

        List<PartyData> finalData = dto.getAllParties();

        assertEquals(Long.valueOf(101), finalData.get(0).getPartyId());
        assertEquals("Adidas", finalData.get(0).getPartyName());
        assertEquals(Party.PartyType.CLIENT, finalData.get(0).getPartyType());
    }

}
