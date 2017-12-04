/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class UserMenu {
    
    private BankAccount bankAccount;
    
    protected void printAdminMenu() {
            System.out.println("Please choose one of the following options:");
            System.out.println("(1) View My Account");
            System.out.println("(2) View Members' Accounts");
            System.out.println("(3) Deposit to Member's Accounts");
            System.out.println("(4) Withdraw from Members' Accounts");
            System.out.println("(5) Send Today's Transactions and Exit");
            System.out.println("(0) Exit");
    }
    
    protected void printMemberMenu() {
            System.out.println("Please choose one of the following options:");
            System.out.println("(1) View My Account");
            System.out.println("(2) Deposit to Cooperative's Account");
            System.out.println("(3) Deposit to a Member's Accounts");
            System.out.println("(4) Send Today's Transactions");
            System.out.println("(0) Exit");
    }
    
    protected int menuSelector() {
        Scanner menuScanner = new Scanner(System.in);
        while (!menuScanner.hasNextInt()){
            menuScanner.nextLine();
            System.out.println("Invalid input! Please type a valid integer!");
        }
        return menuScanner.nextInt();
    }
    
    protected boolean amountsAreAvailable(){
        return false;
    }
    
    protected void deposit(BankAccount otherAccount){
        
    }

//    protected void viewAccount(BankAccount bankAcnt) {
//        System.out.println(bankAcnt.toString());
//    }

    protected void viewAllAccounts(ArrayList<BankAccount> accountList) {
        Iterator it = accountList.iterator();
        if (it.hasNext()){
            while(it.hasNext()){
                System.out.println(it.next().toString());
            }
        }else{
            System.out.println("No users exist...");
        }
    }
    
    protected ArrayList<BankAccount> removeAdminSelf(BankAccount bankAcnt, ArrayList<BankAccount> accountList){
        for (int i=0;i<accountList.size();i++){
            String username = accountList.get(i).getUsername();
            if (username.equals("admin")  || username.equals(bankAcnt.getUsername())){
                accountList.remove(i);
                i--;
            }       
        }
        return accountList;
    }

    protected BankAccount depositMenu(ArrayList<BankAccount> accountList) {
        for (int i=0;i<accountList.size();i++){
            System.out.println("(" + (i+1) + ") " + accountList.get(i).getUsername());
        }
        System.out.println("(0) Return to Main Menu");
        int choice;
        do{
            System.out.println("Please enter a number from 0 to " + accountList.size() + ":");
            choice = menuSelector();
        }while(choice < 0 || choice > accountList.size());
        return (choice==0 ? null : accountList.get(choice-1));
    }
    
    protected BigDecimal enterAmount(){
        System.out.println("Please enter an amount (amount will be rounded to 2 decimal points):");
        Scanner menuScanner = new Scanner(System.in);
        while (!menuScanner.hasNextBigDecimal()){
            menuScanner.nextLine();
            System.out.println("Invalid input! Please type a valid amount!");
        }
        return menuScanner.nextBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP);
    }
        
    
}
