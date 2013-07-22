package com.ociweb.purefat.useCase.foundation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public abstract class AbstractPureFATUseCase<S, R> implements TestablePureFATUseCase<S, R> {

    private static final ExpectedFailureCatalog NO_FAILURE = new ExpectedFailureCatalog() {
                                                        @Override
                                                        public boolean isFailureExpected(int index) {
                                                            return false;
                                                        }};
    private static final int queueCapacity = 10;
    protected final int samplesCount = 100000;
    protected final boolean testBrokenCode;
    
    private final BlockingQueue<S> sampleQueue = new ArrayBlockingQueue<S>(queueCapacity);
    private final BlockingQueue<R> reportQueue = new ArrayBlockingQueue<R>(queueCapacity);

    public AbstractPureFATUseCase(boolean testBrokenCode) {
        this.testBrokenCode = testBrokenCode;
    }

    @Override
    public ExpectedFailureCatalog computeFailureCatalog() {
        return NO_FAILURE;
    }

    @Override
    public ExpectedFailureCatalog samplesFailureCatalog() {
        return NO_FAILURE;
    }
    
    @Override
    public ExpectedFailureCatalog validateFailureCatalog() {
        return NO_FAILURE;
    }

    @Override
    public Iterator<S> samples() {
        
        return new Iterator<S>() {
            int countRemaining = samplesCount;
            
            @Override
            public boolean hasNext() {
                return countRemaining>0;
            }

            @Override
            public S next() {
                if (--countRemaining<0) {
                    throw new NoSuchElementException();
                }
                S result = simulatedSample(samplesCount - countRemaining);
                try {
                    sampleQueue.put(result);
                } catch (InterruptedException e) {
                    countRemaining=0;
                    Thread.interrupted();
                }
                return result;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }};
    }
    protected abstract S simulatedSample(int idx);

    @Override
    public Iterator<R> compute() {

        return new Iterator<R>() {
            int countRemaining = samplesCount;
            
            @Override
            public boolean hasNext() {
                return countRemaining>0;
            }

            @Override
            public R next() {
                if (--countRemaining<0) {
                    throw new NoSuchElementException();
                }
                S sample;
                try {
                    sample = sampleQueue.take();
                    R result = simulateCompute(samplesCount - countRemaining, sample);
                    reportQueue.put(result);
                    return result;
                } catch (InterruptedException e) {
                    countRemaining=0;
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }};
    }
    protected abstract R simulateCompute(int idx, S sample);
    
    
    @Override
    public Iterator<Object> validate() {
        
        return new Iterator<Object>() {
            int countRemaining = samplesCount;
            
            @Override
            public boolean hasNext() {
                return countRemaining>0;
            }

            @Override
            public Object next() {
                if (--countRemaining<0) {
                    throw new NoSuchElementException();
                }
                R report;
                try {
                    report = reportQueue.take();
                    simulateValidate(samplesCount - countRemaining, report);
                } catch (InterruptedException e) {
                    countRemaining=0;
                    Thread.currentThread().interrupt();
                }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }};
    }
    protected abstract void simulateValidate(int idx, R result);
    
}
