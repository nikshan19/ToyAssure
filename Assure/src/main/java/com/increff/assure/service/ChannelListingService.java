package com.increff.assure.service;

import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.commons.Exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class ChannelListingService {

    @Autowired
    private ChannelListingDao dao;

    public List<ChannelListingPojo> addChannelListings(List<ChannelListingPojo> pojoList) throws ApiException {
        List<ChannelListingPojo> channelListingPojoList = new ArrayList<>();
        for(ChannelListingPojo pojo : pojoList){
            ChannelListingPojo exists = dao.select(pojo.getId());
            if(Objects.nonNull(exists)){
                channelListingPojoList.add(exists);
                continue;
            }
            dao.insert(pojo);
            channelListingPojoList.add(pojo);
        }
        return channelListingPojoList;
    }

    public List<ChannelListingPojo> getByClientIdChannelIdChannelSkuIds(Long clientId, Long channelId, List<String> channelSkuIds){
        return dao.selectByClientIdChannelIdChannelSkuId(clientId, channelId, channelSkuIds);
    }
}