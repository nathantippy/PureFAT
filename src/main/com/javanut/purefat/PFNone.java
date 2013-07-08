package com.javanut.purefat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PFNone implements PFImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(PFNone.class);
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#dispose(java.lang.Number)
     */
    @Override
    public final void dispose(Number number) {
    }

    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsFinite(java.lang.Number)
     */
    @Override
    public final void auditIsFinite(Number number) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsFinite(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsFinite(Number number,String label) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsGT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGT(Number number,Number lt) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsGTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGTE(Number number,Number lt) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsLT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLT(Number number,Number lt) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsLTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLTE(Number number,Number lte) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.Number, double)
     */
    @Override
    public final void auditIsNear(Number number, Number near, double epsilon) {
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsNotZero(Number number,String label) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#auditIsPositive(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsPositive(Number number,String label) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#logAuditTrail(java.lang.Number, com.javanut.purefat.FATFormat)
     */
    @Override
    public final void logAuditTrail(Number keyNumber, FATFormat format) {
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String)
     */
    @Override
    public final Double audit(double value, String label) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(int, java.lang.String)
     */
    @Override
    public final Integer audit(int value, String label) {
        return new Integer(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        return new Double(value);
    }

    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        return new Double(value);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final Double audit(double value, String label, String expressionText, Number[] params) {
        return new Double(value);
    }
}
