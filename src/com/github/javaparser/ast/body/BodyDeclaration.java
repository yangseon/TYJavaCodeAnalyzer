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

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.observer.ObservableProperty;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.javaparser.utils.Utils.assertNotNull;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.BodyDeclarationMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
/**
 * Modified on 6/2/2017 yangseon ryu(ysryu)
 * -. set commentLoc
 */
/**
 * Any declaration that can appear between the { and } of a class, interface, or enum.
 *
 * @author Julio Vilmar Gesser
 */
public abstract class BodyDeclaration<T extends Node> extends Node implements NodeWithAnnotations<T> {

    private NodeList<AnnotationExpr> annotations;
    
    // ysryu
    protected long commentLoc = 0;  
    

    public BodyDeclaration() {
        this(null, new NodeList<>());
    }

    public BodyDeclaration(NodeList<AnnotationExpr> annotations) {
        this(null, annotations);
    }

    public BodyDeclaration(Range range, NodeList<AnnotationExpr> annotations) {
        super(range);
        setAnnotations(annotations);
        this.commentLoc = 0;  
    }

    @Override
    public final NodeList<AnnotationExpr> getAnnotations() {
        return annotations;
    }

    /**
     * @param annotations a null value is currently treated as an empty list. This behavior could change in the future,
     * so please avoid passing null
     */
    @SuppressWarnings("unchecked")
    @Override
    public final T setAnnotations(final NodeList<AnnotationExpr> annotations) {
        assertNotNull(annotations);
        notifyPropertyChange(ObservableProperty.ANNOTATIONS, this.annotations, annotations);
        if (this.annotations != null)
            this.annotations.setParentNode(null);
        this.annotations = annotations;
        setAsParentNodeOf(annotations);
        return (T) this;
    }

    @Override
    public List<NodeList<?>> getNodeLists() {
        return Arrays.asList(annotations);
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        for (int i = 0; i < annotations.size(); i++) {
            if (annotations.get(i) == node) {
                annotations.remove(i);
                return true;
            }
        }
        return super.remove(node);
    }

    @Override
    public BodyDeclaration<?> clone() {
        return (BodyDeclaration<?>) accept(new CloneVisitor(), null);
    }

    @Override
    public BodyDeclarationMetaModel getMetaModel() {
        return JavaParserMetaModel.bodyDeclarationMetaModel;
    }
    
    //ysryu
	public final long getCommentLoc() {
		Long commentLoc = 0l;
    	List<Comment>  commList= this.getAllContainedComments();
    	this.getComment().ifPresent(commList::add);
    	if(commList != null && commList.isEmpty() == false) {
    		for(Comment comm:commList) {
    			commentLoc +=comm.getLoc();
    		}
    	}
		return commentLoc;
	}
	
    /**
     * Added on 6/5/2017 yangseon ryu(ysryu)
     * set base code Loc from Callable Declaration 
     * 
     * @return
     */
    protected long setCodeLoc() {
        Set<Integer> declLines = new HashSet<Integer>();

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
        	this.codeLoc = declLines.size();
        } else {
        	this.codeLoc =  1;
        }
        return this.codeLoc;
    }
}
