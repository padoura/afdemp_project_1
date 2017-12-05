/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public class FileController {
    
    private File file;
    
    public FileController(String filename){
        file = new File(filename);
    }
    
    public void setFile(String filename){
        file = new File(filename);
    }
    
    public void fileWrite(String transaction){
        try {
            file.createNewFile();
        } catch (IOException ex) {
        
        }

        FileWriter writer = null; 
        try {
            writer = new FileWriter(file);
        } catch (IOException ex) {
            
        }

        try {
            writer.write(transaction);
        } catch (IOException ex) {
           
        }
        
        try {
            writer.flush();
        } catch (IOException ex) {
            
        }
        
        try {
            writer.close();
        } catch (IOException ex) {
           
        }
    }
    
}
