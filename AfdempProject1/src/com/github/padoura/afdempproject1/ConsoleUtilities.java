/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public final class ConsoleUtilities {
    
    private ConsoleUtilities(){
    }
    
    public  static void waitForEnter() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Press Enter to continue...");
        scn.nextLine();
        clearConsole();
    }
    
    public static void setWindowsChcp(){
        final String os = System.getProperty("os.name");
        if (os.contains("Windows")){
            try {
                new ProcessBuilder("cmd.exe", "/c", "C:\\Windows\\System32\\chcp", "1252").inheritIO().start().waitFor();
            } catch (IOException ex) {
                System.out.println("Fail");
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void clearConsole(){
        final String os = System.getProperty("os.name");
        printNewlines(20);
        if (os.contains("Windows")){
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException ex) {
                printNewlines(20);
            }
        }
        else{
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
    
    public static void printNewlines(int numLines){
        System.out.println(new String(new char[numLines]).replace("\0", "\n"));
    }
    
    public static String askForPassword(){
        if (System.console() != null){
            return maskCredential("password");
        }else{
            return askForCredential("password");
        }
    }
    
    public static String askForUsername(){
        return askForCredential("username");
    }
    
    public static String askForCredential(String type){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a " + type + ":");
        return scanner.nextLine();
    }
    
    public static String maskCredential(String type){
        Console console = System.console();
        console.printf("Please enter a " + type + ":\n");
        return new String(console.readPassword());
    }
    
    
    public static int intSelector() {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()){
            scanner.nextLine();
            System.out.println("Invalid input! Please type a valid integer!");
        }
        return scanner.nextInt();
    }
}
