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
public class LoginController {
    
    private int numFails;
    private static LoginController instance;

    private LoginController() {
        this.numFails = 0;
    }
    
    protected static LoginController getInstance(){
        if (instance == null)
            instance = new LoginController();
        return instance;
    }
    
    protected BankAccount getLoginInfo(BankAccount bankAccount){
        bankAccount.setUsername(ConsoleUtilities.askForUsername());
        bankAccount.setPassword(ConsoleUtilities.askForPassword());
        return bankAccount;
    }
    
    protected void addFailedAttempt(){
        System.out.println("Wrong username/password. Please try again.");
       numFails++; 
    }
    
    protected boolean tryAgain(){
        return numFails < 3;
    }
}
