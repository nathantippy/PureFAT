package com.javanut.purefat;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PureFAT {
    
    /*
     * 
     
     slf4j math assert
     
     to make direct insert of values work the * operator must be used in all cases
     if two constants must be multiplied it should be documented as 123(456) or (132)(456)(789)
     apply normal precidence rules
     expressions need to be wrapped in () to ensrue consistency when injecting
     use {} for injection point to match slf4j so it can be leveraged
     
     TODO: use ${myname} for named args how can this be leveraged:  maven,puppet,bash,ant,groovy 
     
     
     NOTE: assume that every label/expression is unique and fetch its line number only once!.
     TODO: need new meta data structure to hold line numbers and stats.
     TODO: need validating compiler to throw compile error if they are NOT unique.
           use this to do the validation ToolProvider.getSystemJavaCompiler()
     
     */

    private static final String MESSAGE = "Value does not match required constraints, check log for details.";
    private static final Logger logger = LoggerFactory.getLogger(PureFAT.class);
    
    //TODO: starting with large enought value makes bigg diference.
    //TODO: must serialize final value for next start up.
    static RingBuffer ringBuffer;

    static {
        
        assert(init());
        
        
    }
    
    //TODO: if this can be done as an annotation then this might be a great open source project
    //TODO: build parser validation so expressions can be used by wolframalpha
    //TODO: build second execute to validate answer of first and implmentation matches documentation.
    //TODO: because of precidence this must be parsed and confirm that it has surrounding () of highest precidence
    //TODO: when logging tree need more of a "table" look to help colapse the repeats 
    //TODO: need repeat count when display table to easily find reductions.
    
    //TODO: is built to support multi threading debug how can we support multi node cluster?
    //TODO: low RAM model where every audit is logged and log files are loaded to reconstruct stack.
    //       value lineno expr params (param lineno or systemHash)
    //TODO: optional MongoDB adapter for logback for processing later, map reduce?
    
    //TODO: send expected value and get report of the expected inputs to make that happen.
    //TODO: reading byte codes may auto generate the template to greatly simplify the usages.
    
    public static void useLessRAM(boolean useLessRAM) {
        assert(ringBuffer.useLessRAM(useLessRAM));
    }
    
    private static boolean init() {
        ringBuffer = new RingBuffer(20000000);
        return true;
    }

    public static boolean logExpression(Number keyNumber) {
        Function expression;
        //synchronized(expressionMap) {
            expression = (Function) ringBuffer.get(keyNumber);
        //}
        if (null!=expression) {//TODO: at different levels look deeper, at top level can log expressions without parse validation.
            expression.log(keyNumber.toString()+"=",logger);//TODO: deep log and shallow log?
            return true; //TODO: add this check!   Math.abs(expression.doubleValue()-result.doubleValue())<EPSILON;
        } else {
            return false;
        }
        
    }
    
    public static boolean dispose(Number number) {
        //GC this number in RingBuffer, on pass 2 start to check for these?
        ringBuffer.dispose(number);
        return true;
    }
    
    /**
     * Same as dispose but write details to log for analysis later
     * @param number
     */
    public static void archive(Number number) {
        
    }
    
    public static boolean isFinite(Number number, String label) {
        if (null==number || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            auditTrail(number,FATFormat.table);
            //logExpressionTree(number,label);
            //new Exception("bad number:"+number).printStackTrace();
            return false;
        }
        return true;
    }
    public static boolean isNotZero(Number number, String label) {
        if (number.doubleValue()==0d) {
            auditTrail(number,FATFormat.table);
            //logExpressionTree(number,label);
            //new Exception("bad number:"+number).printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean isFinite(Number number) {
        if (null==number || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            auditTrail(number,FATFormat.table);
            //logExpressionTree(number,"isFinite");
            //new Exception("bad number:"+number).printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean isPositive(Number number,String label) {
        if (null==number || number.doubleValue()<0 || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            auditTrail(number,FATFormat.table);
            //logExpressionTree(number,label);
            //new Exception(label+" "+number+" is not positive").printStackTrace();
            return false;
        }
        return true;
    }
    
    //TODO rename fun as audit
    
    public static boolean auditTrail(Number keyNumber, FATFormat format) {
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        switch (format) {
            
            case expression:
                break;
            case tree:
                break;
            case table:
                LinkedHashMap<Number, Function> table =  new LinkedHashMap<Number,Function>();
                populateTable(keyNumber, table);
                logTable(table);
                logger.info("called from:{}",stackTrace[2].toString());
                return true;
        }
        return true;
    }
    
    
    private static void logTable(LinkedHashMap<Number, Function> table) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        
        String equals = " = ";
        
        for(Entry<Number,Function> entry : table.entrySet()) {
            //do not report the undefined missing values
            if (entry.getValue().getPrivateIndex()>=0) {

                //first row TODO: compute max length for the values in these columns
                addColumn(builder, 21, entry.getKey().toString());
                builder.append(equals);
                
                addColumn(builder, 13, entry.getValue().label());
                builder.append(equals);
                
                builder.append(entry.getValue())
                       .append("\n");
                
                addColumn(builder,21+equals.length()+13,"");
                builder.append(equals)
                       .append(entry.getValue().decoratedLabel(ringBuffer))
                       .append("\n");
                
                addColumn(builder,21+equals.length()+13,"");
                builder.append(equals)
                       .append(entry.getValue().stackElement())
                       .append("\n");
            }
        }
        logger.info(builder.toString());
        
    }

    public static void addColumn(StringBuilder builder, int minKeyLength,
            String key) {
        builder.append(key);
        int s = minKeyLength-key.length();
        while (--s>=0) {
            builder.append(' ');
        }
    }

    public static boolean logExpressionTree(Number keyNumber, String label) {
        if (keyNumber==null) {
            return false;
        }
        //new Exception(label).printStackTrace();
        StringBuilder builder = new StringBuilder();
        builder.append(label);
        
       // synchronized(expressionMap) {
        
          //TODO: urgent.need tree to print breadth first not depth first and as a table with duplicates removed!!
        
            buildExpressionTree(keyNumber, builder, "");
       // }
        builder.append("\n").append(label);
        
        logger.info(builder.toString());
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private static void populateTable(Number keyNumber, LinkedHashMap<Number,Function> table) {
        Function ex = (Function)ringBuffer.get(keyNumber);
        if (null!=ex) {
            if (!ex.isLabel()) {
                for(Number param: ex.params()) {
                    if (param==keyNumber) {
                        //self referential
                        table.put(keyNumber,ex);
                    } else {
                        if (null!=param) {
                            populateTable(param,table);
                        }
                    }
                }
            }
            //always record my self at the end as a rollup unless self referenced
            
            table.put(keyNumber, ex);
        } else {
            table.put(keyNumber, new Function(keyNumber));
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    private static void buildExpressionTree(Number keyNumber, StringBuilder target, String tab) {
        Function ex = (Function)ringBuffer.get(keyNumber);
        if (null!=ex) {
            String newtab = tab+"    ";
            
            if (!ex.isLabel()) {
                for(Number param: ex.params()) {
                    if (param==keyNumber) {
                        target.append(newtab).append(ex.toString()).append('\n');
                    } else {
                        if (null!=param) {
                            buildExpressionTree(param,target,newtab);
                        }
                    }
                }
            }
            //always record my self at the end as a rollup
            target.append('\n');
            target.append(tab);
            
            //Expression label = (Expression)labelMap.get(keyNumber);
            //if (null!=label) {
            //    String labelText = label.localExpressionText(expressionMap);
             //   target.append(labelText).append('=');
            //} 
            target.append(keyNumber.toString()).append('=');
            String label = ex.label();
            if (null!=label) {
                target.append(label).append('=');
            }
            
            target.append(ex.toString());//localExpressionText(expressionMap));//shows values 
        } else {
            target.append("unable to find "+keyNumber);
        }
    }
    
    public static boolean consistantExpression(Number keyNumber) {
        //TODO: deep analysis of each expression step to ensure dev matches work accomplished.
        
        return true;
    }
    
    public final static Double label(double value, String label) {
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,Function.labelWrap,boxed));
        return boxed;
    }
    
    /*
     * Multiple keyNumbers may be given the same label but each keyNumber may only have one label
     * label to keys is one-to-many 
     * no two sets of labels may ever intersect 
     * 
     */
    public final static boolean saveLabel(Number keyNumber, String label) {
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label,Function.labelWrap,keyNumber);
        //return saveExpression(keyNumber,label,Function.labelWrap,keyNumber);
    }

    
    public final static boolean saveExpression(  Number keyNumber,
                                                    String label,
                                                    String expressionText) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText);
    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number p1) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1);

    }
    
    public final static boolean saveExpression(Number keyNumber,
                                                  String label,
                                                  String expressionText,
                                                  Number p1, Number p2) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1, p2);

    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number p1,Number p2,Number p3) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1, p2, p3);

    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number p1,Number p2,Number p3,Number p4) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1, p2, p3, p4);

    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number p1,Number p2,Number p3,Number p4,Number p5) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1, p2, p3, p4, p5);

    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number p1,Number p2,Number p3,Number p4,Number p5,Number p6) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1, p2, p3, p4, p5, p6);

    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number p1,Number p2,Number p3,Number p4,Number p5,Number p6, Number p7) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, p1, p2, p3, p4, p5, p6, p7);

    }
    
    public final static boolean saveExpression(  Number keyNumber,
            String label,
            String expressionText,
            Number[] params) {
        //save expression quickly and get out, do no heavy work here
        return ringBuffer.findFuncShell(new WeakReference<Number>(keyNumber)).init(label, expressionText, params);

    }
    
    
    //TODO: rename these as PURE? as a sort of attribute flag for testing
    //TODO: keep the constraint of each input number for the bounds testing of this function!
    //TODO: go back to generic T so we can use booleans and other classes without side effects.
    //TODO: assume pure functions everwhere and justify with comments the places where they are not used.
    
    //TODO: rename fun to audit()
    //function audit trail.
    
    //NOTE:constraints are separate calls with their own assert, save happens first so we can look up source params
    
    public final static Double fun(double value, String label, String expressionText, Number p1) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1));
        return boxed;
        
    }
    
    //  fun.result
    //  fun.constraint().result  TODO: rules for asserting bounds, may be source for autmoated unit tests
    //  fun.metaData().result    TODO: provides GUID and then line numbers, call perf numbs, param distribution
    //  @attribute may be the best way to inject these identifiers at compile time.
    // or use to confirm the signature of each fun is unique eg (label+text+argCount+constraint) 
    //        just assume unique and as we can afford it confirm by getting line numbers?
    
    //TODO: two modes for constraint, stopping and logging
    

    
    public final static Double fun(double value, String label, String expressionText, Number p1, Number p2) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1,p2));
        return boxed;
        
    }
       
    public final static Double fun(double value, String label, String expressionText, Number p1, Number p2, Number p3) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1,p2,p3));
        return boxed;
        
    }
  
    
    public final static Double fun(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1,p2,p3,p4));
        return boxed;
        
    }
   
    
    public final static Double fun(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1,p2,p3,p4,p5));
        return boxed;
        
    }
 
    
    public final static Double fun(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1,p2,p3,p4,p5,p6));
        return boxed;
        
    }

    
    public final static Double fun(double value, String label, String expressionText, Number p1, Number p2, Number p3, Number p4, Number p5, Number p6, Number p7) {
        
        Double boxed = new Double(value);
        assert(ringBuffer.findFuncShell(new WeakReference<Number>(boxed)).init(label,expressionText,p1,p2,p3,p4,p5,p6,p7));
        return boxed;
        
    }


}
