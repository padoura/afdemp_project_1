/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

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
import java.util.logging.Logger;

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
    
    protected static DbController getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
    protected void checkConnectivity(){
        if (connectionIsAvailable()){
            System.out.println("Database connection tested successfully!");
        }else{
            System.out.println("Database connection could not be established!");
        }
    }
    
    protected boolean connectionIsAvailable(){
        connect();
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
    
    private void connect(){
        int flag = 0;
        while(flag < 100){
            switch (tryConnection()) {
                case "connected": flag = 100;
                            break;
                case "driver": flag = 100;
                            break;
                case "db": flag++;
                // no default  
           }
        }
    }
    
    private String tryConnection(){
        if (!driverOk())
            return "driver";
        System.out.println("Connecting to database...");
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
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean isConnected(){
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryCloseConnection(){
        try {
            conn.close();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryCreateStatement(){
        try {
            stmt = conn.createStatement();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryExecuteStatement(String query){
        try {
            rs = stmt.executeQuery(query);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean hasResultSet(){
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void tryCloseResultSet(){
        try {
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected BankAccount loadAccount(BankAccount account){
        connect();
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
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean trySetStringToStatement(int index, String username) {
        try {
            ((PreparedStatement) stmt).setString(index, username);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private boolean tryExecutePreparedStatement(){
        try {
            rs = ((PreparedStatement) stmt).executeQuery();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private BigDecimal tryGetBigDecimal(){
        try {
            return rs.getBigDecimal("amount");
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private Integer tryGetInteger(){
        try {
            return rs.getInt("user_id");
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    protected ArrayList<BankAccount> loadAllAccounts() {
        connect();
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
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    protected boolean credentialsAreCorrect(BankAccount account){
        connect();
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
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    protected boolean balanceHasNotChanged(BankAccount account){
        connect();
        String query = "SELECT balance_has_not_changed(?,?);";
        
        if (!tryPrepareStatement(query) || !trySetIntToStatement(1, account.getId()) 
                || !trySetBigDecimalToStatement(2, account.getOldBalance()) || !tryExecutePreparedStatement() ){
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
    
    private boolean trySetIntToStatement(int index, Integer id) {
        try {
            ((PreparedStatement) stmt).setInt(index, id);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }    

    private boolean trySetBigDecimalToStatement(int index, BigDecimal amount) {
        try {
            ((PreparedStatement) stmt).setBigDecimal(index, amount);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    protected boolean updateAccount(BankAccount account) {
        if (balanceHasNotChanged(account)){
            String query = "UPDATE accounts SET transaction_date = ?, amount = ? WHERE user_id = ?";
            
            if (!tryPrepareStatement(query) || !trySetTimestampToStatement(1, account.getLastTransactionDate()) || !trySetIntToStatement(3, account.getId()) 
                    || !trySetBigDecimalToStatement(2, account.getBalance()) ){
                closeStatementAndConnection();
                return false;
            }
            
            if(tryExecutePreparedUpdate()==1){
                closeStatementAndConnection();
                return true;
            }else{
                closeStatementAndConnection();
                return false;
            }
        }else{
            closeStatementAndConnection();
            return false;
        }
    }
    
    private boolean trySetTimestampToStatement(int index, Timestamp timestamp) {
        try {
            ((PreparedStatement) stmt).setTimestamp(index, timestamp);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private int tryExecutePreparedUpdate(){
        try {
            return ((PreparedStatement) stmt).executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
}



