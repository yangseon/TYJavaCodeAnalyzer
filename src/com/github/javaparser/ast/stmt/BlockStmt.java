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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Arrays;
import java.util.List;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.BlockStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/26/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * Statements in between { and }.
 *
 * @author Julio Vilmar Gesser
 */
public final class BlockStmt extends Statement implements NodeWithStatements<BlockStmt> {

    private NodeList<Statement> statements;

    public BlockStmt() {
        this(null, new NodeList<>());
    }

    @AllFieldsConstructor
    public BlockStmt(final NodeList<Statement> statements) {
        this(null, statements);
    }

    public BlockStmt(final Range range, final NodeList<Statement> statements) {
        super(range);
        setStatements(statements);
       
        // ysryu
        this.codeLoc = 0;
        
        Node firstNode = null, lastNode = null;
        Statement firstStatement = null, lastStatement = null;
    	
        if (this.statements != null && this.statements.isEmpty() == false) {
            for(Statement s:this.statements) {
            	if (s != null && s.getRange().isPresent()) {
	            	this.codeLoc += s.getCodeLoc();
	            	this.complexity += (s.getComplexity() -1);

	            	if(firstStatement != null && lastStatement != null) {
	            		// contains with val
	            		if(firstStatement.getBeginLine() <= s.getBeginLine()
            					&& lastStatement.getEndLine() >= s.getEndLine()) {
	            			this.codeLoc -= s.getCodeLoc();
	        	        }
	            		else if(firstStatement.getBeginLine() == s.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastStatement.getEndLine() == s.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= s.getBeginLine()
	            					&& lastNode.getEndLine() >= s.getEndLine()) {
		            			this.codeLoc -= s.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == s.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == s.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	            	
	            	if(firstStatement == null) {
	            		firstStatement = s;
		     		} else if(firstStatement.getBeginLine() > s.getBeginLine()) {
		     			firstStatement = s;
		     		}
		     		
		     		if(lastStatement == null) {
		     			lastStatement = s;
		     		} else if(lastStatement.getEndLine() < s.getEndLine()) {
		     			lastStatement = s;
		     		}
            	}
            	
            }
            
            if(firstStatement != null && firstStatement.getRange().isPresent()) {
	            if(firstNode == null) { 
	            	firstNode = firstStatement;
	            }
            }
            if(lastStatement != null && lastStatement.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastStatement;
	            }
        	}
        }
        
        if(firstNode != null && firstNode.getRange().isPresent()) {
            if(firstNode.getBeginLine() > this.getBeginLine()) {
        		this.codeLoc++;
        	}
        } else {
        	// this body is empty "{ "
        	if(this.getRange().isPresent()) {
        		this.codeLoc++;
        	}
        }
      
        if(lastNode != null && lastNode.getRange().isPresent()) {
        	if(lastNode.getEndLine() < this.getEndLine()) {
        		this.codeLoc++;
        	}
        } else {// this body is empty
        	// this body is empty " } "
        	if(this.getRange().isPresent()) {
	        	if(this.getBeginLine() != this.getEndLine()) {
	        		this.codeLoc++;
	        	}
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

    public NodeList<Statement> getStatements() {
        return statements;
    }

    public BlockStmt setStatements(final NodeList<Statement> statements) {
        assertNotNull(statements);
        notifyPropertyChange(ObservableProperty.STATEMENTS, this.statements, statements);
        if (this.statements != null) {
            this.statements.setParentNode(null);
        }
        this.statements = statements;
        setAsParentNodeOf(statements);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getStatements());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i) == node) {
                statements.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public BlockStmt clone() {
        return (BlockStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public BlockStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.blockStmtMetaModel;
    }
}
