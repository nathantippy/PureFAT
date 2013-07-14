package com.javanut.purefat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javanut.purefat.FATReport;

public class PFVerbose implements PFImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(PFVerbose.class);
    private final FunctionAuditTrail auditTrail;

    public PFVerbose(FunctionAuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#dispose(java.lang.Number)
     */
    @Override
    public final void flush(Number number) {
        auditTrail.flush(number);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsFinite(java.lang.Number)
     */
    @Override
    public final void auditIsFinite(Number number) {
        Constraint.isFinite(number);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsFinite(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsFinite(Number number,String label) {
        Constraint.isFinite(number, label);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsGT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGT(Number number,Number lt) {
        Constraint.isGT(number,lt);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsGTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGTE(Number number,Number lt) {
        Constraint.isGTE(number,lt);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsLT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLT(Number number,Number lt) {
        Constraint.isLT(number,lt);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsLTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLTE(Number number,Number lte) {
        Constraint.isLTE(number, lte);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.Number, double)
     */
    @Override
    public final void auditIsNear(Number number, Number near, double epsilon) {
        Constraint.isNear(number, near, epsilon);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsNotZero(Number number,String label) {
        Constraint.isNotZero(number,label);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsPositive(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsPositive(Number number,String label) {
        Constraint.isPositive(number,label);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#logAuditTrail(java.lang.Number, com.javanut.purefat.FATFormat)
     */
    @Override
    public final void logAuditTrail(Number keyNumber, FATReport format) {
        format.log(logger, auditTrail, keyNumber, Thread.currentThread().getStackTrace());
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String)
     */
    @Override
    public final Double audit(double value, String label) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,LABEL_WRAP, boxed);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String)
     */
    @Override
    public final Integer audit(int value, String label) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,LABEL_WRAP, boxed);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1, p2);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4, p5);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4, p5, p6);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4, p5, p6, p7);
        return boxed;
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number[] params) {
        Double boxed = new Double(value);
        auditTrail.save(boxed,label,expressionText, params);
        return boxed;
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1, Number p2) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1, p2);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4, p5);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4, p5, p6);
        return boxed;
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, p1, p2, p3, p4, p5, p6, p7);
        return boxed;
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final Integer audit(int value, String label, String expressionText, Number[] params) {
        Integer boxed = new Integer(value);
        auditTrail.save(boxed,label,expressionText, params);
        return boxed;
    }
}
