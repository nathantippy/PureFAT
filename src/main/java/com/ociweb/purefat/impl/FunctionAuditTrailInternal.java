/**
 * Copyright (c) 2013, Nathan Tippy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * @author  Nathan Tippy <tippyn@ociweb.com>
 * bitcoin:1NBzAoTTf1PZpYTn7WbXDTf17gddJHC8eY?amount=0.01&message=PFAT%20donation
 *
 */
package com.ociweb.purefat.impl;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FunctionAuditTrailInternal implements FunctionAuditTrail  {
    
    //Do not make this too much larger it will grow as needed.
    private final static int RING_BUFFER_INITIAL_SIZE = 1<<20;//1M elements;
    private final static String RING_BUFFER_INITIAL_SIZE_KEY = "purefat.ringbuffer.size";
    
    private final static boolean RING_BUFFER_GROW_DEFAULT = true;
    private final static String RING_BUFFER_GROW_KEY = "purefat.ringbuffer.grow";
    
    private static final Logger logger = LoggerFactory.getLogger(FunctionAuditTrailInternal.class);
    private final Object ringLock = new Object();
    
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
        this(Integer.parseInt(System.getProperty(RING_BUFFER_INITIAL_SIZE_KEY, Integer.toString(RING_BUFFER_INITIAL_SIZE))),
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
        synchronized(ringLock) {
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
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String)
     */
    @Override
    public final boolean save(Number number, String label, String expression) {
        saveMetaData(label, expression);
        Function f = findFuncShell(number);
        f.init(label, expression);
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1) {
        saveMetaData(label, expression);
        Function f = findFuncShell(number);
        f.init(label, expression, p1);
        return true;
    }

    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2) {
        saveMetaData(label, expression);
        Function f = findFuncShell(number);
        f.init(label, expression, p1, p2);
        return true;
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4, Number p5) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4, p5);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4, p5, p6);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number, java.lang.Number)
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, p1, p2, p3, p4, p5, p6, p7);
    }
    
    /* (non-Javadoc)
     * @see com.ociweb.purefat.ExpressionWriter#save(java.lang.Number, java.lang.String, java.lang.String, java.lang.Number[])
     */
    @Override
    public final boolean save(Number number, String label, String expression, Number[] params) {
        saveMetaData(label, expression);
        return findFuncShell(number).init(label, expression, params);
    }
    
    /**
    * Looks forward for next empty slot, because these are the oldest its very
    * likely that one will be found at the next index.
    * @return
    */
    private final Function findFuncShell(Number number) {
        WeakReference<Number> tempRef = new WeakReference<Number>(number);
       //always start with oldest position 
       synchronized(ringLock) {
           int i = bufferPos+1;
           //shortcut!, don't do the complex work if we already have a winner
           if (i>=bufferSize || null != bufferRefs[i].get()) {
               
               i = bufferPos;
               
               byte loopCount = 0;
               do {
                   if (++i==bufferSize) {
                       i = 0;
                   }
                   //if looped back to the original position may need to grow
                   if (i==bufferPos) {
                       //if configured to grow array for faster speed gc is skipped
                       if (!shouldGrowArray) {
                           System.gc();//NOTE: may be slower but greatly saves RAM
                       }
                       
                       //only force growth if this is the 2rd pass with nothing found.
                       boolean mustGrowArray = ++loopCount>1; 
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
                                       //the front of these arrays are identical
                                       //so set the arrays before size limits
                                       this.bufferRefs = newBufferRefs;
                                       this.funcShell = newBufferExpr;
                                       //we only grow and never shrink so this works
                                       this.lastBufferSize = bufferSize;
                                       this.bufferSize = newBufferSize;
                                   } catch (Throwable outOfMemory) {
                                       if (mustGrowArray) {
                                           throw new OutOfMemoryError("Need more memory to hold expressions saved at this fast rate.");
                                       }
                                       //use as-is and do not grow further.
                                       shouldGrowArray = false;
                                   }


                       }
                   } 
               } while (null != bufferRefs[i].get());
           }
           bufferPos = i; //store for next usage
           bufferRefs[i] = tempRef; //grab my index
           return funcShell[i];
       }
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

    @Override
    public boolean continueAuditTo(String channelId, Number boxed) {
        // not sure this implementation can use this for anything
        return true;
    }

    @Override
    public boolean continueAuditFrom(String channelId, Number boxed) {
        save(boxed, "Channel:"+channelId, PFImpl.LABEL_WRAP, boxed);
        return true;
    }

}
