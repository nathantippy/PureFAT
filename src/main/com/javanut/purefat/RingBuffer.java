package com.javanut.purefat;

import java.lang.ref.WeakReference;

class RingBuffer  {
    
    private int bufferSize;
    private int lastBufferSize;
    
    private WeakReference<Number>[] bufferRefs;
    private Function[]              bufferExpr;
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
        bufferExpr = new Function[bufferSize];
        
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
                    return bufferExpr[i];
                }
                if (--i<0) {
                    i = bufferSize-1;
                }
            } while (i!=startLookingFrom);
            return null;//not found, looked everywhere
        }
    }

    public final void put(Number key, Function value) { //must be very fast
        WeakReference<Number> tempRef = new WeakReference<Number>(key);
        synchronized(lock) {
            int myIndex = findIndex();
            bufferPos = myIndex; //store for next usage
            bufferRefs[myIndex] = tempRef; //grab my index
            value.setPrivateIndex(myIndex);
            bufferExpr[myIndex] = value;
        }
    }

   /**
    * Looks forward for next empty slot, because these are the oldest its very
    * likely that one will be found at the next index.
    * 
    * @return
    */
   private int findIndex() {
       int i = bufferPos;
       int loopCount = 0;
       do {
           i++;
           if (i==bufferSize) {
               //System.err.println("loop back around again!");
               i = 0;
           }
           if (i==bufferPos) {
               //looped back to original position must gc and run again
               boolean mustGrowArray = ++loopCount>1; 
               if (mustGrowArray || shouldGrowArray) {
                   //Grow the array with factorial
                   int newBufferSize = lastBufferSize+bufferSize;
                   try {
                       System.gc();
                       WeakReference<Number>[] newBufferRefs = new WeakReference[newBufferSize];
                       //System.err.println("grow to "+newBufferSize+" must:"+mustGrowArray+" should:"+shouldGrowArray);
                       
                       Function[] newBufferExpr = new Function[newBufferSize];
                       System.arraycopy(bufferRefs, 0, newBufferRefs, 0, bufferSize);
                       System.arraycopy(bufferExpr, 0, newBufferExpr, 0, bufferSize);
                       //put it out there
                       lastBufferSize = bufferSize;
                       bufferSize = newBufferSize;
                       bufferRefs = newBufferRefs;
                       bufferExpr = newBufferExpr;
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
       } while (null!=bufferRefs[i] && null!=bufferRefs[i].get());
       
       return i;
       
   }

}
