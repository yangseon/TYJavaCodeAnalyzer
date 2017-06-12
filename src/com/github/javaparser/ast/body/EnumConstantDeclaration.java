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
package com.github.javaparser.ast.body;

import static com.github.javaparser.utils.Utils.assertNotNull;

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.EnumConstantDeclarationMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/30/2017 yangseon ryu(ysryu)
 * -. set codeLoc, set complexity
 */
/**
 * One of the values an enum can take. A(1) and B(2) in this example: <code>enum X { A(1), B(2) }</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class EnumConstantDeclaration extends BodyDeclaration<EnumConstantDeclaration> implements NodeWithJavadoc<EnumConstantDeclaration>, NodeWithSimpleName<EnumConstantDeclaration>, NodeWithArguments<EnumConstantDeclaration> {

    private SimpleName name;

    private NodeList<Expression> arguments;

    private NodeList<BodyDeclaration<?>> classBody;

    public EnumConstantDeclaration() {
        this(null, new NodeList<>(), new SimpleName(), new NodeList<>(), new NodeList<>());
    }

    public EnumConstantDeclaration(String name) {
        this(null, new NodeList<>(), new SimpleName(name), new NodeList<>(), new NodeList<>());
    }

    @AllFieldsConstructor
    public EnumConstantDeclaration(NodeList<AnnotationExpr> annotations, SimpleName name, NodeList<Expression> arguments, NodeList<BodyDeclaration<?>> classBody) {
        this(null, annotations, name, arguments, classBody);
    }

    public EnumConstantDeclaration(Range range, NodeList<AnnotationExpr> annotations, SimpleName name, NodeList<Expression> arguments, NodeList<BodyDeclaration<?>> classBody) {
        super(range, annotations);
        setName(name);
        setArguments(arguments);
        setClassBody(classBody);
        

        //ysryu
        this.codeLoc = 0;
        Node firstNode = null, lastNode = null;

        if(this.name != null) {
        	firstNode = this.name;
        	lastNode = this.name;
        	this.codeLoc += this.name.getLoc();
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
	        	} 
            }
            if(lastArg != null && lastArg.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastArg;
	            } 
            }
        }
        
        
        BodyDeclaration<?> firstClassBody = null, lastClassBody = null;
 
        if(this.classBody != null && this.classBody.isEmpty() == false) {
	    	for(BodyDeclaration<?> bd:this.classBody) {
	    		if (bd != null && bd.getRange().isPresent()) {
		    		this.codeLoc += bd.getCodeLoc();
		    		this.complexity += (bd.getComplexity() -1);
		    		
		    		if(firstArg != null && lastArg != null) {
	            		// contains with arg
	            		if(firstArg.getBeginLine() <= bd.getBeginLine()
            					&& lastArg.getEndLine() >= bd.getEndLine()) {
	            			this.codeLoc -= bd.getCodeLoc();
	        	        }
	            		else if(firstArg.getBeginLine() == bd.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastArg.getEndLine() == bd.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= bd.getBeginLine()
	            					&& lastNode.getEndLine() >= bd.getEndLine()) {
		            			this.codeLoc -= bd.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == bd.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == bd.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
		    		
		    		if(firstClassBody == null) {
		    			firstClassBody = bd;
		     		} else if(firstClassBody.getBeginLine() > bd.getBeginLine()) {
		     			firstClassBody = bd;
		     		}
		     		
		     		if(lastClassBody == null) {
		     			lastClassBody = bd;
		     		} else if(lastClassBody.getEndLine() < bd.getEndLine()) {
		     			lastClassBody = bd;
		     		}
		     		
					
	    		}
	    	}
	    	if(firstClassBody != null && firstClassBody.getRange().isPresent()) {
		    	 if(firstNode == null) {
	        		firstNode = firstClassBody;
	        	} else {
	        		if(firstNode.getBeginLine() > firstClassBody.getBeginLine()) {
	        			firstNode = firstClassBody;
	        		}
	        	}
	    	}
	    	if(lastClassBody != null && lastClassBody.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastClassBody;
	            } else {
	            	if(lastNode.getEndLine() < lastClassBody.getEndLine()) {
	            		lastNode = lastClassBody;
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
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public NodeList<Expression> getArguments() {
        return arguments;
    }

    public NodeList<BodyDeclaration<?>> getClassBody() {
        return classBody;
    }

    @Override
    public SimpleName getName() {
        return name;
    }

    public EnumConstantDeclaration setArguments(final NodeList<Expression> arguments) {
        assertNotNull(arguments);
        notifyPropertyChange(ObservableProperty.ARGUMENTS, this.arguments, arguments);
        if (this.arguments != null)
            this.arguments.setParentNode(null);
        this.arguments = arguments;
        setAsParentNodeOf(arguments);
        return this;
    }

    public EnumConstantDeclaration setClassBody(final NodeList<BodyDeclaration<?>> classBody) {
        assertNotNull(classBody);
        notifyPropertyChange(ObservableProperty.CLASS_BODY, this.classBody, classBody);
        if (this.classBody != null)
            this.classBody.setParentNode(null);
        this.classBody = classBody;
        setAsParentNodeOf(classBody);
        return this;
    }

    @Override
    public EnumConstantDeclaration setName(final SimpleName name) {
        assertNotNull(name);
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null)
            this.name.setParentNode(null);
        this.name = name;
        setAsParentNodeOf(name);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getArguments(), getClassBody(), getAnnotations());
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
        for (int i = 0; i < classBody.size(); i++) {
            if (classBody.get(i) == node) {
                classBody.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public EnumConstantDeclaration clone() {
        return (EnumConstantDeclaration) accept(new CloneVisitor(), null);
    }

    @Override
    public EnumConstantDeclarationMetaModel getMetaModel() {
        return JavaParserMetaModel.enumConstantDeclarationMetaModel;
    }
}
