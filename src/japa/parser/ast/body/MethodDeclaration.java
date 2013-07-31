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
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.GenericVisitor;
import japa.parser.ast.visitor.VoidVisitor;

import java.util.List;

/**
 * @author Julio Vilmar Gesser
 */
public final class MethodDeclaration extends BodyDeclaration {

    private int modifiers;

    private List<TypeParameter> typeParameters;

    private Type type;

    private String name;

    private List<Parameter> parameters;

    private int arrayCount;

    private List<NameExpr> throws_;

    private BlockStmt body;
    
    private String signature;
    
    public MethodDeclaration() {
    }

    public MethodDeclaration(int modifiers, Type type, String name) {
        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
    }

    public MethodDeclaration(int modifiers, Type type, String name, List<Parameter> parameters) {
        this.modifiers = modifiers;
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    public MethodDeclaration(JavadocComment javaDoc, int modifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, Type type, String name, List<Parameter> parameters, int arrayCount, List<NameExpr> throws_, BlockStmt block, List<Comment> comments) {
        super(annotations, javaDoc);
        this.modifiers = modifiers;
        this.typeParameters = typeParameters;
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.arrayCount = arrayCount;
        this.throws_ = throws_;
        this.body = block;
        
        super.loc = this.body.getEndLine() - body.getBeginLine() + 1;
        this.commentLoc = 0;
        
    }

    @SuppressWarnings("rawtypes")
	public MethodDeclaration(int beginLine, int beginColumn, int endLine, int endColumn, JavadocComment javaDoc, int modifiers, List<AnnotationExpr> annotations, List<TypeParameter> typeParameters, 
    		Type type, String name, List<Parameter> parameters, int arrayCount, List<NameExpr> throws_, BlockStmt block, List<Comment> comments, List imports, PackageDeclaration pakage) {
        super(beginLine, beginColumn, endLine, endColumn, annotations, javaDoc, comments);
        this.modifiers = modifiers;
        this.typeParameters = typeParameters;
        this.type = type;
        this.name = name;
        this.parameters = parameters;
        this.arrayCount = arrayCount;
        this.throws_ = throws_;
        this.body = block;
        
        //ysryu
        this.commentLoc = 0;
        this.codeLoc = 0;
        this.loc = 0;
        
        if(body != null) {
        	this.loc = this.body.getLoc();
        	this.codeLoc = this.body.getCodeLoc();
        	
    		if(beginLine < this.body.getBeginLine()) this.codeLoc++;
    		if(endLine > this.body.getEndLine()) this.codeLoc++;
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
        
      
        if(body != null) {
        	List<Statement> slist = body.getStmts();
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

    public int getArrayCount() {
        return arrayCount;
    }

    public BlockStmt getBody() {
        return body;
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

    public Type getType() {
        return type;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void setArrayCount(int arrayCount) {
        this.arrayCount = arrayCount;
    }

    public void setBody(BlockStmt body) {
        this.body = body;
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

    public void setType(Type type) {
        this.type = type;
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
		
		int size;
		ImportDeclaration imp;
		String name;
		boolean matchflag = false;
		String paramtype = null;
		
		if(parameters != null) {
			sb.append("(");

			for(Parameter p:parameters) {
//				System.out.println(p.getType());
				
				split = p.toString().split(" ");
				
				paramtype = split[0];
				
				// ex. @ModelAttribute("M_mdmAgentInstallLoginAdapterMngVO") M_mdmAgentInstallLoginAdapterMngVO input
				if(paramtype.startsWith("@")) {					
					if(split.length > 2)
						paramtype = split[1];
					else if(split.length == 2) {
						// ex. @ModelAttribute("M_mdmAgentInstallLoginAdapterMngVO")M_mdmAgentInstallLoginAdapterMngVO input
						int endStrIdx = paramtype.lastIndexOf('"');
						int endBracketIdx = paramtype.lastIndexOf(')');
						
						if(endBracketIdx-1 == endStrIdx) {
							paramtype = paramtype.substring(endBracketIdx+1);
						}
						
						
					}
				}
				
				// array, ex. String[]
				if(paramtype.endsWith("[]")) {
					sb.append("[");
					paramtype = paramtype.substring(0, paramtype.length()-2);
				}
				
				// collection, ex. List<String>
				index = paramtype.indexOf('<');
				if(index > 0)
					paramtype = paramtype.substring(0, index);				
				paramtype.replace('<', ' ');
				split = paramtype.split(" ");
				
			
				if("int".equals(paramtype)) {
  					sb.append("I");
  				} else if("float".equals(paramtype)) {
  					sb.append("F");
  				} else if("double".equals(paramtype)) {
  					sb.append("D");
  				} else if("boolean".equals(paramtype)) {
  					sb.append("Z");
  				} else if("long".equals(paramtype)) {
  					sb.append("J");
  				} else if("byte".equals(paramtype)) {
  					sb.append("B");
  				} else if("char".equals(paramtype)) {
  					sb.append("C");
  				} else if("short".equals(paramtype)) {
  					sb.append("S");
				} else {
					paramtype = paramtype.replaceAll("\\.", "/");
					matchflag = false;
					if(checkJavaLangPackage(paramtype)) {
						paramtype = "java/lang/"+paramtype;
						matchflag = true;
					} else {
						if(imports != null) {
							size = imports.size();
							
							for(int i = 0; i < size; i++) {
								imp = (ImportDeclaration)imports.get(i);				
								name = imp.getName().getName();
								
								if("*".equals(name)) continue;
								if(name.equals(paramtype)) {
									paramtype = imp.getName().toString().replace(".", "/");
									matchflag = true;
								}
							}
						}
					}
					if(!matchflag) {
						paramtype = pakage.getName().toString()+"/"+ paramtype;
						paramtype = paramtype.replace(".", "/");
					}
					
					sb.append("L"+paramtype+";");
				}
				
			}
			sb.append(")");
		} else {
			sb.append("()");
		}
		String typename = type.toString();
		if("void".equals(typename)) {
			sb.append("V");
		} else if("int".equals(typename)) {
			sb.append("I");
		} else if("float".equals(typename)) {
			sb.append("F");
		} else if("double".equals(typename)) {
			sb.append("D");
		} else if("boolean".equals(typename)) {
			sb.append("Z");
		} else if("long".equals(typename)) {
			sb.append("J");
		} else if("byte".equals(typename)) {
			sb.append("B");
		} else if("char".equals(typename)) {
			sb.append("C");
		} else if("short".equals(typename)) {
			sb.append("S");
		} else {
			
			typename = typename.replaceAll("\\.", "/");
			
			if(typename.endsWith("[]")) {
				sb.append("[");
				typename = typename.substring(0, typename.length()-2);
			}
			index = typename.indexOf('<');
			if(index > 0)
				typename = typename.substring(0, index);
	
			matchflag = false;
			if(checkJavaLangPackage(typename)) {
				typename = "java/lang/"+typename;
				matchflag = true;
			} else {
				if(imports != null) {
					size = imports.size();
					
					for(int i = 0; i < size; i++) {
						imp = (ImportDeclaration)imports.get(i);				
						name = imp.getName().getName();
						if("*".equals(name)) continue;
						if(name.equals(typename)) {
							typename = imp.getName().toString().replace(".", "/");
							matchflag = true;
						}
					}
				}
			}
			if(!matchflag && pakage != null) {
				if(pakage.getName() != null) {
					typename = pakage.getName().toString()+"/"+ typename;
					typename = typename.replace(".", "/");
				}
			}
			sb.append("L"+typename+";");
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
