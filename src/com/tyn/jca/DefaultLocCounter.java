/*
 * Copyright 2013 TONGYANG Networks Co. All rights Reserved.
 * TONGYANG Networks PROPRIETARY. Use is subject to license terms.
 * 
 * This file is part of Java Code Analyzer.
 *
 * Java Code Analyzer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java Code Analyzer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Code Analyzer.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 04/20/2013
 */
package com.tyn.jca;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class DefaultLocCounter {

	public static LocInfo parse(FileInputStream fileIn, String encodingName) throws IOException{
        Charset encoding = Charset.forName(encodingName);
        Reader fileReader = new InputStreamReader(fileIn, encoding);
		
		return getLinesCount(fileReader);		
	}
	
	public static LocInfo getLinesCount(Reader fileReader) throws IOException {
		
		LocInfo info  = new LocInfo();
		LineNumberReader reader  = new LineNumberReader(fileReader);
		String lineRead = null;
		int minLine = -1;
		int maxLine = -1;
		int lineCounter = 0;
		int emptyConter = 0;
		while ((lineRead = reader.readLine()) != null) {
			
			if(lineRead.trim().length() > 0) {
				if(minLine == - 1) {
					minLine = lineCounter; 
				}
				
				maxLine = lineCounter;
			} else {
				emptyConter++;
			}
			lineCounter++;
		}


		reader.close();
	
        info.setLoc(maxLine - minLine + 1);
        info.setCodeLoc(reader.getLineNumber() - emptyConter);
	    return info;
	}
}
