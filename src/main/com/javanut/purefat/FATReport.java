package com.javanut.purefat;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import com.javanut.purefat.impl.Function;
import com.javanut.purefat.impl.FunctionAuditTrail;

public enum FATReport {
   table {
       private final String EQUALS_SYMBOL = " = ";
       
    @Override
    public boolean log(Logger logger, FunctionAuditTrail functionAuditTrail, Number keyNumber, StackTraceElement[] stackTrace) {
        LinkedHashMap<Number, Function> table =  new LinkedHashMap<Number,Function>();
        populateTable(functionAuditTrail, keyNumber, table);
        logTable(logger, functionAuditTrail, table);
        logger.info("called from: {}",firstExternalLocation(stackTrace));
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private void populateTable(FunctionAuditTrail functionAuditTrail, Number keyNumber, LinkedHashMap<Number,Function> table) {
        Function ex = (Function)functionAuditTrail.get(keyNumber);
        if (null!=ex) {
            if (!ex.isLabel()) {
                for(Number param: ex.params()) {
                    if (param==keyNumber) {
                        //self referential
                        table.put(keyNumber,ex);
                    } else {
                        if (null!=param) {
                            populateTable(functionAuditTrail,param,table);
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
    
    private void logTable(Logger logger, FunctionAuditTrail functionAuditTrail, LinkedHashMap<Number, Function> table) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        
        
        for(Entry<Number,Function> entry : table.entrySet()) {
            //do not report the undefined missing values
            if (entry.getValue().getPrivateIndex()>=0) {

                //first row TODO: compute max length for the values in these columns
                leftJustify(builder, 21, entry.getKey().toString());
                builder.append(EQUALS_SYMBOL);
                
                leftJustify(builder, 13, entry.getValue().label());
                builder.append(EQUALS_SYMBOL);
                
                builder.append(entry.getValue())
                       .append("\n");
                
                leftJustify(builder,21+EQUALS_SYMBOL.length()+13,"");
                builder.append(EQUALS_SYMBOL)
                       .append(labelInterpolate(functionAuditTrail, entry.getValue()))
                       .append("\n");
                
                leftJustify(builder,21+EQUALS_SYMBOL.length()+13,"");
                builder.append(EQUALS_SYMBOL)
                       .append(functionAuditTrail.metaData(entry.getValue()).stackElement())
                       .append("\n");
            }
        }
        logger.info(builder.toString());
        
    }
    
}, 
   tree {
    @Override
    public boolean log(Logger logger, FunctionAuditTrail functionAuditTrail, Number keyNumber, StackTraceElement[] stackTrace) {
        if (keyNumber==null) {
            return false;
        }
        //new Exception(label).printStackTrace();
        StringBuilder builder = new StringBuilder();
        String label="";
        builder.append(label);
        
       // synchronized(expressionMap) {
        
          //TODO: urgent.need tree to print breadth first not depth first and as a table with duplicates removed!!
        
            buildExpressionTree(functionAuditTrail,keyNumber, builder, "");
       // }
        builder.append("\n").append(label);
        
        logger.info(builder.toString());
        return true;
    }
    

    
    
    
    @SuppressWarnings("unchecked")
    private void buildExpressionTree(FunctionAuditTrail functionAuditTrail, Number keyNumber, StringBuilder target, String tab) {
        Function ex = (Function)functionAuditTrail.get(keyNumber);
        if (null!=ex) {
            String newtab = tab+"    ";
            
            if (!ex.isLabel()) {
                for(Number param: ex.params()) {
                    if (param==keyNumber) {
                        target.append(newtab).append(ex.toString()).append('\n');
                    } else {
                        if (null!=param) {
                            buildExpressionTree(functionAuditTrail,param,target,newtab);
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
    
}, 
   expression {
    @Override
    public boolean log(Logger logger, FunctionAuditTrail functionAuditTrail, Number keyNumber, StackTraceElement[] stackTrace) {

            Function expression = (Function) functionAuditTrail.get(keyNumber);
            if (null!=expression) {
                expression.log(keyNumber.toString()+"=",logger);//TODO: deep log and shallow log?
                return true;
            } else {
                return false;
            }

    }
};

public abstract boolean log(Logger logger, FunctionAuditTrail functionAuditTrail, Number keyNumber, StackTraceElement[] stackTrace);

private static final void leftJustify(StringBuilder builder,
        int minKeyLength,
        String key) {
    builder.append(key);
    int s = minKeyLength-key.length();
    while (--s>=0) {
         builder.append(' ');
    }
}

private static final String firstExternalLocation(StackTraceElement[] stackTrace) {
    int i = 0;//will never be the one at zero 
    while (++i<stackTrace.length) {
        if (-1==stackTrace[i].getClassName().indexOf(".purefat.") &&
            -1==stackTrace[i].getClassName().indexOf("java.lang.Thread") ) {
            return stackTrace[i].toString();
        }
    }
    return "Unknown";
}


private static String labelInterpolate(FunctionAuditTrail functionAuditTrail, Function fun) {
    Number[] parms = fun.params();
    int i = parms.length;
    String[] labels = new String[i];
    while (--i>=0) {
        Function f = functionAuditTrail.get(parms[i],fun);
        labels[i] = (null==f ? parms[i].toString() : f.label() );
    }
    return MessageFormatter.arrayFormat(fun.text(), labels).getMessage();
}

}
