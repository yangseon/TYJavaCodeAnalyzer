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
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Optional;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.DerivedProperty;
import com.github.javaparser.metamodel.IfStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * An if-then-else statement. The else is optional.
 * <br/>In <code>if(a==5) hurray() else boo();</code> the condition is a==5,
 * hurray() is the thenStmt, and boo() is the elseStmt.
 *
 * @author Julio Vilmar Gesser
 */
public final class IfStmt extends Statement {

    private Expression condition;

    private Statement thenStmt;

    private Statement elseStmt;

    public IfStmt() {
        this(null, new BooleanLiteralExpr(), new ReturnStmt(), null);
    }

    @AllFieldsConstructor
    public IfStmt(final Expression condition, final Statement thenStmt, final Statement elseStmt) {
        this(null, condition, thenStmt, elseStmt);
    }

    public IfStmt(Range range, final Expression condition, final Statement thenStmt, final Statement elseStmt) {
        super(range);
        setCondition(condition);
        setThenStmt(thenStmt);
        setElseStmt(elseStmt);
        
        // ysryu
        this.codeLoc = 0;
        
        // Increase complexity - Selection    if, else, case, default.
        this.complexity++;     
        
        Node firstNode = null, lastNode = null;
        
        if (this.condition != null && this.condition.getRange().isPresent()) {
        	this.codeLoc += this.condition.getCodeLoc();
            this.complexity += (this.condition.getComplexity() - 1);
            
			if(firstNode == null) {
				firstNode = this.condition;
			}
			
			if(lastNode == null) {
				lastNode = this.condition;
			}
        }
  
        if(this.thenStmt != null && this.thenStmt.getRange().isPresent()) {     
    		this.codeLoc += this.thenStmt.getCodeLoc();
    		this.complexity += (this.thenStmt.getComplexity() - 1);

    		if (lastNode != null && lastNode.getRange().isPresent()) {
        		if(lastNode.getEndLine() == this.thenStmt.getBeginLine()) {
            		this.codeLoc--;
            	}
        	}
    		
            if(firstNode == null) {
        		firstNode = this.thenStmt;
        	} else {
        		if(firstNode.getBeginLine() > this.thenStmt.getBeginLine()) {
        			firstNode = this.thenStmt;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.thenStmt;
            } else {
            	if(lastNode.getEndLine() < this.thenStmt.getEndLine()) {
            		lastNode = this.thenStmt;
        		}
            }
    		
        }
        
        if(this.elseStmt != null && this.elseStmt.getRange().isPresent()) {     
     		this.codeLoc += this.elseStmt.getCodeLoc();
     		this.complexity += (this.elseStmt.getComplexity() - 1);

     		if (lastNode != null && lastNode.getRange().isPresent()) {
        		if(lastNode.getEndLine() == this.elseStmt.getBeginLine()) {
            		this.codeLoc--;
            	}
        	}
            if(firstNode == null) {
        		firstNode = this.elseStmt;
        	} else {
        		if(firstNode.getBeginLine() > this.elseStmt.getBeginLine()) {
        			firstNode = this.elseStmt;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.elseStmt;
            } else {
            	if(lastNode.getEndLine() < this.elseStmt.getEndLine()) {
            		lastNode = this.elseStmt;
        		}
            }
            
            // just like "else"  has only one line. "else" has no block stmt and has stmt
            /*
             *   private int jj_ntk_f() {
    		 *		if ((jj_nt=token.next) == null)
      		 *			return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    		 *		else
      		 *			return (jj_ntk = jj_nt.kind);
  			 *		}
             */
            if(this.thenStmt != null && this.thenStmt.getRange().isPresent()) {  
            	if(this.elseStmt.getBeginLine() - this.thenStmt.getEndLine() > 1) {
	            	this.codeLoc++;
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

    public Expression getCondition() {
        return condition;
    }

    public Optional<Statement> getElseStmt() {
        return Optional.ofNullable(elseStmt);
    }

    public Statement getThenStmt() {
        return thenStmt;
    }

    public IfStmt setCondition(final Expression condition) {
        assertNotNull(condition);
        notifyPropertyChange(ObservableProperty.CONDITION, this.condition, condition);
        if (this.condition != null) {
            this.condition.setParentNode(null);
        }
        this.condition = condition;
        setAsParentNodeOf(condition);
        return this;
    }

    /**
     * Sets the elseStmt
     *
     * @param elseStmt the elseStmt, can be null
     * @return this, the IfStmt
     */
    public IfStmt setElseStmt(final Statement elseStmt) {
        notifyPropertyChange(ObservableProperty.ELSE_STMT, this.elseStmt, elseStmt);
        if(this.elseStmt != null) {     
        	this.elseStmt.setParentNode(null);
         }
        this.elseStmt = elseStmt;
        setAsParentNodeOf(elseStmt);
        return this;
    }

    public IfStmt setThenStmt(final Statement thenStmt) {
        assertNotNull(thenStmt);
        notifyPropertyChange(ObservableProperty.THEN_STMT, this.thenStmt, thenStmt);
        if(this.thenStmt != null) {     
        	this.thenStmt.setParentNode(null);
        }
        this.thenStmt = thenStmt;
        setAsParentNodeOf(thenStmt);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        if (elseStmt != null) {
            if (node == elseStmt) {
                removeElseStmt();
                return true;
            }
        }
        return super.remove(node);
    }

    public IfStmt removeElseStmt() {
        return setElseStmt((Statement) null);
    }

    @DerivedProperty
    public boolean hasThenBlock() {
        return thenStmt instanceof BlockStmt;
    }

    @DerivedProperty
    public boolean hasElseBlock() {
        return elseStmt instanceof BlockStmt || elseStmt instanceof IfStmt;
    }

    @Override
    public IfStmt clone() {
        return (IfStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public IfStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.ifStmtMetaModel;
    }
}
