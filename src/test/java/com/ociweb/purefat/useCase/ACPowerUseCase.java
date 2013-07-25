package com.ociweb.purefat.useCase;

import static com.ociweb.purefat.PureFAT.*;

import com.ociweb.purefat.FATTemplate;
import com.ociweb.purefat.useCase.foundation.AbstractPureFATUseCase;
import com.ociweb.purefat.useCase.foundation.ExpectedFailureCatalog;

public class ACPowerUseCase extends AbstractPureFATUseCase<Double, Double> {

    //the frequency is expected to be 60 cycles per second.
    //so we need to cover 2 PI * 60 every second.
    //for nyquist limit we must sample more than 2x per cycle.
    //to make an easy test we will sample 4 times per expected cycle.
    //the radians per step will be (2 PI)/4
    
    private final Double maxAmplitude = audit(120d,"maxAmplitude");
    private final Integer samplesPerCycle = audit(4, "samplesPerCycle");
    private final Double sineRootMeanSquared = audit(1d/Math.sqrt(2d),"sineRootMeanSquared","(1/sqrt(2))"); 
    
    private final int ringBufferSize = samplesPerCycle;//results after 1 cycle
    private final Double[] ringBuffer = new Double[ringBufferSize];
    private int ringBufferPos = 0;
    
    
    public ACPowerUseCase(boolean testBrokenCode) {
        super(testBrokenCode);
    }

    @Override
    protected Double simulatedSample(int idx) {
        Integer sampleIndex = continueAuditFrom("TestFramework", idx);
        Double radians = audit((idx*2d*Math.PI)/(double)samplesPerCycle,"radians","({}*2*pi/{})",sampleIndex,samplesPerCycle);
        return audit(Math.sin(radians)*maxAmplitude,"sampleVoltage","sin({})*{}",radians,maxAmplitude);
    }
    
    @Override
    protected Double simulateCompute(int idx, Double sample) {
        
        auditIsGTE(sample, - maxAmplitude);
        auditIsLTE(sample,   maxAmplitude);
        
        if (--ringBufferPos<0) {
            ringBufferPos = ringBufferSize-1; 
        }
        ringBuffer[ringBufferPos] = audit(Math.abs(sample),"abs","abs({})",sample);
        
        Double sum = audit(0d,"initialSum");
        Integer count = audit(0,"initialCount");
        int i = ringBufferSize;
        while(--i>=0) {
            if (null!=ringBuffer[i]) {
                sum = audit(sum+(ringBuffer[i]*ringBuffer[i]),"sumAC","({}+({}*{}))",sum,ringBuffer[i],ringBuffer[i]);
                count = audit(count+1,"countAC","({}+1)",count);
            }
        }
        Double rootMeanSquare = audit(Math.sqrt(sum/count),"rootMeanSquare","sqrt({}/{})",sum,count);
        return audit(rootMeanSquare/sineRootMeanSquared,"computedVoltage","({}/{})",rootMeanSquare,sineRootMeanSquared);
    }

    @Override
    protected void simulateValidate(int idx, Double result) {
        
        auditIsNotZero(result);
        if (idx>=ringBufferSize || testBrokenCode) {
            //we can measure the voltage within 9 places with only 4 samples.
            auditIsNear(result, maxAmplitude, 1E-9);
        }
  
        
      //All four of these reports look good.
//        logAuditTrail(result, FATTemplate.table);
//        logAuditTrail(result, FATTemplate.tree);
//        logAuditTrail(result, FATTemplate.summary);
//        logAuditTrail(result, FATTemplate.expression);
        
        
    }
    
    @Override
    public ExpectedFailureCatalog validateFailureCatalog() {
        return new ExpectedFailureCatalog() {

            @Override
            public boolean isFailureExpected(int idx) {

                //1 and 3 are strange because we still get the right
                //answer when this is not expected.
                if (testBrokenCode && idx!=1 && idx!=3) {
                    // first go around on the shaft will report an error with
                    // the broken code
                    return idx<ringBufferSize;
                }
                return false;
            }

        };
    }

}
