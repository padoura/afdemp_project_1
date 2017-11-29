/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        // TODO code application logic here
        dbCtrl = new DbController();
        dbCtrl.checkConnectivity();
        loginCtrl = new LoginController();
        loginCtrl.showLoginDialog();
    }
    
    
    private static void waitForEnter() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Press Enter to continue...");
        scn.nextLine();
    }
    
    private static void clearConsole(){
        final String os = System.getProperty("os.name");
        try {
            if (os.contains("Windows")){
                Runtime.getRuntime().exec("cls");
            }
            else{
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException e) {
               e.printStackTrace();
        }
    }
}
