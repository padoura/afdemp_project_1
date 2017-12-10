/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.padoura.afdempproject1;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

/**
 *
 * @author padoura <padoura@users.noreply.github.com>
 */
public final class FormattingUtilities {

    protected static String getFormattedCurrency(BigDecimal amount) {
        Currency euro = Currency.getInstance("EUR");
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("el-GR"));
        format.setCurrency(euro);
        return format.format(amount);
    }

    static String getFormattedCurrency(int i) {
        return getFormattedCurrency(BigDecimal.valueOf(0));
    }

    private FormattingUtilities() {
    }
    
    protected static String getFormattedCurrentDate(){
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        return date.format(formatter);
    }
    
    protected static String getFormattedCurrentDateTime(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return dateTime.format(formatter);
    }
    
    protected static String formatTimestamp(Timestamp timestamp){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return timestamp.toLocalDateTime().format(formatter);
    } 
}
