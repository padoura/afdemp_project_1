/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import com.mysql.jdbc.CallableStatement;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.util.jar.Pack200.Packer.PASS;
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
    private static final String PASSWORD = "afdemp";
    
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public DbController() {
        this.conn = null;
        this.stmt = null;
        this.rs = null;
    }
    
    public void checkConnectivity(){
        if (connectionIsAvailable()){
            System.out.println("Database connection tested successfully!");
        }else{
            System.out.println("Database connection could not be established!");
        }
    }
    
    public boolean connectionIsAvailable(){
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
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            return "driver";
        }

        //STEP 3: Open a connection
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
    
    
    public BankAccount loadAccount(BankAccount account){
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
                account.setBalance(rs.getBigDecimal("amount"));
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
    
    
    public boolean credentialsAreCorrect(BankAccount account){
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
    
    public boolean balanceIsEnough(String username, BigDecimal amount){
        connect();
        String query = "SELECT amount_is_available(?, ?);";
        boolean result;
        try {
            stmt = conn.prepareStatement(query);
        } catch (SQLException ex) {
            System.out.println("Problem with database connection...");
            closeConnection();
            return false;
        }
        
        try {
            ((PreparedStatement) stmt).setString(1, username);
            ((PreparedStatement) stmt).setBigDecimal(2, amount);
        } catch (SQLException ex) {
            System.out.println("Balance could not be checked.");
            closeConnection();
            return false;
        }
        
        
        try {
            rs = ((PreparedStatement) stmt).executeQuery();
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
            closeConnection();
            return result;
        } catch (SQLException ex) {
            System.out.println("Random rs.close error.");
            closeConnection();
            return false;
        }
    }
    
    
    
    
    
    
        
//    private void dbCommit() {
//        try {
//            conn.commit();
//            System.out.println("All changes saved.");
//        } catch (SQLException e) {
//            System.out.println("Changes could be saved...");
//        }
//    }
    
    

    
    
//    public void closeConnection(){
//        try {
////            System.out.println("Closing connection...");
//            dbCommit();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void checkConnectivity() {
//        System.out.println("Welcome!");
//        System.out.println("Please wait while database connection is tested...");
//        dbConnect(DB_URL, USERNAME, PASSWORD);
//        encryptDb();
//        closeConnection();
//        System.out.println("Connection test complete!");
//    }
//    
//    /*
//     * Encrypt column password (according to deliverable 2c).
//     */
//    private void encryptDb() {
//        PreparedStatement statement;
//        try {
//            if (!passwordIsBinary()){
//                String query = "ALTER TABLE users MODIFY password VARBINARY(30);";
//                statement = conn.prepareStatement(query);
//                int updatedRows = statement.executeUpdate();
//                query = "UPDATE users SET password = AES_ENCRYPT(password, 'This is a funny key')";
//                statement = conn.prepareStatement(query);
//                updatedRows = statement.executeUpdate();
//                System.out.println("Passwords encrypting...");
//            }
//        } catch (SQLException e1) {
//            System.out.println("An error occured...");
//            e1.printStackTrace();
//            dbAbort();
//        }
//    }
//    
//    private boolean passwordIsBinary(){
//        try {
//            String query = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS "
//                    + "WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'password';";
//            PreparedStatement statement = conn.prepareStatement(query);
//            ResultSet result = statement.executeQuery();
//            return (result.next() && result.getString(1).equals("varbinary"));
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        return true;
//    }
//    
//    public void dbConnect (String ip, int port, String database, String username, String password) {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+database,username,password);
//            conn.setAutoCommit(false);
//        } catch(ClassNotFoundException | SQLException e) {
//            System.out.println("Connection could not be established. Please check server status and username/password.");
//            e.printStackTrace();
//        }
//    }
//    
//    
//    public void dbAbort() {
//        try {
//            conn.rollback();
//            System.out.println("Uncommitted changes were cancelled.");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    
    
}



