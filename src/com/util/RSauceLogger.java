package com.util;
/**
 * Utility logging and printing class for RSauce
 */
public class RSauceLogger {

    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    public static void print(Object o){
        System.out.print(o);
    }

    public static void println(Object o){
        System.out.println(o);
    }

    public static void printWarning(Object o){
        System.out.print(ANSI_YELLOW + "WARNING: " + o);
    }

    public static void printWarningln(Object o){
        System.out.println(ANSI_YELLOW + "WARNING: " + o);
    }

    public static void printError(Object o){
        System.out.print(ANSI_RED + "ERROR: " + o);
    }

    public static void printErrorln(Object o){
        System.out.println(ANSI_RED + "ERROR: " + o);
    }
}
