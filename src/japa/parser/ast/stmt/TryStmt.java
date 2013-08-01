/*
 * Copyright (C) 2007 J�lio Vilmar Gesser.
 * 
 * This file is part of Java 1.5 parser and Abstract Syntax Tree.
 *
 * Java 1.5 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java 1.5 parser and Abstract Syntax Tree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.5 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 18/11/2006
 */

/**
 * Modified on 4/24/2013 yangseon ryu(ysryu)
 * -. Count codeLoc and  complexity  
 */
package japa.parser.ast.stmt;

import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

import java.util.List;

/**
 * @author Julio Vilmar Gesser
 */
public final class TryStmt extends Statement {

    private BlockStmt tryBlock;

    private List<CatchClause> catchs;

    private BlockStmt finallyBlock;

    public TryStmt() {
    }

    public TryStmt(BlockStmt tryBlock, List<CatchClause> catchs, BlockStmt finallyBlock) {
        this.tryBlock = tryBlock;
        this.catchs = catchs;
        this.finallyBlock = finallyBlock;
    }

    public TryStmt(int beginLine, int beginColumn, int endLine, int endColumn, BlockStmt tryBlock, List<CatchClause> catchs, BlockStmt finallyBlock) {
        super(beginLine, beginColumn, endLine, endColumn);
        this.tryBlock = tryBlock;
        this.catchs = catchs;
        this.finallyBlock = finallyBlock;
        
        //ysryu
        this.codeLoc = 0;
     	int lastCatchEndLine = 0, firstCatchBeineLine = endLine;
     	this.complexity++;
        if(this.tryBlock != null) {        	
    		this.codeLoc += tryBlock.getCodeLoc();    	
    		if(beginLine < this.tryBlock.getBeginLine()) this.codeLoc++;

    		this.complexity += (this.tryBlock.getComplexity() -1);
//    		System.out.println("tryBlock ("+tryBlock.getBeginLine()+","+ tryBlock.getEndLine()+") codeLoc="+tryBlock.getCodeLoc());
        }
        if(this.catchs != null) {
        	for(CatchClause c:this.catchs) {
        		this.codeLoc += c.getCodeLoc();
        		if(lastCatchEndLine < c.getEndLine()) {
					lastCatchEndLine = c.getEndLine();
				}
				if(firstCatchBeineLine > c.getBeginLine()) {
					firstCatchBeineLine = c.getBeginLine();
				}
			
				this.complexity += (c.getComplexity() -1);
				
//	    		System.out.println("tryBlock - CatchClause ("+c.getBeginLine()+","+ c.getEndLine()+") codeLoc="+c.getCodeLoc());

        	}
        	this.complexity += (this.catchs.size() -1);
        }     
        if(this.finallyBlock != null) {        	
     		this.codeLoc += finallyBlock.getCodeLoc();
     		this.complexity += (this.finallyBlock.getComplexity() -1);
//     		System.out.println("finallyBlock ("+finallyBlock.getBeginLine()+","+ finallyBlock.getEndLine()+") codeLoc="+finallyBlock.getCodeLoc());
     	}
		 	
		if(tryBlock != null && firstCatchBeineLine == tryBlock.getEndLine()) {
			this.codeLoc--;
		}
		if(finallyBlock != null && lastCatchEndLine == finallyBlock.getBeginLine()) {
			this.codeLoc--;
		}
		
        if(finallyBlock != null && finallyBlock.getEndLine() < endLine) {
        	this.codeLoc++;
        } else if(finallyBlock == null && catchs != null && lastCatchEndLine < endLine) {
        	this.codeLoc++;
        }

//		System.out.println("TryStmt ("+beginLine+","+ endLine+") codeLoc="+this.codeLoc);

    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public List<CatchClause> getCatchs() {
        return catchs;
    }

    public BlockStmt getFinallyBlock() {
        return finallyBlock;
    }

    public BlockStmt getTryBlock() {
        return tryBlock;
    }

    public void setCatchs(List<CatchClause> catchs) {
        this.catchs = catchs;
    }

    public void setFinallyBlock(BlockStmt finallyBlock) {
        this.finallyBlock = finallyBlock;
    }

    public void setTryBlock(BlockStmt tryBlock) {
        this.tryBlock = tryBlock;
    }
}
