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
package com.ociweb.purefat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import com.ociweb.purefat.impl.FunMetaData;
import com.ociweb.purefat.impl.Function;
import com.ociweb.purefat.impl.FunctionAuditTrail;
import com.ociweb.purefat.impl.Util;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

public enum FATTemplate {
    table {

        @Override
        public boolean log(Logger logger,
                FunctionAuditTrail functionAuditTrail, Number keyNumber,
                StackTraceElement[] stackTrace) {
            List<Number> columnNumbers = new ArrayList<Number>();
            List<Function> columnFunctions = new ArrayList<Function>();
            populateTable(functionAuditTrail, keyNumber, columnNumbers, columnFunctions);
            logTable(logger, functionAuditTrail, columnNumbers, columnFunctions);
            return true;
        }

        private void logTable(Logger logger,
                FunctionAuditTrail functionAuditTrail,
                List<Number> rawColumnNumbers, 
                List<Function> rawColumnFunctions) {
            StringBuilder builder = new StringBuilder();

            // <Value> = <Expression> <StackElement> <LabelInterpolated>
            List<String> templateColumnValue = new ArrayList<String>();
            int columnWidth = 0;

            List<String> templateColumnExpression = new ArrayList<String>();
            int columnExpressionWidth = 0;

            List<String> templateColumnStackElement = new ArrayList<String>();
            int columnStackElementWidth = 0;

            List<String> templateColumnLabelInterpolated = new ArrayList<String>();
            int columnLabelInterpolatedWidth = 0;

            int idx = 0;
            while (idx<rawColumnNumbers.size()) {
                Number num = rawColumnNumbers.get(idx);
                Function fun = rawColumnFunctions.get(idx);
                
                // do not report the undefined missing values
                if (fun.getPrivateIndex() >= 0) {

                    columnWidth = columnWidth(num.toString(),
                            templateColumnValue, columnWidth);
                    columnExpressionWidth = columnWidth(fun.toString(), templateColumnExpression,
                            columnExpressionWidth);
                    columnStackElementWidth = columnWidth(functionAuditTrail
                            .metaData(fun).stackElement(),
                            templateColumnStackElement, columnStackElementWidth);

                    String interpolated = fun.labelName()
                            + Util.wrapId(num);
                    if (!fun.isLabel()) {
                        interpolated = interpolated
                                + EQUALS_SYMBOL
                                + idInterpolate(functionAuditTrail,
                                        fun, false);
                    }
                    columnLabelInterpolatedWidth = columnWidth(interpolated,
                            templateColumnLabelInterpolated,
                            columnLabelInterpolatedWidth);

                }
                idx++;
            }

            int i = 0;
            while (i < templateColumnValue.size()) {

                builder.append("\n  ");
                leftJustify(builder, columnWidth, templateColumnValue.get(i));
                builder.append(EQUALS_SYMBOL);
                leftJustify(builder, columnExpressionWidth,
                        templateColumnExpression.get(i));
                builder.append(SPACE);
                leftJustify(builder, columnStackElementWidth,
                        templateColumnStackElement.get(i));
                builder.append(SPACE);
                leftJustify(builder, columnLabelInterpolatedWidth,
                        templateColumnLabelInterpolated.get(i));
                i++;
            }

            logger.info(builder.toString());

        }

    },
    tree {
        @Override
        public boolean log(Logger logger,
                FunctionAuditTrail functionAuditTrail, Number keyNumber,
                StackTraceElement[] stackTrace) {
            if (keyNumber == null) {
                return false;
            }
            StringBuilder builder = new StringBuilder();
            String label = "";
            builder.append(label);
            buildExpressionTree(functionAuditTrail, keyNumber, builder, "");
            builder.append("\n").append(label);

            logger.info(builder.toString());
            return true;
        }

        @SuppressWarnings("unchecked")
        private void buildExpressionTree(FunctionAuditTrail functionAuditTrail,
                Number keyNumber, StringBuilder target, String tab) {
            Function ex = (Function) functionAuditTrail.get(keyNumber);
            if (null != ex) {
                String newtab = tab + " ";

                if (!ex.isLabel()) {
                    for (Number param : ex.params()) {
                        if (param == keyNumber) {
                            target.append(newtab).append(ex.toString())
                                    .append('\n');
                        } else {
                            if (null != param) {
                                buildExpressionTree(functionAuditTrail, param,
                                        target, newtab);
                            }
                        }
                    }
                }
                // always record my self at the end as a rollup
                target.append('\n');
                target.append(tab);

                target.append(keyNumber.toString()).append('=');
                String label = ex.labelTag();
                if (null != label) {
                    target.append(label).append('=');
                }

                target.append(ex.toString());
            } else {
                target.append("unable to find " + keyNumber);
            }
        }

    },
    summary {
        @Override
        public boolean log(Logger logger,
                FunctionAuditTrail functionAuditTrail, Number keyNumber,
                StackTraceElement[] stackTrace) {
            List<Number> columnNumbers = new ArrayList<Number>();
            List<Function> columnFunctions = new ArrayList<Function>();
            populateTable(functionAuditTrail, keyNumber, columnNumbers, columnFunctions);
            logTableSummary(logger, functionAuditTrail, columnNumbers, columnFunctions);
            return true;
        }

        private void logTableSummary(Logger logger,
                FunctionAuditTrail functionAuditTrail,
                List<Number> rawColumnNumbers, List<Function> rawColumnFunctions) {
            StringBuilder builder = new StringBuilder();

            // <StackElement> <CallCount> <LabelExpression>

            List<String> templateColumnStackElement = new ArrayList<String>();
            int columnStackElementWidth = 0;

            List<AtomicInteger> templateColumnCallCount = new ArrayList<AtomicInteger>();
            int columnCallCountWidth = 0;

            List<String> templateColumnLabelInterpolated = new ArrayList<String>();
            int columnLabelInterpolatedWidth = 0;
            
            List<Number> templateColumnNumber = new ArrayList<Number>();
            List<Function> templateColumnFunction = new ArrayList<Function>();

            Map<String, AtomicInteger> done = new HashMap<String, AtomicInteger>();
            int idx = 0;
            while (idx<rawColumnNumbers.size()) {
                Number num = rawColumnNumbers.get(idx);
                Function fun = rawColumnFunctions.get(idx);
                
                // do not report the undefined missing values
                if (fun.getPrivateIndex() >= 0) {

                    // each unique interpolated string gets a line
                    String interpolated = fun.labelTag();
                    if (!fun.isLabel()) {
                        interpolated = interpolated
                                + EQUALS_SYMBOL
                                + idInterpolate(functionAuditTrail,
                                                fun, true);
                    }

                    if (!done.containsKey(interpolated)) {
                        AtomicInteger callCount = new AtomicInteger(1);

                        columnStackElementWidth = columnWidth(
                                functionAuditTrail.metaData(fun)
                                        .stackElement(), templateColumnStackElement,
                                columnStackElementWidth);

                        columnCallCountWidth = columnWidth(callCount,
                                templateColumnCallCount, columnCallCountWidth);

                        columnLabelInterpolatedWidth = columnWidth(
                                interpolated, templateColumnLabelInterpolated,
                                columnLabelInterpolatedWidth);
                        
                        templateColumnFunction.add(fun);
                        templateColumnNumber.add(num);

                        done.put(interpolated, callCount);

                    } else {
                        columnCallCountWidth = Math.max(
                                columnCallCountWidth,
                                Integer.toString(
                                        done.get(interpolated)
                                                .incrementAndGet()).length());
                    }
                }
                idx++;
            }

            int i = 0;
            while (i < templateColumnStackElement.size()) {

                builder.append("\n  ");
                leftJustify(builder, columnStackElementWidth,
                        templateColumnStackElement.get(i));
                builder.append(SPACE);
                leftJustify(builder, columnCallCountWidth,
                        templateColumnCallCount.get(i).toString());
                builder.append(SPACE);
                leftJustify(builder, columnLabelInterpolatedWidth,
                        templateColumnLabelInterpolated.get(i));
                builder.append(SPACE);
                if(1==templateColumnCallCount.get(i).intValue()) {
                    //there is only one usage so display the real values.
                    builder.append(templateColumnNumber.get(i).toString()).append(EQUALS_SYMBOL);
                    builder.append(templateColumnFunction.get(i).toString());
                }
                
                i++;
            }

            logger.info(builder.toString());

        }
    },
    expression {
        @Override
        public boolean log(Logger logger,
                FunctionAuditTrail functionAuditTrail, Number keyNumber,
                StackTraceElement[] stackTrace) {

            Function expression = (Function) functionAuditTrail.get(keyNumber);
            if (null != expression) {
                logger.info(keyNumber + EQUALS_SYMBOL
                        + deepBuild(functionAuditTrail, expression));
                return true;
            } else {
                return false;
            }

        }

        private final String deepBuild(FunctionAuditTrail functionAuditTrail,
                Function expression) {

            Number[] pars = expression.params();
            int i = pars.length;
            Object[] array = new Object[i];
            while (--i >= 0) {
                Function pFun = functionAuditTrail.get(pars[i]);
                if (null == pFun || pFun.isLabel()) {
                    array[i] = pars[i];
                } else {
                    array[i] = deepBuild(functionAuditTrail, pFun);
                }
            }
            return MessageFormatter.arrayFormat(expression.text(), array)
                    .getMessage();
        }
    };

    private static final String EQUALS_SYMBOL = " = ";
    private static final String SPACE = "  ";

    public abstract boolean log(Logger logger,
            FunctionAuditTrail functionAuditTrail, Number keyNumber,
            StackTraceElement[] stackTrace);

    private static final void leftJustify(StringBuilder builder,
            int minKeyLength, String key) {
        builder.append(key);
        int s = minKeyLength - key.length();
        while (--s >= 0) {
            builder.append(' ');
        }
    }

    private static final String idInterpolate(
            FunctionAuditTrail functionAuditTrail, Function fun,
            boolean labelOnly) {
        Number[] parms = fun.params();
        int i = parms.length;
        String[] ids = new String[i];
        while (--i >= 0) {
            Function paramFun = functionAuditTrail.get(parms[i], fun);
            if (labelOnly) {
                ids[i] = (null == paramFun ? "${UNKNOWN}" : paramFun.labelTag());
            } else {
                ids[i] = (null == paramFun ? "" : paramFun.labelName())
                        + Util.wrapId(parms[i]);
            }
        }
        return MessageFormatter.arrayFormat(fun.text(), ids).getMessage();
    }

    private static final <T> int columnWidth(T value, List<T> column,
            int columnWidth) {
        column.add(value);
        return Math.max(columnWidth, value.toString().length());
    }

    private static final void populateTable(
            FunctionAuditTrail functionAuditTrail, Number keyNumber,
            List<Number> columnNumbers, List<Function> columnFunctions) {
        
        Function ex = (Function) functionAuditTrail.get(keyNumber);
        if (null != ex) {
            if (!ex.isLabel()) {
                for (Number param : ex.params()) {
                    if (param == keyNumber) {
                        // self referential
                        columnNumbers.add(param);
                        columnFunctions.add(ex);
                    } else {
                        if (null != param) {
                            populateTable(functionAuditTrail, param, 
                                          columnNumbers, columnFunctions);
                        }
                    }
                }
            }
            columnNumbers.add(keyNumber);
            columnFunctions.add(ex);
        } else {
            columnNumbers.add(keyNumber);
            columnFunctions.add(new Function(keyNumber));
        }
    }

}
