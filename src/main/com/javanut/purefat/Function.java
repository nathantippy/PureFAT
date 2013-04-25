package com.javanut.purefat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

class Function extends Number {

    final static String        labelWrap = "{}";
    
    private String           text;
    private byte            paramCount;
    private String           label;
    
    private final Number[]         params;
    private final int              privateIdx;

    private Boolean          validated;//once validated no need to validate a second time.
    private Double           eval;//once evaluated no need to evaluate a second time
    
    private static final int MAX_PARAMS = 7;
    private static final Logger logger = LoggerFactory.getLogger(Function.class);
    
    public Function(int idx) {
        privateIdx = idx;
        params = new Number[MAX_PARAMS];
    }
    
    public Function(Number undef) {
        //missing value
        privateIdx = -1;
        params = new Number[]{undef};
        label = "undefined";
        text = labelWrap;
    }

    private final void reset(String label, String expressionText) {
        this.label = label;
        this.text = expressionText;
        this.validated = null;
        this.eval = null;
    }

    public String stackElement() {
        MetaFunction usageData = usageData(label,text);
        return usageData==null? "" :usageData.stackElement();
    }
    
    private final MetaFunction usageData(String label, String expressionText) {
        Map<String,MetaFunction> m = functionMeta.get(expressionText);
        if (null == m) {
            m = new HashMap<String,MetaFunction>();
            functionMeta.put(expressionText, m);
        }
        MetaFunction mf = m.get(label);
        if (null==mf) {
            m.put(label,new MetaFunction(Thread.currentThread().getStackTrace()));
        }
        return mf;
    }
    
    private final static Map<String, Map<String,MetaFunction>> functionMeta = new HashMap<String,Map<String,MetaFunction>>();
    
    public final boolean init(String label, String expressionText) {
        this.paramCount = 0;
        reset(label,expressionText);
        usageData(label, expressionText);
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number[] paramArray) {
        this.paramCount = (byte) paramArray.length;
        System.arraycopy(paramArray, 0, params, 0, paramArray.length);
        reset(label,expressionText);
        usageData(label, expressionText);//TODO: optional, gather call count and params stats min/max/mean/stddev
        
        return true;
    }

    
    public final boolean init(String label, String expressionText,
                      Number p0) {
        this.paramCount = 1;
        this.params[0] = p0;
        
        reset(label,expressionText);
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number p0,Number p1) {
        this.paramCount = 2;
        this.params[0] = p0;
        this.params[1] = p1;
        
        reset(label,expressionText);
        
        
        //TODO:report performance as (Doc CPU, time added per 10000)
        //baseline    .006788
        //            .006348
        //            .004335
        //stacktrace  .049262
        //            .047659
        //lookup      .045646
        //            .035137
        //systemhash  .004671  //Best option for distributed write and lazy analysis
        //            .006798
        //logger.info .098470
        
       // logger.info(expressionText,p0,p1);
//        int i;
//        i = System.identityHashCode(p0);
//        i+= System.identityHashCode(p1); 
//        if (i==0) {
//            System.err.println("error");
//        }
        
//        StackTraceElement temp = Thread.currentThread().getStackTrace()[2];
//        if (temp.getLineNumber()==0) {
//            System.err.println("err");
//        }
        
        //System.err.println("line:"+temp.getLineNumber());
        
        
//        //test
//        int t=0;
//        RingBuffer ring = PureFAT.ringBuffer;
//        //for(Number n:paramArray) {
//            Function found = ring.get(p0);//, this);
//            //get line number?
//            t+=found.getPrivateIndex();
//            //found.privateIdx;
//            found = ring.get(p1);//, this);
//            //get line number?
//            t+=found.getPrivateIndex();
//          
//        //}
//        if (t==0) {
//            System.err.println("no data found!");
//        }
//        
        
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number p0,Number p1,Number p2) {
        this.paramCount = 3;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        
        reset(label,expressionText);
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number p0,Number p1,Number p2,Number p3) {
        this.paramCount = 4;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        
        reset(label,expressionText);
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number p0,Number p1,Number p2,Number p3,Number p4) {
        this.paramCount = 5;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        
        reset(label,expressionText);
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number p0,Number p1,Number p2,Number p3,Number p4,Number p5) {
        this.paramCount = 6;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        
        reset(label,expressionText);
        return true;
    }
    
    public final boolean init(String label, String expressionText,
            Number p0,Number p1,Number p2,Number p3,Number p4,Number p5,Number p6) {
        this.paramCount = 7;
        this.params[0] = p0;
        this.params[1] = p1;
        this.params[2] = p2;
        this.params[3] = p3;
        this.params[4] = p4;
        this.params[5] = p5;
        this.params[6] = p6;
        
        reset(label,expressionText);
        return true;
    }
                           
    
    public final Number[] params() {
        return Arrays.copyOf(params, paramCount);
    }
    
    public boolean isValid() {
        if (null==validated) {
            assert(text.indexOf("}{")==-1): "Bad expression text "+text;
            assert(text.indexOf("}(")==-1): "Bad expression text "+text;
            assert(text.indexOf("){")==-1): "Bad expression text "+text;
            assert(text.indexOf("2{")==-1): "Bad expression text "+text;
            //todo how to fix numbers? regex?
            //check count of {} matches array length
            //TODO: validate recusrively
            validated = true;//hack
            
        }
        return validated;
    }
    
    public String decoratedLabel(RingBuffer ringbuffer) {
        Number[] parms = params();
        int i = parms.length;
        String[] labels = new String[i];
        while (--i>=0) {
            Function f = ringbuffer.get(parms[i],this);
            labels[i] = (null==f ? parms[i].toString() : f.label() );
        }
        return MessageFormatter.arrayFormat(text, labels).getMessage();
    }
    
    public String toString() {
        return MessageFormatter.arrayFormat(text, params()).getMessage();
    }
    
//    public String localExpressionText( Map<Number,Function> childrenMap) { //TODO: must parse and eval this
//        Number[] param = deepParamArray(childrenMap);
//        return MessageFormatter.arrayFormat(text, param).getMessage();
//    }
    public String text() {
        return text;
    }
    
    private Number[] deepParamArray(Map<Number, Function> childrenMap) {
        Number[] param = new Number[paramCount]; //TOOD:keep and lazy init.
        int i = paramCount;
        while (--i>=0) {
            if (null!=params[i]) {
                param[i] = childrenMap.get(params[i]);
            }
            if (null==param[i]) {
                param[i] = params[i]; //child not found so use constant parameter
            }
        }
        return param;
    }
    
   // public String localLabelText()

    public void log(String label, Logger logger) {
        logger.info(label+' '+text, params);
    }

    //TODO:add my evaluation implmentations here
    //http://code.google.com/p/symja/wiki/RunSymja
    //may want my own simplified parse tree for fewer dependencies
    
    @Override
    public int intValue() {
        if (null==eval) {
            eval();
        }
        return eval.intValue();
    }

    @Override
    public long longValue() {
        if (null==eval) {
            eval();
        }
        return eval.longValue();
    }

    @Override
    public float floatValue() {
        if (null==eval) {
            eval();
        }
        return eval.floatValue();
    }

    @Override
    public double doubleValue() {
        if (null==eval) {
            eval();
        }
        return eval.doubleValue();
    }


    
    private void eval() {
        //do it now
        
    }

    public String label() {
        return "${"+label+"}";
    }

    public boolean isLabel() {
        return text==labelWrap;//special instance just for constant labels
    }


    public int getPrivateIndex() {
        return privateIdx;
    }



}
