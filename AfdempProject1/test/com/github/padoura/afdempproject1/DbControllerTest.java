/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import org.afdemp.project1.controller.DbController;
import org.afdemp.project1.model.BankAccount;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author padoura
 */
public class DbControllerTest {
    
    public DbControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of checkConnectivity method, of class DbController.
     */
    @Test
    public void testCheckConnectivity() {
        System.out.println("checkConnectivity");
        DbController instance = DbController.getInstance();
        boolean expResult = true;
        boolean result = instance.connectionIsAvailable();
        assertEquals(expResult, result);
    }

    /**
     * Test of loadAccount method, of class DbController.
     */
    @Test
    public void testLoadAccount() {
        System.out.println("loadAccount");
        BankAccount account = new BankAccount("admin");
        DbController instance = DbController.getInstance();
        BankAccount expResult = new BankAccount("admin");
        expResult.setBalance(BigDecimal.valueOf(100000).setScale(2, BigDecimal.ROUND_HALF_UP));
        expResult.setLastTransactionDate(Timestamp.valueOf(LocalDateTime.parse("2017-11-13 19:28:47",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        BankAccount result = instance.loadAccount(account);
        assertEquals(expResult.toString(), result.toString());
    }

    /**
     * Test of passwordIsCorrect method, of class DbController.
     */
    @Test
    public void testPasswordIsCorrect() {
        System.out.println("passwordIsCorrect");
        BankAccount account = new BankAccount("admin","admin");
        DbController instance = DbController.getInstance();
        boolean expResult = true;
        boolean result = instance.credentialsAreCorrect(account);
        assertEquals(expResult, result);
    }
}
