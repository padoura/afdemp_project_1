/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.Console;
import java.util.Scanner;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class LoginController {
    
    private int numFails;

    public LoginController() {
        this.numFails = 0;
    }
    
    public BankAccount getLoginInfo(BankAccount bankAccount){
        bankAccount.setUsername(askForUsername());
        bankAccount.setPassword(askForPassword());
        return bankAccount;
    }
    
    public void addFailedAttempt(){
        System.out.println("Wrong username/password. Please try again.");
       numFails++; 
    }
    
    public boolean tryAgain(){
        return numFails < 3;
    }
    
    private String askForPassword(){
        if (System.console() != null){
            return maskCredential("password");
        }else{
            return askForCredential("password");
        }
    }
    
    protected String askForUsername(){
        return askForCredential("username");
    }
    
    private String askForCredential(String type){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a " + type + ":");
        return scanner.nextLine();
    }
    
    private String maskCredential(String type){
        Console console = System.console();
        console.printf("Please enter a " + type + ":\n");
        return new String(console.readPassword());
    }
    
}
