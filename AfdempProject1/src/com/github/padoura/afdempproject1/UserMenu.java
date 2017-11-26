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
public abstract class UserMenu {
    
    private BankAccount bankAccount;
    
    public void viewAccount(BankAccount account){
        
    }
    
    public boolean amountsAreAvailable(){
        return false;
    }
    
    public void deposit(BankAccount otherAccount){
        
    }
    
    public abstract void menuDialog();
    
}
