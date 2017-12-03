/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class BankAccount {
    
    private BigDecimal balance;
    private String username;
    private String password;
    private Timestamp lastTransactionDate;

    protected BankAccount(String username) {
        this.username = username;
        this.password = null;
        this.balance = null;
        this.lastTransactionDate = null;
    }
    
    protected BankAccount() {
        this.username = null;
        this.password = null;
        this.balance = null;
        this.lastTransactionDate = null;
    }

    protected BankAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = null;
        this.lastTransactionDate = null;
    }
    
    protected BankAccount(String username, Timestamp lastTransactionDate, BigDecimal balance) {
        this.username = username;
        this.password = null;
        this.balance = balance;
        this.lastTransactionDate = lastTransactionDate;
    }


    
    protected void deposit(BankAccount otherAccount, BigDecimal amount){
        
    }

    protected void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected void setLastTransactionDate(Timestamp lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    protected BigDecimal getBalance() {
        return balance;
    }

    protected String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    protected Timestamp getLastTransactionDate() {
        return lastTransactionDate;
    }

    protected boolean isAdmin() {
        return username.equals("admin");
    }

    @Override
    public String toString() {
        return "Username: " + username + " Balance:" + balance + " € Last Transaction:" + lastTransactionDate ;
    }
    
}
