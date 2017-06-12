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
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.ForeachStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/26/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * A for-each statement.
 * <br/><code>for(Object o: objects) { ... }</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class ForeachStmt extends Statement implements NodeWithBody<ForeachStmt> {

    private VariableDeclarationExpr variable;

    private Expression iterable;

    private Statement body;

    public ForeachStmt() {
        this(null, new VariableDeclarationExpr(), new NameExpr(), new ReturnStmt());
    }

    @AllFieldsConstructor
    public ForeachStmt(final VariableDeclarationExpr variable, final Expression iterable, final Statement body) {
        this(null, variable, iterable, body);
    }

    public ForeachStmt(Range range, final VariableDeclarationExpr variable, final Expression iterable, final Statement body) {
    	super(range);
        setVariable(variable);
        setIterable(iterable);
        setBody(body);
        
        // ysryu
        this.codeLoc = 0;
        
        // Increase complexity - Loops    for, while, do-while, break, and continue.
        this.complexity++;
        
        Node firstNode = null, lastNode = null;
        if (this.variable != null && this.variable.getRange().isPresent()) {
        	this.codeLoc += this.variable.getCodeLoc();
        	this.complexity += (this.variable.getComplexity() - 1);
        	
        	if(firstNode == null) { 
             	firstNode = this.variable;
            }
            if(lastNode == null) {
             	lastNode = this.variable;
            }
        }
        
        if (this.iterable != null && this.iterable.getRange().isPresent()) {
        	this.codeLoc += this.iterable.getCodeLoc();
        	this.complexity += (this.iterable.getComplexity() - 1);
        	
        	if(lastNode != null && lastNode.getRange().isPresent()) {
				if(lastNode.getEndLine() == this.iterable.getBeginLine()) {
					this.codeLoc--;
				}
			}
        	
        	if(firstNode == null) {
        		firstNode = this.iterable;
        	} else {
        		if(firstNode.getBeginLine() > this.iterable.getBeginLine()) {
        			firstNode = this.iterable;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.iterable;
            } else {
            	if(lastNode.getEndLine() < this.iterable.getEndLine()) {
            		lastNode = this.iterable;
        		}
            }
        	
        }
        
        if (this.body != null && this.body.getRange().isPresent()) {
            this.codeLoc += this.body.getCodeLoc();
            this.complexity += (this.body.getComplexity() - 1);
            
            if (lastNode != null) {
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

    public ForeachStmt(VariableDeclarationExpr variable, String iterable, BlockStmt body) {
        this(null, variable, new NameExpr(iterable), body);
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

    public Expression getIterable() {
        return iterable;
    }

    public VariableDeclarationExpr getVariable() {
        return variable;
    }

    @Override
    public ForeachStmt setBody(final Statement body) {
        assertNotNull(body);
        notifyPropertyChange(ObservableProperty.BODY, this.body, body);
        if (this.body != null) {
            this.body.setParentNode(null);
        }
        this.body = body;
        setAsParentNodeOf(body);
        return this;
    }

    public ForeachStmt setIterable(final Expression iterable) {
        assertNotNull(iterable);
        notifyPropertyChange(ObservableProperty.ITERABLE, this.iterable, iterable);
        if (this.iterable != null) {
            this.iterable.setParentNode(null);
        }
        this.iterable = iterable;
        setAsParentNodeOf(iterable);
        return this;
    }

    public ForeachStmt setVariable(final VariableDeclarationExpr variable) {
        assertNotNull(variable);
        notifyPropertyChange(ObservableProperty.VARIABLE, this.variable, variable);
        if (this.variable != null)
            this.variable.setParentNode(null);
        this.variable = variable;
        setAsParentNodeOf(variable);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public ForeachStmt clone() {
        return (ForeachStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public ForeachStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.foreachStmtMetaModel;
    }
}
