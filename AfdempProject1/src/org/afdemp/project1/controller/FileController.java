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

import org.afdemp.project1.util.LoggerController;
import org.afdemp.project1.model.BankAccount;
import org.afdemp.project1.util.FormattingUtilities;
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
    
    public FileController(){
        file = null;
        filename = null;
        buffer = new StringBuilder();
    }
    
    public boolean fileWrite(){
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
    
    public void setFilename(BankAccount account) {
        if (account.isAdmin())
            filename = "statement_admin_" + FormattingUtilities.getFormattedCurrentDate() + ".txt";
        else{
            filename = "statement_user_" + account.getUsername() + "_" + FormattingUtilities.getFormattedCurrentDate() + ".txt";
        }
            
    }
    
    public void appendToBuffer(String transaction){
        buffer.append(transaction);
        buffer.append("\n");
    }
    
}
