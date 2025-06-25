package com.provana.utils;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> TL = new ThreadLocal<>();
    public static ExtentTest getTest() { return TL.get();}
    public static void setTest(ExtentTest t){ TL.set(t);}
    public static void removeTest(){ TL.remove();}
}