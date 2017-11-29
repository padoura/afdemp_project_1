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
    
    
    public boolean showLoginDialog(){
        String username = askForUsername();
        String password = askForPassword();
        return false;
    }
    
    private String askForUsername(){
        return askForCredential("username");
    }
    
    private boolean userExists(){
        return false;
    }
    
    private boolean adminPriviledges(){
        return false;
    }
    
    private String askForPassword(){
        if (System.console() != null){
            return maskCredential("password");
        }else{
            return askForCredential("password");
        }
    }
    
    private String askForCredential(String type){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your " + ":");
        return scanner.nextLine();
    }
    
    private String maskCredential(String type){
        Console console = System.console();
        console.printf("Please enter your " + ":\n");
        return new String(console.readPassword());
    }
    
}
