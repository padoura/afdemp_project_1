/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.math.BigDecimal;
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
    

    
    private static void initializeMenu() {
        menu = UserMenu.getInstance();
        fileCtrl = new FileController();
        fileCtrl.setFilename(bankAcnt);
        fileCtrl.appendToBuffer(bankAcnt.getUsername() + " logged in at " + FormattingUtilities.getFormattedCurrentDateTime());
    }
    
    public static void main(String[] args) {
        initializeApp();
        if (passLogin()){
            runApp();
        }else
            System.out.println("Three consecutive login failures...\n App terminated.");
    }
    
    public static void initializeApp(){
        ConsoleUtilities.setWindowsChcp();
        dbCtrl = DbController.getInstance();
        dbCtrl.checkConnectivity();
        bankAcnt = new BankAccount();
        loginCtrl = LoginController.getInstance();
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
                case 0: terminateApp();
                        break;
                default: System.out.println("Please choose a value between 0 and 4!");
            }
        }while(choice != 0 && choice != 4);
    }
    
    public static void runApp() {
        initializeMenu();
        if (bankAcnt.isAdmin()){
            loopAdminMenu();
        }else{
            loopMemberMenu();
        }
    }
    
    public static boolean passLogin(){
        while (loginCtrl.tryAgain()){
            loginCtrl.getLoginInfo(bankAcnt);
            if (dbCtrl.credentialsAreCorrect(bankAcnt)){
                System.out.println("Login successful!");
                bankAcnt.setPassword(null);
                return true;
            }
            else
                loginCtrl.addFailedAttempt();
        }
        bankAcnt.setPassword(null);
        return false;
    }
    
    private static void terminateApp() {
        ConsoleUtilities.clearConsole();
        System.out.println("Thanks for using our app! Bye!");
    }
    
    private static void viewMyAccount() {
        dbCtrl.loadAccount(bankAcnt);
        System.out.println(bankAcnt.toString());
        fileCtrl.appendToBuffer(bankAcnt.getUsername() +  " viewed his/her account at " + FormattingUtilities.getFormattedCurrentDateTime());
    }
    
    private static void depositToAdmin() {
        BankAccount adminAccount = dbCtrl.loadAccount(new BankAccount("admin"));
        dbCtrl.loadAccount(bankAcnt);
        ConsoleUtilities.clearConsole();
        if (adminAccount != null){
            executeSingleDeposit(adminAccount);
        }else{
            System.out.println("There is no admin account!");
        }
    }
    
    private static void withdrawFromAdminLoop() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        ListIterator<BankAccount> it = accountList.listIterator();
        if (!it.hasNext()){
            System.out.println("No members exist!");
        }else{
            while(it.hasNext()){
                BankAccount otherAccount = it.next();
                System.out.println("You are about to withdraw from member " + otherAccount.getUsername() + ":");
                executeSingleWithdraw(otherAccount);
                dbCtrl.loadAccount(bankAcnt);
            }
        }
    }
    
    private static void depositToMemberLoop() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        ListIterator<BankAccount> it = accountList.listIterator();
        if (!it.hasNext()){
            System.out.println("No members exist!");
        }else{
            while(it.hasNext()){
                BankAccount otherAccount = it.next();
                System.out.println("You are about to deposit to member " + otherAccount.getUsername() + ":");
                executeSingleDeposit(otherAccount);
                dbCtrl.loadAccount(bankAcnt);
            }
        }
    }
    
    private static void depositToMember() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        BankAccount otherAccount = menu.chooseFromDepositMenu(accountList);
        if (otherAccount != null)
                executeSingleDeposit(otherAccount);
    }
    
    private static void executeSingleDeposit(BankAccount otherAccount){
        BigDecimal amount = menu.enterAmount();
        if (amount.equals(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP)))
            System.out.println("(No deposit was made ("+ FormattingUtilities.getFormattedCurrency(0) + " selected)...)");
        else if(bankAcnt.hasEnoughBalance(amount)){
            bankAcnt.withdraw(amount);
            otherAccount.deposit(amount);
            updateAccounts(otherAccount);
        }
        else
            System.out.println("Not enough balance! Your remaining balance is " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()) + ".");
    }
    
    private static void executeSingleWithdraw(BankAccount otherAccount){
        BigDecimal amount = menu.enterAmount();
        if (amount.equals(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP)))
            System.out.println("(No deposit was made ("+ FormattingUtilities.getFormattedCurrency(0) + " selected)...)");
        else if(otherAccount.hasEnoughBalance(amount)){
            otherAccount.withdraw(amount);
            bankAcnt.deposit(amount);
            updateAccounts(otherAccount);
        }
        else
            System.out.println("Not enough balance! " + "Member " + otherAccount.getUsername() 
                    + " has " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()) + " remaining.");
    }
    
    private static void updateAccounts(BankAccount otherAccount){
        if (dbCtrl.updateAccount(bankAcnt) && dbCtrl.updateAccount(otherAccount)){
            System.out.println("Deposit successful!");
            System.out.println("Your new balance is: " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()));
            fileCtrl.appendToBuffer("User " + bankAcnt.getUsername() + " deposited " 
                    + FormattingUtilities.getFormattedCurrency(bankAcnt.getOldBalance().subtract(bankAcnt.getBalance())) 
                    + " to the account of user " + otherAccount.getUsername() + " at " + FormattingUtilities.getFormattedCurrentDateTime());
        }else{
            System.out.println("Database connection problem. Deposit could not be completed. Try again later...");
            bankAcnt.setBalance(bankAcnt.getOldBalance());
        }
    }
    
    private static void logTransactions() {
        fileCtrl.fileWrite();
        System.out.println("Transactions were saved to file successfully!");
        ConsoleUtilities.waitForEnter();
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
}