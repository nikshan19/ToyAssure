package com.increff.assure.service;

import com.increff.assure.dao.InventoryDao;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.pojo.OrderPojo;
import com.increff.assure.spring.AbstractUnitTest;
import com.increff.commons.Constants.OrderStatus;
import com.increff.commons.Exception.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.increff.assure.spring.TestPojo.createBinSkuPojo;
import static com.increff.assure.spring.TestPojo.createInventoryPojo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InventoryServiceTest extends AbstractUnitTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryDao dao;

    @Test
    public void testCreateInventory(){
        List<InventoryPojo> listBefore = dao.selectAll();

        inventoryService.createInventory(15L, 10L);

        List<InventoryPojo> listAfter = dao.selectAll();

        assertEquals(listBefore.size()+1, listAfter.size());
        assertEquals(Long.valueOf(15), listAfter.get(0).getGlobalSkuId());
        assertEquals(Long.valueOf(10L), listAfter.get(0).getAvailableQty());
        assertEquals(Long.valueOf(0), listAfter.get(0).getAllocatedQty());
        assertEquals(Long.valueOf(0), listAfter.get(0).getFulfilledQty());

    }
    /* when inventory with globals SKU ID is not present in db*/
    @Test
    public void testAddInventory1() throws ApiException {

        BinSkuPojo pojo = createBinSkuPojo(100L, 15L, 5L);

        inventoryService.addInventory(pojo);

        List<InventoryPojo> listAfter = dao.selectAll();

        assertEquals(Long.valueOf(15), listAfter.get(0).getGlobalSkuId());
        assertEquals(Long.valueOf(100L), listAfter.get(0).getAvailableQty());
        assertEquals(Long.valueOf(0), listAfter.get(0).getAllocatedQty());
        assertEquals(Long.valueOf(0), listAfter.get(0).getFulfilledQty());

    }
    /* when inventory with give global SKU ID already exists */
    @Test
    public void testAddInventory2() throws ApiException {
        InventoryPojo inventoryPojo = createInventoryPojo(15L, 10L, 0L, 0L);
        dao.insert(inventoryPojo);

        BinSkuPojo pojo = createBinSkuPojo(100L, 15L, 5L);

        inventoryService.addInventory(pojo);

        List<InventoryPojo> listAfter = dao.selectAll();

        assertEquals(Long.valueOf(15), listAfter.get(0).getGlobalSkuId());
        assertEquals(Long.valueOf(110L), listAfter.get(0).getAvailableQty());
        assertEquals(Long.valueOf(0), listAfter.get(0).getAllocatedQty());
        assertEquals(Long.valueOf(0), listAfter.get(0).getFulfilledQty());

    }

    @Test
    public void testUpdateInventory() throws ApiException {

        InventoryPojo inventoryPojo = createInventoryPojo(15L, 10L, 0L, 0L);
        dao.insert(inventoryPojo);

        inventoryService.updateInventory(15L, 50L);

        assertEquals(Long.valueOf(60), inventoryPojo.getAvailableQty());
    }

    @Test
    public void testAvailableAndAllocatedQty() throws ApiException {

        InventoryPojo inventoryPojo = createInventoryPojo(15L, 10L, 0L, 0L);
        dao.insert(inventoryPojo);

        inventoryService.updateAvailableAndAllocatedQty(5L, 15L);

        assertEquals(Long.valueOf(5), inventoryPojo.getAvailableQty());
        assertEquals(Long.valueOf(5), inventoryPojo.getAllocatedQty());
    }

    @Test
    public void testUpdateFulfilledQty() throws ApiException {
        Map<Long, Long> globalSkuIdToQty = new HashMap<>();
        globalSkuIdToQty.put(15L, 10L);

        InventoryPojo inventoryPojo = createInventoryPojo(15L, 10L, 10L, 0L);
        dao.insert(inventoryPojo);

        inventoryService.updateFulfilledQty(globalSkuIdToQty);

        assertEquals(Long.valueOf(0L), inventoryPojo.getAllocatedQty());
        assertEquals(Long.valueOf(10L), inventoryPojo.getFulfilledQty());

    }

    @Test
    public void testGetInventory(){

        InventoryPojo inventoryPojo = createInventoryPojo(15L, 10L, 10L, 0L);
        dao.insert(inventoryPojo);

        InventoryPojo pojo = inventoryService.getInventory(inventoryPojo.getGlobalSkuId());

        assertEquals(pojo.getAvailableQty(), inventoryPojo.getAvailableQty());
        assertEquals(pojo.getAllocatedQty(), inventoryPojo.getAllocatedQty());
        assertEquals(pojo.getFulfilledQty(), inventoryPojo.getFulfilledQty());
        assertEquals(pojo.getId(), inventoryPojo.getId());

    }

    @Test
    public void testUpdateAvailableQty() throws ApiException {

        InventoryPojo inventoryPojo = createInventoryPojo(15L, 10L, 10L, 0L);
        dao.insert(inventoryPojo);

        inventoryService.updateAvailableQty(inventoryPojo.getId(), 100L);

        assertEquals(Long.valueOf(110), inventoryPojo.getAvailableQty());
    }

    @Test
    public void testGetCheckGlobalSkuId(){

        try{
            inventoryService.getCheckGlobalSkuId(10L);
            fail();
        } catch (ApiException e) {
            assertEquals("Inventory with Global SKU ID: "+10L+" doesn't exist", e.getMessage());
        }
    }



}
