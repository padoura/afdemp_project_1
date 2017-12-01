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
import java.util.ArrayList;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class DbController {
    
    private Connection conn;
    private static final String IP = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE_NAME = "localhost";
    private static final String USERNAME = "afdemp";
    private static final String PASSWORD = "afdemp";
    
    
    public void closeConnection(){
        try {
//            System.out.println("Closing connection...");
            dbCommit();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkConnectivity() {
        System.out.println("Welcome!");
        System.out.println("Please wait while database connection is tested...");
        dbConnect(IP, PORT, DATABASE_NAME, USERNAME, PASSWORD);
        encryptDb();
        closeConnection();
        System.out.println("Connection test complete!");
    }
    
    /*
     * Encrypt column password (according to deliverable 2c).
     */
    private void encryptDb() {
        PreparedStatement statement;
        try {
            if (!passwordIsBinary()){
                String query = "ALTER TABLE users MODIFY password VARBINARY(30);";
                statement = conn.prepareStatement(query);
                int updatedRows = statement.executeUpdate();
                query = "UPDATE users SET password = AES_ENCRYPT(password, 'This is a funny key')";
                statement = conn.prepareStatement(query);
                updatedRows = statement.executeUpdate();
                System.out.println("Passwords encrypting...");
            }
        } catch (SQLException e1) {
            System.out.println("An error occured...");
            e1.printStackTrace();
            dbAbort();
        }
    }
    
    private boolean passwordIsBinary(){
        try {
            String query = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS "
                    + "WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'password';";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            return (result.next() && result.getString(1).equals("varbinary"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }
    
    public void dbConnect (String ip, int port, String database, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+database,username,password);
            conn.setAutoCommit(false);
        } catch(ClassNotFoundException | SQLException e) {
            System.out.println("Connection could not be established. Please check server status and username/password.");
            e.printStackTrace();
        }
    }
    
    private void dbCommit() {
        try {
            conn.commit();
            System.out.println("All changes saved.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public void dbAbort() {
        try {
            conn.rollback();
            System.out.println("Uncommitted changes were cancelled.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public BankAccount loadAccount(BankAccount account){
        try {
            String query = "SELECT * FROM accounts_of_users WHERE username = ?;";
            PreparedStatement statement = conn.prepareStatement(query);
            
            statement.setString(1, account.getUsername());
            ResultSet result = statement.executeQuery();
            if (result.next()){
                account.setId(result.getInt("id"));
                account.setLastTransactionDate(result.getDate("transaction_date"));
                account.setBalance(result.getBigDecimal("amount"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return account;
    }
    
    public boolean userExists(){
        
    }
    
    
}



