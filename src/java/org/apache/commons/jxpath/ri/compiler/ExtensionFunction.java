/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jxpath/src/java/org/apache/commons/jxpath/ri/compiler/ExtensionFunction.java,v 1.10 2003/03/11 00:59:22 dmitri Exp $
 * $Revision: 1.10 $
 * $Date: 2003/03/11 00:59:22 $
 *
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Plotnix, Inc,
 * <http://www.plotnix.com/>.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;

import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.QName;

/**
 * Represents  an element of the parse tree representing an extension function
 * call.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 1.10 $ $Date: 2003/03/11 00:59:22 $
 */
public class ExtensionFunction extends Operation {

    private QName functionName;

    public ExtensionFunction(QName functionName, Expression args[]) {
        super(args);
        this.functionName = functionName;
    }

    public QName getFunctionName() {
        return functionName;
    }

    /**
     * An extension function gets the current context, therefore it MAY be
     * context dependent.
     */
    public boolean computeContextDependent() {
        return true;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(functionName);
        buffer.append('(');
        Expression args[] = getArguments();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(args[i]);
            }
        }
        buffer.append(')');
        return buffer.toString();
    }
    
    public Object compute(EvalContext context) {
        return computeValue(context);
    }

    public Object computeValue(EvalContext context) {
        Object[] parameters = null;
        if (args != null) {
            parameters = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                parameters[i] = convert(args[i].compute(context));
            }
        }

        Function function =
            context.getRootContext().getFunction(functionName, parameters);
        if (function == null) {
            throw new JXPathException(
                "No such function: "
                    + functionName
                    + Arrays.asList(parameters));
        }

        return function.invoke(context, parameters);
    }
    
    private Object convert(Object object) {
        if (object instanceof EvalContext) {
            return ((EvalContext) object).getNodeSet();
        }
        return object;
    }  
}