package com.increff.assure.dto;

import com.increff.assure.dao.PartyDao;
import com.increff.assure.pojo.PartyPojo;
import com.increff.assure.service.PartyService;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.assure.spring.TestPojo;
import com.increff.commons.Constants.Party;
import com.increff.commons.Data.PartyData;
import com.increff.commons.Exception.ApiException;
import com.increff.commons.Form.PartyForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.increff.assure.spring.TestPojo.createMemberPojo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PartyDtoTest extends AbstractUnitTest {

        @Autowired
        private PartyDto dto;
        @Autowired
        private PartyDao dao;


      /* when name is in lower case and has no white spaces.*/
        @Test
        public void testAddValid1() throws ApiException {

            List<PartyPojo> data_before = dao.selectAll();
            PartyForm form = getPartyForm();
            dto.add(form);
            List<PartyPojo> data_after = dao.selectAll();

            assertEquals(data_before.size()+1, data_after.size());

        }

   /* when name has Capital letters and has empty spaces.*/

    @Test
    public void testAddValid2() throws ApiException {

        List<PartyPojo> data_before = dao.selectAll();

        PartyForm form = getPartyForm();
        form.setPartyName("    NikShan    ");
        dto.add(form);

        List<PartyPojo> data_after = dao.selectAll();

        assertEquals(data_before.size()+1, data_after.size());
        assertEquals("nikshan", dao.select(data_after.get(data_after.size()-1).getPartyId()).getPartyName());

    }




        /* when name is empty.....*/
        @Test
        public void testAddInvalid1(){
         PartyForm form = getPartyForm();
         form.setPartyName("     ");

         try{
             dto.add(form);
             fail();
         }catch (ApiException e){
             assertEquals("Party Name must not be empty", e.getMessage());
         }
        }



        @Test
        public void testGetAll() throws ApiException {

            List<PartyData> data_before = dto.getAll();

            PartyPojo partyPojo = createMemberPojo("abc", Party.PartyType.CLIENT);
            dao.insert(partyPojo);

            List<PartyData> data_after = dto.getAll();

            assertEquals(data_before.size()+1, data_after.size());
        }

        @Test
        public void testGet() throws ApiException {
            PartyPojo partyPojo = createMemberPojo("abc", Party.PartyType.CLIENT);
            dao.insert(partyPojo);

            assertEquals("abc", dto.get(partyPojo.getPartyId()).getPartyName());
            assertEquals(Party.PartyType.CLIENT, dto.get(partyPojo.getPartyId()).getPartyType());
        }

        @Test
        public void testGetPartyByType() throws ApiException {

            List<PartyData> data_before = dto.getPartyByType(Party.PartyType.CLIENT);

            PartyForm form = getPartyForm();
            dto.add(form);

            List<PartyData> data_after = dto.getPartyByType(form.getPartyType());

            assertEquals(data_before.size()+1, data_after.size());
        }


        private PartyForm getPartyForm(){
            PartyForm form = new PartyForm();
            form.setPartyType(Party.PartyType.CLIENT);
            form.setPartyName("name2");
            return form;
        }


    }
