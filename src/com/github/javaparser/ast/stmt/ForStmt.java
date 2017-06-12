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

import static com.github.javaparser.utils.Utils.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.ForStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * A classic for statement.
 * <br/>In <code>for(int a=3,b==5; a<99; a++) { ... }</code> the intialization is int a=3,b=5, 
 * compare is b==5, update is a++, and the statement or block statement following it is in body.  
 *
 * @author Julio Vilmar Gesser
 */
public final class ForStmt extends Statement implements NodeWithBody<ForStmt> {

    private NodeList<Expression> initialization;

    private Expression compare;

    private NodeList<Expression> update;

    private Statement body;

    public ForStmt() {
        this(null, new NodeList<>(), new BooleanLiteralExpr(), new NodeList<>(), new ReturnStmt());
    }

    @AllFieldsConstructor
    public ForStmt(final NodeList<Expression> initialization, final Expression compare, final NodeList<Expression> update, final Statement body) {
        this(null, initialization, compare, update, body);
    }

    public ForStmt(Range range, final NodeList<Expression> initialization, final Expression compare, final NodeList<Expression> update, final Statement body) {
        super(range);
        setCompare(compare);
        setInitialization(initialization);
        setUpdate(update);
        setBody(body);
        
        // ysryu
        this.codeLoc = 0;
        
        // Increase complexity - Loops    for, while, do-while, break, and continue.
        this.complexity++;
        
        Node firstNode = null, lastNode = null;
        
       	Expression firstInitialization = null, lastInitialization = null;
    	
        if (this.initialization != null && this.initialization.isEmpty() == false) {
            for(Expression init:this.initialization) {
            	if(init != null && init.getRange().isPresent()) {
	            	this.codeLoc += init.getCodeLoc();
	            	this.complexity += (init.getComplexity() - 1);
	            	
	            	if(firstInitialization != null && lastInitialization != null) {
	            		// contains with arg
	            		if(firstInitialization.getBeginLine() <= init.getBeginLine()
            					&& lastInitialization.getEndLine() >= init.getEndLine()) {
	            			this.codeLoc -= init.getCodeLoc();
	        	        }
	            		else if(firstInitialization.getBeginLine() == init.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastInitialization.getEndLine() == init.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= init.getBeginLine()
	            					&& lastNode.getEndLine() >= init.getEndLine()) {
		            			this.codeLoc -= init.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == init.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == init.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	            	
	        		if(firstInitialization == null) {
	        			firstInitialization = init;
	        		} else {
	        			if(firstInitialization.getBeginLine() > init.getBeginLine()) {
	        				firstInitialization = init;
	        			}
	        		}
	        		
	         		if(lastInitialization == null) {
	         			lastInitialization = init;
	        		} else {
	        			if(lastInitialization.getEndLine() < init.getEndLine()) {
	        				lastInitialization = init;
	        			}
	        		}
	            }
            }
            if(firstInitialization != null && firstInitialization.getRange().isPresent()) {
	            if(firstNode == null) { 
	            	firstNode = firstInitialization;
	            }
            }
            if(lastInitialization != null && lastInitialization.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastInitialization;
	            }
            }
        }
        
        if (this.compare != null && this.compare.getRange().isPresent()) {
        	this.codeLoc += this.compare.getCodeLoc();
        	this.complexity += (this.compare.getComplexity() - 1);
        	if(lastNode != null && lastNode.getRange().isPresent()) {
	        	if(lastNode.getEndLine() == this.compare.getBeginLine()) {
	            	this.codeLoc--;
	            }
        	}
        	if(firstNode == null) {
        		firstNode = this.compare;
        	} else {
        		if(firstNode.getBeginLine() > this.compare.getBeginLine()) {
        			firstNode = this.compare;
        		}
        	}
        	
        	
        	if(lastNode == null) {
            	lastNode = this.compare;
            } else {
            	if(lastNode.getEndLine() < this.compare.getEndLine()) {
            		lastNode = this.compare;
        		}
            }
        }
        
      	Expression firstUpdate = null, lastUpdate = null;
    
        if (this.update != null && this.update.isEmpty() == false) {
            for(Expression up:this.update) {
            	if(up != null && up.getRange().isPresent()) {
	            	this.codeLoc += up.getCodeLoc();
	            	this.complexity += (up.getComplexity() - 1);
	            	
	            	if(firstUpdate != null && lastUpdate != null) {
	            		// contains with arg
	            		if(firstUpdate.getBeginLine() <= up.getBeginLine()
            					&& lastUpdate.getEndLine() >= up.getEndLine()) {
	            			this.codeLoc -= up.getCodeLoc();
	        	        }
	            		else if(firstUpdate.getBeginLine() == up.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastUpdate.getEndLine() == up.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= up.getBeginLine()
	            					&& lastNode.getEndLine() >= up.getEndLine()) {
		            			this.codeLoc -= up.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == up.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == up.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	            	
	            	
	        		if(firstUpdate == null) {
	        			firstUpdate = up;
	        		} else {
	        			if(firstUpdate.getBeginLine() > up.getBeginLine()) {
	        				firstUpdate = up;
	        			}
	        		}
	        		
	         		if(lastUpdate == null) {
	         			lastUpdate = up;
	        		} else {
	        			if(lastUpdate.getEndLine() < up.getEndLine()) {
	        				lastUpdate = up;
	        			}
	        		}
	            }
            }

            if(firstUpdate != null && firstUpdate.getRange().isPresent()) {
	            if(firstNode == null) {
	        		firstNode = firstUpdate;
	        	} else {
	        		if(firstNode.getBeginLine() > firstUpdate.getBeginLine()) {
	        			firstNode = firstUpdate;
	        		}
	        	}
            }
            if(lastUpdate != null && lastUpdate.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastUpdate;
	            } else {
	            	if(lastNode.getEndLine() < lastUpdate.getEndLine()) {
	            		lastNode = lastUpdate;
	        		}
	            }
            }
        }
        
        if (this.body != null && this.body.getRange().isPresent()) {
            this.codeLoc += this.body.getCodeLoc();
            this.complexity += (this.body.getComplexity() - 1);

            if (lastNode != null && lastNode.getRange().isPresent()) {
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

    public Optional<Expression> getCompare() {
        return Optional.ofNullable(compare);
    }

    public NodeList<Expression> getInitialization() {
        return initialization;
    }

    public NodeList<Expression> getUpdate() {
        return update;
    }

    @Override
    public ForStmt setBody(final Statement body) {
        assertNotNull(body);
        notifyPropertyChange(ObservableProperty.BODY, this.body, body);
        if (this.body != null) {
            this.body.setParentNode(null);
        }
        this.body = body;
        setAsParentNodeOf(body);
        return this;
    }

    /**
     * Sets the compare
     *
     * @param compare the compare, can be null
     * @return this, the ForStmt
     */
    public ForStmt setCompare(final Expression compare) {
        notifyPropertyChange(ObservableProperty.COMPARE, this.compare, compare);
        if(this.compare != null) {
        	this.compare.setParentNode(null);
        }
        this.compare = compare;
        setAsParentNodeOf(compare);
        return this;
    }

    public ForStmt setInitialization(final NodeList<Expression> initialization) {
        assertNotNull(initialization);
        notifyPropertyChange(ObservableProperty.INITIALIZATION, this.initialization, initialization);
        if (this.initialization != null)
            this.initialization.setParentNode(null);
        this.initialization = initialization;
        setAsParentNodeOf(initialization);
        return this;
    }

    public ForStmt setUpdate(final NodeList<Expression> update) {
        assertNotNull(update);
        notifyPropertyChange(ObservableProperty.UPDATE, this.update, update);
        if (this.update != null)
            this.update.setParentNode(null);
        this.update = update;
        setAsParentNodeOf(update);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getInitialization(), getUpdate());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        if (compare != null) {
            if (node == compare) {
                removeCompare();
                return true;
            }
        }
        for (int i = 0; i < initialization.size(); i++) {
            if (initialization.get(i) == node) {
                initialization.remove(i);
                return true;
            }
        }
        for (int i = 0; i < update.size(); i++) {
            if (update.get(i) == node) {
                update.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    public ForStmt removeCompare() {
        return setCompare((Expression) null);
    }

    @Override
    public ForStmt clone() {
        return (ForStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public ForStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.forStmtMetaModel;
    }
}
