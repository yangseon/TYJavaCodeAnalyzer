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
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

import java.util.List;

/**
 * @author Julio Vilmar Gesser
 */
public final class ConstructorDeclaration extends BodyDeclaration {

    private int modifiers;

    private List<TypeParameter> typeParameters;

    private String name;

    private List<Parameter> parameters;

    private List<NameExpr> throws_;

    private BlockStmt block;
    
    private String signature;
    
    public ConstructorDeclaration() {
    }

    public ConstructorDeclaration(int modifiers, String name) {
        this.modifiers = modifiers;
        this.name = name;
    }

    public ConstructorDeclaration(JavadocComment javaDoc, int modifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, String name, List<Parameter> parameters, List<NameExpr> throws_, BlockStmt block) {
        super(annotations, javaDoc);
        this.modifiers = modifiers;
        this.typeParameters = typeParameters;
        this.name = name;
        this.parameters = parameters;
        this.throws_ = throws_;
        this.block = block;
    }

    @SuppressWarnings("rawtypes")
	public ConstructorDeclaration(int beginLine, int beginColumn, int endLine, int endColumn, JavadocComment javaDoc, int modifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, 
    		String name, List<Parameter> parameters, List<NameExpr> throws_, BlockStmt block, List<Comment> comments, List imports, PackageDeclaration pakage) {
        super(beginLine, beginColumn, endLine, endColumn, annotations, javaDoc, comments);
        this.modifiers = modifiers;
        this.typeParameters = typeParameters;
        this.name = name;
        this.parameters = parameters;
        this.throws_ = throws_;
        this.block = block;
        
      //ysryu
        this.commentLoc = 0;
        this.codeLoc = 0;
        this.loc = 0;
        
        if(block != null) {
        	this.loc = this.block.getLoc();
        	this.codeLoc += block.getCodeLoc();
        	if(beginLine < this.block.getBeginLine()) this.codeLoc++;
    		if(endLine > this.block.getEndLine()) this.codeLoc++;
        	
        }
        
        if(javaDoc != null) {
            this.commentLoc += javaDoc.getLoc();
         }
        
        if(comments != null) {
	        for(Comment c:comments) {
	        	if(beginLine <= c.getBeginLine() && endLine >= c.getEndLine() ) {
		        	this.commentLoc += c.getLoc();
	        	}	        	
	        }
        }	
        if(block != null) {
 	     	List<Statement> slist = block.getStmts();
			if(slist != null) {
				for(Statement s:slist) {
					complexity += (s.getComplexity() - 1);
				}
	        }			
        }
        
		if(codeLoc > loc) codeLoc = loc;
		mappingSignature(imports, pakage);
		
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public BlockStmt getBlock() {
        return block;
    }

    /**
     * Return the modifiers of this member declaration.
     * 
     * @see ModifierSet
     * @return modifiers
     */
    public int getModifiers() {
        return modifiers;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<NameExpr> getThrows() {
        return throws_;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void setBlock(BlockStmt block) {
        this.block = block;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void setThrows(List<NameExpr> throws_) {
        this.throws_ = throws_;
    }

    public void setTypeParameters(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }
    	
    // ysryu

	@SuppressWarnings("rawtypes")
	private void mappingSignature(List imports, PackageDeclaration pakage) {
    	StringBuffer sb = new StringBuffer();
		String[] split = null;
		int index = -1;
		
		int size = 0;
		ImportDeclaration imp;
		String name;
		boolean matchflag = false;
		
		if(parameters != null) {
			sb.append("(");

			for(Parameter p:parameters) {
//				System.out.println(p.getType());
				
				split = p.toString().split(" ");
				
				if(split[0].endsWith("[]")) {
					sb.append("[");
					split[0] = split[0].substring(0, split[0].length()-2);
				}
				
				index = split[0].indexOf('<');
				if(index > 0)
					split[0] = split[0].substring(0, index);
				
				split[0].replace('<', ' ');
				split = split[0].split(" ");
				
				if("int".equals(split[0])) {
  					sb.append("I");
  				} else if("float".equals(split[0])) {
  					sb.append("F");
  				} else if("double".equals(split[0])) {
  					sb.append("D");
  				} else if("boolean".equals(split[0])) {
  					sb.append("Z");
  				} else if("long".equals(split[0])) {
  					sb.append("J");
  				} else if("byte".equals(split[0])) {
  					sb.append("B");
  				} else if("char".equals(split[0])) {
  					sb.append("C");
  				} else if("short".equals(split[0])) {
  					sb.append("S");
				} else {
					split[0] = split[0].replaceAll("\\.", "/");
					matchflag = false;
					if(checkJavaLangPackage(split[0])) {
						split[0] = "java/lang/"+split[0];
						matchflag = true;
					} else {
						if(imports != null) {
							size = imports.size();
							for(int i = 0; i < size; i++) {
								imp = (ImportDeclaration)imports.get(i);				
								name = imp.getName().getName();
								
								if("*".equals(name)) continue;
								if(name.equals(split[0])) {
									split[0] = imp.getName().toString().replace(".", "/");
									matchflag = true;
								}
							}
						}
					}
					
					if(!matchflag) {
						split[0] = pakage.getName().toString()+"/"+ split[0];
						split[0] = split[0].replace(".", "/");
					}
					sb.append("L"+split[0]+";");
				}
				
			}
			sb.append(")");
		} else {
			sb.append("()");
		}
		
		this.signature = sb.toString();
    }
    
    
    private boolean checkJavaLangPackage(String typename) {
    	// java.lang의 interface 또는 class
    	if(// interface
    			"Appendable".equals(typename) ||
    			"CharSequence".equals(typename)	||
    			"Cloneable".equals(typename)	||
    			"Comparable".equals(typename)	||
    			"Iterable".equals(typename)	||
    			"Readable".equals(typename)	||
    			"Runnable".equals(typename)	||
    			"Thread".equals(typename)	||
    			"Thread.UncaughtExceptionHandler".equals(typename)	||
    			// class
    			"Boolean".equals(typename)	||
    			"Byte".equals(typename)	||
    			"Character".equals(typename)	||
    			"Character".equals(typename)	||
    			"Character.Subset".equals(typename)	||
    			"Character.UnicodeBlock".equals(typename)	||
    			"Class".equals(typename)	||
    			"ClassLoader".equals(typename)	||
    			"Compiler".equals(typename)	||
    			"Double".equals(typename)	||
    			"Enum".equals(typename)	||
    			"Float".equals(typename)	||
    			"InheritableThreadLocal".equals(typename)	||
    			"Integer".equals(typename)	||
    			"Long".equals(typename)	||
    			"Math".equals(typename)	||
    			"Number".equals(typename)	||
    			"Object".equals(typename)	||
    			"Package".equals(typename)	||
    			"Process".equals(typename)	||
    			"ProcessBuilder".equals(typename)	||
    			"Runtime".equals(typename)	||
    			"RuntimePermission".equals(typename)	||
    			"SecurityManager".equals(typename)	||
    			"Short".equals(typename)	||
    			"StackTraceElement".equals(typename)	||
    			"StrictMath".equals(typename)	||  
    			"String".equals(typename)	||
    			"StringBuffer".equals(typename)	||
    			"StringBuilder".equals(typename)	||
    			"System".equals(typename)	||
    			"ThreadGroup".equals(typename)	||
    			"ThreadLocal".equals(typename)	||
    			"Throwable".equals(typename)	||
    			//enum
    			"Thread.State".equals(typename)
    			) {
    		return true;
    	}
    	
    	return false;
    }
    

	public final String getSignature() {
		return signature;
	}
}
