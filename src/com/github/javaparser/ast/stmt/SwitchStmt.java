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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Arrays;
import java.util.List;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.SwitchStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * A switch statement.
 * <br/>In <code>switch(a) { ... }</code> the selector is "a",
 * and the contents of the { ... } are the entries.
 *
 * @author Julio Vilmar Gesser
 * @see SwitchEntryStmt
 */
public final class SwitchStmt extends Statement {

    private Expression selector;

    private NodeList<SwitchEntryStmt> entries;

    public SwitchStmt() {
        this(null, new NameExpr(), new NodeList<>());
    }

    @AllFieldsConstructor
    public SwitchStmt(final Expression selector, final NodeList<SwitchEntryStmt> entries) {
        this(null, selector, entries);
    }

    public SwitchStmt(Range range, final Expression selector, final NodeList<SwitchEntryStmt> entries) {
        super(range);
        setSelector(selector);
        setEntries(entries);
        
        
        // ysryu
        this.codeLoc = 0;
      
        Node firstNode = null, lastNode = null;
        
        if (this.selector != null && this.selector.getRange().isPresent()) {
            this.codeLoc += this.selector.getCodeLoc();
            this.complexity += (this.selector.getComplexity() - 1);
            
            if(firstNode == null) { 
             	firstNode = this.selector;
            }
            if(lastNode == null) {
             	lastNode = this.selector;
            }
        }

        Statement firstEntry = null, lastEntry = null;

        if(this.entries != null && this.entries.isEmpty() == false) {
            for(Statement e:entries) {
            	if(e != null && e.getRange().isPresent()) {
 	        		this.codeLoc += e.getCodeLoc();
	        		this.complexity += (e.getComplexity() - 1);
	        		
	        		if(firstEntry != null && lastEntry != null) {
	            		// contains with arg
	            		if(firstEntry.getBeginLine() <= e.getBeginLine()
            					&& lastEntry.getEndLine() >= e.getEndLine()) {
	            			this.codeLoc -= e.getCodeLoc();
	        	        }
	            		else if(firstEntry.getBeginLine() == e.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastEntry.getEndLine() == e.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= e.getBeginLine()
	            					&& lastNode.getEndLine() >= e.getEndLine()) {
		            			this.codeLoc -= e.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == e.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == e.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	        		
	        		
	        		if(firstEntry == null) {
	        			firstEntry = e;
	        		} else if(firstEntry.getBeginLine() > e.getBeginLine()) {
	        			firstEntry = e;
	        		}
	        		
	        		if(lastEntry == null) {
	        			lastEntry = e;
	        		} else if(lastEntry.getEndLine() < e.getEndLine()) {
	        			lastEntry = e;
	        		}
	        	}
            }
            
            if(firstEntry != null && firstEntry.getRange().isPresent()) {
	            if(firstNode == null) {
	        		firstNode = firstEntry;
	        	} else {
	        		if(firstNode.getBeginLine() > firstEntry.getBeginLine()) {
	        			firstNode = firstEntry;
	        		}
	        	}
            }
            if(lastEntry != null && lastEntry.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastEntry;
	            } else {
	            	if(lastNode.getEndLine() < lastEntry.getEndLine()) {
	            		lastNode = lastEntry;
	        		}
	            }
            }
        }
        if(firstNode != null && firstNode.getRange().isPresent()) {
            if(firstNode.getBeginLine() > this.getBeginLine()) {
        		this.codeLoc++;
        	}
            if(firstEntry != null && firstEntry.getRange().isPresent()) {
     		
                /*
            	 *  switch (pos)
        		 *  {
        		 *     case 0:
            	 */
        		if(firstEntry.getBeginLine() - firstNode.getEndLine() > 1) {
        			this.codeLoc++;
        		}
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

    public NodeList<SwitchEntryStmt> getEntries() {
        return entries;
    }

    public SwitchEntryStmt getEntry(int i) {
        return getEntries().get(i);
    }

    public Expression getSelector() {
        return selector;
    }

    public SwitchStmt setEntries(final NodeList<SwitchEntryStmt> entries) {
        assertNotNull(entries);
        notifyPropertyChange(ObservableProperty.ENTRIES, this.entries, entries);
        if (this.entries != null) {
            this.entries.setParentNode(null);
        }
        this.entries = entries;
        setAsParentNodeOf(entries);
        return this;
    }

    public SwitchStmt setEntry(int i, SwitchEntryStmt entry) {
        getEntries().set(i, entry);
        return this;
    }

    public SwitchStmt addEntry(SwitchEntryStmt entry) {
        getEntries().add(entry);
        return this;
    }

    public SwitchStmt setSelector(final Expression selector) {
        assertNotNull(selector);
        notifyPropertyChange(ObservableProperty.SELECTOR, this.selector, selector);
        if (this.selector != null) {
            this.selector.setParentNode(null);
        }
        this.selector = selector;
        setAsParentNodeOf(selector);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getEntries());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) == node) {
                entries.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public SwitchStmt clone() {
        return (SwitchStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public SwitchStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.switchStmtMetaModel;
    }
}
