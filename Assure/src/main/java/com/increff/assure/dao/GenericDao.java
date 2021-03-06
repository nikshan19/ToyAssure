package com.increff.assure.dao;

import com.increff.commons.Exception.ApiException;
import org.hibernate.Session;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

import static com.increff.commons.Constants.ConstantNames.PARTITION_SIZE;

@Repository
public class GenericDao<T> {

    @PersistenceContext
    EntityManager em;

    public  void insert(T obj){
        em.persist(obj);
    }

    public  void delete(Long id){
        T p = em.find(getC(), id);
        em.remove(p);
    }

    public  void update(T obj){

    }

    public T select(Long id){
        T t;
        try {
            t = em.find(getC(), id);
        }catch(NoResultException e){
            t=null;
        }
        return t;
    }

    public List<T> selectAll(){
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getC());
        criteria.from(getC());
        List<T> data = em.createQuery(criteria).getResultList();
        return data;
    }

    protected <T>TypedQuery<T> getQuery(String jpql, Class<T> c) {
        return em.createQuery(jpql, c);

    }
    protected <T>T getSingle(TypedQuery<T> query) {
        return query.getResultList().stream().findFirst().orElse(null);
    }

    protected Class<T> getC(){
        return (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), GenericDao.class);
    }

    protected <T> List<List<T>> partition(List<T> list){
        Long partitionSize = PARTITION_SIZE;
        List<List<T>> partitions = new ArrayList<>();

        for (int i=0; i<list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize.intValue(), list.size())));
        }
        return partitions;
    }
}
