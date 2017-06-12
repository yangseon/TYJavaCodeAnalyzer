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
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.SwitchEntryStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * One case in a switch statement.
 * <br/><pre>
 * switch (i) {
 *   case 1:
 *   case 2:
 *     System.out.println(444);
 *     break;
 *   default:
 *     System.out.println(0);
 * }
 * </pre>
 * This contains three SwitchEntryStmts.
 * <br/>The first one has label 1 and no statements.
 * <br/>The second has label 2 and two statements (the println and the break).
 * <br/>The third, the default, has no label and one statement.
 *
 * @author Julio Vilmar Gesser
 * @see SwitchStmt
 */
public final class SwitchEntryStmt extends Statement implements NodeWithStatements<SwitchEntryStmt> {

    private Expression label;

    private NodeList<Statement> statements;

    public SwitchEntryStmt() {
        this(null, null, new NodeList<>());
    }

    @AllFieldsConstructor
    public SwitchEntryStmt(final Expression label, final NodeList<Statement> statements) {
        this(null, label, statements);
    }

    public SwitchEntryStmt(Range range, final Expression label, final NodeList<Statement> statements) {
        super(range);
        setLabel(label);
        setStatements(statements);
        
        // ysryu
        this.codeLoc = 0;
        
        // Increase complexity - Selection    if, else, case, default.
        this.complexity++;     
        
        Node firstNode = null, lastNode = null;
        
        if (this.label != null && this.label.getRange().isPresent()) {
            this.codeLoc += this.label.getCodeLoc();
            this.complexity += (this.label.getComplexity() -1);
            
            if(firstNode == null) { 
             	firstNode = this.label;
             }
             if(lastNode == null) {
             	lastNode = this.label;
             }
        }
    	
        Statement firstStatement = null, lastStatement = null;

        if (this.statements != null && this.statements.isEmpty() == false) {
        	
            for(Statement s:this.statements) {
            	if(s != null && s.getRange().isPresent()) {
	            	this.codeLoc += s.getCodeLoc();
	            	this.complexity += (s.getComplexity() -1);
	            	
	            	if(firstStatement != null && lastStatement != null) {
	            		// contains with arg
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
	        	} else {
	        		if(firstNode.getBeginLine() > firstStatement.getBeginLine()) {
	        			firstNode = firstStatement;
	        		}
	        	}
            }
            if(lastStatement != null && lastStatement.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastStatement;
	            } else {
	            	if(lastNode.getEndLine() < lastStatement.getEndLine()) {
	            		lastNode = lastStatement;
	        		}
	            }
            }
            
        }
        
        if(firstNode != null && firstNode.getRange().isPresent() == true) {
        	if(firstNode.getBeginLine() > this.getBeginLine()) {
        		this.codeLoc++;
        	}
        }
        
        if(lastNode != null  && lastNode.getRange().isPresent() == true) {
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

    public Optional<Expression> getLabel() {
        return Optional.ofNullable(label);
    }

    public NodeList<Statement> getStatements() {
        return statements;
    }

    /**
     * Sets the label
     *
     * @param label the label, can be null
     * @return this, the SwitchEntryStmt
     */
    public SwitchEntryStmt setLabel(final Expression label) {
        notifyPropertyChange(ObservableProperty.LABEL, this.label, label);
        if (this.label != null) {
            this.label.setParentNode(null);
        }
        this.label = label;
        setAsParentNodeOf(label);
        return this;
    }

    public SwitchEntryStmt setStatements(final NodeList<Statement> statements) {
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
        if (label != null) {
            if (node == label) {
                removeLabel();
                return true;
            }
        }
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i) == node) {
                statements.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    public SwitchEntryStmt removeLabel() {
        return setLabel((Expression) null);
    }

    @Override
    public SwitchEntryStmt clone() {
        return (SwitchEntryStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public SwitchEntryStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.switchEntryStmtMetaModel;
    }
}
