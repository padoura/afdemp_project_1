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

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class LoginController {
    
    private int numFails;

    private LoginController() {
        this.numFails = 0;
    }
    
    private static class SingletonHelper {
        private static final LoginController INSTANCE = new LoginController();
    }
    
    public static LoginController getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
    public BankAccount getLoginInfo(BankAccount bankAccount){
        bankAccount.setUsername(ConsoleUtilities.askForUsername());
        bankAccount.setPassword(ConsoleUtilities.askForPassword());
        return bankAccount;
    }
    
    public void addFailedAttempt(){
        System.out.println("Wrong username/password. Please try again.");
       numFails++; 
    }
    
    public boolean tryAgain(){
        return numFails < 3;
    }
}
