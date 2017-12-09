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
    private BigDecimal oldBalance;
    private String username;
    private String password;
    private Timestamp lastTransactionDate;
    private Timestamp oldLastTransactionDate;
    private Integer id;

    protected BankAccount(String username) {
        this.username = username;
        this.password = null;
        this.balance = null;
        this.oldBalance = null;
        this.lastTransactionDate = null;
        this.oldLastTransactionDate = null;
        this.id = null;
    }
    
    protected BankAccount() {
        this.username = null;
        this.password = null;
        this.balance = null;
        this.oldBalance = null;
        this.lastTransactionDate = null;
        this.oldLastTransactionDate = null;
        this.id = null;
    }

    protected BankAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = null;
        this.oldBalance = null;
        this.lastTransactionDate = null;
        this.oldLastTransactionDate = null;
        this.id = null;
    }
    
    protected BankAccount(String username, Timestamp lastTransactionDate, BigDecimal balance, Integer id) {
        this.username = username;
        this.password = null;
        this.balance = balance;
        this.oldBalance = balance;
        this.lastTransactionDate = lastTransactionDate;
        this.oldLastTransactionDate = lastTransactionDate;
        this.id = id;
    }

    protected void setOldBalance(BigDecimal oldBalance) {
        this.oldBalance = oldBalance;
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
    
    protected void setOldLastTransactionDate(Timestamp lastTransactionDate) {
        this.oldLastTransactionDate = lastTransactionDate;
    }

    protected void setId(Integer id) {
        this.id = id;
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
    
    protected Timestamp getOldLastTransactionDate() {
        return oldLastTransactionDate;
    }
    
    protected Integer getId(){
        return id;
    }

    protected BigDecimal getOldBalance() {
        return oldBalance;
    }

    protected boolean isAdmin() {
        return username.equals("admin");
    }

    @Override
    public String toString() {
        return "Username: " + username + " Balance: " + FormattingUtilities.getFormattedCurrency(balance) + " Last Transaction: " + FormattingUtilities.formatTimestamp(lastTransactionDate) ;
    }
    
    protected BigDecimal deposit(BigDecimal amount){
        balance = balance.add(amount);
        return balance;
    }
    
    protected BigDecimal withdraw(BigDecimal amount){
        balance = balance.subtract(amount);
        return balance;
    }

    boolean hasEnoughBalance(BigDecimal amount) {
        return balance.compareTo(amount)>=0;
    }
    
}
