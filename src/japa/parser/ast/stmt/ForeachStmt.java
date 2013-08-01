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
 * Created on 07/11/2006
 */
/**
 * Modified on 4/24/2013 yangseon ryu(ysryu)
 * -. Count codeLoc and  complexity  
 */
package japa.parser.ast.stmt;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class ForeachStmt extends Statement {

    private VariableDeclarationExpr var;

    private Expression iterable;

    private Statement body;

    public ForeachStmt() {
    }

    public ForeachStmt(VariableDeclarationExpr var, Expression iterable, Statement body) {
        this.var = var;
        this.iterable = iterable;
        this.body = body;
    }

    public ForeachStmt(int beginLine, int beginColumn, int endLine, int endColumn, VariableDeclarationExpr var, Expression iterable, Statement body) {
        super(beginLine, beginColumn, endLine, endColumn);
        this.var = var;
        this.iterable = iterable;
        this.body = body;
        
        //ysryu
        this.codeLoc = 0;
        this.complexity++;
        if(this.body != null) {        	
    		this.codeLoc += body.getCodeLoc();
    		if(beginLine < body.getBeginLine()) {
    			this.codeLoc++;
    		}
    		if(endLine > body.getEndLine()) {
    			this.codeLoc++;
    		}    		
    		this.complexity += (this.body.getComplexity() - 1);
        }   
        if(this.iterable != null) {
        	this.complexity += (this.iterable.getComplexity() - 1);
        }
//      	System.out.println("ForeachStmt ("+beginLine+","+ endLine+") complexity " + this.complexity );       
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public Statement getBody() {
        return body;
    }

    public Expression getIterable() {
        return iterable;
    }

    public VariableDeclarationExpr getVariable() {
        return var;
    }

    public void setBody(Statement body) {
        this.body = body;
    }

    public void setIterable(Expression iterable) {
        this.iterable = iterable;
    }

    public void setVariable(VariableDeclarationExpr var) {
        this.var = var;
    }
}
