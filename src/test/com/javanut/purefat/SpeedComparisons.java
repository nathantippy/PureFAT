package com.javanut.purefat;

//import static com.javanut.purefat.PureFAT.*;


import org.junit.Test;

import com.javanut.purefat.impl.PFImpl;
import com.javanut.purefat.impl.PFNone;
import com.javanut.purefat.impl.PFVerbose;
import com.javanut.purefat.impl.FunctionAuditTrailInternal;

import static org.junit.Assert.*;

public class SpeedComparisons {

    double step = .8d;
    double min = 1d;
    double max = 2000d;
    
    double steps = (max-min)/step;
    double callCount = steps*steps;
    
    

    /**
     * This is not really a test and can not fail.
     * 
     * The purpose of this code is for performance testing of new ideas.
     * 
     */
    @Test 
    public void speedTest() {
        
        //side effect: this also initialized the internal static ring buffer
        //             therefore its not counted by the timer, by design.
        //PureFAT.useLessRAM(true);//this is the default but here for clarity
        
        double baseTime = speedTestUnboxed();
        System.out.println();
        
        double temp;
        
        temp = speedTestBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        temp = speedTestNaiveAutoBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        baseTime = Math.min(speedTestUnboxed(),baseTime);
        System.out.println();
        
        temp = speedTestBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower SECOND");
        
        temp = speedTestNaiveAutoBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower SECOND");
        
        temp = speedTestForceAudited(new PFNone(),"boxed forced no checks");
        System.out.println("change "+((temp/baseTime)-1)+"x slower");

        //Test does NOT use the PFDefault implementation because it changes 
        //behavior based on -ea and makes testing harder.
        System.setProperty("purefat.ringbuffer.grow", "true");//helps speed up the test
        PFVerbose strictImpl = new PFVerbose(new FunctionAuditTrailInternal());
        temp = speedTestForceAudited(strictImpl,"boxed forced audited");
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        //must let go of this instance because we dont have the RAM for two.
        strictImpl=null;
        
        temp = speedTestBoxedAssertAudited();
        System.out.println("change "+((temp/baseTime)-1)+"x slower SECOND");
    }
    
    
    public double speedTestUnboxed() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            double i = max;
            while (i>=min) {
                double value = distanceUnboxed(j,i);
                assertFalse(Double.isNaN(value));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("unboxed duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    

    public double speedTestBoxed() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            Double boxJ = new Double(j);
            double i = max;
            while (i>=min) {
                Double boxI = new Double(i);
                Double value = distanceBoxed(boxJ, boxI);
                assertFalse(Double.isNaN(value));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("boxed duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    public double speedTestNaiveAutoBoxed() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            double i = max;
            while (i>=min) {
                double value = distanceBoxed(j,i);
                assertFalse(Double.isNaN(value));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("auto boxed duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    public double speedTestForceAudited(PFImpl impl, String label) {
        
        //Can not use singleton static interface to ensure this test is not 
        //modified by -ea so PFImpl is passed in.
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            
            Double jBox = new Double(j); 
            impl.audit(jBox,"j",PFImpl.LABEL_WRAP,j);
            
            double i = max;
            while (i>=min) {
                Double iBox = new Double(i);
                impl.audit(iBox,"i",PFImpl.LABEL_WRAP,i);
                
                Double value = distanceBoxedSavedExpression(jBox,iBox,impl);
                assertFalse(Double.isNaN(value));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println(label+" duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    public double speedTestBoxedAssertAudited() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            Double jBox = PureFAT.audit(j,"j");
            double i = max;
            while (i>=min) {
                Double iBox = PureFAT.audit(i,"i");
                Double value = distanceBoxedAssertAudited(jBox,iBox);
                assertFalse(Double.isNaN(value));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("boxed assert audited duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    
    /**
     * Baseline method with a simple unboxed implementation. All other 
     * implmentations must do the exact same work as this one to make any 
     * comparsions.
     * 
     * @param a
     * @param b
     * @return
     */
    private final double distanceUnboxed(double a, double b) {
        return Math.sqrt((a*a) + (b*b));
    }
    
    /**
     * Same as unboxed method except that it requires boxed arguments and 
     * returns an explicitly boxed result.
     * 
     * @param a
     * @param b
     * @return
     */
    private final Double distanceBoxed(Double a, Double b) {
        return new Double(Math.sqrt((a*a) + (b*b)));
    }

    /**
     * Same as above except it uses the fun method as it is expected to be used.
     * When this test is run with assertions off it will not save the expression.
     * 
     * @param a
     * @param b
     * @return
     */
    private final Double distanceBoxedAssertAudited(Double a, Double b) {
        //if assert is on we need to skip this test becase it will be costly like saveExpression above
        
        return PureFAT.audit(Math.sqrt((a*a) + (b*b)),"distance",
                    "sqrt(({}^2)+({}^2))", a, b);
    }
    
    /**
     * Does the same work as above but always saves the expression.
     * @param a
     * @param b
     * @param internalStrictImpl 
     * @return
     */
    private final Double distanceBoxedSavedExpression(Double a, Double b, PFImpl impl) {
        
        Double boxed = new Double(Math.sqrt((a*a) + (b*b)));
        impl.audit(boxed, "distance", "sqrt(({}^2)+({}^2))", a, b);
        return boxed;
        
    }
    
}
