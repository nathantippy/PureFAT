package com.javanut.purefat.impl;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.MessageFormatter;


public class FunctionAuditTrailInternal implements FunctionAuditTrail  {
    
    
    private final static int RING_BUFFER_SIZE_DEFAULT = 1<<24;//16M;
    private final static String RING_BUFFER_SIZE_KEY = "purefat.ringbuffer.size";
    
    private final static boolean RING_BUFFER_GROW_DEFAULT = true;
    private final static String RING_BUFFER_GROW_KEY = "purefat.ringbuffer.grow";
    
    private static final Logger logger = LoggerFactory.getLogger(FunctionAuditTrailInternal.class);
    private final Object lock = new Object();
    private final Set<Number> dispose = new HashSet<Number>();
    
    private int                     bufferSize;
    private int                     lastBufferSize;
    private WeakReference<Number>[] bufferRefs;
    private Function[]              funcShell;
    private int                     bufferPos = 0;

    //default is to go fast as possible, this is not final because it may be
    //turned off later if an out of memory exception is encountered.
    private boolean                shouldGrowArray = RING_BUFFER_GROW_DEFAULT;

    private final Map<String, Map<String,FunMetaData>> functionMeta = new HashMap<String,Map<String,FunMetaData>>();
    
    public FunctionAuditTrailInternal() {
        this(Integer.parseInt(System.getProperty(RING_BUFFER_SIZE_KEY, Integer.toString(RING_BUFFER_SIZE_DEFAULT))),
             Boolean.parseBoolean(System.getProperty(RING_BUFFER_GROW_KEY, Boolean.toString(RING_BUFFER_GROW_DEFAULT)))
             );
    }
    
    /**
     * 
     * @param initialSize starting buffer size for expressions
     * @param useLessRam if true use more aggressive GC and less RAM, is slower
     */
    private FunctionAuditTrailInternal(int initialSize, boolean grow) {
        
        shouldGrowArray = grow;
        bufferSize     = initialSize;
        lastBufferSize = initialSize;
        //did not help because all still get added to map unless gc is called!
        bufferRefs = new WeakReference[bufferSize];
        
        funcShell = new Function[bufferSize];
        int j = bufferSize;
        while (--j>=0) {
            bufferRefs[j] =  new WeakReference<Number>(null);
            funcShell[j] = new Function(j);
        }
        
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

    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1) {
        saveMetaData(label, expression);
        Function f = findFuncShell(number);
        f.init(label, expression, p1);
        return true;
    }

    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2) {
        saveMetaData(label, expression);
        Function f = findFuncShell(number);
        f.init(label, expression, p1, p2);
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4, Number p5) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4, p5);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4, p5, p6);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4, p5, p6, p7);
    }
    
    /* (non-Javadoc)
     * @see com.javanut.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number[] params) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, params);
    }
    
    /**
    * Looks forward for next empty slot, because these are the oldest its very
    * likely that one will be found at the next index.
    * 
    * TODO: use lock in each shell so no sync is needed over the walk.
    * TODO: use lock for resize to remove sync
    * 
    * @return
    */
    private final Function findFuncShell(Number number) {
        WeakReference<Number> tempRef = new WeakReference<Number>(number);
       //always start with oldest position 
       synchronized(lock) {
           
           int i = bufferPos+1;//shortcut!
           if (i < bufferSize && null == bufferRefs[i].get()) {
               bufferPos = i; //store for next usage
               bufferRefs[i] = tempRef; //grab my index
               return funcShell[i];
           }
           
           //test for remove numbers
           if (i<bufferSize && !dispose.isEmpty()) {
               int hash = System.identityHashCode(bufferRefs[i].get());
               boolean ok;
               synchronized(dispose) {
                   ok = dispose.remove(hash); 
               }
               if (ok) {
                     bufferRefs[i].clear();
                     bufferPos = i; //store for next usage
                     bufferRefs[i] = tempRef; //grab my index
                     return funcShell[i];
               }
           }
           


           i = bufferPos;
           
           byte loopCount = 0;
           do {
               if (++i==bufferSize) {
                   i = 0;
               }
               if (i==bufferPos) {
                   //looped back to original position
                   //if any disposed values remain they must have already been removed
                   if (!dispose.isEmpty()) {
                       logger.trace(dispose.size()+" unnessisary call(s) to dispose cleared.");
                       dispose.clear();
                   }
                   
                   //if configured to grow array for faster speed gc is skipped
                   if (!shouldGrowArray) {
                       System.err.println("GC");
                       System.gc();//NOTE: may be slower but greatly saves RAM
                   }
                   
                   //only force growth if this is the 2rd pass with nothing found.
                   boolean mustGrowArray = ++loopCount>1; 
                   //NOTE: if this does not work must implment log based solution.
                   if (mustGrowArray || shouldGrowArray) {
                       loopCount=0;
                       //step = 0;
                       //Grow the array with factorial
                       int newBufferSize = lastBufferSize + bufferSize;
                       try {
                           @SuppressWarnings("unchecked")
                           WeakReference<Number>[] newBufferRefs = new WeakReference[newBufferSize];
                           
                           logger.info("growing ring buffer to {} reason:{} ",newBufferSize, mustGrowArray ? "Must for volume" : "Should for performance" );
                           
                           Function[] newBufferExpr = new Function[newBufferSize];
                           System.arraycopy(bufferRefs, 0, newBufferRefs, 0, bufferSize);
                           System.arraycopy(funcShell, 0, newBufferExpr, 0, bufferSize);
                           int j = newBufferSize;
                           while (--j>=bufferSize) {
                               newBufferRefs[j] = new WeakReference<Number>(null);
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

    public boolean flush(Number number) {
        int identityHashCode = System.identityHashCode(number);
        synchronized(dispose) {
            dispose.add(identityHashCode);
        }
        return true;
    }

    public final FunMetaData metaData(Function fun) {
        String expressionText = fun.text();
        Map<String,FunMetaData> m = functionMeta.get(expressionText);
        if (null == m) {
            return FunMetaData.NONE;
        }
        
        String label = fun.labelName();
        FunMetaData fmd = m.get(label);
        if (null == fmd) {
            return FunMetaData.NONE;
        } else {
            return fmd;
        }
    }
    
    private final void saveMetaData(String label, String expressionText) {
        Map<String,FunMetaData> m = functionMeta.get(expressionText);
        if (null == m) {
            m = new HashMap<String,FunMetaData>();
            functionMeta.put(expressionText, m);
        }
        if (!m.containsKey(label)) {
            m.put(label,new FunMetaData(Thread.currentThread().getStackTrace()));
        }
    }

}
