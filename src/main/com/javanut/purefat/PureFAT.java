package com.javanut.purefat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PureFAT {
    
    private final static String PUREFAT_NONE_KEY = "purefat.none";
    private final static String PUREFAT_VERBOSE_KEY = "purefat.verbose";
    
    //package protected because this is the logger used for writing all the 
    //expressions. Writing expressions externally requires debug level.
    static final Logger logger = LoggerFactory.getLogger(PureFAT.class);
    private static final PFImpl pf = chooseImpl();
    public static boolean isDebugEnabled = logger.isDebugEnabled();
    

    private static PFImpl chooseImpl() {
        if (System.getProperties().containsKey(PUREFAT_VERBOSE_KEY)) {
            return new PFVerbose();
        }
        if (System.getProperties().containsKey(PUREFAT_NONE_KEY)) {
            return new PFNone();
        }
        return new PFDefault();
    }

    /**
     * Dump from RAM before growing array or using GC even when still in use.
     * 
     * @param number
     */
    public static final void dispose(Number number) {
        pf.dispose(number);
    }
    
    //auditIsTightRadian()  +- pi only
    //auditIsPositiveRadian() 0 to 2PI
    //auditIsLooseRadian() -pi to 2PI  //bad idea?
    
    public static final void auditIsFinite(Number number) {
        pf.auditIsFinite(number);
    }
    
    public static final void auditIsFinite(Number number,String label) {
        pf.auditIsFinite(number, label);
    }
    
    public static final void auditIsGT(Number number,Number lt) {
        pf.auditIsGT(number, lt);
    }
    
    public static final void auditIsGTE(Number number,Number lt) {
        pf.auditIsGTE(number, lt);
    }
    
    public static final void auditIsLT(Number number,Number lt) {
        pf.auditIsLT(number, lt);
    }
    
    public static final void auditIsLTE(Number number,Number lte) {
        pf.auditIsLTE(number, lte);
    }
    
    public static final void auditIsNear(Number number, Number near, double epsilon) {
        pf.auditIsNear(number, near, epsilon);
    }

    public static final void auditIsNotZero(Number number,String label) {
        pf.auditIsNotZero(number, label);
    }
    
    public static final void auditIsPositive(Number number,String label) {
        pf.auditIsPositive(number, label);
    }
    
    public static final void logAuditTrail(Number keyNumber, FATFormat format) {
        pf.logAuditTrail(keyNumber, format);
    }
    
    public static final Double audit(double value, String label) {
        return pf.audit(value, label);
    }

    public static final Integer audit(int value, String label) {
        return pf.audit(value, label);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1) {
        return pf.audit(value, label, expressionText, p1);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2) {
        return pf.audit(value, label, expressionText, p1, p2);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3) {
        return pf.audit(value, label, expressionText, p1, p2, p3);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        return pf.audit(value, label, expressionText, p1, p2, p3, p4);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        return pf.audit(value, label, expressionText, p1, p2 ,p3, p4, p5);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        return pf.audit(value, label, expressionText, p1, p2, p3, p4, p5, p6);
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        return pf.audit(value, label, expressionText, p1, p2, p3, p4, p5, p6, p7);
    }
    
    public static final Double audit(double value, String label, String expressionText, Number[] params) {
        return pf.audit(value, label, expressionText, params);
    }
}
