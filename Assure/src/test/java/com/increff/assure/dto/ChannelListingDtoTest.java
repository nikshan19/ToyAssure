package com.increff.assure.dto;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.dao.PartyDao;
import com.increff.assure.dao.ProductDao;
import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.PartyPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Constants.Party;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.ChannelListingForm;
import com.increff.commons.Form.UploadChannelListingForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.increff.assure.spring.TestPojo.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChannelListingDtoTest extends AbstractUnitTest {

    @Autowired
    private ChannelListingDto dto;

    @Autowired
    private PartyDao partyDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ChannelListingDao channelListingDao;

    /* when all inputs are valid and are in lower case and have no white spaces*/
    @Test
    public void testAddValid1() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", productPojo.getClientSkuId());
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form}));
        dto.addChannelListings(uploadForm);

        List<ChannelListingPojo> listAfter = channelListingDao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(productPojo.getGlobalSkuId(), listAfter.get(listAfter.size()-1).getGlobalSkuId());
    }

    /* when all inputs are valid and are in Upper case and have white spaces*/
    @Test
    public void testAddValid2() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", "   CsKiud1    ");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form}));
        dto.addChannelListings(uploadForm);

        List<ChannelListingPojo> listAfter = channelListingDao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(productPojo.getGlobalSkuId(), listAfter.get(listAfter.size()-1).getGlobalSkuId());
    }

    /* when client ID is invalid */
    @Test
    public void testAddInvalid1() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", "   CsKiud1    ");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(5765765L
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form}));
        try{
            dto.addChannelListings(uploadForm);
            fail();
        }catch (ApiException e){
            assertEquals("Party with given id: "+5765765L+ " doesn't exist", e.getMessage());
        }
    }

    /* when channel ID is invalid */
    @Test
    public void testAddInvalid2() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", "   CsKiud1    ");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , 65765775L, Arrays.asList(new ChannelListingForm[]{form}));
        try{
            dto.addChannelListings(uploadForm);
            fail();
        }catch (ApiException e){
            assertEquals("Channel doesn't exist with id: "+65765775L, e.getMessage());
        }
    }

    /* when client SKU ID is empty */
    @Test
    public void testAddInvalid3() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", "       ");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form}));
        try{
            dto.addChannelListings(uploadForm);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Client SKU ID(s) at indexes: "+" ["+1+"]", e.getMessage());
        }
    }

    /* when channel SKU ID is empty */
    @Test
    public void testAddInvalid4() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("       ", "cskiud1");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form}));
        try{
            dto.addChannelListings(uploadForm);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Channel SKU ID(s) at indexes: "+" ["+1+"]", e.getMessage());
        }
    }


    /* when channel SKU ID is duplicate */
    @Test
    public void testAddInvalid5() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", "cskiud1");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form, form}));

        try{
            dto.addChannelListings(uploadForm);
            fail();
        }catch (ApiException e){
            assertEquals("Duplicate Channel SKU ID(s): "+" [\""+form.getChannelSkuId()+"\"]", e.getMessage());
        }
    }


    /* when channel SKU ID is already present in DB */
    @Test
    public void testAddInvalid6() throws ApiException {
        List<ChannelListingPojo> listBefore = channelListingDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        ChannelPojo channelPojo = createChannelPojo("c1", Invoice.InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        ChannelListingForm form = createChannelListingForm("chskuid1", "cskiud1");
        UploadChannelListingForm uploadForm = createUploadChannelListingForm(partyPojo.getPartyId()
                , channelPojo.getId(), Arrays.asList(new ChannelListingForm[]{form}));
        dto.addChannelListings(uploadForm);

        try{
            dto.addChannelListings(uploadForm);
            fail();
        }catch (ApiException e){
            assertEquals("Channel SKU ID(s) already exist(s): "+ " [\""+"chskuid1"+"\"]", e.getMessage());
        }
    }




}
