/*
 * Copyright (C) 2007-2010 J첬lio Vilmar Gesser.
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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithParameters;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;

import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.DerivedProperty;
import com.github.javaparser.metamodel.LambdaExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * A lambda expression. The parameters are on the left side of the ->.
 * If a parameter uses type inference (it has no type specified) then its type is set to UnknownType.
 * If they are in ( ), "isEnclosingParameters" is true.
 * The body is to the right of the ->.
 * <br/><code>(a, b) -> a+b</code>
 * <br/><code>a -> ...</code>
 * <br/><code>(Long a) -> {println(a);}</code>
 *
 * @author Raquel Pau
 */
public class LambdaExpr extends Expression implements NodeWithParameters<LambdaExpr> {

    private NodeList<Parameter> parameters;

    private boolean isEnclosingParameters;

    private Statement body;

    public LambdaExpr() {
        this(null, new NodeList<>(), new ReturnStmt(), false);
    }

    @AllFieldsConstructor
    public LambdaExpr(NodeList<Parameter> parameters, Statement body, boolean isEnclosingParameters) {
    	this(null, parameters, body, isEnclosingParameters);
    }

    public LambdaExpr(Range range, NodeList<Parameter> parameters, Statement body, boolean isEnclosingParameters) {
        super(range);
       
        setParameters(parameters);
        setBody(body);
        setEnclosingParameters(isEnclosingParameters);
    }

    @Override
    public NodeList<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public LambdaExpr setParameters(final NodeList<Parameter> parameters) {
        assertNotNull(parameters);
        notifyPropertyChange(ObservableProperty.PARAMETERS, this.parameters, parameters);
        if (this.parameters != null)
            this.parameters.setParentNode(null);
        this.parameters = parameters;
        setAsParentNodeOf(parameters);
        this.codeLoc = this.setCodeLoc(this.body);
        return this;
    }

    public Statement getBody() {
        return body;
    }

    public LambdaExpr setBody(final Statement body) {
        assertNotNull(body);
        notifyPropertyChange(ObservableProperty.BODY, this.body, body);
        
        if (this.body != null) {
            this.body.setParentNode(null);
        }
        this.body = body;
        setAsParentNodeOf(body);
        
        /**
         * Lambda의 최초에 body를 설정하지 않고, 중간에 body를 설정하기 때문에 여기서 확인 함
         */
        //ysryu
        if (this.body != null) {
            Node firstNode = null, lastNode = null;
            
            if (this.body.getRange().isPresent()) {
            	
            	this.codeLoc = this.setCodeLoc(this.body);
 
            	this.codeLoc += this.body.getCodeLoc();
	        	this.complexity += (this.body.getComplexity() - 1);
	        	
	        	if(firstNode == null) { 
	             	firstNode = this.body;
	            }
	            if(lastNode == null) {
	             	lastNode = this.body;
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
        return this;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public boolean isEnclosingParameters() {
        return isEnclosingParameters;
    }

    public LambdaExpr setEnclosingParameters(final boolean isEnclosingParameters) {
        notifyPropertyChange(ObservableProperty.ENCLOSING_PARAMETERS, this.isEnclosingParameters, isEnclosingParameters);
        this.isEnclosingParameters = isEnclosingParameters;
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getParameters());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i) == node) {
                parameters.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }
    
    
    /**
     * Added on 6/9/2017 yangseon ryu(ysryu)
     * set base code Loc from Lambda Expression 
     * 
     * @return
     */
    protected long setCodeLoc(final Statement body) {
    	this.codeLoc = 0;

    	Set<Integer> declLines = new HashSet<Integer>();
        
    	for(NodeList<?>  nl:this.getNodeLists()) {
        	if(nl != null && nl.isEmpty() == false) {
        		for(Node n:nl) {
	        		if(n != null && n.getRange().isPresent()) {
	        			
	        			for(int lNum = n.getBeginLine(); lNum <= n.getEndLine(); lNum++) {
	        				declLines.add(new Integer(lNum));
	        			}
	            	}
	        	}
        	}
        }
     
        /*
         * if(this.codeLoc < gap)  is just like - seperated ')'
         * 
         * public ParseException(Token currentTokenVal,
         *			 int[][] expectedTokenSequencesVal,
         *			 String[] tokenImageVal
         *			)
         *	{
         *
         * if(this.codeLoc > gap)  is just like  -  overlap with body
         * 
         *  return Collector.of(NodeList::new, NodeList::add, (left, right) -> {
         *  	 left.addAll(right);
         *	   return left;
         *	});
         */
        if(body != null) {
        	Integer bodyBeginLine = new Integer(body.getBeginLine());
        	if(declLines.contains(bodyBeginLine)) {
        		declLines.remove(bodyBeginLine);
        	}
        	this.codeLoc = declLines.size();
        }
        return this.codeLoc;
    }

    @DerivedProperty
    public Optional<Expression> getExpressionBody() {
        if (body instanceof ExpressionStmt) {
            return Optional.of(((ExpressionStmt) body).getExpression());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public LambdaExpr clone() {
        return (LambdaExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public LambdaExprMetaModel getMetaModel() {
        return JavaParserMetaModel.lambdaExprMetaModel;
    }
}
