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
package com.javanut.purefat.impl;


public interface FunctionAuditTrail {

    public boolean flush(Number key);
    
    public Function get(Number key);
    
    public Function get(Number key, Function startHere);
    
    public FunMetaData metaData(Function fun);
    
    public boolean save(Number number, String label, String expression,
            Number p1);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6);

    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6,
            Number p7);

    public boolean save(Number number, String label, String expression,
            Number[] params);

    public boolean continueAuditTo(String channelId, Number boxed);

    public boolean continueAuditFrom(String channelId, Number boxed);

}