/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

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

    protected void viewAccount(BankAccount bankAcnt) {
        System.out.println(bankAcnt.toString());
    }
    
}
