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
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.AssignExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.printer.Printable;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * An assignment expression. It supports the operators that are found the the AssignExpr.Operator enum.
 * <br/><code>a=5</code>
 * <br/><code>time+=500</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class AssignExpr extends Expression {

    public enum Operator implements Printable {

        ASSIGN("="), PLUS("+="), MINUS("-="), MULTIPLY("*="), DIVIDE("/="), AND("&="), OR("|="), XOR("^="), REMAINDER("%="), LEFT_SHIFT("<<="), SIGNED_RIGHT_SHIFT(">>="), UNSIGNED_RIGHT_SHIFT(">>>=");

        private final String codeRepresentation;

        Operator(String codeRepresentation) {
            this.codeRepresentation = codeRepresentation;
        }

        public String asString() {
            return codeRepresentation;
        }
    }

    private Expression target;

    private Expression value;

    private Operator operator;

    public AssignExpr() {
        this(null, new NameExpr(), new StringLiteralExpr(), Operator.ASSIGN);
    }

    @AllFieldsConstructor
    public AssignExpr(Expression target, Expression value, Operator operator) {
        this(null, target, value, operator);
    }

    public AssignExpr(Range range, Expression target, Expression value, Operator operator) {
        super(range);
        setTarget(target);
        setValue(value);
        setOperator(operator);
        
        // ysryu
        this.codeLoc = 0;
        
        Node firstNode = null, lastNode = null;
        
        if (this.target != null && this.target.getRange().isPresent()) {
        	this.codeLoc += this.target.getCodeLoc();
        	this.complexity += (this.target.getComplexity() - 1);
        	
            if(firstNode == null) { 
            	firstNode = this.target;
            }
            if(lastNode == null) {
            	lastNode = this.target;
            }
        }
        
        if (this.value != null && this.value.getRange().isPresent()) {
        	this.codeLoc += this.value.getCodeLoc();
        	this.complexity += (this.value.getComplexity() - 1);
        	
            if(lastNode != null) {
				if(lastNode.getEndLine() == this.value.getBeginLine()) {
					this.codeLoc--;
				}
			}
            
            if(firstNode == null) {
        		firstNode = this.value;
        	} else {
        		if(firstNode.getBeginLine() > this.value.getBeginLine()) {
        			firstNode = this.value;
        		}
        	}
        	
        	
        	if(lastNode == null) {
            	lastNode = this.value;
            } else {
            	if(lastNode.getEndLine() < this.value.getEndLine()) {
            		lastNode = this.value;
        		}
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
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }

    public AssignExpr setOperator(final Operator operator) {
        assertNotNull(operator);
        notifyPropertyChange(ObservableProperty.OPERATOR, this.operator, operator);
        this.operator = operator;
        return this;
    }

    public AssignExpr setTarget(final Expression target) {
        assertNotNull(target);
        notifyPropertyChange(ObservableProperty.TARGET, this.target, target);
        if (this.target != null)
            this.target.setParentNode(null);
        this.target = target;
        setAsParentNodeOf(target);
        return this;
    }

    public AssignExpr setValue(final Expression value) {
        assertNotNull(value);
        notifyPropertyChange(ObservableProperty.VALUE, this.value, value);
        if (this.value != null)
            this.value.setParentNode(null);
        this.value = value;
        setAsParentNodeOf(value);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public AssignExpr clone() {
        return (AssignExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public AssignExprMetaModel getMetaModel() {
        return JavaParserMetaModel.assignExprMetaModel;
    }
}