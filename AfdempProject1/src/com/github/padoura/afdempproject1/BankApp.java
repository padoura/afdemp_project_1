/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

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
            menu.printAdminMenu();
            choice = menu.menuSelector();
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
        clearConsole();
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
    
    private static void depositToAdmin() {
        clearConsole();
        BankAccount adminAccount = dbCtrl.loadAccount(new BankAccount("admin"));
        dbCtrl.loadAccount(bankAcnt);
        if (adminAccount != null){
            BigDecimal amount = menu.enterAmount();
            if(bankAcnt.hasEnoughBalance(amount)){
                bankAcnt.withdraw(amount);
                adminAccount.deposit(amount);
                updateAccounts(adminAccount);
            }
            else if (amount.equals(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP)))
                System.out.println("(No deposit was made (0 EUR selected)...)");
            else
                System.out.println("Not enough balance! Your remaining balance is " + bankAcnt.getBalance() + " €");
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
        if(bankAcnt.hasEnoughBalance(amount)){
            bankAcnt.withdraw(amount);
            otherAccount.deposit(amount);
            updateAccounts(otherAccount);
        }else if (amount.equals(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP)))
            System.out.println("(No deposit was made (0 EUR selected)...)");
        else
            System.out.println("Not enough balance! Your remaining balance is " + bankAcnt.getBalance() + " EUR");
    }
    
    private static void executeSingleWithdraw(BankAccount otherAccount){
        BigDecimal amount = menu.enterAmount();
        if(otherAccount.hasEnoughBalance(amount)){
            otherAccount.withdraw(amount);
            bankAcnt.deposit(amount);
            updateAccounts(otherAccount);
        }else if (amount.equals(BigDecimal.valueOf(0).setScale(2, BigDecimal.ROUND_HALF_UP)))
            System.out.println("(No deposit was made (0 EUR selected)...)");
        else
            System.out.println("Not enough balance! Your remaining balance is " + bankAcnt.getBalance() + " EUR");
    }
    
    private static void updateAccounts(BankAccount otherAccount){
        if (dbCtrl.updateAccount(bankAcnt) && dbCtrl.updateAccount(otherAccount)){
            System.out.println("Deposit successful!");
            System.out.println("Your new balance is: " + bankAcnt.getBalance() + " €");
        }else{
            System.out.println("Database connection problem. Deposit could not be completed. Try again later...");
            bankAcnt.setBalance(bankAcnt.getOldBalance());
        }
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
            System.out.println(account.toString());
        }
    }
    
    private static ArrayList<BankAccount> loadAvailableAccounts(){
        ArrayList<BankAccount> accountList = dbCtrl.loadAllAccounts();
        accountList = removeAdminSelf(accountList);
        dbCtrl.loadAccount(bankAcnt);
        clearConsole();
        return accountList;
    }
    
    private static ArrayList<BankAccount> removeAdminSelf(ArrayList<BankAccount> accountList){
        for (int i=0;i<accountList.size();i++){
            String username = accountList.get(i).getUsername();
            if (username.equals("admin")  || username.equals(bankAcnt.getUsername())){
                accountList.remove(i);
                i--;
            }       
        }
        return accountList;
    }
    
}
