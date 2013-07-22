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
package com.ociweb.purefat.useCase;

import static com.ociweb.purefat.PureFAT.audit;
import static com.ociweb.purefat.PureFAT.auditIsFinite;
import static com.ociweb.purefat.PureFAT.auditIsGTE;
import static com.ociweb.purefat.PureFAT.auditIsLTE;
import static com.ociweb.purefat.PureFAT.continueAuditFrom;
import static com.ociweb.purefat.PureFAT.logAuditTrail;

import java.util.HashSet;
import java.util.Set;

import com.ociweb.purefat.FATTemplate;
import com.ociweb.purefat.useCase.foundation.AbstractPureFATUseCase;
import com.ociweb.purefat.useCase.foundation.ExpectedFailureCatalog;

/**
 * Class demonstrating how to use PureFAT in production.
 * 
 * All the unrelated code has been hidden in the abstract class except for
 * validateFailureCatalog which was needed for testing.
 */
public class MotorRPMUseCase extends AbstractPureFATUseCase<Number, Number> {

    // one half of motor shaft is white and the other black.
    // as the motor spins a sampler will sense the color at 1024 times a second.
    // black is mapped to 0 and white is mapped to 1
    // the RPMs for this test is expected to be 240

    private final Integer samplesPerSecond = audit(1024, "samplesPerSecond");
    private final Integer samplesPerMinute = audit(60 * samplesPerSecond,
            "samplesPerMinute", "60*{}", samplesPerSecond);
    private final int shaftSampleBits = 7;// for generating faux samples

    Set<Number> toInvestigate = new HashSet<Number>();

    Number last = audit(-1, "unknown");
    Integer count = audit(0, "initial");
    Double rpm = audit(0d, "initial");

    public MotorRPMUseCase(boolean testBrokenCode) {
        super(testBrokenCode);
    }

    @Override
    protected Number simulatedSample(int idx) {
        // in this example 1 is white and 0 is black
        // this makes 128 (1 or 0) in a row before switching.
        return continueAuditFrom("shaftSample", (idx >> 7) % 2);
    }

    @Override
    protected Number simulateCompute(int idx, Number sample) {
        auditIsGTE(sample, 0);
        auditIsLTE(sample, 1);
        if (last.equals(sample)) {
            count = audit(count + 1, "count", "({}+1)", count);
        } else {
            // must not cause divide by zero error upon start up.
            // but DO allow it when we are testing the broken code case
            if (count > 0 || testBrokenCode) {
                Integer samplesPerRevolution = audit(count * 2,
                        "samplesPerRevolution", "({}*2)", count);
                rpm = audit(samplesPerMinute / (double) samplesPerRevolution,
                        "rpm", "({}/{})", samplesPerMinute,
                        samplesPerRevolution);
            }
            // reset count
            last = sample;
            count = audit(1, "first");
        }

        return rpm;
    }

    @Override
    public void simulateValidate(int idx, Number result) {

        // these are disasters causing a detailed table log and a throw.
        auditIsFinite(result);
        auditIsGTE(result, 0);

        // these checks are just for quality and get logged.
        // in this case we are investigating why the RPMs are slightly high
        // at times. Keeping each unique value in a set may help to reduce
        // the number of situations requiring investigation, YMMV.
        if (result.doubleValue() > 241 && !toInvestigate.contains(result)) {
            toInvestigate.add(result);
            // log a stack of the functions with a count of how many times each
            // was called for this value.
            logAuditTrail(result, FATTemplate.summary);
            // log the entire human readable expression in one line
            logAuditTrail(result, FATTemplate.expression);
        }
    }

    @Override
    public ExpectedFailureCatalog validateFailureCatalog() {
        return new ExpectedFailureCatalog() {

            @Override
            public boolean isFailureExpected(int index) {

                if (testBrokenCode) {
                    // first go around on the shaft will report an error with
                    // the broken code
                    return index < ((1 << shaftSampleBits) - 1);
                }
                return false;
            }

        };
    }

}
