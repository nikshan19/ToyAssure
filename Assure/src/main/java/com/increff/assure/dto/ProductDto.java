package com.increff.assure.dto;
import com.increff.assure.pojo.ProductPojo;
import com.increff.assure.service.PartyService;
import com.increff.assure.service.ProductService;
import com.increff.commons.Data.BinSkuData;
import com.increff.commons.Data.ErrorData;
import com.increff.commons.Data.ProductData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.ProductForm;
import com.increff.commons.Form.ProductSearchForm;
import com.increff.commons.Form.UpdateProductForm;
import com.increff.commons.Form.UploadProductForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.increff.assure.util.NormalizeUtil.normalize;
import static com.increff.commons.Util.ConvertUtil.convert;
import static com.increff.commons.Util.ValidationUtil.checkDuplicates;

@Service
public class ProductDto extends AbstractDto {

    @Autowired
    private ProductService service;
    @Autowired
    private PartyService partyService;

    public void add(UploadProductForm uploadProductForm) throws ApiException{
        formValidation(uploadProductForm);
        normalize(uploadProductForm.getFormList());
        validateUploadProductForm(uploadProductForm);

        List<ProductPojo> productPojoList = convert(uploadProductForm.getFormList(), ProductPojo.class);
        checkClient(uploadProductForm.getClientId(), productPojoList);
        service.add(uploadProductForm.getClientId(), productPojoList);
    }

    public void update(Long id, UpdateProductForm form) throws ApiException{
        formValidation(form);
        normalize(form);
        checkForm(form);
        ProductPojo p = convert(form, ProductPojo.class);
        service.update(id, p);
    }

    public ProductData get(Long id) throws ApiException {
        ProductPojo p = service.get(id);
        return convert(p, ProductData.class);

    }

    public List<ProductData> search(ProductSearchForm form){
        validateSearchForm(form);
        return service.search(form.getClientId(), form.getClientSkuId()).stream().map(x->convert(x, ProductData.class))
                .collect(Collectors.toList());
    }


    public List<ProductData> getAll(){
        List<ProductPojo> list = service.getAll();
        return list.stream().map(x->convert(x,ProductData.class)).collect(Collectors.toList());
    }

    private void checkClient(Long id, List<ProductPojo> pojoList) throws ApiException {
        partyService.getCheck(id);
        for(ProductPojo p: pojoList){
            p.setClientId(id);
        }

    }

    private void checkForm(UpdateProductForm form) throws ApiException {

        if(form.getBrandId().trim().isEmpty()){
            throw new ApiException("Brand ID must not be empty");
        }
        else if(form.getProductName().trim().isEmpty()){
            throw new ApiException("Product name must not be empty");
        }
        else if(form.getDescription().trim().isEmpty()){
            throw new ApiException("Product description must not be empty");
        }
        else if(form.getDescription().length()>255){
            throw new ApiException("Description length must not be greater than 255");
        }
    }

    private void validateProductName(List<ProductForm> formList) throws ApiException {
        List<Long> indexes = new ArrayList<>();
        Long index=1L;
        for(ProductForm form: formList){
            if(form.getProductName().trim().isEmpty()){
                indexes.add(index);
                index++;
            }
        }
        if(indexes.size()>0){
            throw new ApiException(ErrorData.convert("Empty Product Name(s) at: ", indexes));
        }

    }

    private void validateBrandId(List<ProductForm> formList) throws ApiException {
        List<Long> indexes = new ArrayList<>();
        Long index=1L;
        for(ProductForm form: formList){
            if(form.getBrandId().trim().isEmpty()){
                indexes.add(index);
                index++;
            }
        }
        if(indexes.size()>0){
            throw new ApiException(ErrorData.convert("Empty Brand ID(s) at: ", indexes));
        }
    }

    private void validateSearchForm(ProductSearchForm form){
        if(form.getClientSkuId().isEmpty()){
            form.setClientSkuId(null);
        }
    }

    private void validateProductDesc(List<ProductForm> formList) throws ApiException {
        List<Long> indexes = new ArrayList<>();
        List<Long> indexes2 = new ArrayList<>();
        Long index=1L;
        for(ProductForm form: formList){
            if(form.getDescription().trim().isEmpty()){
                indexes.add(index);
                index++;
                continue;
            }
            if(form.getDescription().length()>255){
                indexes2.add(index);
                index++;
            }

        }
        if(indexes.size()>0){
            throw new ApiException(ErrorData.convert("Empty Product Description(s) at: ", indexes));
        }
        if(indexes2.size()>0){
            throw new ApiException(ErrorData.convert("Description length greater than 255 at: ", indexes2));
        }
    }

    private void validateClientSkuId(List<ProductForm> formList) throws ApiException {
        List<Long> indexes = new ArrayList<>();
        Long index=1L;
        for(ProductForm form: formList){
            if(form.getClientSkuId().trim().isEmpty()){
                indexes.add(index);
                index++;
            }
        }
        if(indexes.size()>0){
            throw new ApiException(ErrorData.convert("Empty Client SKU ID(s) at: ", indexes));
        }
    }



    private void validateUploadProductForm(UploadProductForm uploadProductForm) throws ApiException {
        validateProductName(uploadProductForm.getFormList());
        validateBrandId(uploadProductForm.getFormList());
        validateProductDesc(uploadProductForm.getFormList());
        validateClientSkuId(uploadProductForm.getFormList());
        List<String> clientSkuIds = uploadProductForm.getFormList().stream().map(ProductForm::getClientSkuId)
                .collect(Collectors.toList());
        checkDuplicates(clientSkuIds, "Client SKU ID(s)");
        getCheckClientSkuId(uploadProductForm.getClientId(), clientSkuIds);
    }

    private void getCheckClientSkuId(Long clientId, List<String> clientSkuIds) throws ApiException {
        List<ProductPojo> pojoList = service.getByClientSkuIdsAndClientId(clientId, clientSkuIds);
        List<String> finalClientSkuIds = pojoList.stream().map(ProductPojo::getClientSkuId).collect(Collectors.toList());

        if(finalClientSkuIds.size()>0){
            throw new ApiException(ErrorData.convert("Client SKU ID(s) already exist: ", finalClientSkuIds));
        }

    }








}
