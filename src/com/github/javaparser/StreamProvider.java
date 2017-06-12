/* Generated By:JavaCC: Do not edit this line. StreamProvider.java Version 6.1 */
/* JavaCCOptions:KEEP_LINE_COLUMN=true */
/*
 *
 * This file is part of Java 1.8 parser and Abstract Syntax Tree.
 *
 * Java 1.8 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.8 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.javaparser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * NOTE : This generated class can be safely deleted if installing in a GWT installation (use StringProvider instead)
 */
public class StreamProvider implements Provider {

	Reader _reader;

	public StreamProvider(Reader reader) {
		_reader = reader;
	}
	
	public StreamProvider(InputStream stream) throws IOException {
		_reader = new BufferedReader(new InputStreamReader(stream));
	}
	
	public StreamProvider(InputStream stream, String charsetName) throws IOException {
		_reader = new BufferedReader(new InputStreamReader(stream, charsetName));
	}

	@Override
	public int read(char[] buffer, int off, int len) throws IOException {
	   int result = _reader.read(buffer, off, len);

	   /* CBA -- Added 2014/03/29 -- 
	             This logic allows the generated Java code to be easily translated to C# (via sharpen) -
	             as in C# 0 represents end of file, and in Java, -1 represents end of file
	             See : http://msdn.microsoft.com/en-us/library/9kstw824(v=vs.110).aspx
	             ** Technically, this is not required for java but the overhead is extremely low compared to the code generation benefits.
	   */
	   
	   if (result == 0) {
	      if (off < buffer.length && len > 0) {
	        result = -1;
	      }
	   }
	   
		return result;
	}

	@Override
	public void close() throws IOException {
		_reader.close();
	}

}

/* JavaCC - OriginalChecksum=cd87639e096e4dba70ee420063faa6f0 (do not edit this line) */
