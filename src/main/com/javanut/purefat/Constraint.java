package com.javanut.purefat;

import static com.javanut.purefat.PureFAT.*;

public class Constraint {

    private final Constraint next;
    private final String label;
    
    public Constraint(String label) {
        this.next = null;
        this.label = label;
    }
    
    protected Constraint(Constraint next, String label) {
        this.next = next;
        this.label = label;
    }
    
    public Constraint gteZero() {
        return new Constraint(this,"Must be zero or positive") {
            @Override
            boolean validate(Number boxed) {
                if (boxed.doubleValue()<0d) {
                    logExpressionTree(boxed, label()+' '+label);
                    return false;
                }
                return next.validate(boxed);
            }
            @Override 
            String label() {
                return next.label();
            }
        };
    }
    
    public Constraint lteZero() {
        return new Constraint(this, "Must be zero or negative") {
            @Override
            boolean validate(Number boxed) {
                if (boxed.doubleValue()>0d) {
                    logExpressionTree(boxed, label()+' '+label);
                    return false;
                }
                return next.validate(boxed);
            }
            @Override 
            String label() {
                return next.label();
            }
        };
    }
    
    public Constraint gtZero() {
        return new Constraint(this,"Must be positive") {
            @Override
            boolean validate(Number boxed) {
                if (boxed.doubleValue()<=0d) {
                    logExpressionTree(boxed, label()+' '+label);
                    return false;
                }
                return next.validate(boxed);
            }
            @Override 
            String label() {
                return next.label();
            }
        };
    }
    
    public Constraint ltZero() {
        return new Constraint(this, "Must be negative") {
            @Override
            boolean validate(Number boxed) {
                if (boxed.doubleValue()>=0d) {
                    logExpressionTree(boxed, label()+' '+label);
                    return false;
                }
                return next.validate(boxed);
            }
            @Override 
            String label() {
                return next.label();
            }
        };
    }
    
    public Constraint isNumber() {
        return new Constraint(this,"Must be number") {
            @Override
            boolean validate(Number boxed) {
                if (Double.isNaN(boxed.doubleValue())) {
                    logExpressionTree(boxed, label()+' '+label);
                    return false;
                }
                return next.validate(boxed);
            }
            @Override 
            String label() {
                return next.label();
            }
        };
    }
    
    public Constraint isFinite() {
        return new Constraint(this,"Must be finite") {
            @Override
            boolean validate(Number boxed) {
                if (Double.isInfinite(boxed.doubleValue())) {
                    logExpressionTree(boxed, label()+' '+label);
                    return false;
                }
                return next.validate(boxed);
            }
            @Override 
            String label() {
                return next.label();
            }
        };
    }
    
    String label() {
        return label;
    }
    
    /**
     * override for custom range checking.
     * 
     * @param boxed
     * @return
     */
    
    boolean validate(Number boxed) {
        return true;
    }

}
