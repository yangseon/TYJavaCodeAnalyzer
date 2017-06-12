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
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.TryStmtMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set complexity
 * -. set code loc
 */
/**
 * The try statement.
 * <br/><pre>
 * try (InputStream i = new FileInputStream("file")) {
 *   // do things
 * } catch (IOException e) {
 *   e.printStackTrace();
 * } finally {
 *   System.out.println("Finally!!!");
 * }
 * </pre>
 * In this code, "i" is a resource, "// do things" is the content of the tryBlock,
 * there is one catch clause that catches IOException e, and there is a finally block.
 * <p>All of these are optional, but they should not all be empty or none at the same time.
 *
 * @author Julio Vilmar Gesser
 * @see CatchClause
 */
public final class TryStmt extends Statement {

    private NodeList<VariableDeclarationExpr> resources;

    private BlockStmt tryBlock;

    private NodeList<CatchClause> catchClauses;

    private BlockStmt finallyBlock;

    public TryStmt() {
        this(null, new NodeList<>(), new BlockStmt(), new NodeList<>(), null);
    }

    public TryStmt(final BlockStmt tryBlock, final NodeList<CatchClause> catchClauses, final BlockStmt finallyBlock) {
        this(null, new NodeList<>(), tryBlock, catchClauses, finallyBlock);
    }

    @AllFieldsConstructor
    public TryStmt(NodeList<VariableDeclarationExpr> resources, final BlockStmt tryBlock, final NodeList<CatchClause> catchClauses, final BlockStmt finallyBlock) {
        this(null, resources, tryBlock, catchClauses, finallyBlock);
    }

    public TryStmt(Range range, NodeList<VariableDeclarationExpr> resources, final BlockStmt tryBlock, final NodeList<CatchClause> catchClauses, final BlockStmt finallyBlock) {
        super(range);
        setResources(resources);
        setTryBlock(tryBlock);
        setCatchClauses(catchClauses);
        setFinallyBlock(finallyBlock);
        
        
        //ysryu
        this.codeLoc = 0;

        Node firstNode = null, lastNode = null;
        
     	VariableDeclarationExpr firstExpr = null, lastExpr = null;
     
     	if (this.resources != null && this.resources.isEmpty() == false)  {
 			for(VariableDeclarationExpr r:this.resources) {
 				if(r != null && r.getRange().isPresent()) {
	 				this.codeLoc += r.getCodeLoc();
	 				this.complexity += (r.getComplexity() -1);
	 				
	 				if(firstExpr != null && lastExpr != null) {
	            		// contains with arg
	            		if(firstExpr.getBeginLine() <= r.getBeginLine()
            					&& lastExpr.getEndLine() >= r.getEndLine()) {
	            			this.codeLoc -= r.getCodeLoc();
	        	        }
	            		else if(firstExpr.getBeginLine() == r.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastExpr.getEndLine() == r.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= r.getBeginLine()
	            					&& lastNode.getEndLine() >= r.getEndLine()) {
		            			this.codeLoc -= r.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == r.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == r.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	 				
	            	
	            	if(firstExpr == null) {
	            		firstExpr = r;
		     		} else if(firstExpr.getBeginLine() > r.getBeginLine()) {
		     			firstExpr = r;
		     		}
		     		
		     		if(lastExpr == null) {
		     			lastExpr = r;
		     		} else if(lastExpr.getEndLine() < r.getEndLine()) {
		     			lastExpr = r;
		     		}
	 			}
 			}
 			if(firstExpr != null && firstExpr.getRange().isPresent()) {
	 			if(firstNode == null) {
	        		firstNode = firstExpr;
	        	} 
 			}
 			if(lastExpr != null && lastExpr.getRange().isPresent()) {
	            if(lastNode == null) {
	            	lastNode = lastExpr;
	            } 
 			}
 		}
 		
 		
     	if(this.tryBlock != null && this.tryBlock.getRange().isPresent()) {        	
    		this.codeLoc += this.tryBlock.getCodeLoc();  
    		this.complexity += (this.tryBlock.getComplexity() -1);
    		
    		if(lastNode != null && lastNode.getRange().isPresent()) {
				if(lastNode.getEndLine() == this.tryBlock.getBeginLine()) {
					this.codeLoc--;
				}
			}
    		if(firstNode == null) {
        		firstNode = this.tryBlock;
        	} else {
        		if(firstNode.getBeginLine() > this.tryBlock.getBeginLine()) {
        			firstNode = this.tryBlock;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.tryBlock;
            } else {
            	if(lastNode.getEndLine() < this.tryBlock.getEndLine()) {
            		lastNode = this.tryBlock;
        		}
            }
        }
     	
     	CatchClause  firstCatchClause = null, lastCatchClause = null;
     	
        if(this.catchClauses != null && this.catchClauses.isEmpty() == false) {
        	for(CatchClause c:this.catchClauses) {
        		if(c != null && c.getRange().isPresent()) {
	        		this.codeLoc += c.getCodeLoc();
	        		this.complexity += (c.getComplexity() -1);
	        		
	        		if(firstCatchClause != null && lastCatchClause != null) {
	            		// contains with arg
	            		if(firstCatchClause.getBeginLine() <= c.getBeginLine()
            					&& lastCatchClause.getEndLine() >= c.getEndLine()) {
	            			this.codeLoc -= c.getCodeLoc();
	        	        }
	            		else if(firstCatchClause.getBeginLine() == c.getEndLine()) {
	            			this.codeLoc--;
	            		} else if(lastCatchClause.getEndLine() == c.getBeginLine()) {
	            			this.codeLoc--;
	            		}
	            	} else {
	            		
	            		if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= c.getBeginLine()
	            					&& lastNode.getEndLine() >= c.getEndLine()) {
		            			this.codeLoc -= c.getCodeLoc();
		        	        }
		            		else if(firstNode.getBeginLine() == c.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == c.getBeginLine()) {
		            			this.codeLoc--;
		            		}
	            		}
	            	}
	        		
	        		
	        		if(firstCatchClause == null) {
	        			firstCatchClause = c;
	 	     		} else if(firstCatchClause.getBeginLine() > c.getBeginLine()) {
	 	     			firstCatchClause = c;
	 	     		}
	 	     		
	 	     		if(lastCatchClause == null) {
	 	     			lastCatchClause = c;
	 	     		} else if(lastCatchClause.getEndLine()< c.getEndLine()) {
	 	     			lastCatchClause = c;
	 	     		}
		    	}
        	}

        	if(firstCatchClause != null && firstCatchClause.getRange().isPresent()) {
	        	if(firstNode == null) {
	        		firstNode = firstCatchClause;
	        	} else {
	        		if(firstNode.getBeginLine() > firstCatchClause.getBeginLine()) {
	        			firstNode = firstCatchClause;
	        		}
	        	}
        	}
        	
        	if(lastCatchClause != null && lastCatchClause.getRange().isPresent()) {
    	        
	            if(lastNode == null) {
	            	lastNode = lastCatchClause;
	            } else {
	            	if(lastNode.getEndLine() < lastCatchClause.getEndLine()) {
	            		lastNode = lastCatchClause;
	        		}
	            }
        	}
        } 
        
        
        
        
        if(this.finallyBlock != null && this.finallyBlock.getRange().isPresent()) {        	
     		this.codeLoc += finallyBlock.getCodeLoc();
     		this.complexity += (this.finallyBlock.getComplexity() -1);
     		
     		if(lastNode != null && lastNode.getRange().isPresent()) {
				if(lastNode.getEndLine() == this.finallyBlock.getBeginLine()) {
					this.codeLoc--;
				}
			}
     		
     		if(firstNode == null) {
        		firstNode = this.finallyBlock;
        	} else {
        		if(firstNode.getBeginLine() > this.finallyBlock.getBeginLine()) {
        			firstNode = this.finallyBlock;
        		}
        	}
            
            if(lastNode == null) {
            	lastNode = this.finallyBlock;
            } else {
            	if(lastNode.getEndLine() < this.finallyBlock.getEndLine()) {
            		lastNode = this.finallyBlock;
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

    public NodeList<CatchClause> getCatchClauses() {
        return catchClauses;
    }

    public Optional<BlockStmt> getFinallyBlock() {
        return Optional.ofNullable(finallyBlock);
    }

    public Optional<BlockStmt> getTryBlock() {
        return Optional.ofNullable(tryBlock);
    }

    public NodeList<VariableDeclarationExpr> getResources() {
        return resources;
    }

    public TryStmt setCatchClauses(final NodeList<CatchClause> catchClauses) {
        assertNotNull(catchClauses);
        notifyPropertyChange(ObservableProperty.CATCH_CLAUSES, this.catchClauses, catchClauses);
        if (this.catchClauses != null)
            this.catchClauses.setParentNode(null);
        this.catchClauses = catchClauses;
        setAsParentNodeOf(catchClauses);
        return this;
    }

    public TryStmt setFinallyBlock(final BlockStmt finallyBlock) {
        notifyPropertyChange(ObservableProperty.FINALLY_BLOCK, this.finallyBlock, finallyBlock);
        if (this.finallyBlock != null)
            this.finallyBlock.setParentNode(null);
        this.finallyBlock = finallyBlock;
        setAsParentNodeOf(finallyBlock);
        return this;
    }

    public TryStmt setTryBlock(final BlockStmt tryBlock) {
        notifyPropertyChange(ObservableProperty.TRY_BLOCK, this.tryBlock, tryBlock);
        if (this.tryBlock != null)
            this.tryBlock.setParentNode(null);
        this.tryBlock = tryBlock;
        setAsParentNodeOf(tryBlock);
        return this;
    }

    public TryStmt setResources(final NodeList<VariableDeclarationExpr> resources) {
        assertNotNull(resources);
        notifyPropertyChange(ObservableProperty.RESOURCES, this.resources, resources);
        if (this.resources != null)
            this.resources.setParentNode(null);
        this.resources = resources;
        setAsParentNodeOf(resources);
        return this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getCatchClauses(), getResources());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < catchClauses.size(); i++) {
            if (catchClauses.get(i) == node) {
                catchClauses.remove(i);
                return true;
            }
        }
        if (finallyBlock != null) {
            if (node == finallyBlock) {
                removeFinallyBlock();
                return true;
            }
        }
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i) == node) {
                resources.remove(i);
                return true;
            }
        }
        if (tryBlock != null) {
            if (node == tryBlock) {
                removeTryBlock();
                return true;
            }
        }
        return super.remove(node);
    }

    public TryStmt removeFinallyBlock() {
        return setFinallyBlock((BlockStmt) null);
    }

    public TryStmt removeTryBlock() {
        return setTryBlock((BlockStmt) null);
    }

    @Override
    public TryStmt clone() {
        return (TryStmt) accept(new CloneVisitor(), null);
    }

    @Override
    public TryStmtMetaModel getMetaModel() {
        return JavaParserMetaModel.tryStmtMetaModel;
    }
}
