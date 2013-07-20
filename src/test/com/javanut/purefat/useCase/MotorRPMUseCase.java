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
package com.javanut.purefat.useCase;

import static com.javanut.purefat.PureFAT.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.javanut.purefat.FATTemplate;

public class MotorRPMUseCase implements ExampleUseCase {

    //one half of motor shaft is white and the other black.
    
    private final int samplesCount = 100000;
    private final Integer samplesPerSecond = audit(1024,"samplesPerSecond");
    private final Integer samplesPerMinute = audit(60*samplesPerSecond,"samplesPerMinute","60*{}",samplesPerSecond);
    private final boolean testBrokenCode;
    private final int shaftSampleBits = 7;//for generating faux samples

    Number last = audit(-1,"unknown");
    Integer count = audit(0,"initial");
    Double rpm = audit(0d,"initial");

    public MotorRPMUseCase(boolean testBrokenCode) {
        this.testBrokenCode = testBrokenCode;
    }

    @Override
    public Iterator<Number> samples() {
        //TODO: after other examples find way to extract iterator?
        return new Iterator<Number>() {
            int countRemaining = samplesCount;
            
            @Override
            public boolean hasNext() {
                return countRemaining>0;
            }

            @Override
            public Number next() {
                if (--countRemaining<0) {
                    throw new NoSuchElementException();
                }
                //in this example 1 is white and 0 is black
                //this makes 128 (1 or 0) in a row before switching.
                Integer result = audit(((samplesCount-countRemaining)>>7)%2,"shaftSample");
                
//                continueAuditTo("RawData", result);
//                //TODO: need lookup to work with this? put in another test!
//                Integer newResult = continueAuditFrom("RawData", result.intValue());
                
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }};
        
    }

    @Override
    public int samplesCount() {
        return samplesCount;
    }
    
    @Override
    public Number computeResult(Number sample) {

        if (last.equals(sample)) {
            count = audit(count+1,"count","({}+1)",count);
        } else {
            //must not cause divide by zero error upon start up.
            if (count>0 || testBrokenCode) {
                Integer samplesPerRevolution = audit(count*2,"samplesPerRevolution","({}*2)",count);
                rpm = audit(samplesPerMinute/(double)samplesPerRevolution,"rpm","({}/{})",samplesPerMinute,samplesPerRevolution);
            }
            //reset count
            last = sample;
            count = audit(1,"first");
        }

        return rpm;
    }

    @Override
    public void validatResult(Number result) {

        //these are disasters causing a throw.
        auditIsFinite(result);
        auditIsGTE(result, 0);
        
        //these checks are just for quality and get logged.
        if (result.doubleValue()>0 && result.doubleValue()>=242) {
            //   logAuditTrail(result, FATTemplate.table);
            //   logAuditTrail(result, FATTemplate.expression);
            logAuditTrail(result, FATTemplate.summary);
        }

    }

    @Override
    public boolean isFailureExpected(int index) {
        if (testBrokenCode) {
            //first go around on the shaft will report an error with the broken code
            return index<((1<<shaftSampleBits)-1);
        }
        return false;
    }

}
