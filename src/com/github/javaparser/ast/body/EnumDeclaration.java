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
package com.github.javaparser.ast.body;

import static com.github.javaparser.utils.Utils.assertNonEmpty;
import static com.github.javaparser.utils.Utils.assertNotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.Range;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithImplements;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.EnumDeclarationMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;

/**
 * The declaration of an enum.<br/><code>enum X { ... }</code>
 *
 * @author Julio Vilmar Gesser
 */
public final class EnumDeclaration extends TypeDeclaration<EnumDeclaration> implements NodeWithImplements<EnumDeclaration> {

    private NodeList<ClassOrInterfaceType> implementedTypes;

    private NodeList<EnumConstantDeclaration> entries;

    public EnumDeclaration() {
        this(null, EnumSet.noneOf(Modifier.class), new NodeList<>(), new SimpleName(), new NodeList<>(), new NodeList<>(), new NodeList<>());
    }

    public EnumDeclaration(EnumSet<Modifier> modifiers, String name) {
        this(null, modifiers, new NodeList<>(), new SimpleName(name), new NodeList<>(), new NodeList<>(), new NodeList<>());
    }

    @AllFieldsConstructor
    public EnumDeclaration(EnumSet<Modifier> modifiers, NodeList<AnnotationExpr> annotations, SimpleName name, NodeList<ClassOrInterfaceType> implementedTypes, NodeList<EnumConstantDeclaration> entries, NodeList<BodyDeclaration<?>> members) {
        this(null, modifiers, annotations, name, implementedTypes, entries, members);
    }

    public EnumDeclaration(Range range, EnumSet<Modifier> modifiers, NodeList<AnnotationExpr> annotations, SimpleName name, NodeList<ClassOrInterfaceType> implementedTypes, NodeList<EnumConstantDeclaration> entries, NodeList<BodyDeclaration<?>> members) {
        super(range, annotations, modifiers, name, members);
        setImplementedTypes(implementedTypes);
        setEntries(entries);
        
        
        // ysryu
//        this.codeLoc = 0;
        this.codeLoc = 0;
    	Node firstNode = null, lastNode = null;
    	
    	if(this.getName() != null) {
    		firstNode = this.getName();
    		lastNode = this.getName();
    		this.codeLoc+= this.getName().getLoc();
    	}
    	
        for(NodeList<?>  nl:this.getNodeLists()) {
        	
        	if(nl != null && nl.isEmpty() == false) {
        		
        		for(Node n:nl) {
	        		if(n != null && n.getRange().isPresent()) {
		            	this.codeLoc += n.getCodeLoc();
		            	this.complexity += (n.getComplexity() -1);
		            	
		            	if(firstNode != null && lastNode != null) {
	            			if(firstNode.getBeginLine() <= n.getBeginLine()
	            					&& lastNode.getEndLine() >= n.getEndLine()) {
		            			this.codeLoc -= n.getCodeLoc();
		                   }
		            		else if(firstNode.getBeginLine() == n.getEndLine()) {
		            			this.codeLoc--;
		            		} else if(lastNode.getEndLine() == n.getBeginLine()) {
		            			this.codeLoc--;
		             		}
	            		}
		            	
		            	if(firstNode == null) {
		            		firstNode = n;
			     		} else if(firstNode.getBeginLine() > n.getBeginLine()) {
			     			firstNode = n;
			     		}
			     		
			     		if(lastNode == null) {
			     			lastNode = n;
			     		} else if(lastNode.getEndLine() < n.getEndLine()) {
			     			lastNode = n;
			     		}
			     		
	            	}
	        	}
        	}
        }
       
        if(lastNode != null && lastNode.getRange().isPresent()) {
        	if(lastNode.getEndLine() < this.getEndLine()) {
        		this.codeLoc++;
        	}
        } else {// this body is empty
        	if(this.getRange().isPresent()) {
	        	if(this.getBeginLine() != this.getEndLine()) {
	        		this.codeLoc++;
	        	}
        	}
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

    public NodeList<EnumConstantDeclaration> getEntries() {
        return entries;
    }

    public EnumConstantDeclaration getEntry(int i) {
        return getEntries().get(i);
    }

    public EnumDeclaration setEntry(int i, EnumConstantDeclaration element) {
        getEntries().set(i, element);
        return this;
    }

    public EnumDeclaration addEntry(EnumConstantDeclaration element) {
        getEntries().add(element);
        return this;
    }

    @Override
    public NodeList<ClassOrInterfaceType> getImplementedTypes() {
        return implementedTypes;
    }

    public EnumDeclaration setEntries(final NodeList<EnumConstantDeclaration> entries) {
        assertNotNull(entries);
        notifyPropertyChange(ObservableProperty.ENTRIES, this.entries, entries);
        if (this.entries != null)
            this.entries.setParentNode(null);
        this.entries = entries;
        setAsParentNodeOf(entries);
        return this;
    }

    @Override
    public EnumDeclaration setImplementedTypes(final NodeList<ClassOrInterfaceType> implementedTypes) {
        assertNotNull(implementedTypes);
        notifyPropertyChange(ObservableProperty.IMPLEMENTED_TYPES, this.implementedTypes, implementedTypes);
        if (this.implementedTypes != null)
            this.implementedTypes.setParentNode(null);
        this.implementedTypes = implementedTypes;
        setAsParentNodeOf(implementedTypes);
        return this;
    }

    public EnumConstantDeclaration addEnumConstant(String name) {
        assertNonEmpty(name);
        EnumConstantDeclaration enumConstant = new EnumConstantDeclaration(name);
        getEntries().add(enumConstant);
        return enumConstant;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(getEntries(), getImplementedTypes(), getMembers(), getAnnotations());
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) == node) {
                entries.remove(i);
                return true;
            }
        }
        for (int i = 0; i < implementedTypes.size(); i++) {
            if (implementedTypes.get(i) == node) {
                implementedTypes.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public EnumDeclaration clone() {
        return (EnumDeclaration) accept(new CloneVisitor(), null);
    }

    @Override
    public EnumDeclarationMetaModel getMetaModel() {
        return JavaParserMetaModel.enumDeclarationMetaModel;
    }
}
