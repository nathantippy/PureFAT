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
package com.ociweb.purefat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static com.ociweb.purefat.PureFAT.*;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.ociweb.purefat.impl.FATConstraintViolation;
import com.ociweb.purefat.useCase.ExampleUseCase;
import com.ociweb.purefat.useCase.MotorRPMUseCase;

public class ExampleUsages {

    static {
        //Only use internal logging to speed up test.
//     (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PureFAT.class).setLevel(Level.TRACE);//TODO: hack to check on logger?
    //    System.setProperty("purefat.external", "true");
        
        //Do not use assertions, it makes testing inconsistent.
        System.setProperty("purefat.verbose", "true");

    }
    
    static final Logger logger = LoggerFactory.getLogger(ExampleUsages.class);
    
    @Test
    public void testMotorRPM() {
        simulateProdEnvironment(new MotorRPMUseCase(true));
        simulateProdEnvironment(new MotorRPMUseCase(false));
    }
    
    /**
     * Simulation uses threads for each step of the processing to demonstrate
     * that audit trails are still reproducible even after changing threads and
     * to demonstrate that PureFAT is thread safe.
     * 
     * @param useCase
     */
    private void simulateProdEnvironment(ExampleUseCase useCase) {
        int queueCapacity = 10;
        
        //simulated production hardware samples are written to this queue
        BlockingQueue<Number> sampleQueue = new ArrayBlockingQueue<Number>(queueCapacity);
        //simulated production infrastructure reads from sampleQueue and writes to this queue
        BlockingQueue<Number> reportQueue = new ArrayBlockingQueue<Number>(queueCapacity);
        //simulated production report generation reads from the report queue
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        //this thread will take samples from the hardware and put them on sampleQueue
        Future<?> f1 = executor.submit(productionSampler(useCase,sampleQueue));
        //this thread will take samples from sampleQueue do some computation and put the
        //result on the report queue
        Future<?> f2 = executor.submit(productionInfrastructure(useCase,sampleQueue,reportQueue));
        //this thread will take the results off the report queue and confirm the expected values.
        Future<?> f3 = executor.submit(productionReports(useCase,reportQueue));
        
        try {
            f1.get();
            f2.get();
            f3.get();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof Error) {
                throw (Error)ee.getCause();
            }
            ee.printStackTrace();
        }
        executor.shutdown();
    }

    private Runnable productionSampler(final ExampleUseCase useCase,
                                        final BlockingQueue<Number> sampleQueue) {
        
        return new Runnable() {
            @Override
            public void run() {
                Iterator<Number> samples = useCase.samples();
                while (samples.hasNext()) {
                    try {
                        Number next = samples.next();
                        sampleQueue.put(next);
                    } catch (FATConstraintViolation cv) {
                        logger.error("unexpected",cv);
                        fail("Unexpected exception");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            
        };
    }

    private Runnable productionInfrastructure(final ExampleUseCase useCase,
                                               final BlockingQueue<Number> sampleQueue, 
                                               final BlockingQueue<Number> reportQueue) {
        
        return new Runnable() {
            @Override
            public void run() {
                int count = useCase.samplesCount();
                while (--count>=0) {
                    try {
                        Number sample = sampleQueue.take();
                        Number result = useCase.computeResult(sample);
                        reportQueue.put(result);
                    } catch (FATConstraintViolation cv) {
                        logger.error("unexpected",cv);
                        fail("Unexpected exception");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            
        };
    }

    private Runnable productionReports(final ExampleUseCase useCase,
                                        final BlockingQueue<Number> reportQueue) {
        
        return new Runnable() {
            @Override
            public void run() {
                AssertionError assertionError = null;
                int remainingCount = useCase.samplesCount();
                int index = 0;
                while (--remainingCount>=0) {
                    try {
                        try {
                            Number result = reportQueue.take();
                            useCase.validatResult(result);
                            assertFalse("Failure expected at index "+index,
                                        useCase.isFailureExpected(index));
                        } catch (FATConstraintViolation cv) {
                            logger.error("isExpected:"+useCase.isFailureExpected(index), cv);
                            assertTrue("No falure expected at index "+index,
                                       useCase.isFailureExpected(index));
                            
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    } catch (AssertionError ae) {
                        //for thread management simplicity nothing is stopped
                        //when an error is detected. Instead we keep it for later.
                        if (null==assertionError) {
                            assertionError = ae;
                        } else {
                            assertionError.addSuppressed(ae);
                        }
                    }
                    index++;
                }
                if (null!=assertionError) {
                    throw assertionError;
                }
            }
        };
    }

}
