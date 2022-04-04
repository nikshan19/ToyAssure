package com.increff.assure.dto;

import com.increff.assure.dao.PartyDao;
import com.increff.assure.dao.ProductDao;
import com.increff.assure.pojo.PartyPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.ProductData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.ProductForm;
import com.increff.commons.Form.UpdateProductForm;
import com.increff.commons.Form.UploadProductForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.increff.assure.spring.TestPojo.*;
import static com.increff.commons.Constants.ConstantNames.MAX_DESCRIPTION_STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto dto;

    @Autowired
    private PartyDao partyDao;
    @Autowired
    private ProductDao productDao;


    /*  when all the inputs are valid and are in lower case and have no white spaces */
    @Test
    public void testAddValid1() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(productForm.getProductName(), productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductName());
        assertEquals(productForm.getBrandId(), productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getBrandId());
        assertEquals(productForm.getProductMrp(), productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductMrp());


    }

    /*  when all the inputs are valid and are in Upper case and have white spaces */
    @Test
    public void testAddValid2() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("  p1   ", "b1", "  Cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals("p1", productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductName());
        assertEquals("cskuid1", productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getClientSkuId());


    }
/* where productName is blank*/
    @Test
    public void testAddInvalid1() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("    ", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        try {
            dto.add(uploadProductForm);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Product Name(s) at: "+" ["+1+"]", e.getMessage());
        }


    }

    /* where brandId is blank*/
    @Test
    public void testAddInvalid2() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "   ", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        try {
            dto.add(uploadProductForm);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Brand ID(s) at: "+" ["+1+"]", e.getMessage());
        }


    }

    /* where description is blank*/
    @Test
    public void testAddInvalid6() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "     "
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        try {
            dto.add(uploadProductForm);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Product Description(s) at: "+" ["+1+"]", e.getMessage());
        }


    }

    /* where description is blank*/
    @Test
    public void testAddInvalid7() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);

        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1",
                MAX_DESCRIPTION_STRING, 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        try {
            dto.add(uploadProductForm);
            fail();
        }catch (ApiException e){
            assertEquals("Description length greater than 255 at: "+" ["+1+"]", e.getMessage());
        }


    }

    /* where clientSkuId is blank*/
    @Test
    public void testAddInvalid3() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "  ", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        try {
            dto.add(uploadProductForm);
            fail();
        }catch (ApiException e){
            assertEquals("Empty Client SKU ID(s) at: "+" ["+1+"]", e.getMessage());
        }


    }

    /* where clientSkuId is duplicate*/
    @Test
    public void testAddInvalid4() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm, productForm}));

        try {
            dto.add(uploadProductForm);
            fail();
        }catch (ApiException e){
            assertEquals("Duplicate Client SKU ID(s): "+" [\""+productForm.getClientSkuId()+"\"]", e.getMessage());
        }


    }

    /* where clientSkuId already exists*/
    @Test
    public void testAddInvalid5() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        dto.add(uploadProductForm);
        UploadProductForm uploadProductForm2 = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));

        try {
            dto.add(uploadProductForm2);
            fail();
        }catch (ApiException e){
            assertEquals("Client SKU ID(s) already exist: "+" [\""+"cskuid1"+"\"]", e.getMessage());
        }


    }


/* when all inputs are valid*/
    @Test
    public void testUpdateValid() throws ApiException {

        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();
        ProductPojo current_product = listAfter.get(listAfter.size()-1);
        UpdateProductForm form = createUpdateProductForm("b22", 100.0, "desc2", "p2");
        dto.update(current_product.getGlobalSkuId(), form);

        assertEquals("b22", productDao.select(current_product.getGlobalSkuId()).getBrandId());
        assertEquals(Double.valueOf(100), productDao.select(current_product.getGlobalSkuId()).getProductMrp());
        assertEquals("desc2", productDao.select(current_product.getGlobalSkuId()).getDescription());
        assertEquals("p2", productDao.select(current_product.getGlobalSkuId()).getProductName());

    }

    /* when Brand ID is blank*/
    @Test
    public void testUpdateInvalid1() throws ApiException {

        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();
        ProductPojo current_product = listAfter.get(listAfter.size()-1);
        UpdateProductForm form = createUpdateProductForm("      ", 100.0, "desc2", "p2");

        try {
            dto.update(current_product.getGlobalSkuId(), form);
            fail();
        }catch (ApiException e){
            assertEquals("Brand ID must not be empty", e.getMessage());
        }

    }

    /* when Product Name is blank*/
    @Test
    public void testUpdateInvalid2() throws ApiException {

        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();
        ProductPojo current_product = listAfter.get(listAfter.size()-1);
        UpdateProductForm form = createUpdateProductForm("hg", 100.0, "desc2", " ");

        try {
            dto.update(current_product.getGlobalSkuId(), form);
            fail();
        }catch (ApiException e){
            assertEquals("Product name must not be empty", e.getMessage());
        }

    }


    /* when Product Description is blank*/
    @Test
    public void testUpdateInvalid3() throws ApiException {

        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();
        ProductPojo current_product = listAfter.get(listAfter.size()-1);
        UpdateProductForm form = createUpdateProductForm("hg", 100.0, " ", "p1");

        try {
            dto.update(current_product.getGlobalSkuId(), form);
            fail();
        }catch (ApiException e){
            assertEquals("Product description must not be empty", e.getMessage());
        }

    }


    /* when Product Description is more tha 255 characters*/
    @Test
    public void testUpdateInvalid4() throws ApiException {

        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();
        ProductPojo current_product = listAfter.get(listAfter.size()-1);
        UpdateProductForm form = createUpdateProductForm("hg", 100.0, MAX_DESCRIPTION_STRING
                , "p1");

        try {
            dto.update(current_product.getGlobalSkuId(), form);
            fail();
        }catch (ApiException e){
            assertEquals("Description length must not be greater than 255", e.getMessage());
        }

    }


    @Test
    public void testGetAll() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductData> listAfter = dto.getAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(productForm.getProductName(), productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductName());
        assertEquals(productForm.getBrandId(), productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getBrandId());
        assertEquals(productForm.getProductMrp(), productDao.select(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductMrp());


    }

    @Test
    public void testGet() throws ApiException {
        List<ProductPojo> listBefore = productDao.selectAll();

        PartyPojo partyPojo = createMemberPojo("m1", Party.PartyType.CLIENT);
        partyDao.insert(partyPojo);
        ProductForm productForm  = createProductForm("p1", "b1", "cskuid1", "desc1"
                , 10.0);
        UploadProductForm uploadProductForm = createUploadProductForm(partyPojo.getPartyId()
                , Arrays.asList(new ProductForm[]{productForm}));
        dto.add(uploadProductForm);

        List<ProductPojo> listAfter = productDao.selectAll();

        assertEquals(productForm.getProductName(), dto.get(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductName());
        assertEquals(productForm.getBrandId(), dto.get(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getBrandId());
        assertEquals(productForm.getProductMrp(), dto.get(listAfter.get(listAfter.size()-1)
                .getGlobalSkuId()).getProductMrp());


    }









}
