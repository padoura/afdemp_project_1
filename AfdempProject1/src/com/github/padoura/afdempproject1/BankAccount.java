/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class BankAccount {
    
    private BigDecimal balance;
    private String username;
    private String password;
    private Date lastTransactionDate;
    private Integer id;
    
    public void deposit(BankAccount otherAccount, BigDecimal amount){
        
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BankAccount{" + ", username=" + username + ", lastTransactionDate=" + lastTransactionDate + "balance=" + balance + '}' ;
    }
    
    
    
    

    
}
