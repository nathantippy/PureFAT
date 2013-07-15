package com.javanut.purefat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javanut.purefat.FATTemplate;

import static com.javanut.purefat.PureFAT.*;

public class Constraint {

    private static final Logger logger = LoggerFactory.getLogger(Constraint.class);
    
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
    
}