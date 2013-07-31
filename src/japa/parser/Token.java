/* Generated By:JavaCC: Do not edit this line. Token.java Version 4.1 */
/* JavaCCOptions:TOKEN_EXTENDS=,KEEP_LINE_COL=null */
/*
 * Copyright (C) 2008 J�lio Vilmar Gesser.
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
package japa.parser;

/**
 * Describes the input token stream.
 */

public class Token {

  /**
   * An integer that describes the kind of this token.  This numbering
   * system is determined by JavaCCParser, and a table of these numbers is
   * stored in the file ...Constants.java.
   */
  public int kind;

  /** The line number of the first character of this Token. */
  public int beginLine;
  /** The column number of the first character of this Token. */
  public int beginColumn;
  /** The line number of the last character of this Token. */
  public int endLine;
  /** The column number of the last character of this Token. */
  public int endColumn;

  /**
   * The string image of the token.
   */
  public String image;

  /**
   * A reference to the next regular (non-special) token from the input
   * stream.  If this is the last token from the input stream, or if the
   * token manager has not read tokens beyond this one, this field is
   * set to null.  This is true only if this token is also a regular
   * token.  Otherwise, see below for a description of the contents of
   * this field.
   */
  public Token next;

  /**
   * This field is used to access special tokens that occur prior to this
   * token, but after the immediately preceding regular (non-special) token.
   * If there are no such special tokens, this field is set to null.
   * When there are more than one such special token, this field refers
   * to the last of these special tokens, which in turn refers to the next
   * previous special token through its specialToken field, and so on
   * until the first special token (whose specialToken field is null).
   * The next fields of special tokens refer to other special tokens that
   * immediately follow it (without an intervening regular token).  If there
   * is no such token, this field is null.
   */
  public Token specialToken;

  /**
   * An optional attribute value of the Token.
   * Tokens which are not used as syntactic sugar will often contain
   * meaningful values that will be used later on by the compiler or
   * interpreter. This attribute value is often different from the image.
   * Any subclass of Token that actually wants to return a non-null value can
   * override this method as appropriate.
   */
  public Object getValue() {
    return null;
  }

  /**
   * No-argument constructor
   */
  public Token() {}

  /**
   * Constructs a new token for the specified Image.
   */
  public Token(int kind)
  {
     this(kind, null);
  }

  /**
   * Constructs a new token for the specified Image and Kind.
   */
  public Token(int kind, String image)
  {
     this.kind = kind;
     this.image = image;
  }

  /**
   * Returns the image.
   */
  public String toString()
  {
     return image;
  }

  /**
   * Returns a new Token object, by default. However, if you want, you
   * can create and return subclass objects based on the value of ofKind.
   * Simply add the cases to the switch for all those special cases.
   * For example, if you have a subclass of Token called IDToken that
   * you want to create if ofKind is ID, simply add something like :
   *
   *    case MyParserConstants.ID : return new IDToken(ofKind, image);
   *
   * to the following switch statement. Then you can cast matchedToken
   * variable to the appropriate type and use sit in your lexical actions.
   */
  public static Token newToken(int ofKind, String image)
  {
     switch(ofKind)
     {
       default : return new Token(ofKind, image);
     }
  }

  public static Token newToken(int ofKind)
  {
     return newToken(ofKind, null);
  }

}
/* JavaCC - OriginalChecksum=36e116391da53a8cb5fc7d23289ae0c7 (do not edit this line) */
