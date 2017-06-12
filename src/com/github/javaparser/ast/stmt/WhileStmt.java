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
package com.github.javaparser.ast.stmt;

import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.WhileStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * A while statement.
 * <br/><code>while(true) { ... }</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class WhileStmt extends Statement implements NodeWithBody<WhileStmt> {

    private Expression condition;

    private Statement body;

    public WhileStmt() {
        this(null, new BooleanLiteralExpr(), new ReturnStmt());
    }

    @AllFieldsConstructor
    public WhileStmt(final Expression condition, final Statement body) {
        this(null, condition, body);
    }

    public WhileStmt(Range range, final Expression condition, final Statement body) {
        super(range);
        setCondition(condition);
        setBody(body);
        
        //ysryu
        this.codeLoc = 0;
   
        // Increase complexity - Loops    for, while, do-while, break, and continue.
        this.complexity++;
        
        Node firstNode = null, lastNode = null;
        if (this.condition != null && this.condition.getRange().isPresent()) {
        	this.codeLoc += this.condition.getCodeLoc();
            this.complexity += (this.condition.getComplexity() -1);

            if(firstNode == null) { 
             	firstNode = this.condition;
            }
            if(lastNode == null) {
             	lastNode = this.condition;
            }
        }
        
        if (this.body != null && this.body.getRange().isPresent()) {
        	this.codeLoc += this.body.getCodeLoc();
            this.complexity += (this.body.getComplexity() - 1);
            
            if(lastNode != null && lastNode.getRange().isPresent()) {
				if(lastNode.getEndLine() == this.body.getBeginLine()) {
					this.codeLoc--;
				}
			}
            
            if(firstNode == null) {
        		firstNode = this.body;
        	} else {
        		if(firstNode.getBeginLine() > this.body.getBeginLine()) {
        			firstNode = this.body;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.body;
            } else {
            	if(lastNode.getEndLine() < this.body.getEndLine()) {
            		lastNode = this.body;
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
    public <R, A> R accept(final GenericVisitor<R, A> v, final A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(final VoidVisitor<A> v, final A arg) {
        v.visit(this, arg);
    }

    @Override
    public Statement getBody() {
        return body;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public WhileStmt setBody(final Statement body) {
        assertNotNull(body);
        notifyPropertyChange(ObservableProperty.BODY, this.body, body);
        if (this.body != null) {
            this.body.setParentNode(null);
        }
        this.body = body;
        setAsParentNodeOf(body);
        return this;
    }

    public WhileStmt setCondition(final Expression condition) {
        assertNotNull(condition);
        notifyPropertyChange(ObservableProperty.CONDITION, this.condition, condition);
        if (this.condition != null) {
            this.condition.setParentNode(null);
        }
        this.condition = condition;
        setAsParentNodeOf(condition);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public WhileStmt clone() {
        return (WhileStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public WhileStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.whileStmtMetaModel;
    }
}