package com.javanut.purefat.impl;


public interface FunctionAuditTrail {

    public boolean flush(Number key);
    
    public Function get(Number key);
    
    public Function get(Number key, Function startHere);
    
    public FunMetaData metaData(Function fun);
    
    public boolean save(Number number, String label, String expression,
            Number p1);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7);

    public boolean save(Number number, String label, String expression,
            Number[] params);

}