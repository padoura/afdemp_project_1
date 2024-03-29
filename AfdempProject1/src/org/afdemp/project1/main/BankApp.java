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
package org.afdemp.project1.main;

import org.afdemp.project1.util.LoggerController;
import org.afdemp.project1.controller.UserMenu;
import org.afdemp.project1.controller.LoginController;
import org.afdemp.project1.controller.FileController;
import org.afdemp.project1.controller.DbController;
import org.afdemp.project1.model.BankAccount;
import org.afdemp.project1.util.ConsoleUtilities;
import org.afdemp.project1.util.FormattingUtilities;
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
    private static final String LOGFILENAME = "myLogFile.txt";
    
    private BankApp(){
    }
    
    /**
     * The main method. Either login fails thrice and program ends or
     * passes and app runs.
     * @param args
     */
    public static void main(String[] args) {
        if (!initializeApp())
            System.out.println("Check database connection and try again later...");
        else if (passLogin()){
            runApp();
        }else{
            System.out.println("Three consecutive login failures...\n"
                    + "App terminated.");
            ConsoleUtilities.waitForEnter();
        }
    }
    
    private static boolean initializeApp(){
        LoggerController.setLogger(LOGFILENAME);
        bankAcnt = new BankAccount();
        loginCtrl = LoginController.getInstance();
        dbCtrl = DbController.getInstance();
        return dbCtrl.checkConnectivity();
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
        fileCtrl.appendToBuffer("Logged in at " + FormattingUtilities.getFormattedCurrentDateTime());
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
        if (dbCtrl.loadAccount(bankAcnt).getLastTransactionDate() != null){
            System.out.println(bankAcnt.toString());
            fileCtrl.appendToBuffer("Viewed his/her account at " 
                    + FormattingUtilities.getFormattedCurrentDateTime());
        }else{
            System.out.println("Database connection could not be established...");
        }
    }
    
    private static void viewMemberAccount() {
        BankAccount account = new BankAccount(ConsoleUtilities.askForUsername());
        dbCtrl.loadAccount(account);
        if (account.getBalance() == null){
            System.out.println("User " + account.getUsername() + " does not exist or database connection problem...");
        }else{
            System.out.println(account.toString());
            fileCtrl.appendToBuffer("Viewed the account of " +  account.getUsername() 
                    + " at " + FormattingUtilities.getFormattedCurrentDateTime());
        }
    }
    
    private static void depositToMemberLoop() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        if (accountList != null){
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
        }else{
            System.out.println("Database connection could not be established...");
        }
    }    
    
    private static ArrayList<BankAccount> loadAvailableAccounts(){
        ArrayList<BankAccount> accountList = dbCtrl.loadAllAccounts();
        if (accountList == null)
            return null;
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
            System.out.println("No deposit was made ("+ FormattingUtilities.getFormattedCurrency(0) + " selected)...");
        else if(bankAcnt.hasEnoughBalance(amount)){
            executeSingleDeposit(otherAccount, amount);
        }
        else{
            System.out.println("Not enough balance! Your remaining balance is " 
                    + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()) + ".");
            fileCtrl.appendToBuffer("Attempted to deposit " 
                    + FormattingUtilities.getFormattedCurrency(amount) 
                    + " to the account of " + otherAccount.getUsername() + " at " + FormattingUtilities.getFormattedCurrentDateTime() +
                    " without having enough balance.");
        }
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
        if (dbCtrl.updateAccounts(bankAcnt, otherAccount)){
            System.out.println("Deposit successful!");
            System.out.println("Your new balance is: " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()));
            fileCtrl.appendToBuffer("Deposited (withdrew if negative) " 
                    + FormattingUtilities.getFormattedCurrency(bankAcnt.getOldBalance().subtract(bankAcnt.getBalance())) 
                    + " to the account of " + otherAccount.getUsername() + " at " + bankAcnt.getLastTransactionDate());
        }else{
            System.out.println("Database communication error. Deposit could not be completed. Try again later...");
            fileCtrl.appendToBuffer("Attempted to deposit (withdraw if negative) " 
                    + FormattingUtilities.getFormattedCurrency(bankAcnt.getOldBalance().subtract(bankAcnt.getBalance())) 
                    + " to the account of " + otherAccount.getUsername() + " at " + bankAcnt.getLastTransactionDate() +
                    " but a database communication error occured.");
            bankAcnt.setBalance(bankAcnt.getOldBalance());
            bankAcnt.setLastTransactionDate(bankAcnt.getOldLastTransactionDate());
        }
    }
    
    private static void withdrawFromAdminLoop() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        if (accountList != null){
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
        }else{
            System.out.println("Database connection could not be established...");
        }
    }
    
    private static void trySingleWithdraw(BankAccount otherAccount){
        BigDecimal amount = menu.enterAmount();
        if (amount.doubleValue() == 0)
            System.out.println("No withdraw was made ("+ FormattingUtilities.getFormattedCurrency(0) + " selected)...");
        else if(otherAccount.hasEnoughBalance(amount)){
            executeSingleWithdraw(otherAccount, amount);
        }
        else{
            System.out.println("Not enough balance! " + "Member " + otherAccount.getUsername() 
                    + " has " + FormattingUtilities.getFormattedCurrency(bankAcnt.getBalance()) + " remaining.");
            
            fileCtrl.appendToBuffer("Attempted to withdraw " 
                    + FormattingUtilities.getFormattedCurrency(amount)
                    + " from the account of " + otherAccount.getUsername() + " at " + FormattingUtilities.getFormattedCurrentDateTime() +
                    " without having enough balance.");
        }
            
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
            System.out.println("There was a problem saving transactions to file...");
        ConsoleUtilities.waitForEnter();
    }
    
    private static void terminateApp() {
        ConsoleUtilities.clearConsole();
        System.out.println("Thanks for using our app! Bye!");
        ConsoleUtilities.waitForEnter();
    }   
    
    private static void depositToMember() {
        ArrayList<BankAccount> accountList = loadAvailableAccounts();
        if (accountList!=null){
            BankAccount otherAccount = menu.chooseFromDepositMenu(accountList);
            if (otherAccount != null)
                    trySingleDeposit(otherAccount);
        }else{
            System.out.println("Database connection could not be established...");
        }
    }
    
    private static void depositToAdmin() {
        BankAccount adminAccount = dbCtrl.loadAccount(new BankAccount("admin"));
        if (adminAccount.getLastTransactionDate() != null && dbCtrl.loadAccount(bankAcnt).getLastTransactionDate() != null)
            trySingleDeposit(adminAccount);
        else
            System.out.println("Database connection could not be established...");
    }
}