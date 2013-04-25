package com.javanut.purefat;

import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RingBuffer  {
    
    private static final Logger logger = LoggerFactory.getLogger(RingBuffer.class);
    private int bufferSize;
    private int lastBufferSize;
    
    private WeakReference<Number>[] bufferRefs;
    private Function[]              funcShell;
    private int bufferPos = 0;
    private boolean shouldGrowArray = true; //default is to go fast as possible
    private Object lock = new Object();
    
    /**
     * 
     * @param initialSize starting buffer size for expressions
     * @param useLessRam if true use more aggressive GC and less RAM, is slower
     */
    public RingBuffer(int initialSize) {
        
        bufferSize     = initialSize;
        lastBufferSize = initialSize;
        //did not help because all still get added to map unless gc is called!
        bufferRefs = new WeakReference[bufferSize];
        
        funcShell = new Function[bufferSize];
        int j = bufferSize;
        while (--j>=0) {
            bufferRefs[j] =  new WeakReference(null);
            funcShell[j] = new Function(j);
        }
        
    }

    public void useLessRAM(boolean useLessRam) {
        shouldGrowArray = !useLessRam;
        
    }

    public final Function get(Number key) {
        return get(key,bufferPos);
    }
    
    public final Function get(Number key, Function startHere) {
        return get(key,startHere.getPrivateIndex());
    }
    
    /**
     * This is only called when we need to debug an expression that has recently
     * failed.  Because this was "recent" we know that the fastest way of finding
     * the key is to start at the current/parentExpression position and go back.
     * 
     * @param key
     * @param startLookingFrom
     * @return
     */
    private final Function get(Number key, int startLookingFrom) {
        synchronized(lock) {
            int i = startLookingFrom;
            do {
                if (null!=bufferRefs[i] && bufferRefs[i].get()==key) {
                    return funcShell[i];
                }
                if (--i<0) {
                    i = bufferSize-1;
                }
            } while (i!=startLookingFrom);
            return null;//not found, looked everywhere
        }
    }

    /**
    * Looks forward for next empty slot, because these are the oldest its very
    * likely that one will be found at the next index.
    * 
    * @return
    */
    public final Function findFuncShell(WeakReference<Number> tempRef) {
       //always start with oldest position 
       synchronized(lock) {
           
           int i = bufferPos+1;//shortcut!
           if (i < bufferSize && null == bufferRefs[i].get()) {
               bufferPos = i; //store for next usage
               bufferRefs[i] = tempRef; //grab my index
               return funcShell[i];
           }
           i = bufferPos;
           
           byte loopCount = 0;
           do {
               if (++i==bufferSize) {
                   i = 0;
               }
               if (i==bufferPos) {
                   //looped back to original position must gc and run again
                   boolean mustGrowArray = ++loopCount>1; 
                   if (mustGrowArray || shouldGrowArray) {
                       loopCount=0;
                       //step = 0;
                       //Grow the array with factorial
                       int newBufferSize = lastBufferSize + bufferSize;
                       try {
                           System.gc();
                           WeakReference<Number>[] newBufferRefs = new WeakReference[newBufferSize];
                           
                           logger.info("growing ring buffer to {} reason:{} ",newBufferSize, mustGrowArray ? "Must for volume" : "Should for performance" );
                           
                           Function[] newBufferExpr = new Function[newBufferSize];
                           System.arraycopy(bufferRefs, 0, newBufferRefs, 0, bufferSize);
                           System.arraycopy(funcShell, 0, newBufferExpr, 0, bufferSize);
                           int j = newBufferSize;
                           while (--j>=bufferSize) {
                               newBufferRefs[j] = new WeakReference(null);
                               newBufferExpr[j] = new Function(j);
                           }
                           
                           //put it out there
                           lastBufferSize = bufferSize;
                           bufferSize = newBufferSize;
                           bufferRefs = newBufferRefs;
                           funcShell = newBufferExpr;
                       } catch (Throwable outOfMemory) {
                           if (mustGrowArray) {
                               throw new OutOfMemoryError("Need more memory to hold expressions saved at this fast rate.");
                           }
                           System.err.println("no more memory to grow array.");
                           //use as-is and do not grow further.
                           shouldGrowArray = false;
                       }
                   }
               }
           } while (null != bufferRefs[i].get());
           
           bufferPos = i; //store for next usage
           bufferRefs[i] = tempRef; //grab my index
           return funcShell[i];
       }
   }

}
