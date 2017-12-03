/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class BankApp {

    
    private static DbController dbCtrl;
    private static LoginController loginCtrl;
    private static FileController fileCtrl;
    private static BankAccount bankAcnt ;
    private static UserMenu menu;
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initializeApp();
        if (passLogin()){
            bankAcnt.setPassword(null);
            runApp();
        }else
            System.out.println("Three consecutive login failures...\n App terminated.");
    }
    
    private static void initializeApp(){
        dbCtrl = new DbController();
        dbCtrl.checkConnectivity();
        bankAcnt = new BankAccount();
        loginCtrl = new LoginController();
    }
    
    private static void loopAdminMenu(){
        int choice;
        do{
            waitForEnter();
            clearConsole();
            menu.printAdminMenu();
            choice = menu.menuSelector();
            switch (choice) {
                case 1: viewMyAccount();
                        break;
                case 2: viewMemberAccount();
                        break;
                case 3: depositToMember();
                        break;
                case 4: depositToAdmin();
                        break;
                case 5: logTransactions();
                        terminate();
                        break;
                case 0: terminate();
                        break;
                default: System.out.println("Please choose a value between 0 and 5!");
            }
        }while(choice != 0);
    }
    
    private static void loopMemberMenu(){
        int choice;
        do{
            waitForEnter();
            clearConsole();
            menu.printMemberMenu();
            choice = menu.menuSelector();
            switch (choice) {
                case 1: viewMyAccount();
                        break;
                case 3: depositToMember();
                        break;
                case 2: depositToAdmin();
                        break;
                case 4: logTransactions();
                        break;
                case 0: terminate();
                        break;
                default: System.out.println("Please choose a value between 0 and 4!");
            }
        }while(choice != 0); 
    }
    
    private static void runApp() {
        menu = new UserMenu();
        if (bankAcnt.isAdmin()){
            loopAdminMenu();
        }else{
            loopMemberMenu();
        }
    }
    
    private static boolean passLogin(){
        while (loginCtrl.tryAgain()){
            loginCtrl.getLoginInfo(bankAcnt);
            if (dbCtrl.credentialsAreCorrect(bankAcnt)){
                System.out.println("Login successful!");
                return true;
            }
            else
                loginCtrl.addFailedAttempt();
        }
        return false;
    }
    
    
    private static void waitForEnter() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Press Enter to continue...");
        scn.nextLine();
    }
    
    private static void clearConsole(){
        final String os = System.getProperty("os.name");
        printNewlines();
        if (os.contains("Windows")){
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException ex) {
                printNewlines();
            }
        }
        else{
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
    
    private static void printNewlines(){
        System.out.println(new String(new char[20]).replace("\0", "\n"));
    }
    
    private static void terminate() {
        clearConsole();
        System.out.println("Thanks for using our app! Bye!");
    }
    
    private static void viewMyAccount() {
        dbCtrl.loadAccount(bankAcnt);
        System.out.println(bankAcnt.toString());
    }

//    private static void viewAllAccounts() {
//        ArrayList<BankAccount> accountList = dbCtrl.loadAllAccounts();
//        menu.viewAllAccounts(accountList);
//    }

    private static void depositToMember() {
        ArrayList<BankAccount> accountList = dbCtrl.loadAllAccounts();
        clearConsole();
        BankAccount otherAccount = menu.depositMenu(bankAcnt, accountList);
        if (otherAccount != null){
            // TO DO HERE...
        }
    }

    private static void depositToAdmin() {
        
    }

    private static void logTransactions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void viewMemberAccount() {
        BankAccount account = new BankAccount(loginCtrl.askForUsername());
        dbCtrl.loadAccount(account);
        if (account.getBalance() == null){
            System.out.println("User " + account.getUsername() + " does not exist.");
        }else{
            System.out.println(bankAcnt.toString());
        }
       
    }
}
