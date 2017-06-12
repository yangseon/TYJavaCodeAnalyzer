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

public class LocInfo {
	
	private long loc = 0;
	
	private long codeLoc = 0;
	
	public LocInfo() {
		loc = 0;
		codeLoc = 0;
	}

	public long getLoc() {
		return loc;
	}

	public void setLoc(long loc) {
		this.loc = loc;
	}

	public long getCodeLoc() {
		return codeLoc;
	}

	public void setCodeLoc(long codeLoc) {
		this.codeLoc = codeLoc;
	}

	
}
