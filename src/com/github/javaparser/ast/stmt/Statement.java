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
package com.github.javaparser.ast.stmt;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.metamodel.StatementMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;

/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. set code loc
 */

/**
 * A base class for all statements.
 *
 * @author Julio Vilmar Gesser
 */
public abstract class Statement extends Node {

    public Statement(final Range range) {
        super(range);
         if(this.getRange().isPresent()) {
        	this.codeLoc = this.getRange().get().getLoC();
         } else {
        	 this.codeLoc = 0;
         }
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public Statement clone() {
        return (Statement) accept(new CloneVisitor(), null);
    }

    @Override
    public StatementMetaModel getMetaModel() {
        return JavaParserMetaModel.statementMetaModel;
    }
}
