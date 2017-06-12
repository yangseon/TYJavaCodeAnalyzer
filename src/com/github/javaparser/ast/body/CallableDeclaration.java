/*
 * Copyright (C) 2007-2010 JÃºlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2017 The JavaParser Team.
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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.Range;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.CallableDeclarationMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;

/**
 * Represents a declaration which is callable eg. a method or a constructor.
 */
public abstract class CallableDeclaration<T extends Node> extends BodyDeclaration<T> {

    private EnumSet<Modifier> modifiers;

    private NodeList<TypeParameter> typeParameters;

    private SimpleName name;

    private NodeList<Parameter> parameters;

    private NodeList<ReferenceType<?>> thrownExceptions;
    
    //ysryu
    private NodeList<ImportDeclaration> imports = null;
    private PackageDeclaration pakage = null;

    public CallableDeclaration(Range range, EnumSet<Modifier> modifiers, NodeList<AnnotationExpr> annotations, NodeList<TypeParameter> typeParameters, SimpleName name, NodeList<Parameter> parameters, NodeList<ReferenceType<?>> thrownExceptions,
    		// ysryu
    		NodeList<ImportDeclaration> imports, PackageDeclaration pakage) {
        super(range, annotations);
        setModifiers(modifiers);
        setTypeParameters(typeParameters);
        setName(name);
        setParameters(parameters);
        setThrownExceptions(thrownExceptions);
        
         //ysryu
        this.imports = imports;
        this.pakage = pakage;
    }

    /**
     * Return the modifiers of this member declaration.
     *
     * @return modifiers
     * @see Modifier
     */
    public EnumSet<Modifier> getModifiers() {
        return modifiers;
    }

    public T setModifiers(final EnumSet<Modifier> modifiers) {
        assertNotNull(modifiers);
        notifyPropertyChange(ObservableProperty.MODIFIERS, this.modifiers, modifiers);
        this.modifiers = modifiers;
        return (T) this;
    }

    public SimpleName getName() {
        return name;
    }

    public T setName(final SimpleName name) {
        assertNotNull(name);
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null)
            this.name.setParentNode(null);
        this.name = name;
        setAsParentNodeOf(name);
        return (T) this;
    }

    public NodeList<Parameter> getParameters() {
        return parameters;
    }

    public T setParameters(final NodeList<Parameter> parameters) {
        assertNotNull(parameters);
        notifyPropertyChange(ObservableProperty.PARAMETERS, this.parameters, parameters);
        if (this.parameters != null)
            this.parameters.setParentNode(null);
        this.parameters = parameters;
        setAsParentNodeOf(parameters);
        return (T) this;
    }

    public NodeList<ReferenceType<?>> getThrownExceptions() {
        return thrownExceptions;
    }

    public T setThrownExceptions(final NodeList<ReferenceType<?>> thrownExceptions) {
        assertNotNull(thrownExceptions);
        notifyPropertyChange(ObservableProperty.THROWN_EXCEPTIONS, this.thrownExceptions, thrownExceptions);
        if (this.thrownExceptions != null)
            this.thrownExceptions.setParentNode(null);
        this.thrownExceptions = thrownExceptions;
        setAsParentNodeOf(thrownExceptions);
        return (T) this;
    }

    public NodeList<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public T setTypeParameters(final NodeList<TypeParameter> typeParameters) {
        assertNotNull(typeParameters);
        notifyPropertyChange(ObservableProperty.TYPE_PARAMETERS, this.typeParameters, typeParameters);
        if (this.typeParameters != null)
            this.typeParameters.setParentNode(null);
        this.typeParameters = typeParameters;
        setAsParentNodeOf(typeParameters);
        return (T) this;
    }

    public String getDeclarationAsString(boolean includingModifiers, boolean includingThrows) {
        return getDeclarationAsString(includingModifiers, includingThrows, true);
    }

    public String getDeclarationAsString() {
        return getDeclarationAsString(true, true, true);
    }

    public abstract String getDeclarationAsString(boolean includingModifiers, boolean includingThrows, boolean includingParameterName);

    protected String appendThrowsIfRequested(boolean includingThrows) {
        StringBuilder sb = new StringBuilder();
        if (includingThrows) {
            boolean firstThrow = true;
            for (ReferenceType<?> thr : getThrownExceptions()) {
                if (firstThrow) {
                    firstThrow = false;
                    sb.append(" throws ");
                } else {
                    sb.append(", ");
                }
                sb.append(thr.toString(prettyPrinterNoCommentsConfiguration));
            }
        }
        return sb.toString();
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getParameters(), getThrownExceptions(), getTypeParameters(), getAnnotations());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i) == node) {
                parameters.remove(i);
                return true;
            }
        }
        for (int i = 0; i < thrownExceptions.size(); i++) {
            if (thrownExceptions.get(i) == node) {
                thrownExceptions.remove(i);
                return true;
            }
        }
        for (int i = 0; i < typeParameters.size(); i++) {
            if (typeParameters.get(i) == node) {
                typeParameters.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public CallableDeclaration<?> clone() {
        return (CallableDeclaration<?>) accept(new CloneVisitor(), null);
    }

    @Override
    public CallableDeclarationMetaModel getMetaModel() {
        return JavaParserMetaModel.callableDeclarationMetaModel;
    }
    
    /**
     * Added on 6/5/2017 yangseon ryu(ysryu)
     * set base code Loc from Callable Declaration 
     * 
     * @return
     */
    protected long setCodeLoc(final BlockStmt body) {
        Set<Integer> declLines = new HashSet<Integer>();
        declLines.add(new Integer(this.name.getBeginLine()));

        this.codeLoc = 0;

        for(NodeList<?>  nl:this.getNodeLists()) {
        	if(nl != null && nl.isEmpty() == false) {
        		for(Node n:nl) {
	        		if(n != null && n.getRange().isPresent()) {
	        			for(int lNum = n.getBeginLine(); lNum <= n.getEndLine(); lNum++) {
	        				declLines.add(new Integer(lNum));
	        			}
	            	}
	        	}
        	}
        }
        
        if(declLines.size() > 0) {
        
        	/* 
        	 * just like 
        	 * 
        	 * @Override				// <- lastNode
        	 * public String toString(){ // <- name
        	 * }
        	 * 
        	 */
        	if(this.getAnnotations().isEmpty() == false 
        			&& declLines.contains(new Integer(this.name.getEndLine())) == false) {
        		declLines.add(new Integer(this.name.getEndLine()));
        	} 
        	this.codeLoc = declLines.size();
        	
        } else {
        	if(this.name != null) { // always true
	        	this.codeLoc =  1;
	        	
        	}
        }
        /*
         * if(this.codeLoc < gap)  is just like - seperated ')'
         * 
         * public ParseException(Token currentTokenVal,
         *			 int[][] expectedTokenSequencesVal,
         *			 String[] tokenImageVal
         *			)
         *	{
         *
         * if(this.codeLoc > gap)  is just like  -  overlap with body
         * 
         *  private static String initialise(Token currentToken,
         *                  int[][] expectedTokenSequences,
         *                 String[] tokenImage,
         *                  String lexicalStateName) {
         */
        if(body != null) {
        	Integer bodyBeginLine = new Integer(body.getBeginLine());
        	if(declLines.contains(bodyBeginLine)) {
        		declLines.remove(bodyBeginLine);
        	}
        	this.codeLoc = declLines.size();
        }
        return this.codeLoc;
    }

	/**
	 * 
	 * http://www.rgagnon.com/javadetails/java-0286.html
	 * 
	 *		 Type     Chararacter 
	 *			boolean      Z 
	 *			byte         B 
	 *			char         C 
	 *			double       D 
	 *			float        F 
	 *			int          I 
	 *			long         J 
	 *			object       L 
	 *			short        S 
	 *			void         V 
	 *			array        [ 
	 * @param typename
	 * @return
	 */
	
	 protected String getSignatureType(String typename) {
	    	String name;
			boolean matchflag = false;
			StringBuilder sb = new StringBuilder();
			StringBuilder sbForArry = null;
			
			int index = -1;
			
			typename = typename.replaceAll("\\.", "/");
			
			while(typename.endsWith("[]")) {
				if(sbForArry == null) {
					sbForArry = new StringBuilder();
				}
				sbForArry.append("[");
				typename = typename.substring(0, typename.length()-2);
			}
			index = typename.indexOf('<');
			if(index > 0)
				typename = typename.substring(0, index);
			
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
		
				matchflag = false;
				if(checkJavaLangPackage(typename)) {
					typename = "java/lang/"+typename;
					matchflag = true;
				} else {
					if(this.imports != null) {
						for(ImportDeclaration imp:this.imports) {
							name = imp.getName().getIdentifier();
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
				sb.append("L"+typename);
				
				
			} 
	    	
	    	if(sbForArry != null) {
	    		sb.insert(0, sbForArry.toString());
	    	}
	    	
	    	sb.append(";");
	    	return sb.toString();
	    }
	    
	    private boolean checkJavaLangPackage(String typename) {
	    	// java.langÀÇ interface ¶Ç´Â class
	    	if(// interface
	    			"Appendable".equals(typename) ||
	    			// Since 1.7 
	    			"AutoCloseable".equals(typename) ||
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
	    			// since 1.7
	    			"ProcessBuilder.Redirect".equals(typename)	||
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
	    			"Thread".equals(typename)	||
	    			
	    			"ThreadGroup".equals(typename)	||
	    			"ThreadLocal".equals(typename)	||
	    			"Throwable".equals(typename)	||
	    			"Void".equals(typename)	||
	    			//enum
	    			// since 1.7
	    			"Character.UnicodeScript".equals(typename) ||
	    			"ProcessBuilder.Redirect.Type".equals(typename) ||
	    			"Thread.State".equals(typename)
	    			
	    		
	    			) {
	    		return true;
	    	}
	    	
	    	return false;
	    }
}
