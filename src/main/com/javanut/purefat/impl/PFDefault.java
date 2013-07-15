package com.javanut.purefat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javanut.purefat.FATTemplate;

public class PFDefault implements PFImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(PFDefault.class);
    private final FunctionAuditTrail auditTrail;

    public PFDefault(FunctionAuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#dispose(java.lang.Number)
     */
    @Override
    public final void flush(Number number) {
        assert(auditTrail.flush(number));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsFinite(java.lang.Number)
     */
    @Override
    public final void auditIsFinite(Number number) {
        assert(Constraint.isFinite(number));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsGT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGT(Number number,Number lt) {
        assert(Constraint.isGT(number,lt));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsGTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGTE(Number number,Number lt) {
        assert(Constraint.isGTE(number,lt));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsLT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLT(Number number,Number lt) {
        assert(Constraint.isLT(number,lt));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsLTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLTE(Number number,Number lte) {
        assert(Constraint.isLTE(number, lte));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.Number, double)
     */
    @Override
    public final void auditIsNear(Number number, Number near, double epsilon) {
        assert(Constraint.isNear(number, near, epsilon));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsNotZero(Number number) {
        assert(Constraint.isNotZero(number));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsPositive(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsPositive(Number number) {
        assert(Constraint.isPositive(number));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#logAuditTrail(java.lang.Number, com.javanut.purefat.FATFormat)
     */
    @Override
    public final void logAuditTrail(Number keyNumber, FATTemplate format) {
        assert(format.log(logger, auditTrail, keyNumber, Thread.currentThread().getStackTrace()));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String)
     */
    @Override
    public final void audit(Number value, String label) {
        assert(auditTrail.save(value,label,LABEL_WRAP,value));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1) {
        assert(auditTrail.save(value,label,expressionText,p1));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2) {
        assert(auditTrail.save(value,label,expressionText, p1,p2));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3) {
        assert(auditTrail.save(value,label,expressionText, p1,p2,p3));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        assert(auditTrail.save(value,label,expressionText, p1,p2,p3,p4));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        assert(auditTrail.save(value,label,expressionText, p1,p2,p3,p4,p5));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        assert(auditTrail.save(value,label,expressionText, p1,p2,p3,p4,p5,p6));
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        assert(auditTrail.save(value,label,expressionText, p1,p2,p3,p4,p5,p6,p7));
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number[] params) {
        assert(auditTrail.save(value,label,expressionText, params));
    }
    
}
