/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public final class BankApp {
    
    private static DbController dbCtrl;
    private static LoginController loginCtrl;
    private static FileController fileCtrl;
    private static BankAccount bankAcnt ;
    private static UserMenu menu;
    private BankApp(){
    }
    
    /**
     * The main method. Either login fails thrice and program ends or
     * passes and app runs.
     * @param args
     */
    public static void main(String[] args) {
        initializeApp();
        if (passLogin()){
            runApp();
        }else
            System.out.println("Three consecutive login failures...\n App terminated.");
    }
    
    private static void initializeApp(){
        dbCtrl = DbController.getInstance();
        dbCtrl.checkConnectivity();
        bankAcnt = new BankAccount();
        loginCtrl = LoginController.getInstance();
    }
    
    private static boolean passLogin(){
        while (loginCtrl.tryAgain()){
            loginCtrl.getLoginInfo(bankAcnt);
            if (credentialAreCorrect())
                return true;
        }
        bankAcnt.setPassword(null);
        return false;
    }
    
    private static boolean credentialAreCorrect(){
        if (dbCtrl.credentialsAreCorrect(bankAcnt)){
                System.out.println("Login successful!");
                bankAcnt.setPassword(null);
                return true;
            }
        else{
             loginCtrl.addFailedAttempt();
             return false;
        }  
    }
    
    private static void runApp() {
        initializeMenu();
        if (bankAcnt.isAdmin()){
            loopAdminMenu();
        }else{
            loopMemberMenu();
        }
    }

    private static void initializeMenu() {
        menu = UserMenu.getInstance();
        fileCtrl = new FileController();
        fileCtrl.setFilename(bankAcnt);
        fileCtrl.appendToBuffer(bankAcnt.getUsername() + " logged in at " + FormattingUtilities.getFormattedCurrentDateTime());
    }
    
    private static void loopAdminMenu(){
        int choice;
        do{
            ConsoleUtilities.waitForEnter();
            menu.printAdminMenu();
            choice = ConsoleUtilities.intSelector();
            switch (choice) {
                case 1: viewMyAccount();
                        break;
                case 2: viewMemberAccount();
                        break;
                case 3: depositToMemberLoop();
                        break;
                case 4: withdrawFromAdminLoop();
                        break;
                case 5: logTransactions();
                        // no break
                case 0: terminateApp();
                        break;
                default: System.out.println("Please choose a value between 0 and 5!");
            }
        }while(choice != 0 && choice != 5);
    }
    
    private static void loopMemberMenu(){
        int choice;
        do{
            ConsoleUtilities.waitForEnter();
            menu.printMemberMenu();
            choice = ConsoleUtilities.intSelector();
            switch (choice) {
                case 1: viewMyAccount();
                        break;
                case 3: depositToMember();
                        break;
                case 2: depositToAdmin();
                        break;
                case 4: logTransactions();
                        // no break
                case 0: terminateApp();
                        break;
                default: System.out.println("Please choose a value between 0 and 4!");
            }
        }while(choice != 0 && choice != 4);
    }
    
    private static void viewMyAccount() {
        dbCtrl.loadAccount(bankAcnt);
        System.out.println(bankAcnt.toString());
        fileCtrl.appendToBuffer(bankAcnt.getUsername() +  " viewed his/her account at " + FormattingUtilities.getFormattedCurrentDateTime());
    }
    
    private static void viewMemberAccount() {
        BankAccount account = new BankAccount(ConsoleUtilities.askForUsername());
        dbCtrl.loadAccount(account);
        if (account.getBalance() == null){
            System.out.println("User " + account.getUsername() + " does not exist.");
        }else{
            System.out.println(account.toString());
            fileCtrl.appendToBuffer("Admin viewed the account of " +  account.getUsername() 
                    + " at " + FormattingUtilities.getFormattedCurrentDateTime());
        }
    }
    
    private static void depositToMemberLoop() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        ListIterator<BankAccount> iterator = accountList.listIterator();
        if (!iterator.hasNext()){
            System.out.println("No members exist!");
        }else{
            while(iterator.hasNext()){
                BankAccount otherAccount = iterator.next();
                System.out.println("You are about to deposit to member " + otherAccount.getUsername() + ":");
                trySingleDeposit(otherAccount);
                dbCtrl.loadAccount(bankAcnt);
            }
        }
    }    
    
    private static ArrayList<BankAccount> loadAvailableAccounts(){
        ArrayList<BankAccount> accountList = dbCtrl.loadAllAccounts();
        accountList = removeAdminSelf(accountList);
        dbCtrl.loadAccount(bankAcnt);
        ConsoleUtilities.clearConsole();
        return accountList;
    }    
    
    private static ArrayList<BankAccount> removeAdminSelf(ArrayList<BankAccount> accountList){
        for (int i=0;i<accountList.size();i++){
            if (accountList.get(i).isAdmin()  || accountList.get(i).getUsername().equals(bankAcnt.getUsername())){
                accountList.remove(i);
                i--;
            }    
        }
        return accountList;
    }    
    
    private static void trySingleDeposit(BankAccount otherAccount){
        BigDecimal amount = menu.enterAmount();
        if (amount.doubleValue() == 0)
            System.out.println("(No deposit was made ("+ FormattingUtilities.getFormattedCurrency(0) + " selected)...)");
        else if(bankAcnt.hasEnoughBalance(amount)){
            executeSingleDeposit(otherAccount, amount);
        }
        else
            System.out.println("Not enough balance! Your remaining balance is " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()) + ".");
    }
    
    private static void executeSingleDeposit(BankAccount otherAccount, BigDecimal amount){
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
        otherAccount.setLastTransactionDate(currentTimestamp);
        bankAcnt.setLastTransactionDate(currentTimestamp);
        bankAcnt.withdraw(amount);
        otherAccount.deposit(amount);
        updateAccounts(otherAccount);
    }
    
    
    private static void updateAccounts(BankAccount otherAccount){
        if (dbCtrl.updateAccount(bankAcnt) && dbCtrl.updateAccount(otherAccount)){
            System.out.println("Deposit successful!");
            System.out.println("Your new balance is: " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()));
            fileCtrl.appendToBuffer("User " + bankAcnt.getUsername() + " deposited (withdrew if negative)" 
                    + FormattingUtilities.getFormattedCurrency(bankAcnt.getOldBalance().subtract(bankAcnt.getBalance())) 
                    + " to the account of user " + otherAccount.getUsername() + " at " + FormattingUtilities.getFormattedCurrentDateTime());
        }else{
            System.out.println("Database connection problem. Deposit could not be completed. Try again later...");
            bankAcnt.setBalance(bankAcnt.getOldBalance());
            bankAcnt.setOldLastTransactionDate(bankAcnt.getOldLastTransactionDate());
        }
    }
    
    private static void withdrawFromAdminLoop() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        ListIterator<BankAccount> iterator = accountList.listIterator();
        if (!iterator.hasNext()){
            System.out.println("No members exist!");
        }else{
            while(iterator.hasNext()){
                BankAccount otherAccount = iterator.next();
                System.out.println("You are about to withdraw from member " + otherAccount.getUsername() + ":");
                trySingleWithdraw(otherAccount);
                dbCtrl.loadAccount(bankAcnt);
            }
        }
    }
    
    private static void trySingleWithdraw(BankAccount otherAccount){
        BigDecimal amount = menu.enterAmount();
        if (amount.doubleValue() == 0)
            System.out.println("(No deposit was made ("+ FormattingUtilities.getFormattedCurrency(0) + " selected)...)");
        else if(otherAccount.hasEnoughBalance(amount)){
            executeSingleWithdraw(otherAccount, amount);
        }
        else
            System.out.println("Not enough balance! " + "Member " + otherAccount.getUsername() 
                    + " has " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()) + " remaining.");
    }  
    
    private static void executeSingleWithdraw(BankAccount otherAccount, BigDecimal amount){
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
        otherAccount.setLastTransactionDate(currentTimestamp);
        bankAcnt.setLastTransactionDate(currentTimestamp);
        otherAccount.withdraw(amount);
        bankAcnt.deposit(amount);
        updateAccounts(otherAccount);
    }     
    
    private static void logTransactions() {
        if (fileCtrl.fileWrite())
            System.out.println("Transactions were saved to file successfully!");
        else
            System.out.println("Transactions could not be saved to file!");
        ConsoleUtilities.waitForEnter();
    }   
    
    private static void terminateApp() {
        ConsoleUtilities.clearConsole();
        System.out.println("Thanks for using our app! Bye!");
    }   
    
    private static void depositToMember() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        BankAccount otherAccount = menu.chooseFromDepositMenu(accountList);
        if (otherAccount != null)
                trySingleDeposit(otherAccount);
    }  
    
    private static void depositToAdmin() {
        BankAccount adminAccount = dbCtrl.loadAccount(new BankAccount("admin"));
        ConsoleUtilities.clearConsole();
        if (adminAccount != null){
            dbCtrl.loadAccount(bankAcnt);
            trySingleDeposit(adminAccount);
        }else{
            System.out.println("There is no admin account!");
        }
    }
    

    

    

    

    

    

    

    

}