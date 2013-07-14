package com.javanut.purefat.impl;


public class DualAuditTrail implements FunctionAuditTrail {

    private final FunctionAuditTrail primary;
    private final FunctionAuditTrail secondary;
    
    public DualAuditTrail(FunctionAuditTrail primary, FunctionAuditTrail secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }
    
    @Override
    public boolean flush(Number key) {
        return primary.flush(key)&
                secondary.flush(key);
    }

    @Override
    public Function get(Number key) {
        return primary.get(key);
    }

    @Override
    public Function get(Number key, Function startHere) {
        return primary.get(key,startHere);
    }

    @Override
    public FunMetaData metaData(Function fun) {
        return primary.metaData(fun);
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1) {
        return save(number,label,expression,new Number[]{p1});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2) {
        return save(number,label,expression,new Number[]{p1,p2});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3) {
        return save(number,label,expression,new Number[]{p1,p2,p3});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4,p5});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6,p7});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number[] params) {
        return primary.save(number,label,expression,params) & 
                secondary.save(number, label, expression, params);
    }

}
