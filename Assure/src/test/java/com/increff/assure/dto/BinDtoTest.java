package com.increff.assure.dto;

import com.increff.assure.dao.BinDao;
import com.increff.assure.dao.BinSkuDao;
import com.increff.assure.dao.PartyDao;
import com.increff.assure.dao.ProductDao;
import com.increff.assure.pojo.BinPojo;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.PartyPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.BinData;
import com.increff.commons.Data.BinSkuData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.BinSkuForm;
import com.increff.commons.Form.UploadBinSkuForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.increff.assure.spring.TestPojo.*;
import static com.increff.commons.Constants.ConstantNames.SEQ_MAX_VAL;
import static com.increff.commons.Constants.ConstantNames.SEQ_MIN_VAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BinDtoTest extends AbstractUnitTest {

    @Autowired
    private BinDto dto;
    @Autowired
    private PartyDao partyDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BinDao binDao;
    @Autowired
    private BinSkuDao binSkuDao;

    @Test
    public void testAddBin() throws ApiException {
        Long binCount = 5L;
        List<BinData> data = dto.add(binCount);

        assertEquals(binCount, Long.valueOf(data.size()));
    }

    /* when binCount exceeds 5000 */
    @Test
    public void testAddBinInvalid(){
        Long binCount = SEQ_MAX_VAL+1;
        try{
            dto.add(binCount);
            fail();
        } catch (ApiException e) {
            assertEquals("Bins count should be less than "+SEQ_MAX_VAL, e.getMessage());
        }
    }


    @Test
    public void testGetAllBins() throws ApiException {
        List<BinPojo> list_before = binDao.selectAll();
        Long binCount = 5L;
        List<BinData> data = dto.add(binCount);
        List<BinData> data_after = dto.getAllBins();

        assertEquals(list_before.size()+binCount, data_after.size());

    }

    /* when all inputs are in lower case and donot have white spaces.*/
    @Test
    public void testUploadBinSkuValid1() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), productPojo.getClientSkuId(), 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        dto.uploadBinSku(form);

        List<BinSkuPojo> binSkuPojos_after = binSkuDao.selectAll();

        assertEquals(binSkuPojos_before.size()+1, binSkuPojos_after.size());
        assertEquals(productPojo.getGlobalSkuId(), binSkuPojos_after.get(binSkuPojos_after.size()-1).getGlobalSkuId());
        assertEquals(pojoList.get(0).getBinId(), binSkuPojos_after.get(binSkuPojos_after.size()-1).getBinId());

    }

    /* when clientSkuIds are in upper case and have  white spaces.*/
    @Test
    public void testUploadBinSkuValid2() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), "    CSKIUD1   ", 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        dto.uploadBinSku(form);

        List<BinSkuPojo> binSkuPojos_after = binSkuDao.selectAll();

        assertEquals(binSkuPojos_before.size()+1, binSkuPojos_after.size());
        assertEquals(productPojo.getGlobalSkuId(), binSkuPojos_after.get(binSkuPojos_after.size()-1).getGlobalSkuId());
        assertEquals(pojoList.get(0).getBinId(), binSkuPojos_after.get(binSkuPojos_after.size()-1).getBinId());

    }

/* when client ID is invalid*/
    @Test
    public void testUploadBinSkuInvalid1() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), productPojo.getClientSkuId(), 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(20202L,
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        try {
            dto.uploadBinSku(form);
            fail();
        }catch (ApiException e){
            assertEquals("Party with given id: "+ 20202L+ " doesn't exist", e.getMessage());
        }


    }


    /* when bin ID(s) are invalid*/
    @Test
    public void testUploadBinSkuInvalid2() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(67676767L, productPojo.getClientSkuId(), 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        try {
            dto.uploadBinSku(form);
            fail();
        }catch (ApiException e){
            assertEquals("Bin doesn't exist with ID(s): "+" ["+67676767L+"]", e.getMessage());
        }


    }

    /* when clientSkuId(s)  are empty*/
    @Test
    public void testUploadBinSkuInvalid3() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), "    ", 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        try {
            dto.uploadBinSku(form);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Client SKU ID(s) at: "+" ["+1+"]", e.getMessage());
        }

    }


    /* when Combination of clientSkuId(s) and BinId(s) is being repeated */
    @Test
    public void testUploadBinSkuInvalid4() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), productPojo.getClientSkuId(), 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm, binSkuForm}));
        try {
            dto.uploadBinSku(form);
            fail();
        }catch (ApiException e){
            assertEquals("Duplicate Combination of Bin ID(s) and Client SKU ID(s) at indexes: "+" ["+2+"]", e.getMessage());
        }

    }

    /* when clientSkuId(s) are invalid .*/
    @Test
    public void testUploadBinSkuInvalid5() throws ApiException {
        List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), "hgdhsgfhsdgsgfsdfgs552", 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        try {
            dto.uploadBinSku(form);
            fail();
        }catch (ApiException e){
            assertEquals("Invalid Client SKU ID(s): "+" [\""+"hgdhsgfhsdgsgfsdfgs552"+"\"]", e.getMessage());
        }
    }

    @Test
    public void testUpdate() throws ApiException {

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), productPojo.getClientSkuId(), 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        dto.uploadBinSku(form);

        List<BinSkuPojo> binSkuPojos = binSkuDao.selectAll();
        BinSkuPojo currentBinSkuPojo = binSkuPojos.get(binSkuPojos.size()-1);
        binSkuForm.setQty(40L);
        dto.updateSingleBinSku(currentBinSkuPojo.getId(), createUpdateBinSkuForm(40L));

        assertEquals(Long.valueOf(40), binSkuDao.select(currentBinSkuPojo.getId()).getQty());

    }

    @Test
    public void testGetBinSku() throws ApiException {

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                10.0 ,partyPojo.getPartyId());
        productDao.insert(productPojo);

        List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
        BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), productPojo.getClientSkuId(), 100L);
        UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                Arrays.asList(new BinSkuForm[]{binSkuForm}));
        dto.uploadBinSku(form);
        List<BinSkuPojo> binSkuPojos = binSkuDao.selectAll();

        assertEquals(productPojo.getGlobalSkuId(), dto.getBinSku(binSkuPojos.get(binSkuPojos.size()-1).getId())
                .getGlobalSkuId());
        assertEquals(pojoList.get(0).getBinId(), dto.getBinSku(binSkuPojos.get(binSkuPojos.size()-1).getId())
                .getBinId());

    }

    @Test
    public void testGetAllBinSku() throws ApiException {

            List<BinSkuPojo> binSkuPojos_before = binSkuDao.selectAll();

            PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
            partyDao.insert(partyPojo);

            ProductPojo productPojo = createProductPojo("p1", "b1", "cskiud1", "desc1",
                    10.0 ,partyPojo.getPartyId());
            productDao.insert(productPojo);

            List<BinPojo> pojoList = binDao.insert(Arrays.asList(new BinPojo[]{new BinPojo()}));
            BinSkuForm binSkuForm = createBinSkuForm(pojoList.get(0).getBinId(), productPojo.getClientSkuId(), 100L);
            UploadBinSkuForm form = createUploadBinSkuForm(productPojo.getClientId(),
                    Arrays.asList(new BinSkuForm[]{binSkuForm}));
            dto.uploadBinSku(form);

            List<BinSkuData> binSkuData_after = dto.getAllBinSku();

            assertEquals(binSkuPojos_before.size()+1, binSkuData_after.size());


        }

    }










