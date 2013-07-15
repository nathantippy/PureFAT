package com.javanut.purefat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javanut.purefat.impl.DualAuditTrail;
import com.javanut.purefat.impl.FunctionAuditTrail;
import com.javanut.purefat.impl.PFDefault;
import com.javanut.purefat.impl.PFImpl;
import com.javanut.purefat.impl.PFNone;
import com.javanut.purefat.impl.PFVerbose;
import com.javanut.purefat.impl.FunctionAuditTrailInternal;
import com.javanut.purefat.impl.FunctionAuditTrailExternal;

public class PureFAT {
    
    //System property to prevent any storage or checking
    private final static String PUREFAT_NONE_KEY = "purefat.none";
    
    //System property to select internal (in memory) setup
    private final static String PUREFAT_INTERNAL_KEY = "purefat.internal";
    
    //System property to select external (log file) setup
    private final static String PUREFAT_EXTERNAL_KEY = "purefat.external";
    
    //System property to select verbose logging of errors without assertions
    private final static String PUREFAT_VERBOSE_KEY = "purefat.verbose";
    
    
    //package protected because this is the logger used for writing all the 
    //expressions. Writing expressions externally requires debug level.
    static final Logger logger = LoggerFactory.getLogger(PureFAT.class);
    private static final PFImpl pf = chooseImpl();
    public static boolean isDebugEnabled = logger.isDebugEnabled();
    

    private static PFImpl chooseImpl() {
        
        ///////////////
        //do nothing
        //in memory
        //external log
        //both
        /////////////
        
        if (System.getProperties().containsKey(PUREFAT_NONE_KEY)) {
            return new PFNone();
        } 
        
        FunctionAuditTrail fat;
        boolean isInternal = System.getProperties().containsKey(PUREFAT_INTERNAL_KEY);
        boolean isExternal = System.getProperties().containsKey(PUREFAT_EXTERNAL_KEY);
        
        if (isInternal) {
            if (isExternal) {
                fat = new DualAuditTrail(new FunctionAuditTrailInternal(),new FunctionAuditTrailExternal(logger));
            } else {
                fat = new FunctionAuditTrailInternal();
            }
        } else {
            if (isExternal) {
                fat = new FunctionAuditTrailExternal(logger);
            } else {
                //if nothing is set use both (default)
                fat = new DualAuditTrail(new FunctionAuditTrailInternal(),new FunctionAuditTrailExternal(logger));
            }
        }
        
        ////////////////
        //always on OR use assert
        ///////////////
        if (System.getProperties().containsKey(PUREFAT_VERBOSE_KEY)) {
            return new PFVerbose(fat);
        }
        return new PFDefault(fat);
    }

    /**
     * Dump from RAM before growing array or using GC even when still in use.
     * 
     * @param number
     */
    public static final void flush(Number number) {
        pf.flush(number);
    }
    
    public static final void auditIsFinite(Number number) {
        pf.auditIsFinite(number);
    }
    
    public static final void auditIsGT(Number number,Number gt) {
        pf.auditIsGT(number, gt);
    }
    
    public static final void auditIsGTE(Number number,Number gte) {
        pf.auditIsGTE(number, gte);
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

    public static final void auditIsNotZero(Number number) {
        pf.auditIsNotZero(number);
    }
    
    public static final void auditIsPositive(Number number) {
        pf.auditIsPositive(number);
    }
    
    public static final void logAuditTrail(Number keyNumber, FATTemplate format) {
        pf.logAuditTrail(keyNumber, format);
    }
    
    public static final Double audit(double value, String label) {
        Double boxed = new Double(value);
        pf.audit(boxed, label);
        return boxed;
    }

    public static final Integer audit(int value, String label) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1, p2);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3, p4);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1, p2 ,p3, p4, p5);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3, p4, p5, p6);
        return boxed;
    }

    public static final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3, p4, p5, p6, p7);
        return boxed;
    }
    
    public static final Double audit(double value, String label, String expressionText, Number[] params) {
        Double boxed = new Double(value);
        pf.audit(boxed, label, expressionText, params);
        return boxed;
    }
    
    public static final Integer audit(int value, String label, String expressionText, Number p1) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1);
        return boxed;
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1, p2);
        return boxed;
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3);
        return boxed;
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3, p4);
        return boxed;
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1, p2 ,p3, p4, p5);
        return boxed;
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3, p4, p5, p6);
        return boxed;
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, p1, p2, p3, p4, p5, p6, p7);
        return boxed;
    }
    
    public static final Integer audit(int value, String label, String expressionText, Number[] params) {
        Integer boxed = new Integer(value);
        pf.audit(boxed, label, expressionText, params);
        return boxed;
    }
    
    //TODO: add Number for each of these
    
}
