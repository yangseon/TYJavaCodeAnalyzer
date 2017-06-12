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
import com.github.javaparser.metamodel.ArrayAccessExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * Array brackets [] being used to get a value from an array.
 * In <br/><code>getNames()[15*15]</code> the name expression is getNames() and the index expression is 15*15.
 *
 * @author Julio Vilmar Gesser
 */
public final class ArrayAccessExpr extends Expression {

    private Expression name;

    private Expression index;

    public ArrayAccessExpr() {
        this(null, new NameExpr(), new IntegerLiteralExpr());
    }

    @AllFieldsConstructor
    public ArrayAccessExpr(Expression name, Expression index) {
        this(null, name, index);
    }

    public ArrayAccessExpr(Range range, Expression name, Expression index) {
        super(range);
        setName(name);
        setIndex(index);
        
        //ysryu
        this.codeLoc = 0;
        Node firstNode = null, lastNode = null;
        
        if (this.name != null && this.name.getRange().isPresent()) {
        	this.codeLoc += this.name.getCodeLoc();
        	this.complexity += (this.name.getComplexity() -1);
        	
        	if(firstNode == null) { 
             	firstNode = this.name;
            }
            if(lastNode == null) {
             	lastNode = this.name;
            }
        }
        
        if (this.index != null && this.index.getRange().isPresent()) {
        	this.codeLoc += this.index.getCodeLoc();
        	this.complexity += (this.index.getComplexity() -1);
        	
        	 if (lastNode != null && lastNode.getRange().isPresent()) {
         		if(lastNode.getEndLine() == this.index.getBeginLine()) {
             		this.codeLoc--;
             	}
         	}
        	 if(firstNode == null) {
         		firstNode = this.index;
         	} else {
         		if(firstNode.getBeginLine() > this.index.getBeginLine()) {
         			firstNode = this.index;
         		}
         	}
             
             if(lastNode == null) {
             	lastNode = this.index;
             } else {
             	if(lastNode.getEndLine() < this.index.getEndLine()) {
             		lastNode = this.index;
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

    public Expression getIndex() {
        return index;
    }

    public Expression getName() {
        return name;
    }

    public ArrayAccessExpr setIndex(final Expression index) {
        assertNotNull(index);
        notifyPropertyChange(ObservableProperty.INDEX, this.index, index);
        if (this.index != null)
            this.index.setParentNode(null);
        this.index = index;
        setAsParentNodeOf(index);
        return this;
    }

    public ArrayAccessExpr setName(final Expression name) {
        assertNotNull(name);
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null)
            this.name.setParentNode(null);
        this.name = name;
        setAsParentNodeOf(name);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public ArrayAccessExpr clone() {
        return (ArrayAccessExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public ArrayAccessExprMetaModel getMetaModel() {
        return JavaParserMetaModel.arrayAccessExprMetaModel;
    }
}
