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

import japa.parser.ast.Node;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class CatchClause extends Node {

    private Parameter except;

    private BlockStmt catchBlock;

    public CatchClause() {
    }

    public CatchClause(Parameter except, BlockStmt catchBlock) {
        this.except = except;
        this.catchBlock = catchBlock;
    }

    public CatchClause(int beginLine, int beginColumn, int endLine, int endColumn, Parameter except, BlockStmt catchBlock) {
        super(beginLine, beginColumn, endLine, endColumn);
        this.except = except;
        this.catchBlock = catchBlock;
        
        //ysryu
        this.codeLoc = 0;
      
        if(this.catchBlock != null) {  

    		this.codeLoc += this.catchBlock.getCodeLoc();
    		if(beginLine < this.catchBlock.getBeginLine()) this.codeLoc++;
    		if(endLine > this.catchBlock.getEndLine()) this.codeLoc++;
    		
    		this.complexity += (catchBlock.getComplexity() -1);
    		
//    		System.out.println("CatchClause ("+catchBlock.getBeginLine()+","+ catchBlock.getEndLine()+") codeLoc="+catchBlock.getCodeLoc());
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

    public BlockStmt getCatchBlock() {
        return catchBlock;
    }

    public Parameter getExcept() {
        return except;
    }

    public void setCatchBlock(BlockStmt catchBlock) {
        this.catchBlock = catchBlock;
    }

    public void setExcept(Parameter except) {
        this.except = except;
    }
}
