/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class DbController {
    
    private static DbController instance;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/afdemp_java_1";
    private static final String USERNAME = "afdemp";
    private static final String PASSWORD = "afdemp";
    
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private DbController() {
        this.conn = null;
        this.stmt = null;
        this.rs = null;
    }
    
    protected static DbController getInstance(){
        if (instance == null)
            instance = new DbController();
        return instance;
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
        
        try {
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            System.out.println("Paei to statement gia vrouves");
            return false;
        }
        String sql;
        sql = "SELECT * FROM users LIMIT 1";
        try {
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();
            return false;
        } catch (SQLException ex) {
            System.out.println("Moufa to query...");
            return false;
        } finally{
            closeConnection();
        }
    }
    
    private void connect(){
        int flag = 0;
        while(flag < 100){
            String event = tryConnection();
            switch (event) {
                case "ok": flag = 100;
                            break;
                case "driver": flag = 100;
                            break;
                case "db": flag++;
            }
        }
    }
    
    private String tryConnection(){
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            return "driver";
        }
        System.out.println("Connecting to database...");
        try {
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            return "db";
        } 
        return "ok";
    }
    private void closeConnection(){
        try {
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("Tin ekane to statement...");
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println("Paei i sindesi...");
            }
        }
    }
    protected BankAccount loadAccount(BankAccount account){
        connect();
        
        String query = "SELECT * FROM accounts_of_users WHERE username = ?;";
        try {
            stmt = conn.prepareStatement(query);
        } catch (SQLException ex) {
            System.out.println("Problem with database connection...");
            closeConnection();
            return account;
        }
        
        try {
            ((PreparedStatement) stmt).setString(1, account.getUsername());
        } catch (SQLException ex) {
            System.out.println("User could not be searched.");
            closeConnection();
            return account;
        }
        try {
            rs = ((PreparedStatement) stmt).executeQuery();
            if (rs.next()){
                account.setLastTransactionDate(rs.getTimestamp("transaction_date"));
                account.setOldLastTransactionDate(account.getLastTransactionDate());
                account.setBalance(rs.getBigDecimal("amount"));
                account.setOldBalance(account.getBalance());
                account.setId(rs.getInt("user_id"));
            }else{
                System.out.println("No such user exists!");
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("User could not be searched.");
        }finally{
            closeConnection();
            return account;
        }
    }
    
    protected ArrayList<BankAccount> loadAllAccounts() {
        connect();
        ArrayList<BankAccount> accountList = new ArrayList<>();
         
        String query = "SELECT username, transaction_date, amount, user_id "
                + "FROM accounts_of_users;";
        try {
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            System.out.println("Problem with database connection...");
            closeConnection();
            return null;
        }
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()){
                BankAccount account = new BankAccount(rs.getString("username")
                        , rs.getTimestamp("transaction_date"), rs.getBigDecimal("amount"), rs.getInt("user_id"));
                accountList.add(account);
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println("User could not be searched.");
        }finally{
            closeConnection();
            return accountList;
        }
    }
    
    
    
    
    protected boolean credentialsAreCorrect(BankAccount account){
        connect();
        String query = "SELECT user_exists(?, ?);";
        try {
            stmt = conn.prepareStatement(query);
        } catch (SQLException ex) {
            System.out.println("Problem with database connection...");
            closeConnection();
            return false;
        }
        
        try {
            ((PreparedStatement) stmt).setString(1, account.getUsername());
            ((PreparedStatement) stmt).setString(2, account.getPassword());
        } catch (SQLException ex) {
            System.out.println("User could not be searched.");
            closeConnection();
            return false;
        }
        
        try {
            rs = ((PreparedStatement) stmt).executeQuery();
            rs.next();
            boolean result = rs.getBoolean(1);
            rs.close();
            closeConnection();
            return result;
        } catch (SQLException ex) {
            System.out.println("User could not be searched.");
            closeConnection();
            return false;
        }
    }
    
    protected boolean balanceHasNotChanged(BankAccount account){
        connect();
        String query = "SELECT balance_has_not_changed(" + account.getId() + ", " + account.getOldBalance() + ");";
        boolean result;
        try {
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            System.out.println("Problem with database connection...");
            closeConnection();
            return false;
        }
        try {
            rs = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Could not be executed");
            return false;
        }
        try {
            rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        try {
            result = rs.getBoolean(1);
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        try {
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DbController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    protected boolean updateAccount(BankAccount bankAcnt) {
        if (balanceHasNotChanged(bankAcnt)){
            String sql = "UPDATE accounts SET transaction_date = '" + bankAcnt.getLastTransactionDate()
                    + "', amount = '" + bankAcnt.getBalance()+ "' WHERE user_id = " + bankAcnt.getId();
            try {
                stmt = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("Paei to statement gia vrouves");
            }
            int rs;
            try {
                rs = stmt.executeUpdate(sql);
                if (rs == 1) {
                    closeConnection();
                    return true;
                }else{
                    closeConnection();
                    return false;
                }
            } catch (SQLException ex) {
                closeConnection();
                return false;
            }
        }else{
            closeConnection();
            return false;
        }
    } 
}



