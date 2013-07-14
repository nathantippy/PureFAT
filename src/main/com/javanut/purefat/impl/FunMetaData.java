package com.javanut.purefat.impl;

public class FunMetaData {

    static FunMetaData NONE = new FunMetaData(new StackTraceElement[]{});
    private final StackTraceElement callInfo;

    FunMetaData(StackTraceElement[] stackTrace) {
        int i = 0;
        while (++i<stackTrace.length) {
            if (!stackTrace[i].getClassName().contains("purefat.impl") &&
                !stackTrace[i].getClassName().contains("purefat.PureFAT")) {
                this.callInfo = stackTrace[i];
                return;
            }
        }
        
        this.callInfo = new StackTraceElement("Unknown","Unknown","Unknown",0);
    }
    
    public String stackElement() {
        return callInfo.toString();
    }

}
