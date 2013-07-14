package com.javanut.purefat;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.javanut.purefat.useCase.ExampleUseCase;
import com.javanut.purefat.useCase.MotorRPMUseCase;

public class ExampleUsages {

    static {
        System.setProperty("purefat.internal", "true");
        System.setProperty("purefat.verbose", "true");
    }
    
    @Test
    public void testMotorRPM() {
        simulateProdEnvironment(new MotorRPMUseCase());
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
        executor.submit(productionSampler(useCase,sampleQueue));
        //this thread will take samples from sampleQueue do some computation and put the
        //result on the report queue
        executor.submit(productionInfrastructure(useCase,sampleQueue,reportQueue));
        //this thread will take the results off the report queue and confirm the expected values.
        executor.submit(productionReports(useCase,reportQueue));
        
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

    private Runnable productionSampler(final ExampleUseCase useCase,
                                        final BlockingQueue<Number> sampleQueue) {
        
        return new Runnable() {
            @Override
            public void run() {
                Iterator<Number> samples = useCase.samples();
                while (samples.hasNext()) {
                    try {
                        sampleQueue.put(samples.next());
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
                int count = useCase.samplesCount();
                while (--count>=0) {
                    try {
                        Number result = reportQueue.take();
                        useCase.validatResult(result);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            
        };
    }

}
