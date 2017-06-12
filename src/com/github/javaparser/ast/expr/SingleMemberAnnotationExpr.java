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
import com.github.javaparser.metamodel.SingleMemberAnnotationExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. Calculate complexity 
 * -. set codeLoc
 */
/**
 * An annotation that has a single value. <br/><code>@Count(15)</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class SingleMemberAnnotationExpr extends AnnotationExpr {

    private Expression memberValue;

    public SingleMemberAnnotationExpr() {
        this(null, new Name(), new StringLiteralExpr());
    }

    @AllFieldsConstructor
    public SingleMemberAnnotationExpr(final Name name, final Expression memberValue) {
        this(null, name, memberValue);
    }

    public SingleMemberAnnotationExpr(final Range range, final Name name, final Expression memberValue) {
        super(range, name);
        setMemberValue(memberValue);
        
        //ysryu
        this.codeLoc = 0;

        Node firstNode = null, lastNode = null;
        
        if (this.memberValue != null && this.memberValue.getRange().isPresent()) {
        	this.codeLoc += this.memberValue.getCodeLoc();
        	this.complexity += (this.memberValue.getComplexity() - 1);
        	
        	if(firstNode == null) { 
             	firstNode = this.memberValue;
            }
            if(lastNode == null) {
             	lastNode = this.memberValue;
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

    public Expression getMemberValue() {
        return memberValue;
    }

    public SingleMemberAnnotationExpr setMemberValue(final Expression memberValue) {
        assertNotNull(memberValue);
        notifyPropertyChange(ObservableProperty.MEMBER_VALUE, this.memberValue, memberValue);
        if (this.memberValue != null)
            this.memberValue.setParentNode(null);
        this.memberValue = memberValue;
        setAsParentNodeOf(memberValue);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public SingleMemberAnnotationExpr clone() {
        return (SingleMemberAnnotationExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public SingleMemberAnnotationExprMetaModel getMetaModel() {
        return JavaParserMetaModel.singleMemberAnnotationExprMetaModel;
    }
}
