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
import java.util.logging.Level;

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

        Writer writer = tryNewWriter();
        if (writer == null)
            return false;

        if (!tryAppendWriter(writer))
            return false;
        
        return tryCloseWriter(writer);
    }
    
    private Writer tryNewWriter(){
        try {
            return new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private boolean tryAppendWriter(Writer writer){
        try {
            writer.append(buffer.toString());
            return true;
        } catch (IOException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            try {
                writer.close();
            } catch (IOException ex1) {
                LoggerController.getLogger().log(Level.SEVERE, null, ex);
            }finally{
                return false;
            }
        }
    }
    
    private boolean tryCloseWriter(Writer writer){
        try {
            writer.close();
            return true;
        } catch (IOException ex) {
            LoggerController.getLogger().log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    protected void setFilename(BankAccount account) {
        if (account.isAdmin())
            filename = "statement_admin_" + FormattingUtilities.getFormattedCurrentDate() + ".txt";
        else{
            filename = "statement_user_" + account.getUsername() + "_" + FormattingUtilities.getFormattedCurrentDate() + ".txt";
        }
            
    }
    
    protected void appendToBuffer(String transaction){
        buffer.append(transaction);
        buffer.append("\n");
    }
    
}
