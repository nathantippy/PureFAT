package com.javanut.purefat.impl;

import java.rmi.dgc.VMID;

public class Util {
    
    private final static VMID instanceId = new VMID();
    
    private Util(){
    }
    
    public static final String wrapId(Number number) {
        return "[PF"+
                Long.toHexString(System.identityHashCode(number))+
                ']';
    }
    
    public static final String instanceId() {
        return "["+instanceId+"]";
    }
}
