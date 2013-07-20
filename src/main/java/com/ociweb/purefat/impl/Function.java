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

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.MessageFormatter;


public class Function {

    private static final Logger     logger = LoggerFactory.getLogger(Function.class);
    
    //May be many millions of these objects so we must
    //only keep data if it can't be computed any other way.
    private String                  text;
    private String                  label;
    private byte                    paramCount;
    private final Number[]         params; //content is mutable
    private final int              privateIdx;

    Function(int idx) {
        privateIdx = idx;
        params = new Number[PFImpl.MAX_PARAMS];
    }
    
    public Function(Number undef) {
        //missing value
        privateIdx = -1;
        params = new Number[]{undef};
        label = "undefined";
        text = PFImpl.LABEL_WRAP;
    }

    

    
    public final boolean init(String label, 
                                String expressionText) {
        this.paramCount = 0;
        this.params[0] = null;
        this.params[1] = null;
        this.params[2] = null;
        this.params[3] = null;
        this.params[4] = null;
        this.params[5] = null;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    public final boolean init(String label, 
                                String expressionText,
                                Number[] paramArray) {
        this.paramCount = (byte) paramArray.length;
        System.arraycopy(paramArray, 0, params, 0, paramArray.length);
        this.label = label;
        this.text = expressionText;
        return true;
    }

    
    public final boolean init(String label, String expressionText,
                                Number p0) {
        this.paramCount = 1;
        this.params[0] = p0;
        this.params[1] = null;
        this.params[2] = null;
        this.params[3] = null;
        this.params[4] = null;
        this.params[5] = null;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }


    
    public final boolean init(String label, String expressionText,
                                Number p0,Number p1) {
        this.paramCount = 2;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = null;
        this.params[3] = null;
        this.params[4] = null;
        this.params[5] = null;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    public final boolean init(String label, String expressionText,
                                Number p0,Number p1,Number p2) {
        this.paramCount = 3;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = null;
        this.params[4] = null;
        this.params[5] = null;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    public final boolean init(String label, String expressionText,
                                Number p0,Number p1,Number p2,Number p3) {
        this.paramCount = 4;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = null;
        this.params[5] = null;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    public final boolean init(String label, String expressionText,
                                Number p0,Number p1,Number p2,Number p3,Number p4) {
        this.paramCount = 5;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = null;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    public final boolean init(String label, String expressionText,
                                Number p0,Number p1,Number p2,Number p3,Number p4,Number p5) {
        this.paramCount = 6;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = null;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    public final boolean init(String label, String expressionText,
                                Number p0,Number p1,Number p2,Number p3,Number p4,Number p5,Number p6) {
        this.paramCount = 7;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = p6;
        
        this.label = label;
        this.text = expressionText;
        return true;
    }
    
    
    public final Number[] params() {
        return Arrays.copyOf(params, paramCount);
    }
    
    public String toString() {
        return MessageFormatter.arrayFormat(text, params()).getMessage();
    }

    public String text() {
        return text;
    }

    public void log(String label, Logger logger) {
        logger.info(label+' '+text, params);
    }

    public String labelTag() {
        return "${"+label+"}";
    }

    public boolean isLabel() {
        return text==PFImpl.LABEL_WRAP;//special instance just for constant labels
    }


    public int getPrivateIndex() {
        return privateIdx;
    }

    public String labelName() {
       return label;
    }



}
