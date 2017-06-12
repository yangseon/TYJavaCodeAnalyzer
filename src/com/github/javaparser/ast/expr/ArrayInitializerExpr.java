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

import static com.github.javaparser.utils.Utils.assertNotNull;

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.ArrayInitializerExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc 
 */
/**
 * The initialization of an array. In the following sample, the outer { } is an ArrayInitializerExpr.
 * It has two expressions inside: two ArrayInitializerExprs.
 * These have two expressions each, one has 1 and 1, the other two and two.
 * <br/><code>new int[][]{{1, 1}, {2, 2}};</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class ArrayInitializerExpr extends Expression {

    private NodeList<Expression> values;

    public ArrayInitializerExpr() {
        this(null, new NodeList<>());
    }

    @AllFieldsConstructor
    public ArrayInitializerExpr(NodeList<Expression> values) {
        this(null, values);
    }

    public ArrayInitializerExpr(Range range, NodeList<Expression> values) {
        super(range);
        setValues(values);
        
        //ysryu 
        this.codeLoc = 0;
        Node firstNode = null, lastNode = null;
        
        Expression firstExp = null, lastExp = null;

        if (this.values != null && this.values.isEmpty() == false) {
            for(Expression val:this.values) {
            	if (val != null && val.getRange().isPresent()) {
	            	this.codeLoc += val.getCodeLoc();
	            	this.complexity += (val.getComplexity() -1);
	            	
	            	if(firstExp != null && lastExp != null) {
	            		// contains with val
	            		if(firstExp.getBeginLine() <= val.getBeginLine()
            					&& lastExp.getEndLine() >= val.getEndLine()) {
	            			this.codeLoc -= val.getCodeLoc();
	        	        }
	            		else if(firstExp.getBeginLine() == val.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastExp.getEndLine() == val.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= val.getBeginLine()
	            					&& lastNode.getEndLine() >= val.getEndLine()) {
		            			this.codeLoc -= val.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == val.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == val.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	            	
	            	if(firstExp == null) {
	            		firstExp = val;
		     		} else if(firstExp.getBeginLine() > val.getBeginLine()) {
		     			firstExp = val;
		     		}
		     		
		     		if(lastExp == null) {
		     			lastExp = val;
		     		} else if(lastExp.getEndLine() < val.getEndLine()) {
		     			lastExp = val;
		     		}	            	
	            }
            }
            if(firstExp != null && firstExp.getRange().isPresent()) {
	            if(firstNode == null) {
	        		firstNode = firstExp;
	        	} 
            }
            if(lastExp != null && lastExp.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastExp;
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

    public NodeList<Expression> getValues() {
        return values;
    }

    public ArrayInitializerExpr setValues(final NodeList<Expression> values) {
        assertNotNull(values);
        notifyPropertyChange(ObservableProperty.VALUES, this.values, values);
        if (this.values != null)
            this.values.setParentNode(null);
        this.values = values;
        setAsParentNodeOf(values);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getValues());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == node) {
                values.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public ArrayInitializerExpr clone() {
        return (ArrayInitializerExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public ArrayInitializerExprMetaModel getMetaModel() {
        return JavaParserMetaModel.arrayInitializerExprMetaModel;
    }
}
