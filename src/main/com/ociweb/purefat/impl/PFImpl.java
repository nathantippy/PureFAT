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

import com.ociweb.purefat.FATTemplate;

public interface PFImpl {

    final String     LABEL_WRAP = "{}";
    final int        MAX_PARAMS = 7;

    void flush(Number number);

    void auditIsTightRadian(Number number); // only +- pi 
    
    void auditIsPositiveRadian(Number number); // 0 to 2pi

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

    void continueAuditTo(String channelId, Number boxed);

    void continueAuditFrom(String channelId, Number boxed);

}