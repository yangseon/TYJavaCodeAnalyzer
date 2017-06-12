/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package com.github.javaparser.ast.expr;

import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.nodeTypes.NodeWithExpression;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.InstanceOfExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * Usage of the instanceof operator.
 * <br/><code>tool instanceof Drill</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class InstanceOfExpr extends Expression implements NodeWithType<InstanceOfExpr, ReferenceType<?>>, NodeWithExpression<InstanceOfExpr> {

    private Expression expression;

    private ReferenceType<?> type;

    public InstanceOfExpr() {
        this(null, new NameExpr(), new ClassOrInterfaceType());
    }

    @AllFieldsConstructor
    public InstanceOfExpr(final Expression expression, final ReferenceType<?> type) {
        this(null, expression, type);
    }

    public InstanceOfExpr(final Range range, final Expression expression, final ReferenceType<?> type) {
        super(range);
        setExpression(expression);
        setType(type);
        
        //ysryu
        this.codeLoc = 0;

        Node firstNode = null, lastNode = null;
        
        if (this.expression != null && this.expression.getRange().isPresent()) {
        	this.codeLoc += this.expression.getCodeLoc();
        	this.complexity += (this.expression.getComplexity() - 1);
        	
        	if(firstNode == null) { 
             	firstNode = this.expression;
            }
            if(lastNode == null) {
             	lastNode = this.expression;
            }
        }
        
        if(firstNode != null && firstNode.getRange().isPresent()) {
            if(firstNode.getBeginLine() > this.getBeginLine()) {
        		this.codeLoc++;
        	}
        }
      
        if(lastNode != null && lastNode.getRange().isPresent()) {
        	if(lastNode.getEndLine() < this.getEndLine()) {
        		this.codeLoc++;
        	}
        }
    }

    @Override
    public <R, A> R accept(final GenericVisitor<R, A> v, final A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(final VoidVisitor<A> v, final A arg) {
        v.visit(this, arg);
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public ReferenceType<?> getType() {
        return type;
    }

    @Override
    public InstanceOfExpr setExpression(final Expression expression) {
        assertNotNull(expression);
        notifyPropertyChange(ObservableProperty.EXPRESSION, this.expression, expression);
        if (this.expression != null)
            this.expression.setParentNode(null);
        this.expression = expression;
        setAsParentNodeOf(expression);
        return this;
    }

    @Override
    public InstanceOfExpr setType(final ReferenceType<?> type) {
        assertNotNull(type);
        notifyPropertyChange(ObservableProperty.TYPE, this.type, type);
        if (this.type != null)
            this.type.setParentNode(null);
        this.type = type;
        setAsParentNodeOf(type);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public InstanceOfExpr clone() {
        return (InstanceOfExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public InstanceOfExprMetaModel getMetaModel() {
        return JavaParserMetaModel.instanceOfExprMetaModel;
    }
}
