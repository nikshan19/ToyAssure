package com.increff.assure.service;

import com.increff.assure.service.AbstarctService;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Exception.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class AbstrtactServiceTest extends AbstractUnitTest {

    private AbstarctService service = new AbstarctService();

    @Test
    public void testIsNotNull(){
        try{
            service.isNotNull(1L, "Object should be null");
        }catch (ApiException e){
            assertEquals("Object should be null", e.getMessage());
        }
    }

    @Test
    public void testIsNull(){
        try{
            service.isNull(null, "Object cannot be null");
        }catch (ApiException e){
            assertEquals("Object cannot be null", e.getMessage());
        }

    }
}
