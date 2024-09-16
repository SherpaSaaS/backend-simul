package org.example.fmuWindows.utils;

public class DoubleComparator {

    private static double epsilon = 0.00000001d;
    public static int compare(Double a , Double b){
        if(Math.abs(a - b) <epsilon){
            return 0;
        }else{
            return a.compareTo(b);
        }
    }
}
