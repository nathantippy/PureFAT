package com.javanut.purefat.useCase;

import static com.javanut.purefat.PureFAT.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.javanut.purefat.FATReport;

public class MotorRPMUseCase implements ExampleUseCase {

    //one half of motor shaft is white and the other black.
    
    private final int samplesCount = 10000;
    private final Integer samplesPerSecond = audit(100,"samplesPerSecond");
    private final Integer samplesPerMinute = audit(60*samplesPerSecond,"samplesPerMinute","60*{}",samplesPerSecond);
    

    
    public MotorRPMUseCase() {
    }
    // private final int targetRPM
    
    //convert to radians per second.
    
    //n order to convert from RPM to rad/s, multiply the RPM by 0.10472(π/30).
//  To convert rad/s back to RPM, multiply the rad/s by 9.54929(30/π).
    
    @Override
    public Iterator<Number> samples() {
        
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
                //110011001100
                //in this example 1 is white and 0 is black
                return audit((countRemaining>>1)%2,"shaftSample");
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

    Number last = audit(-1,"unknown");
    Integer count = audit(0,"initial");
    Double rpm = audit(0d,"initial");

    
    @Override
    public Number computeResult(Number sample) {
        if (last.equals(sample)) {
            count = audit(count+1,"inc","{}+1",count);
        } else {
            rpm = audit(samplesPerMinute/(double)(count*2),"rpm","{}/({}*2)",samplesPerMinute,count);
            
            //reset count
            last = sample;
            count = audit(1,"first");
        }
        return rpm;
    }

    @Override
    public void validatResult(Number result) {
        // TODO Auto-generated method stub
       // System.err.println(result);
        
        auditIsFinite(result);
        auditIsGTE(result, 0);
        
        logAuditTrail(result, FATReport.table);
        

    }

}
