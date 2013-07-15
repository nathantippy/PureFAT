package com.javanut.purefat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.javanut.purefat.impl.FATConstraintViolation;
import com.javanut.purefat.useCase.ExampleUseCase;
import com.javanut.purefat.useCase.MotorRPMUseCase;

public class ExampleUsages {

    static {
        System.setProperty("purefat.internal", "true");
        System.setProperty("purefat.verbose", "true");
        System.setProperty("purefat.ringbuffer.size","100000");
    }
    
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
                        cv.printStackTrace();
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
                        cv.printStackTrace();
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
                List<AssertionError> allErrors = new ArrayList<AssertionError>();
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
                            assertTrue("No falure expected at index "+index,
                                       useCase.isFailureExpected(index));
                            
                            //check expected message
                            //cv.getMessage()
                            //cv.printStackTrace();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    } catch (AssertionError ae) {
                        allErrors.add(ae);
                    }
                    index++;
                }
                for(AssertionError ae:allErrors) {
                    ae.printStackTrace();
                }
                if (!allErrors.isEmpty()) {
                    throw allErrors.get(0);
                }
            }
            
        };
    }

}
