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
package org.afdemp.project1.controller;

import org.afdemp.project1.util.LoggerController;
import org.afdemp.project1.model.BankAccount;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class DbController {
    
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/afdemp_java_1";
    private static final String USERNAME = "afdemp";
    private static final String PASSWORD = "afdemp"; //it should be read from a file 
    
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private DbController() {
        this.conn = null;
        this.stmt = null;
        this.rs = null;
    }
    
    private static class SingletonHelper {
        private static final DbController INSTANCE = new DbController();
    }
    
    public static DbController getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
    public boolean checkConnectivity(){
        if (connectionIsAvailable()){
            System.out.println("Database connection tested successfully!");
            return true;
        }else{
            System.out.println("Database connection could not be established!");
            return false;
        }
    }
    
    protected boolean connectionIsAvailable(){
        if(!connect())
            return false;
                
        String query = "SELECT * FROM users LIMIT 1";
        
        if (!tryCreateStatement() || !tryExecuteStatement(query)){
            closeStatementAndConnection();
            return false;
        }
        
        if (hasResultSet()){
            tryCloseResultSet();
            return true;
        }
        else{
            tryCloseResultSet();
            closeStatementAndConnection();
            return false;
        }
    }
    
    private boolean connect(){
        final int MAX_FLAG = 3;
        int flag = 0;
        while(flag < MAX_FLAG){
            if (flag > 0){
                System.out.println("Attempt no. " + (flag+1) + " to connect to database...");
            }
            switch (tryConnection()) {
                case "connected": 
                    return true;
                case "driver": 
                    return false;
                case "db": flag++;
                // no default
           }
        }
        return false;
    }
    
    private String tryConnection(){
        if (!driverOk())
            return "driver";
        if (isConnected())
            return "connected";
        else
            return "db";
    }
    
    private boolean driverOk(){
        try {
            Class.forName(JDBC_DRIVER);
            return true;
        } catch (ClassNotFoundException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean isConnected(){
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        } 
    }
    
    private boolean closeStatementAndConnection(){
        return tryCloseStatement() && tryCloseConnection();
    }
    
    private boolean tryCloseStatement(){
        try {
            stmt.close();
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryCloseConnection(){
        try {
            conn.close();
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryCreateStatement(){
        try {
            stmt = conn.createStatement();
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryExecuteStatement(String query){
        try {
            rs = stmt.executeQuery(query);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean hasResultSet(){
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void tryCloseResultSet(){
        try {
            rs.close();
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
        }
    }
    
    public BankAccount loadAccount(BankAccount account){
        if(!connect())
            return account;
        String query = "SELECT * FROM accounts_of_users WHERE username = ?;";
        
        if (!tryPrepareStatement(query) || !trySetStringToStatement(1, account.getUsername()) || !tryExecutePreparedStatement() ){
            closeStatementAndConnection();
            return account;
        }
        
        if (hasResultSet()){
            tryLoadingAccount(account);
            tryCloseResultSet();
        }else
            tryCloseResultSet();
        
        closeStatementAndConnection();
        return account;
    }
    
    private boolean tryPrepareStatement(String query){
        try {
            stmt = conn.prepareStatement(query);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean trySetStringToStatement(int index, String username) {
        try {
            ((PreparedStatement) stmt).setString(index, username);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryExecutePreparedStatement(){
        try {
            rs = ((PreparedStatement) stmt).executeQuery();
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }  
    }

    private BankAccount tryLoadingAccount(BankAccount account){
        account.setLastTransactionDate(tryGetTimestamp());
        account.setOldLastTransactionDate(account.getLastTransactionDate());
        account.setBalance(tryGetBigDecimal());
        account.setOldBalance(account.getBalance());
        account.setId(tryGetInteger());
        return account;
    }
    
    private Timestamp tryGetTimestamp(){
        try {
            return rs.getTimestamp("transaction_date");
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private BigDecimal tryGetBigDecimal(){
        try {
            return rs.getBigDecimal("amount");
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private Integer tryGetInteger(){
        try {
            return rs.getInt("user_id");
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public ArrayList<BankAccount> loadAllAccounts() {
        if(!connect())
            return null;
        ArrayList<BankAccount> accountList = new ArrayList<>();
        String query = "SELECT username, transaction_date, amount, user_id "
                + "FROM accounts_of_users;";
        
        if (!tryCreateStatement() || !tryExecuteStatement(query)){
            closeStatementAndConnection();
            return null;
        }
        
        while (hasResultSet())
            accountList.add(tryLoadingAccountWithUsername());
        
        tryCloseResultSet();
        closeStatementAndConnection();
        if (accountList.isEmpty()){
            return null;
        }else{
            return accountList;
        }
    }
    
    private BankAccount tryLoadingAccountWithUsername(){
        return new BankAccount(tryGetString(), tryGetTimestamp(), tryGetBigDecimal(), tryGetInteger());
    }
    
    private String tryGetString(){
        try {
            return rs.getString("username");
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public boolean credentialsAreCorrect(BankAccount account){
        if(!connect())
            return false;
        String query = "SELECT user_exists(?, ?);";
        
        if (!tryPrepareStatement(query) || !trySetStringToStatement(1, account.getUsername()) 
                || !trySetStringToStatement(2, account.getPassword()) || !tryExecutePreparedStatement() ){
            closeStatementAndConnection();
            return false;
        }
        
        if (hasResultSet()){
            boolean result = tryGetBoolean();
            tryCloseResultSet();
            closeStatementAndConnection();
            return result;
        }else{
            tryCloseResultSet();
            closeStatementAndConnection();
            return false;
        }
    }
    
    private boolean tryGetBoolean(){
        try {
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateAccounts(BankAccount bankAcnt, BankAccount otherAccount){
        if(!connect())
            return false;
        if (updateAccount(bankAcnt) && updateAccount(otherAccount) && tryCommit()){
            closeStatementAndConnection();
            return true;
        }else{
            tryRollback();
            closeStatementAndConnection();
            return false;
        }
    }
    
    protected boolean updateAccount(BankAccount account) {
        if (balanceHasNotChanged(account)){
            String query = "UPDATE accounts SET transaction_date = ?, amount = ? WHERE user_id = ?";
            
            if (!tryPrepareStatement(query) || !trySetTimestampToStatement(1, account.getLastTransactionDate()) || !trySetIntToStatement(3, account.getId()) 
                    || !trySetBigDecimalToStatement(2, account.getBalance()) ){
                return false;
            }
            return tryExecutePreparedUpdate()==1;
        }else{
            return false;
        }
    }
    
    
    protected boolean balanceHasNotChanged(BankAccount account){
        String query = "SELECT balance_has_not_changed(?,?,?);";
        
        if (!trySetAutoCommit(false) || !tryPrepareStatement(query) 
                || !trySetIntToStatement(1, account.getId()) 
                || !trySetBigDecimalToStatement(2, account.getOldBalance()) 
                || !trySetTimestampToStatement(3, account.getOldLastTransactionDate())
                || !tryExecutePreparedStatement() ){
            closeStatementAndConnection();
            return false;
        }
        
        if (hasResultSet()){
            boolean result = tryGetBoolean();
            tryCloseResultSet();
            return result;
        }else{
            tryCloseResultSet();
            return false;
        }
    }
    
    private boolean trySetAutoCommit(boolean setting){
        try {
            conn.setAutoCommit(false);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean trySetIntToStatement(int index, Integer id) {
        try {
            ((PreparedStatement) stmt).setInt(index, id);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }    

    private boolean trySetBigDecimalToStatement(int index, BigDecimal amount) {
        try {
            ((PreparedStatement) stmt).setBigDecimal(index, amount);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean trySetTimestampToStatement(int index, Timestamp timestamp) {
        try {
            ((PreparedStatement) stmt).setTimestamp(index, timestamp);
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private int tryExecutePreparedUpdate(){
        try {
            return ((PreparedStatement) stmt).executeUpdate();
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    private boolean tryCommit(){
        try {
            conn.commit();
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryRollback(){
        try {
            conn.rollback();
            return true;
        } catch (SQLException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
}



