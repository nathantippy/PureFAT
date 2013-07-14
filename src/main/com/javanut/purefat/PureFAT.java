package com.javanut.purefat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javanut.purefat.impl.DualAuditTrail;
import com.javanut.purefat.impl.FunctionAuditTrail;
import com.javanut.purefat.impl.PFDefault;
import com.javanut.purefat.impl.PFImpl;
import com.javanut.purefat.impl.PFNone;
import com.javanut.purefat.impl.PFVerbose;
import com.javanut.purefat.impl.RingBufferAuditTrail;
import com.javanut.purefat.impl.SLF4JAuditTrail;

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
                fat = new DualAuditTrail(new RingBufferAuditTrail(),new SLF4JAuditTrail(logger));
            } else {
                fat = new RingBufferAuditTrail();
            }
        } else {
            if (isExternal) {
                fat = new SLF4JAuditTrail(logger);
            } else {
                //if nothing is set use both (default)
                fat = new DualAuditTrail(new RingBufferAuditTrail(),new SLF4JAuditTrail(logger));
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
    
    public static final void auditIsFinite(Number number,String label) {
        pf.auditIsFinite(number, label);
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

    public static final void auditIsNotZero(Number number,String label) {
        pf.auditIsNotZero(number, label);
    }
    
    public static final void auditIsPositive(Number number,String label) {
        pf.auditIsPositive(number, label);
    }
    
    public static final void logAuditTrail(Number keyNumber, FATReport format) {
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
    
    public static final Integer audit(int value, String label, String expressionText, Number p1) {
        return pf.audit(value, label, expressionText, p1);
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2) {
        return pf.audit(value, label, expressionText, p1, p2);
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3) {
        return pf.audit(value, label, expressionText, p1, p2, p3);
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        return pf.audit(value, label, expressionText, p1, p2, p3, p4);
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        return pf.audit(value, label, expressionText, p1, p2 ,p3, p4, p5);
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        return pf.audit(value, label, expressionText, p1, p2, p3, p4, p5, p6);
    }

    public static final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        return pf.audit(value, label, expressionText, p1, p2, p3, p4, p5, p6, p7);
    }
    
    public static final Integer audit(int value, String label, String expressionText, Number[] params) {
        return pf.audit(value, label, expressionText, params);
    }
    //TODO: perhaps object creation and wrap should be here because it gets duplicated in pf.
}
