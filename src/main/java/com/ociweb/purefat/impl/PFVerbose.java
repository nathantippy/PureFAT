/**
 * Copyright (c) 2013, Nathan Tippy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * @author  Nathan Tippy <tippyn@ociweb.com>
 * bitcoin:1NBzAoTTf1PZpYTn7WbXDTf17gddJHC8eY?amount=0.01&message=PFAT%20donation
 *
 */
package com.ociweb.purefat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ociweb.purefat.FATTemplate;

public class PFVerbose implements PFImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(PFVerbose.class);
    private final FunctionAuditTrail auditTrail;

    public PFVerbose(FunctionAuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsTightRadian(java.lang.Number)
     */
    @Override
    public void auditIsTightRadian(Number number) {
        if (!Constraint.isTightRadian(number)) {
            throw new FATConstraintViolation("not between +/- pi");
        }
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsPositiveRadian(java.lang.Number)
     */
    @Override
    public void auditIsPositiveRadian(Number number) {
        if (!Constraint.isPositiveRadian(number)) {
            throw new FATConstraintViolation("not between 0 and 2pi");
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsFinite(java.lang.Number)
     */
    @Override
    public final void auditIsFinite(Number number) {
        if (!Constraint.isFinite(number)) {
            throw new FATConstraintViolation("not finite"); 
        }
    }

    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsGT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGT(Number number,Number gt) {
        if (!Constraint.isGT(number,gt)) {
            throw new FATConstraintViolation("not greater than "+gt);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsGTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsGTE(Number number,Number gte) {
        if (!Constraint.isGTE(number,gte)) {
            throw new FATConstraintViolation("not greater than or equal "+gte);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsLT(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLT(Number number,Number lt) {
        if (!Constraint.isLT(number,lt)) {
            throw new FATConstraintViolation("not less than "+lt);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsLTE(java.lang.Number, java.lang.Number)
     */
    @Override
    public final void auditIsLTE(Number number,Number lte) {
        if (!Constraint.isLTE(number, lte)) {
            throw new FATConstraintViolation("not less than or equal "+lte);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.Number, double)
     */
    @Override
    public final void auditIsNear(Number number, Number near, double epsilon) {
        if (!Constraint.isNear(number, near, epsilon)) {
            throw new FATConstraintViolation("not within "+epsilon+" of "+near);
        }
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsNotZero(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsNotZero(Number number) {
        if (!Constraint.isNotZero(number)) {
            throw new FATConstraintViolation("is zero");
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#auditIsPositive(java.lang.Number, java.lang.String)
     */
    @Override
    public final void auditIsPositive(Number number) {
        if (!Constraint.isPositive(number)) {
            throw new FATConstraintViolation("not positive");
        }
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#logAuditTrail(java.lang.Number, com.ociweb.purefat.FATFormat)
     */
    @Override
    public final void logAuditTrail(Number keyNumber, FATTemplate format) {
        format.log(logger, auditTrail, keyNumber, Thread.currentThread().getStackTrace());
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String)
     */
    @Override
    public final void audit(Number value, String label) {
        auditTrail.save(value,label,LABEL_WRAP, value);
    }

    @Override
    public void audit(Number value, String label, String expressionText) {
        auditTrail.save(value,label,expressionText);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1) {
        auditTrail.save(value,label,expressionText, p1);
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2) {
        auditTrail.save(value,label,expressionText, p1, p2);
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3) {
        auditTrail.save(value,label,expressionText, p1, p2, p3);
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        auditTrail.save(value,label,expressionText, p1, p2, p3, p4);
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        auditTrail.save(value,label,expressionText, p1, p2, p3, p4, p5);
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        auditTrail.save(value,label,expressionText, p1, p2, p3, p4, p5, p6);
    }

    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        auditTrail.save(value,label,expressionText, p1, p2, p3, p4, p5, p6, p7);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.PFImpl#audit(double, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final void audit(Number value, String label, String expressionText, Number[] params) {
        auditTrail.save(value,label,expressionText, params);
    }

    @Override
    public void continueAuditTo(String channelId, Number boxed) {
        auditTrail.continueAuditTo(channelId, boxed);
    }

    @Override
    public void continueAuditFrom(String channelId, Number boxed) {
        auditTrail.continueAuditFrom(channelId, boxed);
    }
    
}
