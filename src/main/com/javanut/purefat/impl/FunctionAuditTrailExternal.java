package com.javanut.purefat.impl;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.MessageFormatter;

public class FunctionAuditTrailExternal implements FunctionAuditTrail {

    
    private final Logger pureFATLogger;
    
    public FunctionAuditTrailExternal(Logger logger) {
        pureFATLogger = logger;
    }
    
    @Override
    public boolean save(Number number, String label, String expression,
                         Number p1) {
        save(number,label,expression,new Number[]{p1});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2) {
        save(number,label,expression,new Number[]{p1,p2});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2, Number p3) {
        save(number,label,expression,new Number[]{p1,p2,p3});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2, Number p3, Number p4) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4,p5});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
            Number p1, Number p2, Number p3, Number p4, Number p5, Number p6) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number p1, Number p2, Number p3, Number p4,
                        Number p5, Number p6, Number p7) {
        save(number,label,expression,new Number[]{p1,p2,p3,p4,p5,p6,p7});
        return true;
    }

    @Override
    public boolean save(Number number, String label, String expression,
                        Number[] params) {
        
        StringBuilder sb = new StringBuilder(100);
        
        Marker marker = marker(label,expression);
        
        String id = wrapId(number);
        Marker idMarker = MarkerFactory.getMarker(id);
        marker.add(idMarker);
        
        String machineId = Util.instanceId();
        marker.add(MarkerFactory.getMarker(machineId));
        
        sb.append(id);
        wrapExpr(number, expression, sb);
        
        String[] pIdArray = new String[params.length];
        int i = 0;
        while (i<params.length) {
            String pId = wrapId(params[i]);
            pIdArray[i] = pId;
            Marker pIdMarker = MarkerFactory.getMarker(pId);
            idMarker.add(pIdMarker);
        }
        sb.append(" ");
        sb.append(MessageFormatter.arrayFormat(expression, pIdArray));
        
        pureFATLogger.debug(marker, sb.toString(),  params);
        
        return true;
    }
    
    
    private final void wrapExpr(Number number, String expression, StringBuilder sb) {
        sb.append(number).append('=').append(expression).append(' ');
    }

    private final String wrapId(Number number) {
        return "[PF"+Long.toHexString(System.identityHashCode(number))+']';
    }

    private Marker marker(String label, String expression) {
        return MarkerFactory.getMarker(label+':'+expression);
    }

    
    @Override
    public Function get(Number key) {
        // TODO: query logback by this key?
        return null;
    }

    @Override
    public Function get(Number key, Function startHere) {
        // TODO: query logback by this key?
        return null;
    }

    @Override
    public FunMetaData metaData(Function fun) {
        // TODO: query logback by this key? Get the line number data?
        return null;
    }

    @Override
    public boolean flush(Number key) {
        return true;
    }


}
