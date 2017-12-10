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

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public final class LoggerController {

    private static String logFilename;
    private static final Logger LOGGER = Logger.getLogger("BankAppLog"); 

    private LoggerController(){
    }
    
    protected static void setLogger(String logFilename){
        LoggerController.logFilename = logFilename;
        if (tryCreateNewFileIfNotExists())
            tryNewFileHandler(); // sets log file
    }
    
    private static boolean tryCreateNewFileIfNotExists(){
        File file  = new File(logFilename);
        if(!file.exists()){
            try {
                file.createNewFile();
                return true;
            } catch (IOException ex) {
                System.out.println("Error log file could not be created. Stacktrace will be printed in console.");
                return false;
            }
        }
        return true;
    }
    
    private static void tryNewFileHandler(){
        try {
            LOGGER.addHandler(new FileHandler(logFilename, true));
            LOGGER.setUseParentHandlers(false);
        } catch (IOException ex) {
            System.out.println("Error log file could not be created. Stacktrace will be printed in console.");
        } catch (SecurityException ex) {
            System.out.println("Error log file could not be created. Please check your user priviledges.\n"
                    + "Stacktrace will be printed in console.");
        }
    }

    protected static Logger getLogger() {
        return LOGGER;
    }
    
}
