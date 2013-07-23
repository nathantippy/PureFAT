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

public class DualAuditTrail implements FunctionAuditTrail {

    private final FunctionAuditTrail primary;
    private final FunctionAuditTrail secondary;
    
    public DualAuditTrail(FunctionAuditTrail primary, FunctionAuditTrail secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public Function get(Number key) {
        return primary.get(key);
    }

    @Override
    public Function get(Number key, Function startHere) {
        return primary.get(key,startHere);
    }

    @Override
    public FunMetaData metaData(Function fun) {
        return primary.metaData(fun);
    }

    @Override
    public boolean save(Number number, String label, String expression) {
        return save(number,label,expression,new Number[]{});
    }
    
    @Override
    public boolean save(Number number, String label, String expression,
            Number p1) {
        return save(number,label,expression,new Number[]{p1});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2) {
        return save(number,label,expression,new Number[]{p1,p2});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3) {
        return save(number,label,expression,new Number[]{p1,p2,p3});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4,p5});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7) {
        return save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6,p7});
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number[] params) {
        return primary.save(number,label,expression,params) & 
                secondary.save(number, label, expression, params);
    }

    @Override
    public boolean continueAuditTo(String channelId, Number boxed) {
        primary.continueAuditTo(channelId, boxed);
        secondary.continueAuditTo(channelId, boxed);
        return true;
    }

    @Override
    public boolean continueAuditFrom(String channelId, Number boxed) {
        primary.continueAuditFrom(channelId, boxed);
        secondary.continueAuditFrom(channelId, boxed);
        return true;
    }

}
