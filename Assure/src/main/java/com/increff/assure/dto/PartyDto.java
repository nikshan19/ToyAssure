package com.increff.assure.dto;

import com.increff.commons.Constants.Party.PartyType;
import com.increff.commons.Data.PartyData;
import com.increff.commons.Form.PartyForm;
import com.increff.assure.pojo.PartyPojo;
import com.increff.commons.Exception.ApiException;
import com.increff.assure.service.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import static com.increff.assure.util.NormalizeUtil.normalize;
import static com.increff.commons.Util.ConvertUtil.convert;

@Service
public class PartyDto extends AbstractDto {

    @Autowired
    private PartyService service;


    public void add(PartyForm form) throws ApiException{
        formValidation(form);
        checkName(form);
        PartyPojo p = convert(form,PartyPojo.class);
        normalize(p);
        service.add(p);
    }



    public PartyData get(Long id) throws ApiException {
        return convert(service.get(id),PartyData.class);
    }

    public List<PartyData> getAll(){
        List<PartyPojo> list = service.getAll();
        return list.stream().map(x->convert(x,PartyData.class)).collect(Collectors.toList());
    }

    public List<PartyData> getPartyByType(PartyType p){
        List<PartyPojo> listPojo = service.getPartyByType(p);
        return listPojo.stream().map(x->convert(x,PartyData.class)).collect(Collectors.toList());
    }

    private void checkName(PartyForm form) throws ApiException {
        if(form.getPartyName().trim().isEmpty()){
            throw new ApiException("Party Name must not be empty");
        }
    }

}
