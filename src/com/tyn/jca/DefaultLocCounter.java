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
