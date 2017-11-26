/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class AdminMenu extends UserMenu{

    @Override
    public void menuDialog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void withdrawLoop(){
        
    }
    
    public void depositLoop(){
        
    }

    private void printMenu() {
            System.out.println("Please choose one of the following options:");
            System.out.println("(1) View My Account");
            System.out.println("(2) View Members' Accounts");
            System.out.println("(3) Deposit to Member's Accounts");
            System.out.println("(4) Withdraw from Members' Accounts");
            System.out.println("(5) Send Today's Transactions");
            System.out.println("(0) Exit");
    }
    
}
