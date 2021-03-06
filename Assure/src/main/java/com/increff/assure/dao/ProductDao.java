package com.increff.assure.dao;

import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.ProductPojo;
import com.increff.commons.Constants.Party;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao extends GenericDao<ProductPojo> {

    private static final String SELECT_BY_CLIENT_SKU_ID_CLIENT_ID = "select p from ProductPojo p where clientSkuId =: clientSkuId AND clientId =: clientId";
    private static final String SELECT_BY_CLIENT_ID_AND_CLIENT_SKU_IDS = "select p from ProductPojo p where clientId =: clientId AND clientSkuId IN (:clientSkuIds)";
    private static final String SELECT_BY_GLOBAL_SKU_IDS = "select p from ProductPojo p where globalSkuId IN (:globalSkuIds)";
    private static final String SELECT_BY_CLIENT_ID = "select p from ProductPojo p where clientId =: clientId";
//    private static String SELECT_BY_CLIENT_ID_OR_CLIENT_SKUID = "select p from ProductPojo p "
//            + "where (clientId = :clientId OR :clientId IS NULL)  AND "
//            + "(clientSkuId = :clientSkuId OR :clientSkuId IS NULL)";

    public ProductPojo selectByClientSkuIdAndClientId(String clientSkuId, Long clientId){
        ProductPojo p;
        try {
            TypedQuery<ProductPojo> q = getQuery(SELECT_BY_CLIENT_SKU_ID_CLIENT_ID, ProductPojo.class);
            q.setParameter("clientSkuId", clientSkuId);
            q.setParameter("clientId", clientId);
            p = q.getSingleResult();
        }catch (NoResultException e){
            p=null;
        }
        return p;
    }

    public List<ProductPojo> selectByClientIdAndClientSkuIds(Long clientId, List<String> clientSkuIds){
        List<ProductPojo> finalList = new ArrayList<>();
        for(List<String> partitionedClientSkuIds :partition(clientSkuIds)) {
            TypedQuery<ProductPojo> q = getQuery(SELECT_BY_CLIENT_ID_AND_CLIENT_SKU_IDS, ProductPojo.class);
            q.setParameter("clientId", clientId);
            q.setParameter("clientSkuIds", partitionedClientSkuIds);
            finalList.addAll(q.getResultList());
        }
        return finalList;
    }

    // test for no result exception ie .
    // add singleOrNull function in Generic Dao.


    public List<ProductPojo> selectByGlobalSkuIds(List<Long> globalSkuIds){
        List<ProductPojo> finalList = new ArrayList<>();
        for(List<Long> partitionedGlobalSkus: partition(globalSkuIds)){
            TypedQuery<ProductPojo> q = getQuery(SELECT_BY_GLOBAL_SKU_IDS, ProductPojo.class);
            q.setParameter("globalSkuIds", globalSkuIds);
            finalList.addAll(q.getResultList());
        }
        return finalList;
    }


    public List<ProductPojo> selectByClientId(Long clientId){
        List<ProductPojo> productPojoList;
        try{
            TypedQuery<ProductPojo> q = getQuery(SELECT_BY_CLIENT_ID, ProductPojo.class);
            q.setParameter("clientId", clientId);
            productPojoList = q.getResultList();
        }catch (NoResultException e){
            productPojoList= new ArrayList<>();
        }
        return productPojoList;
    }

    public List<ProductPojo> search(Long clientId, String clientSkuId){
        List<ProductPojo> bsp = new ArrayList<>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);

        Root<ProductPojo> root = cq.from(ProductPojo.class);
        List<Predicate> predicates = new ArrayList<>();


        if (clientId == null) {
            predicates.add(cb.equal(root.get("clientSkuId"), clientSkuId));
        }else {
            if (clientSkuId == null) {
                predicates.add(cb.equal(root.get("clientId"), clientId));
            }else {
                predicates.add(cb.equal(root.get("clientSkuId"), clientSkuId));
                predicates.add(cb.equal(root.get("clientId"), clientId));

            }
        }
        cq.where(predicates.toArray(new Predicate[0]));
        Query query=em.createQuery(cq);
        bsp = query.getResultList();
        return bsp;
    }







}
