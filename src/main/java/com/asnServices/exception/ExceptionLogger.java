package com.asnServices.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExceptionLogger {
    private static final Logger exceptionLog = LoggerFactory.getLogger("ExceptionLog"); // Replace "ExceptionLog" with your desired log name

    public static void logException(Exception e, String logID) {
        // You can customize the log format and content based on your needs
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Exception occurred with log ID: ").append(logID).append("\n");
        logMessage.append("Exception message: ").append(e.getMessage()).append("\n");

        // Log the entire stack trace
        logMessage.append("Stack trace:\n");
        for (StackTraceElement element : e.getStackTrace()) {
            logMessage.append("\t").append(element.toString()).append("\n");
        }

        // Log the exception message and stack trace
        exceptionLog.error(logMessage.toString(), e);
    }
}

