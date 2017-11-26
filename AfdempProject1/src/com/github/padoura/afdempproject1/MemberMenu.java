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
public class MemberMenu extends UserMenu{

    @Override
    public void menuDialog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void printMenu() {
            System.out.println("Please choose one of the following options:");
            System.out.println("(1) View My Account");
            System.out.println("(2) Deposit to Cooperative's Account");
            System.out.println("(3) Deposit to a Member's Accounts");
            System.out.println("(5) Send Today's Transactions");
            System.out.println("(0) Exit");
    }
}
