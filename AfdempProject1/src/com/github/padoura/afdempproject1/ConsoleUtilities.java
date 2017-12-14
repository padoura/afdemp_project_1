/*
 * Copyright (C) 2017 padoura <padoura@users.noreply.github.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.padoura.afdempproject1;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public final class ConsoleUtilities {
    
    private ConsoleUtilities(){
    }
    
    public  static void waitForEnter() {
        Scanner scn = new Scanner(System.in, "UTF-8");
        System.out.println("Press Enter to continue...");
        scn.nextLine();
        clearConsole();
    }

    public static void clearConsole(){
        final String os = System.getProperty("os.name");
        printNewlines(20);
        if (os.contains("Windows")){
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException ex) {
                LoggerController.getLogger().log(Level.SEVERE, null, ex);
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
        Scanner scanner = new Scanner(System.in, "UTF-8");
        System.out.println("Please enter a " + type + ":");
        return scanner.nextLine();
    }

    public static String maskCredential(String type){
        Console console = System.console();
        console.printf("Please enter a " + type + ":\n");
        return new String(console.readPassword());
    }

    public static int intSelector() {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        while (!scanner.hasNextInt()){
            scanner.nextLine();
            System.out.println("Invalid input! Please type a valid integer!");
        }
        return scanner.nextInt();
    }
}
