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
package japa.parser.ast.stmt;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class WhileStmt extends Statement {

    private Expression condition;

    private Statement body;

    public WhileStmt() {
    }

    public WhileStmt(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public WhileStmt(int beginLine, int beginColumn, int endLine, int endColumn, Expression condition, Statement body) {
        super(beginLine, beginColumn, endLine, endColumn);
        this.condition = condition;
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
      
        if(this.condition != null) {
        	this.complexity += (this.condition.getComplexity() -1);
        }
//        if(beginLine > 9928 && endLine < 9957)
//        	System.out.println("WhileStmt ("+beginLine+","+ endLine+") codeLoc="+this.codeLoc);
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

    public Expression getCondition() {
        return condition;
    }

    public void setBody(Statement body) {
        this.body = body;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }
}
