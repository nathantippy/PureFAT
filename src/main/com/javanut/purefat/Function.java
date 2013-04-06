package com.javanut.purefat;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

class Function extends Number {

  //TODO: add std dev support into expression
    final static String        labelWrap = "{}";
    private final String       text;
    private final Number[]     origParam;//may be T or may be Expression TODO: remove this is may not be needed after all.
    private final String       label;
    
    private int                     privateIdx;
    private Boolean                 validated;//once validated no need to validate a second time.
    private Double                  eval;//once evaluated no need to evaluate a second time
    
    public Function(String expressionText, Number ... expressionParams) {
        assert(!expressionText.isEmpty());
        this.text = expressionText;
        this.origParam = expressionParams;
        this.label = null;
    }
    
    public Function(String label, String expressionText, Number ...expressionParams) {
        assert(!expressionText.isEmpty());
        this.text = expressionText;
        this.origParam = expressionParams;
        this.label = label;
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
    
    public String toString() {
        return MessageFormatter.arrayFormat(text, origParam).getMessage();
    }
    
    public String localExpressionText( Map<Number,Function> childrenMap) { //TODO: must parse and eval this
        Number[] param = deepParamArray(childrenMap);
        return MessageFormatter.arrayFormat(text, param).getMessage();
    }

    private Number[] deepParamArray(Map<Number, Function> childrenMap) {
        Number[] param = new Number[origParam.length]; //TOOD:keep and lazy init.
        int i = origParam.length;
        while (--i>=0) {
            if (null!=origParam[i]) {
                param[i] = childrenMap.get(origParam[i]);
            }
            if (null==param[i]) {
                param[i] = origParam[i]; //child not found so use constant parameter
            }
        }
        return param;
    }
    
   // public String localLabelText()

    public void log(String label, Logger logger) {
        logger.info(label+' '+text, origParam);
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

    public Number[] parameters() {
        return origParam;
    }

    public String label() {
        return "${"+label+"}";
    }

    public boolean isLabel() {
        return text==labelWrap;//special instance just for constant labels
    }


    public void setPrivateIndex(int privateIdx) {
        this.privateIdx=privateIdx;
    }
    public int getPrivateIndex() {
        return privateIdx;
    }

}
