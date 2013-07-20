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

import static com.ociweb.purefat.PureFAT.*;

public class Constraint {

    private static final Logger logger = LoggerFactory.getLogger(Constraint.class);
    private static final double TWO_PI = Math.PI*2d;
    
    private Constraint() {
    }

    final static boolean isNear(Number number, Number near, double epsilon) {
        if (Math.abs(number.doubleValue()-near.doubleValue())>epsilon) {
            logAuditTrail(number,FATTemplate.table);
            logger.error("{} not near {} epsilon"+epsilon,number,near);
            return false;
        }
        return true;
    }
    
    final static boolean isGTE(Number number, Number gte) {
        if (number.doubleValue()<gte.doubleValue()) {
            logAuditTrail(number,FATTemplate.table);
            logger.error("{} ! >= {}",number,gte);
            return false;
        }
        return true;
    }

    final static boolean isGT(Number number, Number gte) {
        if (number.doubleValue()<=gte.doubleValue()) {
            logAuditTrail(number,FATTemplate.table);
            logger.error("{} ! > {}",number,gte);
            return false;
        }
        return true;
    }
    
    final static boolean isLTE(Number number, Number lte) {
        if (number.doubleValue()>lte.doubleValue()) {
            logAuditTrail(number,FATTemplate.table);
            logger.error("{} ! <= {}",number,lte);
            return false;
        }
        return true;
    }

    final static boolean isLT(Number number, Number lte) {
        if (number.doubleValue()>=lte.doubleValue()) {
            logAuditTrail(number,FATTemplate.table);
            logger.error("{} ! < {}",number,lte);
            return false;
        }
        return true;
    }

    final static boolean isNotZero(Number number) {
        if (number.doubleValue()==0d) {
            logAuditTrail(number,FATTemplate.table);
            return false;
        }
        return true;
    }
    
    final static boolean isFinite(Number number) {
        if (null==number || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            logAuditTrail(number,FATTemplate.table);
            return false;
        }
        return true;
    }
    
    final static boolean isPositive(Number number) {
        if (null==number || number.doubleValue()<0 || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            logAuditTrail(number,FATTemplate.table);
            return false;
        }
        return true;
    }

    /*
     * Must be between + or - pi
     */
    public static boolean isTightRadian(Number number) {
        if (null==number || Double.isNaN(number.doubleValue()) || number.doubleValue()+Math.PI<0 || number.doubleValue()-Math.PI>0) {
            logAuditTrail(number,FATTemplate.table);
            return false;
        }
        return true;
    }

    /*
     * Must be between 0 or 2pi
     */
    public static boolean isPositiveRadian(Number number) {
        if (null==number || Double.isNaN(number.doubleValue()) || number.doubleValue()<0 || number.doubleValue()-TWO_PI>0) {
            logAuditTrail(number,FATTemplate.table);
            return false;
        }
        return true;
    }
    
}
