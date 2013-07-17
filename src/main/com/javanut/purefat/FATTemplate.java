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
package com.javanut.purefat;

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

import com.javanut.purefat.impl.FunMetaData;
import com.javanut.purefat.impl.Function;
import com.javanut.purefat.impl.FunctionAuditTrail;
import com.javanut.purefat.impl.Util;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

public enum FATTemplate {
    table { // TODO: detailed table?

        @Override
        public boolean log(Logger logger,
                FunctionAuditTrail functionAuditTrail, Number keyNumber,
                StackTraceElement[] stackTrace) {
            LinkedHashMap<Number, Function> table = new LinkedHashMap<Number, Function>();
            populateTable(functionAuditTrail, keyNumber, table);
            logTable(logger, functionAuditTrail, table);
            return true;
        }

        private void logTable(Logger logger,
                FunctionAuditTrail functionAuditTrail,
                LinkedHashMap<Number, Function> table) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");

            // TODO: remove label in checks
            // TODO: check the other reports.
            // TODO: can idenical call be rolled up?

            // <Value> = <Expression> <StackElement> <LabelInterpolated>
            List<String> columnValue = new ArrayList<String>();
            int columnWidth = 0;

            List<String> columnExpression = new ArrayList<String>();
            int columnExpressionWidth = 0;

            List<String> columnStackElement = new ArrayList<String>();
            int columnStackElementWidth = 0;

            List<String> columnLabelInterpolated = new ArrayList<String>();
            int columnLabelInterpolatedWidth = 0;

            for (Entry<Number, Function> entry : table.entrySet()) {
                // do not report the undefined missing values
                if (entry.getValue().getPrivateIndex() >= 0) {

                    columnWidth = columnWidth(entry.getKey().toString(),
                            columnValue, columnWidth);
                    columnExpressionWidth = columnWidth(entry.getValue()
                            .toString(), columnExpression,
                            columnExpressionWidth);
                    columnStackElementWidth = columnWidth(functionAuditTrail
                            .metaData(entry.getValue()).stackElement(),
                            columnStackElement, columnStackElementWidth);

                    String interpolated = entry.getValue().labelName()
                            + Util.wrapId(entry.getKey());
                    if (!entry.getValue().isLabel()) {
                        interpolated = interpolated
                                + EQUALS_SYMBOL
                                + idInterpolate(functionAuditTrail,
                                        entry.getValue(), false);
                    }
                    columnLabelInterpolatedWidth = columnWidth(interpolated,
                            columnLabelInterpolated,
                            columnLabelInterpolatedWidth);

                }
            }

            int i = 0;
            while (i < columnValue.size()) {

                leftJustify(builder, columnWidth, columnValue.get(i));
                builder.append(EQUALS_SYMBOL);
                leftJustify(builder, columnExpressionWidth,
                        columnExpression.get(i));
                builder.append(SPACE);
                leftJustify(builder, columnStackElementWidth,
                        columnStackElement.get(i));
                builder.append(SPACE);
                leftJustify(builder, columnLabelInterpolatedWidth,
                        columnLabelInterpolated.get(i));
                builder.append("\n");
                i++;
            }

            logger.info(builder.toString());

        }

    },
    tree { // TODO: still in work, not sure this template is as helpful as it could be.
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
            LinkedHashMap<Number, Function> table = new LinkedHashMap<Number, Function>();
            populateTable(functionAuditTrail, keyNumber, table);
            logTableSummary(logger, functionAuditTrail, table);
            return true;
        }

        private void logTableSummary(Logger logger,
                FunctionAuditTrail functionAuditTrail,
                LinkedHashMap<Number, Function> table) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");

            // <StackElement> <CallCount> <LabelExpression>

            List<String> columnStackElement = new ArrayList<String>();
            int columnStackElementWidth = 0;

            List<AtomicInteger> columnCallCount = new ArrayList<AtomicInteger>();
            int columnCallCountWidth = 0;

            List<String> columnLabelInterpolated = new ArrayList<String>();
            int columnLabelInterpolatedWidth = 0;
            
            List<Number> columnNumber = new ArrayList<Number>();
            List<Function> columnFunction = new ArrayList<Function>();

            Map<String, AtomicInteger> done = new HashMap<String, AtomicInteger>();
            for (Entry<Number, Function> entry : table.entrySet()) {
                // do not report the undefined missing values
                if (entry.getValue().getPrivateIndex() >= 0) {

                    // each unique interpolated string gets a line
                    String interpolated = entry.getValue().labelTag();
                    if (!entry.getValue().isLabel()) {
                        interpolated = interpolated
                                + EQUALS_SYMBOL
                                + idInterpolate(functionAuditTrail,
                                                entry.getValue(), true);
                    }

                    if (!done.containsKey(interpolated)) {
                        AtomicInteger callCount = new AtomicInteger(1);

                        columnStackElementWidth = columnWidth(
                                functionAuditTrail.metaData(entry.getValue())
                                        .stackElement(), columnStackElement,
                                columnStackElementWidth);

                        columnCallCountWidth = columnWidth(callCount,
                                columnCallCount, columnCallCountWidth);

                        columnLabelInterpolatedWidth = columnWidth(
                                interpolated, columnLabelInterpolated,
                                columnLabelInterpolatedWidth);
                        
                        columnFunction.add(entry.getValue());
                        columnNumber.add(entry.getKey());

                        done.put(interpolated, callCount);

                    } else {
                        columnCallCountWidth = Math.max(
                                columnCallCountWidth,
                                Integer.toString(
                                        done.get(interpolated)
                                                .incrementAndGet()).length());
                    }
                }
            }

            int i = 0;
            while (i < columnStackElement.size()) {

                leftJustify(builder, columnStackElementWidth,
                        columnStackElement.get(i));
                builder.append(SPACE);
                leftJustify(builder, columnCallCountWidth,
                        columnCallCount.get(i).toString());
                builder.append(SPACE);
                leftJustify(builder, columnLabelInterpolatedWidth,
                        columnLabelInterpolated.get(i));
                builder.append(SPACE);
                if(1==columnCallCount.get(i).intValue()) {
                    //there is only one usage so display the real values.
                    builder.append(columnNumber.get(i).toString()).append(EQUALS_SYMBOL);
                    builder.append(columnFunction.get(i).toString());
                }
                
                builder.append("\n");
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
            LinkedHashMap<Number, Function> table) {
        Function ex = (Function) functionAuditTrail.get(keyNumber);
        if (null != ex) {
            if (!ex.isLabel()) {
                for (Number param : ex.params()) {
                    if (param == keyNumber) {
                        // self referential
                        table.put(keyNumber, ex);
                    } else {
                        if (null != param) {
                            populateTable(functionAuditTrail, param, table);
                        }
                    }
                }
            }
            table.put(keyNumber, ex);
        } else {
            table.put(keyNumber, new Function(keyNumber));
        }
    }

}
