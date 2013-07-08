package com.javanut.purefat;

import java.io.Serializable;

public class FatNumber extends Number implements Serializable{

    private final Number number;
    private final int id;
    
    public FatNumber(Number number) {
        this.number = number;
        this.id = System.identityHashCode(number);
    }
    
    @Override
    public int intValue() {
        return number.intValue();
    }

    @Override
    public long longValue() {
        return number.longValue();
    }

    @Override
    public float floatValue() {
        return number.floatValue();
    }

    @Override
    public double doubleValue() {
        return number.doubleValue();
    }

}
