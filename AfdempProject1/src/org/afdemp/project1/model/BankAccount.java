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
package org.afdemp.project1.model;

import org.afdemp.project1.util.FormattingUtilities;
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

    public BankAccount(String username) {
        this.username = username;
        this.password = null;
        this.balance = null;
        this.oldBalance = null;
        this.lastTransactionDate = null;
        this.oldLastTransactionDate = null;
        this.id = null;
    }
    
    public BankAccount() {
        this.username = null;
        this.password = null;
        this.balance = null;
        this.oldBalance = null;
        this.lastTransactionDate = null;
        this.oldLastTransactionDate = null;
        this.id = null;
    }

    public BankAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.balance = null;
        this.oldBalance = null;
        this.lastTransactionDate = null;
        this.oldLastTransactionDate = null;
        this.id = null;
    }
    
    public BankAccount(String username, Timestamp lastTransactionDate, BigDecimal balance, Integer id) {
        this.username = username;
        this.password = null;
        this.balance = balance;
        this.oldBalance = balance;
        this.lastTransactionDate = new Timestamp(lastTransactionDate.getTime());
        this.oldLastTransactionDate = new Timestamp(lastTransactionDate.getTime());
        this.id = id;
    }

    public void setOldBalance(BigDecimal oldBalance) {
        this.oldBalance = oldBalance;
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

    public void setLastTransactionDate(Timestamp lastTransactionDate) {
        this.lastTransactionDate = new Timestamp(lastTransactionDate.getTime());
    }
    
    public void setOldLastTransactionDate(Timestamp lastTransactionDate) {
        this.oldLastTransactionDate = new Timestamp(lastTransactionDate.getTime());
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

    public Timestamp getLastTransactionDate() {
        return new Timestamp(lastTransactionDate.getTime());
    }
    
    public Timestamp getOldLastTransactionDate() {
        return new Timestamp(oldLastTransactionDate.getTime());
    }
    
    public Integer getId(){
        return id;
    }

    public BigDecimal getOldBalance() {
        return oldBalance;
    }

    public boolean isAdmin() {
        return username.equals("admin");
    }

    @Override
    public String toString() {
        return "Username: " + username + " Balance: " 
                + FormattingUtilities.getFormattedCurrency(balance) 
                + " Last Transaction: " + FormattingUtilities.formatTimestamp(lastTransactionDate) ;
    }
    
    public BigDecimal deposit(BigDecimal amount){
        balance = balance.add(amount);
        return balance;
    }
    
    public BigDecimal withdraw(BigDecimal amount){
        balance = balance.subtract(amount);
        return balance;
    }

    public boolean hasEnoughBalance(BigDecimal amount) {
        return balance.compareTo(amount)>=0;
    }
    
}
