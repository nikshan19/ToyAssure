package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.assure.service.ChannelListingService;
import com.increff.assure.service.ChannelService;
import com.increff.assure.service.PartyService;
import com.increff.assure.service.ProductService;
import com.increff.commons.Data.ChannelListingData;
import com.increff.commons.Data.ErrorData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.ChannelListingForm;
import com.increff.commons.Form.UploadChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.increff.assure.util.NormalizeUtil.*;
import static com.increff.commons.Util.ConvertUtil.convert;
import static com.increff.commons.Util.ValidationUtil.*;

@Service
public class ChannelListingDto extends AbstractDto {

    @Autowired
    private ChannelListingService service;
    @Autowired
    private PartyService partyService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ProductService productService;

    public List<ChannelListingData> addChannelListings(UploadChannelListingForm form) throws ApiException {
        validateListing(form);
        checkClient(form.getClientId());
        checkChannel(form.getChannelId());
        normalizeChannelListingForm(form.getChannelListings());
        checkClientSkuIds(form.getClientId(), form.getChannelListings());
        checkCombinationUnique(form.getClientId(), form.getChannelId(), form.getChannelListings());

        List<ChannelListingPojo> channelListingPojoList = convertToChannelListingPojo(form);
        return convert(service.addChannelListings(channelListingPojoList), ChannelListingData.class);

    }

    private void checkClient(Long clientId) throws ApiException {
        partyService.getCheck(clientId).getPartyId();//avoid one line methods
    }
    private void checkChannel(Long channelId) throws ApiException {
        channelService.getCheckChannelById(channelId).getId();
    }

    private List<ChannelListingPojo> convertToChannelListingPojo(UploadChannelListingForm uploadChannelListingForm) throws ApiException {
        List<ChannelListingPojo> channelListingPojoList = new ArrayList<>();
        for(ChannelListingForm form : uploadChannelListingForm.getChannelListings()){
            ChannelListingPojo cp = convert(form, ChannelListingPojo.class);
            cp.setClientId(uploadChannelListingForm.getClientId());
            cp.setChannelId(uploadChannelListingForm.getChannelId());
            cp.setGlobalSkuId(getProduct(uploadChannelListingForm.getClientId(), form.getClientSkuId())
                    .getGlobalSkuId());
            channelListingPojoList.add(cp);
        }
        return channelListingPojoList;
    }

    private ProductPojo getProduct(Long clientId, String clientSkuId){
        return productService.getByClientSkuIdAndClientId(clientSkuId, clientId);
    }


    private void checkClientSkuIds(Long clientId, List<ChannelListingForm> formList) throws ApiException {

        List<String> initialClientSkuIds = formList.stream().map(ChannelListingForm::getClientSkuId).collect(Collectors.toList());
        List<ProductPojo> pojoList = productService.getByClientSkuIdsAndClientId(clientId, initialClientSkuIds);
        List<String> finalClientSkuIds = pojoList.stream().map(ProductPojo::getClientSkuId).collect(Collectors.toList());// use contains in set
        List<String> differences = initialClientSkuIds.stream().filter(element -> !finalClientSkuIds.contains(element))
                .collect(Collectors.toList());

        if(differences.size()>0){
            throw new ApiException(ErrorData.convert("Invalid Client SKU ID(s): ", differences));
        }

    }

    private void checkCombinationUnique(Long clientId, Long channelId, List<ChannelListingForm> formList) throws ApiException {
        List<String> initialChannelSkuIds = formList.stream().map(ChannelListingForm::getChannelSkuId).collect(Collectors.toList());
        List<ChannelListingPojo> pojoList = service.getByClientIdChannelIdChannelSkuIds(clientId,channelId,initialChannelSkuIds);
        List<String> finalChannelSkuIds = pojoList.stream().map(ChannelListingPojo::getChannelSkuId).collect(Collectors.toList());
        if(finalChannelSkuIds.size()>0){
            throw new ApiException(ErrorData.convert("Channel SKU ID(s) already exist(s): ", finalChannelSkuIds));
        }
    }

    private void validateListing(UploadChannelListingForm uploadChannelListingForm) throws ApiException {
        checkLimit(Long.valueOf(uploadChannelListingForm.getChannelListings().size()), "Rows");
        formValidation(uploadChannelListingForm);
        List<String> clientSkuIds = uploadChannelListingForm.getChannelListings().stream()
                .map(ChannelListingForm::getClientSkuId).collect(Collectors.toList());
        List<String> channelSkuIds = uploadChannelListingForm.getChannelListings().stream()
                .map(ChannelListingForm::getChannelSkuId).collect(Collectors.toList());
        validateEmptyFields(clientSkuIds, "Client SKU ID(s)");
        validateEmptyFields(channelSkuIds, "Channel SKU ID(s)");
        checkDuplicates(channelSkuIds, "Channel SKU ID(s)");
        checkDuplicates(clientSkuIds, "Client SKU ID(s)");
    }

}
