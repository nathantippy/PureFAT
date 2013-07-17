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

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.MessageFormatter;

public class FunctionAuditTrailExternal implements FunctionAuditTrail {

    
    private final Logger pureFATLogger;
    
    public FunctionAuditTrailExternal(Logger logger) {
        pureFATLogger = logger;
    }
    
    @Override
    public boolean save(Number number, String label, String expression,
                         Number p1) {
        save(number,label,expression,new Number[]{p1});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2) {
        save(number,label,expression,new Number[]{p1,p2});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2, Number p3) {
        save(number,label,expression,new Number[]{p1,p2,p3});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2, Number p3, Number p4) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4,p5});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2, Number p3, Number p4,
                        Number p5, Number p6, Number p7) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6,p7});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number[] params) {
        
        StringBuilder sb = new StringBuilder(100);
        
        Marker marker = marker(label,expression);
        
        String id = wrapId(number);
        Marker idMarker = MarkerFactory.getMarker(id);
        marker.add(idMarker);
        
        String machineId = Util.instanceId();
        marker.add(MarkerFactory.getMarker(machineId));
        
        sb.append(id);
        wrapExpr(number, expression, sb);
        
        String[] pIdArray = new String[params.length];
        int i = 0;
        while (i<params.length) {
            String pId = wrapId(params[i]);
            pIdArray[i] = pId;
            Marker pIdMarker = MarkerFactory.getMarker(pId);
            idMarker.add(pIdMarker);
        }
        sb.append(" ");
        sb.append(MessageFormatter.arrayFormat(expression, pIdArray));
        
        pureFATLogger.debug(marker, sb.toString(),  params);
        
        return true;
    }
    
    
    private final void wrapExpr(Number number, String expression, StringBuilder sb) {
        sb.append(number).append('=').append(expression).append(' ');
    }

    private final String wrapId(Number number) {
        return "[PF"+Long.toHexString(System.identityHashCode(number))+']';
    }

    private Marker marker(String label, String expression) {
        return MarkerFactory.getMarker(label+':'+expression);
    }

    
    @Override
    public Function get(Number key) {
        // TODO: query logback by this key?
        return null;
    }

    @Override
    public Function get(Number key, Function startHere) {
        // TODO: query logback by this key?
        return null;
    }

    @Override
    public FunMetaData metaData(Function fun) {
        // TODO: query logback by this key? Get the line number data?
        return null;
    }

    @Override
    public boolean flush(Number key) {
        return true;
    }


}
