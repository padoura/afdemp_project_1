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
package org.afdemp.project1.controller;

import org.afdemp.project1.model.BankAccount;
import org.afdemp.project1.util.ConsoleUtilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class UserMenu {
    
    private UserMenu() {
    }
    
    private static class SingletonHelper {
        private static final UserMenu INSTANCE = new UserMenu();
    }
    
    public static UserMenu getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
    public void printAdminMenu() {
            System.out.println("Please choose one of the following options:");
            System.out.println("(1) View My Account");
            System.out.println("(2) View Members' Accounts");
            System.out.println("(3) Deposit to Member's Accounts");
            System.out.println("(4) Withdraw from Members' Accounts");
            System.out.println("(5) Send Today's Transactions and Exit");
            System.out.println("(0) Exit");
    }
    
    public void printMemberMenu() {
            System.out.println("Please choose one of the following options:");
            System.out.println("(1) View My Account");
            System.out.println("(2) Deposit to Cooperative's Account");
            System.out.println("(3) Deposit to a Member's Accounts");
            System.out.println("(4) Send Today's Transactions and Exit");
            System.out.println("(0) Exit");
    }

    public BankAccount chooseFromDepositMenu(ArrayList<BankAccount> accountList) {
        printMenuListOfUsers(accountList);
        int choice;
        do{
            System.out.println("Please enter a number from 0 to " + accountList.size() + ":");
            choice = ConsoleUtilities.intSelector();
        }while(choice < 0 || choice > accountList.size());
        return (choice==0 ? null : accountList.get(choice-1));
    }
    
    private void printMenuListOfUsers(ArrayList<BankAccount> accountList){
        for (int i=0;i<accountList.size();i++){
            System.out.println("(" + (i+1) + ") " + accountList.get(i).getUsername());
        }
        System.out.println("(0) Return to Main Menu");
    }
    
    public BigDecimal enterAmount(){
        System.out.println("Please enter an amount with comma decimal separator (amount will be rounded to 2 decimal points):");
        Scanner menuScanner = new Scanner(System.in, "UTF-8").useLocale(Locale.forLanguageTag("el-GR"));
        while (true){
            if (!menuScanner.hasNextBigDecimal()){
                menuScanner.nextLine();
                System.out.println("Invalid input! Comma should be used as decimal separator.\n"
                    + "Please type a valid amount:");
            }else{
                BigDecimal result = menuScanner.nextBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP);
                if (result.compareTo(BigDecimal.ZERO)>=0)
                    return result;
                else{
                    menuScanner.nextLine();
                    System.out.println("Invalid input! The amount cannot be negative.\n"
                            + "Please type a valid amount:");                  
                }
            }
        }
    }
}
