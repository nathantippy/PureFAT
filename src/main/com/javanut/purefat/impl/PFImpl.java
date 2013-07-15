package com.javanut.purefat.impl;

import com.javanut.purefat.FATTemplate;

public interface PFImpl {

    final String     LABEL_WRAP = "{}";
    final int        MAX_PARAMS = 7;

    void flush(Number number);

    //TODO:  auditIsTightRadian()  +- pi only
    //TODO:  auditIsPositiveRadian() 0 to 2PI    

    void auditIsFinite(Number number);

    void auditIsGT(Number number, Number lt);

    void auditIsGTE(Number number, Number lt);

    void auditIsLT(Number number, Number lt);

    void auditIsLTE(Number number, Number lte);

    void auditIsNear(Number number, Number near, double epsilon);

    void auditIsNotZero(Number number);

    void auditIsPositive(Number number);

    void logAuditTrail(Number keyNumber, FATTemplate format);

    void audit(Number value, String label);

    void audit(Number value, String label, String expressionText,
            Number p1);

    void audit(Number value, String label, String expressionText,
            Number p1, Number p2);

    void audit(Number value, String label, String expressionText,
            Number p1, Number p2, Number p3);

    void audit(Number value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4);

    void audit(Number value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5);

    void audit(Number value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6);

    void audit(Number value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7);

    void audit(Number value, String label, String expressionText,
            Number[] params);
    


}