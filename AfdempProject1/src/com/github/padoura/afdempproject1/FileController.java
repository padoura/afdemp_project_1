/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class FileController {
    
    private File file;
    private StringBuilder buffer;
    private String filename;
    
    protected FileController(){
        file = null;
        filename = null;
        buffer = new StringBuilder();
    }
    
    protected boolean fileWrite(){
        file = new File(filename);

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            return false;
        }

        try {
            writer.append(buffer.toString());
        } catch (IOException ex) {
           return false;
        }
        
        try {
            writer.close();
        } catch (IOException ex) {
           return false;
        }
        
        return true;
    }
    
    protected void setFilename(BankAccount account) {
        if (account.isAdmin())
            filename = "statement_admin_" + FormattingUtilities.getFormattedCurrentDate() + ".txt";
        else
            filename = "statement_user_" + account.getUsername() + "_" + FormattingUtilities.getFormattedCurrentDate() + ".txt";
    }
    
    protected void appendToBuffer(String transaction){
        buffer.append(transaction);
        buffer.append("\n");
    }
    
}
