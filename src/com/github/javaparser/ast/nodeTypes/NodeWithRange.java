package com.github.javaparser.ast.nodeTypes;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

import java.util.Optional;
/**
 * Modified on 5/29/2017 yangseon ryu(ysryu)
 * -. getBeginLine, getEndLine
 */
/**
 * A node that has a Range, which is every Node.
 * 
 */
public interface NodeWithRange<N> {
    Optional<Range> getRange();

    N setRange(Range range);

    /**
     * The begin position of this node in the source file.
     */
    default Optional<Position> getBegin() {
        return getRange().map(r -> r.begin);
    }

    /**
     * The end position of this node in the source file.
     */
    default Optional<Position> getEnd() {
        return getRange().map(r -> r.end);
    }

    default boolean containsWithin(Node other) {
        if (getRange().isPresent() && other.getRange().isPresent()) {
            return getRange().get().contains(other.getRange().get());
        }
        return false;
    }

    
    //ysryu
    
    /**
     * The begin line of this node in the source file.
     */
    default int getBeginLine() {
    	return getRange().map(r -> r.begin).get().line;
    }

    /**
     * The end line of this node in the source file.
     */
    default int getEndLine() {
        return getRange().map(r -> r.end).get().line;
    }
    
    /**
     * @deprecated use isAfter() on range
     */
    @Deprecated
    default boolean isPositionedAfter(Position position) {
        return getRange().map(r -> r.isAfter(position)).orElse(false);
    }

    /**
     * @deprecated use isBefore() on range
     */
    @Deprecated
    default boolean isPositionedBefore(Position position) {
        return getRange().map(r -> r.isBefore(position)).orElse(false);
    }
}
