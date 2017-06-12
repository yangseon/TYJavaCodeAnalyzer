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
import com.github.javaparser.metamodel.ConditionalExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * An if-then or if-then-else construct.
 * In <code>if(a){b}else{c}</code>, a is the condition, b is thenExpr, and c is elseExpr.
 *
 * @author Julio Vilmar Gesser
 */
public final class ConditionalExpr extends Expression {

    private Expression condition;

    private Expression thenExpr;

    private Expression elseExpr;

    public ConditionalExpr() {
        this(null, new BooleanLiteralExpr(), new StringLiteralExpr(), new StringLiteralExpr());
    }

    @AllFieldsConstructor
    public ConditionalExpr(Expression condition, Expression thenExpr, Expression elseExpr) {
        this(null, condition, thenExpr, elseExpr);
    }

    public ConditionalExpr(Range range, Expression condition, Expression thenExpr, Expression elseExpr) {
        super(range);
        setCondition(condition);
        setThenExpr(thenExpr);
        setElseExpr(elseExpr);
        
        //ysryu
        this.codeLoc = 0;
        // Increase complexity - Selection    if, else, case, default.
        this.complexity++;
        
        Node firstNode = null, lastNode = null;
        if (this.condition != null && this.condition.getRange().isPresent())  {
        	this.codeLoc += this.condition.getCodeLoc();
        	this.complexity += (this.condition.getComplexity() - 1);
        	
        	if(firstNode == null) { 
             	firstNode = this.condition;
            }
            if(lastNode == null) {
             	lastNode = this.condition;
            }
        }
        
        if(this.thenExpr != null && this.thenExpr.getRange().isPresent()) {     
    		this.codeLoc += this.thenExpr.getCodeLoc();
    		this.complexity += (this.thenExpr.getComplexity() - 1);
    		
    		if (lastNode != null && lastNode.getRange().isPresent()) {
        		if(lastNode.getEndLine() == this.thenExpr.getBeginLine()) {
            		this.codeLoc--;
            	}
        	}
            if(firstNode == null) {
        		firstNode = this.thenExpr;
        	} else {
        		if(firstNode.getBeginLine() > this.thenExpr.getBeginLine()) {
        			firstNode = this.thenExpr;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.thenExpr;
            } else {
            	if(lastNode.getEndLine() < this.thenExpr.getEndLine()) {
            		lastNode = this.thenExpr;
        		}
            }
    		
        }
        
        if(this.elseExpr != null && this.elseExpr.getRange().isPresent()) {     
     		this.codeLoc += this.elseExpr.getCodeLoc();
     		this.complexity += (this.elseExpr.getComplexity() - 1);
     		
     		if (lastNode != null && lastNode.getRange().isPresent()) {
        		if(lastNode.getEndLine() == this.elseExpr.getBeginLine()) {
            		this.codeLoc--;
            	}
        	}
            if(firstNode == null) {
        		firstNode = this.elseExpr;
        	} else {
        		if(firstNode.getBeginLine() > this.elseExpr.getBeginLine()) {
        			firstNode = this.elseExpr;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.elseExpr;
            } else {
            	if(lastNode.getEndLine() < this.elseExpr.getEndLine()) {
            		lastNode = this.elseExpr;
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

    public Expression getCondition() {
        return condition;
    }

    public Expression getElseExpr() {
        return elseExpr;
    }

    public Expression getThenExpr() {
        return thenExpr;
    }

    public ConditionalExpr setCondition(final Expression condition) {
        assertNotNull(condition);
        notifyPropertyChange(ObservableProperty.CONDITION, this.condition, condition);
        if (this.condition != null)
            this.condition.setParentNode(null);
        this.condition = condition;
        setAsParentNodeOf(condition);
        return this;
    }

    public ConditionalExpr setElseExpr(final Expression elseExpr) {
        assertNotNull(elseExpr);
        notifyPropertyChange(ObservableProperty.ELSE_EXPR, this.elseExpr, elseExpr);
        if (this.elseExpr != null)
            this.elseExpr.setParentNode(null);
        this.elseExpr = elseExpr;
        setAsParentNodeOf(elseExpr);
        return this;
    }

    public ConditionalExpr setThenExpr(final Expression thenExpr) {
        assertNotNull(thenExpr);
        notifyPropertyChange(ObservableProperty.THEN_EXPR, this.thenExpr, thenExpr);
        if (this.thenExpr != null)
            this.thenExpr.setParentNode(null);
        this.thenExpr = thenExpr;
        setAsParentNodeOf(thenExpr);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public ConditionalExpr clone() {
        return (ConditionalExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public ConditionalExprMetaModel getMetaModel() {
        return JavaParserMetaModel.conditionalExprMetaModel;
    }
}
