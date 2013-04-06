package com.javanut.purefat;

import com.javanut.purefat.PureFAT;

import org.junit.Test;

public class SpeedComparisons {

    double step = 1d;
    double min = 1d;
    double max = 10000d;
    
    double steps = (max-min)/step;
    double callCount = steps*steps;

    @Test 
    public void speedTest() {
        
        double baseTime = speedTestUnboxed();
        System.out.println();
        
        double temp;
        
        temp = speedTestBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        temp = speedTestAutoBoxed();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        temp = speedTestBoxedAssertAudited();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
        temp = speedTestBoxedAudited();
        System.out.println("change "+((temp/baseTime)-1)+"x slower");
        
    }
    
    
    public double speedTestUnboxed() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            double i = max;
            while (i>=min) {
                double value = distanceUnboxed(j,i);
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
            double i = max;
            while (i>=min) {
                Double value = distanceBoxed(new Double(j),new Double(i));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("boxed duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    public double speedTestAutoBoxed() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            double i = max;
            while (i>=min) {
                double value = distanceBoxed(j,i);
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("auto boxed duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    public double speedTestBoxedAudited() {
        
        long start = System.currentTimeMillis();
        double j = max;
        while (j>=min) {
            double i = max;
            while (i>=min) {
                Double value = distanceBoxedAudited(new Double(j),new Double(i));
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
            double i = max;
            while (i>=min) {
                Double value = distanceBoxedAssertAudited(new Double(j),new Double(i));
                i-=step;
            }
            j-=step;
        }
        long duration = System.currentTimeMillis()-start;
        
        double msPerCall = ((double)duration)/callCount;
        System.out.println("boxed assert audited duration:"+duration+" count:"+callCount+" perCall:"+msPerCall);
        return msPerCall;
    }
    
    
    
    
    private double distanceUnboxed(double a, double b) {
        return Math.sqrt((a*a) + (b*b));
    }
    
    private Double distanceBoxed(Double a, Double b) {
        return new Double(Math.sqrt((a*a) + (b*b)));
    }
    
    private Double distanceBoxedAudited(Double a, Double b) {
        
      Double result = new Double(Math.sqrt((a*a) + (b*b)));
      PureFAT.saveExpression(result, "sqrt(({}^2)+({}^2))", a, b);
      return result;
    }
    
    private Double distanceBoxedAssertAudited(Double a, Double b) {
        
        Double result = new Double(Math.sqrt((a*a) + (b*b)));
        assert(PureFAT.saveExpression(result, "sqrt(({}^2)+({}^2))", a, b));
        return result;
      }
    
    
    
}
