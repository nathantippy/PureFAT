package com.javanut.purefat;

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
     
     
     */

    private static final Logger logger = LoggerFactory.getLogger(PureFAT.class);
    private static final RingBuffer ringBuffer = new RingBuffer(1000000);

    
    //TODO: if this can be done as an annotation then this might be a great open source project
    //TODO: build parser validation so expressions can be used by wolframalpha
    //TODO: build second execute to validate answer of first and implmentation matches documentation.
    //TODO: because of precidence this must be parsed and confirm that it has surrounding () of highest precidence
    //TODO: when logging tree need more of a "table" look to help colapse the repeats 
    //TODO: need repeat count when display table to easily find reductions.
    //TODO: dual row table layout
    
    public static void useLessRAM(boolean useLessRAM) {
        ringBuffer.useLessRAM(useLessRAM);
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
    
    public static boolean isFinite(Number number, String label) {
        if (null==number || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            logExpressionTree(number,label);
            new Exception("bad number:"+number).printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean isPositive(Number number,String label) {
        if (null==number || number.doubleValue()<0 || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())) {
            logExpressionTree(number,label);
            new Exception(label+" "+number+" is not positive").printStackTrace();
            return false;
        }
        return true;
    }
    //TODO: its ok if this is veru slow liniear lookup.
    public static boolean logExpressionTree(Number keyNumber, String label) {
        if (keyNumber==null) {
            return false;
        }
        //new Exception(label).printStackTrace();
        StringBuilder builder = new StringBuilder();
        builder.append(label);
        
       // synchronized(expressionMap) {
            buildExpressionTree(keyNumber, builder, "");
       // }
        builder.append("\n").append(label);
        
        logger.info(builder.toString());
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private static void buildExpressionTree(Number keyNumber, StringBuilder target, String tab) {
        Function ex = (Function)ringBuffer.get(keyNumber);
        if (null!=ex) {
            String newtab = tab+"    ";
            
            if (!ex.isLabel()) {
                for(Number param: ex.parameters()) {
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
    
    /*
     * Multiple keyNumbers may be given the same label but each keyNumber may only have one label
     * label to keys is one-to-many 
     * no two sets of labels may ever intersect 
     * 
     */
    public static boolean saveLabel(Number keyNumber, String label) {
        return saveExpression(keyNumber,label,Function.labelWrap,keyNumber);
    }
    
    @SuppressWarnings("unchecked")
    public final static boolean saveExpression(Number keyNumber,
                                            String expressionText,
                                            Number ... param) {
        return saveExpression(keyNumber,expressionText,expressionText,param);
    }
    
    //TODO: this must be very very fast, hash identity compute is taking too much time.
    //weak btree may be better. 
    @SuppressWarnings("unchecked")
    public final static boolean saveExpression(Number keyNumber,
                                            String label,
                                            String expressionText,
                                            Number ... param) {
        //save expression quickly and get out, do no heavy work here
        ringBuffer.put(keyNumber, new Function(label, expressionText, param));
        return true;
    }
    
}
