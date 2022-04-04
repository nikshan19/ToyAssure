package com.increff.assure.service;
import com.increff.assure.dao.ProductDao;
import com.increff.assure.pojo.ProductPojo;
import com.increff.commons.Exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional(rollbackOn = ApiException.class)
public class ProductService extends AbstarctService{

    @Autowired
    private ProductDao dao;

    public void add(Long id, List<ProductPojo> productPojoList) throws ApiException {
        for(ProductPojo p :productPojoList) {
            ProductPojo exists = dao.selectByClientSkuIdAndClientId(p.getClientSkuId(), id);
           if(Objects.nonNull(exists)){
               continue;
           }
           dao.insert(p);
        }
    }

    public void update(Long id, ProductPojo p) throws ApiException {
        ProductPojo exists = getCheck(id);
        updateFields(exists,p);
    }

    public ProductPojo get(Long id) throws  ApiException{
        return getCheck(id);
    }

    public List<ProductPojo> getByClientId(Long clientId){
        return dao.selectByClientId(clientId);
    }

    public List<ProductPojo> search(Long clientId, String clientSkuId){
        return dao.search(clientId, clientSkuId);
    }


    public List<ProductPojo> getAll(){
        return dao.selectAll();
    }


    public ProductPojo getCheck(Long id) throws ApiException {
        ProductPojo p = dao.select(id);
        if (p == null) {
            throw new ApiException("Party with given id: "+ id+ " doesn't exist");
        }
        return p;
    }


    public ProductPojo getByClientSkuIdAndClientId(String clientSkuId, Long clientId){
        return dao.selectByClientSkuIdAndClientId(clientSkuId, clientId);
    }

    public List<ProductPojo> getByClientSkuIdsAndClientId(Long clientId, List<String> clientSkuIds){
        return dao.selectByClientIdAndClientSkuIds(clientId,clientSkuIds); // partitioning and check for empty list
    }

    public Map<Long, ProductPojo> getByGlobalSkuIds(List<Long> globalSkuIds){
        return dao.selectByGlobalSkuIds(globalSkuIds).stream().collect(Collectors.
                toMap(value ->value.getGlobalSkuId(), value->value));
    }

    private void updateFields(ProductPojo ex, ProductPojo p){
        ex.setProductName(p.getProductName());
        ex.setBrandId(p.getBrandId());
        ex.setProductMrp(p.getProductMrp());
        ex.setDescription(p.getDescription());
    }





}
