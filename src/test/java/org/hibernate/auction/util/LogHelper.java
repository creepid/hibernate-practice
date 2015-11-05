/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hibernate.auction.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author rusakovich
 */
public class LogHelper {
    
    private LogHelper() {
    }
    
    public static void disableLogging(String... exceptions) {
        List<String> exceptionList = Arrays.asList(exceptions);
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            if (!exceptionList.contains(logger.getName())) {
                logger.setLevel(Level.OFF);
            }
        }
        
    }
    
    public static void enableLogging(Level level) {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(level);
        }
    }
    
    public static void setLogging(String loggerName, Level level) {
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            if (logger.getName().equals(loggerName)) {
                logger.setLevel(level);
            }
        }
    }
    
}
