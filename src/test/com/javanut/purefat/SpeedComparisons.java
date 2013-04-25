package com.javanut.purefat;

import static com.javanut.purefat.PureFAT.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class SpeedComparisons {

    double step = .8d;
    double min = 1d;
    double max = 2000d;
    
    double steps = (max-min)/step;
    double callCount = steps*steps;
    
    

    @Test 
    public void speedTest() {
        
        //side effect: this also initialized the internal static ring buffer
        //             therefore its not counted by the timer, by design.
        PureFAT.useLessRAM(true);//this is the default but here for clarity
        
        double baseTime = speedTestUnboxed();
        System.out.println();
        
        double temp;
        
        temp = speedTestBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        temp = speedTestNaiveAutoBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        temp = speedTestBoxedAssertAudited();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        baseTime = Math.min(speedTestUnboxed(),baseTime);
        System.out.println();
        
        temp = speedTestBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower SECOND");
        
        temp = speedTestNaiveAutoBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower SECOND");
        
        temp = speedTestBoxedAssertAudited();
        System.out.println("change "+((temp/baseTime)-1)+"x slower SECOND");

        temp = speedTestForceAudited();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
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
    
    
    public double speedTestForceAudited() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            Double jBox = new Double(j);
            saveLabel(jBox,"j");
            double i = max;
            while (i>=min) {
                Double iBox = new Double(i);
                saveLabel(iBox,"i");
                Double value = distanceBoxedSavedExpression(jBox,iBox);
                assertFalse(Double.isNaN(value));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("boxed audited duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    public double speedTestBoxedAssertAudited() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            Double jBox = label(j,"j");
            double i = max;
            while (i>=min) {
                Double iBox = label(i,"i");
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
        
        return fun(Math.sqrt((a*a) + (b*b)),"distance",
                    "sqrt(({}^2)+({}^2))", a, b);
    }
    
    /**
     * Does the same work as above but always saves the expression.
     * @param a
     * @param b
     * @return
     */
    private final Double distanceBoxedSavedExpression(Double a, Double b) {
        
      Double result = new Double(Math.sqrt((a*a) + (b*b)));
      saveExpression(result, "distance", "sqrt(({}^2)+({}^2))", a, b);
      return result;
    }
    
}
