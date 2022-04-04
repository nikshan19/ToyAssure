package com.increff.assure.dto;
import com.increff.assure.pojo.*;
import com.increff.assure.service.BinService;
import com.increff.assure.service.InventoryService;
import com.increff.assure.service.PartyService;
import com.increff.assure.service.ProductService;
import com.increff.commons.Data.BinData;
import com.increff.commons.Data.BinSkuData;
import com.increff.commons.Data.ErrorData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.BinSkuForm;
import com.increff.commons.Form.BinSkuSearchForm;
import com.increff.commons.Form.UpdateBinSkuForm;
import com.increff.commons.Form.UploadBinSkuForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.increff.assure.util.NormalizeUtil.normalizeClientSkus;
import static com.increff.assure.util.NormalizeUtil.normalizeUploadBinSkuForm;
import static com.increff.commons.Util.ConvertUtil.convert;
import static com.increff.commons.Util.ValidationUtil.checkLimit;

@Service
public class BinDto extends AbstractDto {

    @Autowired
    private BinService service;
    @Autowired
    private PartyService partyService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;

    public List<BinData> add(Long qty) throws ApiException {
        checkLimit(qty, "Bins count");
        return convert(service.add(getBinPojo(qty)),BinData.class);
    }

    public List<BinData> getAllBins(){
        List<BinPojo> listPojo =  service.getAllBins();
        return convert(listPojo, BinData.class);

    }
    @Transactional(rollbackOn = ApiException.class)
    public void uploadBinSku(UploadBinSkuForm uploadBinSkuForm) throws ApiException {
        validateClient(uploadBinSkuForm.getClientId());
        formValidation(uploadBinSkuForm);
        normalizeUploadBinSkuForm(uploadBinSkuForm.getFormList());
        checkBinIds(uploadBinSkuForm.getFormList());
        validateClientSkuId(uploadBinSkuForm.getFormList());
        validateBinAndClientSku(uploadBinSkuForm.getFormList());
        checkClientSkuId(uploadBinSkuForm.getClientId(), uploadBinSkuForm.getFormList());
        for(BinSkuForm binSkuForm:uploadBinSkuForm.getFormList()) {
            BinSkuPojo binSkuPojo = convertBinSkuForm(uploadBinSkuForm.getClientId(),binSkuForm);
            service.addBinSku(binSkuPojo);
            inventoryService.addInventory(binSkuPojo);
        }
    }
    @Transactional(rollbackOn = ApiException.class)
    public void updateSingleBinSku(Long id, UpdateBinSkuForm binSkuForm) throws ApiException {
        BinSkuPojo binSkuPojo = service.getBinSku(id);
        Long qtyToAdd = service.updateQty(binSkuPojo.getId(), binSkuForm.getQty());
        inventoryService.updateInventory(binSkuPojo.getGlobalSkuId(), qtyToAdd);

    }

    public BinSkuData getBinSku(Long id) throws ApiException {
        BinSkuPojo pojo = service.getBinSku(id);
        return convert(pojo, BinSkuData.class);
    }

    public List<BinSkuData> getAllBinSku() throws ApiException {
        List<BinSkuPojo> pojos = service.getAllBinSku();
        return convert(pojos, BinSkuData.class);
    }

    public List<BinSkuData> search(BinSkuSearchForm form){
        return service.search(form.getBinId(), form.getGlobalSkuId()).stream().map(x->convert(x,BinSkuData.class))
                .collect(Collectors.toList());
    }

    private void getGSKUId(Long clientId, String clientSkuId, BinSkuPojo binSkuPojo){
        binSkuPojo.setGlobalSkuId(productService.getByClientSkuIdAndClientId(clientSkuId,clientId).getGlobalSkuId());
    }
    private List<BinPojo> getBinPojo(Long qty){
        BinPojo[] bpArray = new BinPojo[Math.toIntExact(qty)];
        return Arrays.asList(bpArray).stream().map(x->new BinPojo()).collect(Collectors.toList());
    }


    private BinSkuPojo convertBinSkuForm(Long clientId, BinSkuForm binSkuForm){
        BinSkuPojo binSkuPojo = convert(binSkuForm, BinSkuPojo.class);
        getGSKUId(clientId,binSkuForm.getClientSkuId(),binSkuPojo);
        return binSkuPojo;
    }

    private void checkBinIds(List<BinSkuForm> formList) throws ApiException {
        List<Long> initialBinIds = formList.stream().map(BinSkuForm::getBinId).collect(Collectors.toList());
        List<BinPojo> finalBinPojos = service.getCheckBinIdList(initialBinIds);
        List<Long> finalBinIds = finalBinPojos.stream().map(BinPojo::getBinId).collect(Collectors.toList());
        List<Long> differences = initialBinIds.stream().filter(element -> !finalBinIds.contains(element)).collect(Collectors.toList());

        if(differences.size()>0){
            throw new ApiException(ErrorData.convert("Bin doesn't exist with ID(s): ",differences));
        }
    }

    private void validateClientSkuId(List<BinSkuForm> formList ) throws ApiException {
        Long index=1L;
        List<Long> indexes = new ArrayList<>();
        Set<String> clientSkuIds = new HashSet<>();
        List<Long> indexes2 = new ArrayList<>();
        for(BinSkuForm form: formList){
            if(form.getClientSkuId().trim().isEmpty()){
                indexes.add(index);
            }

            index++;
        }
        if(indexes.size()>0){
            throw new ApiException(ErrorData.convert("Empty Client SKU ID(s) at: ", indexes));
        }

    }




    private void checkClientSkuId(Long clientId, List<BinSkuForm> formList) throws ApiException {

        List<String> initialClientSkuIds = formList.stream().map(BinSkuForm::getClientSkuId).collect(Collectors.toList());
        List<ProductPojo> pojoList = productService.getByClientSkuIdsAndClientId(clientId, initialClientSkuIds);
        List<String> finalClientSkuIds = pojoList.stream().map(ProductPojo::getClientSkuId).collect(Collectors.toList());
        List<String> differences = initialClientSkuIds.stream().filter(element -> !finalClientSkuIds.contains(element)).collect(Collectors.toList());

        if(differences.size()>0){
            throw new ApiException(ErrorData.convert("Invalid Client SKU ID(s): ", differences));
        }

    }

    private void validateClient(Long clientId) throws ApiException {
        partyService.getCheck(clientId);
    }

    private void validateBinAndClientSku(List<BinSkuForm> formList) throws ApiException {
        Set<String> set = new HashSet<>();
        List<Long> indexes = new ArrayList<>();
        Long index = 1L;
        for(BinSkuForm form: formList){
            if(!set.contains(form.getClientSkuId()+form.getBinId())){
                set.add(form.getClientSkuId()+form.getBinId());
                index++;
                continue;
            }
            indexes.add(index++);
            if(indexes.size()>0){
                throw new ApiException(ErrorData.convert("Duplicate Combination of Bin ID(s) and Client SKU ID(s) at indexes: ", indexes));
            }

        }
    }






}
