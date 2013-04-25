package com.javanut.purefat;

public class MetaFunction {

    private final StackTraceElement callInfo;
    
    public MetaFunction(StackTraceElement[] stackTrace) {
        int i = 0;
        while (++i<stackTrace.length) {
            if (!stackTrace[i].getClassName().contains("purefat")) {
                callInfo = stackTrace[i];
                return;
            }
        }
        this.callInfo = stackTrace[stackTrace.length-1];
    }
    
    public String stackElement() {
        return callInfo.toString();
    }

}
