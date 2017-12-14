/*
 * Copyright (C) 2017 padoura <padoura@users.noreply.github.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        return "Username: " + username + " Balance: " 
                + FormattingUtilities.getFormattedCurrency(balance) 
                + " Last Transaction: " + FormattingUtilities.formatTimestamp(lastTransactionDate) ;
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
