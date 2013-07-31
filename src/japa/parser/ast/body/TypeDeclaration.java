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
 * Created on 05/10/2006
 */
package japa.parser.ast.body;

import japa.parser.ast.Comment;
import japa.parser.ast.expr.AnnotationExpr;

import java.util.List;

/**
 * @author Julio Vilmar Gesser
 */
public abstract class TypeDeclaration extends BodyDeclaration {

    private String name;

    private int modifiers;

    private List<BodyDeclaration> members;

    public TypeDeclaration() {
    }

    public TypeDeclaration(int modifiers, String name) {
        this.name = name;
        this.modifiers = modifiers;
    }

    public TypeDeclaration(List<AnnotationExpr> annotations, JavadocComment javaDoc, int modifiers, String name, List<BodyDeclaration> members) {
        super(annotations, javaDoc);
        this.name = name;
        this.modifiers = modifiers;
        this.members = members;
    }

    public TypeDeclaration(int beginLine, int beginColumn, int endLine, int endColumn, List<AnnotationExpr> annotations, JavadocComment javaDoc, int modifiers, String name, List<BodyDeclaration> members, List<Comment> comments) {
        super(beginLine, beginColumn, endLine, endColumn, annotations, javaDoc, comments);
        this.name = name;
        this.modifiers = modifiers;
        this.members = members;
        
   
        this.codeLoc = 0;
        int firstBodyLine = endLine, lastBodyLine = beginLine;
        if(members != null) {
        	for(BodyDeclaration b:members) {
        		 this.codeLoc += b.getCodeLoc();
        		 if(firstBodyLine > b.getBeginColumn()) {
        			 firstBodyLine = b.getBeginColumn();
        		 }
        		 if(lastBodyLine < b.getEndLine()) {
        			 lastBodyLine = b.getEndLine();
        		 }
        	}
        	
        	if(beginLine < firstBodyLine) {
        		this.codeLoc++;
        	}
        	if(endLine > lastBodyLine) {
        		this.codeLoc++;
        	}
        } 
        
        
        
        if(codeLoc > loc) codeLoc = loc;
    }

    public final List<BodyDeclaration> getMembers() {
        return members;
    }

    /**
     * Return the modifiers of this type declaration.
     * 
     * @see ModifierSet
     * @return modifiers
     */
    public final int getModifiers() {
        return modifiers;
    }

    public final String getName() {
        return name;
    }

    public void setMembers(List<BodyDeclaration> members) {
        this.members = members;
    }

    public final void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public final void setName(String name) {
        this.name = name;
    }

}
