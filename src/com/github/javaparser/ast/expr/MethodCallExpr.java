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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.MethodCallExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * A method call on an object. <br/><code>circle.circumference()</code> <br/>In <code>a.&lt;String&gt;bb(15);</code> a
 * is the scope, String is a type argument, bb is the name and 15 is an argument.
 *
 * @author Julio Vilmar Gesser
 */
public final class MethodCallExpr extends Expression implements NodeWithTypeArguments<MethodCallExpr>, NodeWithArguments<MethodCallExpr>, NodeWithSimpleName<MethodCallExpr>, NodeWithOptionalScope<MethodCallExpr> {

    private Expression scope;

    private NodeList<Type> typeArguments;

    private SimpleName name;

    private NodeList<Expression> arguments;

    public MethodCallExpr() {
        this(null, null, new NodeList<>(), new SimpleName(), new NodeList<>());
    }

    public MethodCallExpr(final Expression scope, final String name) {
        this(null, scope, new NodeList<>(), new SimpleName(name), new NodeList<>());
    }

    public MethodCallExpr(final Expression scope, final SimpleName name, final NodeList<Expression> arguments) {
        this(null, scope, new NodeList<>(), name, arguments);
    }

    @AllFieldsConstructor
    public MethodCallExpr(final Expression scope, final NodeList<Type> typeArguments, final SimpleName name, final NodeList<Expression> arguments) {
        this(null, scope, typeArguments, name, arguments);
    }

    public MethodCallExpr(final Range range, final Expression scope, final NodeList<Type> typeArguments, final SimpleName name, final NodeList<Expression> arguments) {
        super(range);
        setScope(scope);
        setTypeArguments(typeArguments);
        setName(name);
        setArguments(arguments);
        
      //ysryu
        this.codeLoc = 0;
        Node firstNode = null, lastNode = null;
        
 
    	// same level method 
    	// just like "aa()" or "aa(a,b,c)"
        if(this.name != null) {
        	firstNode = this.name;
        	lastNode = this.name;
        	this.codeLoc += this.name.getLoc();
        }
        
        if (this.scope != null && this.scope.getRange().isPresent()) {
        	this.codeLoc += this.scope.getCodeLoc();
        	this.complexity += (this.scope.getComplexity() - 1);
        	
        	if(firstNode != null && lastNode != null) {
    			if(firstNode.getBeginLine() <= this.scope.getBeginLine()
    					&& lastNode.getEndLine() >= this.scope.getEndLine()) {
        			this.codeLoc -= this.scope.getCodeLoc();
    	        }
        		else if(firstNode.getBeginLine() == this.scope.getEndLine()) {
        			this.codeLoc--;
        		} else if(lastNode.getEndLine() == this.scope.getBeginLine()) {
        			this.codeLoc--;
        		}
    		}
        	
        	if(firstNode == null) { 
             	firstNode = this.scope;
            } else {
    			if(firstNode.getBeginLine() > this.scope.getBeginLine()) {
    				firstNode = this.scope;
    			}
    		}
    		
        	 if(lastNode == null) {
              	lastNode = this.scope;
             } else {
    			if(lastNode.getEndLine() < this.scope.getEndLine()) {
    				lastNode = this.scope;
    			}
    		}
        }
        
        Expression firstArg = null, lastArg = null;
    	
    
        if (this.arguments != null && this.arguments.isEmpty() == false) {
            for(Expression arg:this.arguments) {
            	if (arg != null && arg.getRange().isPresent()) {
	            	this.codeLoc += arg.getCodeLoc();
	            	this.complexity += (arg.getComplexity() - 1);

	            	if(firstArg != null && lastArg != null) {
	                	// contains with arg
	            		if(firstArg.getBeginLine() <= arg.getBeginLine()
            					&& lastArg.getEndLine() >= arg.getEndLine()) {
	            			this.codeLoc -= arg.getCodeLoc();
	         	        }
	            		else if(firstArg.getBeginLine() == arg.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastArg.getEndLine() == arg.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= arg.getBeginLine()
	            					&& lastNode.getEndLine() >= arg.getEndLine()) {
		            			this.codeLoc -= arg.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == arg.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == arg.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	            	
	            	
	        		if(firstArg == null) {
	        			firstArg = arg;
	        		} else {
	        			if(firstArg.getBeginLine() > arg.getBeginLine()) {
	        				firstArg = arg;
	        			}
	        		}
	        		
	         		if(lastArg == null) {
	         			lastArg = arg;
	        		} else {
	        			if(lastArg.getEndLine() < arg.getEndLine()) {
	        				lastArg = arg;
	        			}
	        		}
	            }
            }
            
             
            if(firstArg != null && firstArg.getRange().isPresent()) {
	            if(firstNode == null) {
	        		firstNode = firstArg;
	        	} else {
	        		if(firstNode.getBeginLine() > firstArg.getBeginLine()) {
	        			firstNode = firstArg;
	        		}
	        	}
            }
            if(lastArg != null && lastArg.getRange().isPresent()) {  
	            if(lastNode == null) {
	            	lastNode = lastArg;
	            } else {
	            	if(lastNode.getEndLine() < lastArg.getEndLine()) {
	            		lastNode = lastArg;
	        		}
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

    public NodeList<Expression> getArguments() {
        return arguments;
    }

    @Override
    public SimpleName getName() {
        return name;
    }

    @Override
    public Optional<Expression> getScope() {
        return Optional.ofNullable(scope);
    }

    public MethodCallExpr setArguments(final NodeList<Expression> arguments) {
        assertNotNull(arguments);
        notifyPropertyChange(ObservableProperty.ARGUMENTS, this.arguments, arguments);
        if (this.arguments != null)
            this.arguments.setParentNode(null);
        this.arguments = arguments;
        setAsParentNodeOf(arguments);
        return this;
    }

    @Override
    public MethodCallExpr setName(final SimpleName name) {
        assertNotNull(name);
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null)
            this.name.setParentNode(null);
        this.name = name;
        setAsParentNodeOf(name);
        return this;
    }

    @Override
    public MethodCallExpr setScope(final Expression scope) {
        notifyPropertyChange(ObservableProperty.SCOPE, this.scope, scope);
        if (this.scope != null)
            this.scope.setParentNode(null);
        this.scope = scope;
        setAsParentNodeOf(scope);
        return this;
    }

    @Override
    public Optional<NodeList<Type>> getTypeArguments() {
        return Optional.ofNullable(typeArguments);
    }

    /**
     * Sets the typeArguments
     *
     * @param typeArguments the typeArguments, can be null
     * @return this, the MethodCallExpr
     */
    @Override
    public MethodCallExpr setTypeArguments(final NodeList<Type> typeArguments) {
        notifyPropertyChange(ObservableProperty.TYPE_ARGUMENTS, this.typeArguments, typeArguments);
        if (this.typeArguments != null)
            this.typeArguments.setParentNode(null);
        this.typeArguments = typeArguments;
        setAsParentNodeOf(typeArguments);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getArguments(), getTypeArguments().orElse(null));
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i) == node) {
                arguments.remove(i);
                return true;
            }
        }
        if (scope != null) {
            if (node == scope) {
                removeScope();
                return true;
            }
        }
        if (typeArguments != null) {
            for (int i = 0; i < typeArguments.size(); i++) {
                if (typeArguments.get(i) == node) {
                    typeArguments.remove(i);
                    return true;
                }
            }
        }
        return super.remove(node);
    }

    public MethodCallExpr removeScope() {
        return setScope((Expression) null);
    }

    @Override
    public MethodCallExpr clone() {
        return (MethodCallExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public MethodCallExprMetaModel getMetaModel() {
        return JavaParserMetaModel.methodCallExprMetaModel;
    }
}
