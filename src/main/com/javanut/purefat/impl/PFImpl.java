package com.javanut.purefat.impl;

import com.javanut.purefat.FATReport;

public interface PFImpl {

    final String     LABEL_WRAP = "{}";
    final int        MAX_PARAMS = 7;

    void flush(Number number);

    //TODO:  auditIsTightRadian()  +- pi only
    //TODO:  auditIsPositiveRadian() 0 to 2PI    

    void auditIsFinite(Number number);

    void auditIsFinite(Number number, String label);

    void auditIsGT(Number number, Number lt);

    void auditIsGTE(Number number, Number lt);

    void auditIsLT(Number number, Number lt);

    void auditIsLTE(Number number, Number lte);

    void auditIsNear(Number number, Number near, double epsilon);

    void auditIsNotZero(Number number, String label);

    void auditIsPositive(Number number, String label);

    void logAuditTrail(Number keyNumber, FATReport format);

    Double audit(double value, String label);

    Integer audit(int value, String label);

    Double audit(double value, String label, String expressionText,
            Number p1);

    Double audit(double value, String label, String expressionText,
            Number p1, Number p2);

    Double audit(double value, String label, String expressionText,
            Number p1, Number p2, Number p3);

    Double audit(double value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4);

    Double audit(double value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5);

    Double audit(double value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6);

    Double audit(double value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7);

    Double audit(double value, String label, String expressionText,
            Number[] params);
    
    Integer audit(int value, String label, String expressionText,
            Number p1);

    Integer audit(int value, String label, String expressionText,
            Number p1, Number p2);

    Integer audit(int value, String label, String expressionText,
            Number p1, Number p2, Number p3);

    Integer audit(int value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4);

    Integer audit(int value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5);

    Integer audit(int value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6);

    Integer audit(int value, String label, String expressionText,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7);

    Integer audit(int value, String label, String expressionText,
            Number[] params);

}