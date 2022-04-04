package com.increff.assure.dto;
import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.assure.service.ChannelService;
import com.increff.assure.service.PartyService;
import com.increff.assure.service.ProductService;
import com.increff.commons.Constants.Invoice;
import com.increff.commons.Data.ChannelData;
import com.increff.commons.Data.ErrorData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.ChannelForm;
import com.increff.commons.Form.ChannelListingForm;
import com.increff.commons.Form.UploadChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.increff.assure.util.NormalizeUtil.normalize;
import static com.increff.commons.Util.ConvertUtil.convert;
import static com.increff.commons.Util.ValidationUtil.validateEmptyField;


@Service
public class ChannelDto extends AbstractDto {
    @Autowired
    private ChannelService service;
    @Autowired
    private PartyService partyService;
    @Autowired
    private ProductService productService;

    public ChannelData add(ChannelForm form) throws ApiException {
        checkDefault(form);
        validateEmptyField(form.getChannelName(), "Channel name");
        return convert(service.addChannel(convert(form, ChannelPojo.class)), ChannelData.class);
    }
    public ChannelData getChannel(Long id) throws ApiException {
        ChannelPojo cp = service.getChannel(id);
        return convert(cp, ChannelData.class);
    }
    public List<ChannelData> getAllChannels(){
        List<ChannelPojo> list = service.getAllChannels();
        return list.stream().map(x->convert(x,ChannelData.class)).collect(Collectors.toList());
    }

    private void checkDefault(ChannelForm form) throws ApiException {
        if(form.getChannelName()==null){
            throw new ApiException("Channel Name must not br null");
        }

        if(form.getChannelName().isEmpty()){
            form.setChannelName("internal");
        }
        if(form.getInvoiceType() ==null){
            form.setInvoiceType(Invoice.InvoiceType.SELF);
        }
    }




}
