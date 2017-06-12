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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

/**
 * Java Code Analyser Main
 * 
 * @author 류양선
 *
 */
public class JavaCodeAnalyzerMain {
  
	
	private String targetPath = null;
	private String resultPath = null;

	private List<String> excluded_path_patterns = null;
	private Map<String,String> extension = null;

	private int depth = 0;
	
	private String prefix = null;
	
	public JavaCodeAnalyzerMain(String targetPath, String resultPath, String filterName, int depth, String prefix) {
		this.targetPath = targetPath;
		this.resultPath = resultPath;
		this.depth = depth;
		
		if(prefix != null) {
			if(prefix.startsWith("/")) prefix = prefix.substring(1);
			if(prefix.startsWith("\\")) prefix = prefix.substring(1);
			
			if(prefix.endsWith("/")) prefix = prefix.substring(0,prefix.length()-1);
			if(prefix.endsWith("\\")) prefix = prefix.substring(0,prefix.length()-1);
			
			this.prefix = prefix;
		}
		
		// Create XML Document Object
        InputSource is = null;
        Document document  = null;
        Node filterNode = null;
        NodeList childNodeList = null;
   
        String currDir = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        currDir = currDir.substring(0, currDir.lastIndexOf("/"));
        
        try {
			is = new InputSource(new FileReader(currDir+"\\filefilter.xml"));
//			is = new InputSource(new FileReader("filefilter.xml"));
			
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		} catch (FileNotFoundException e) {
			System.out.println("[Error] An error occurred while reading the file.Please check the <filefilter.xml>.");
			e.printStackTrace();
			System.exit(0);
		} catch (SAXException e) {
			System.out.println("[Error] An error occurred while reading the file.Please check the <filefilter.xml>.");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("[Error] An error occurred while reading the file.Please check the <filefilter.xml>.");
			e.printStackTrace();
			System.exit(0);
		} catch (ParserConfigurationException e) {
			System.out.println("[Error] An error occurred while reading the file.Please check the <filefilter.xml>.");
			e.printStackTrace();
			System.exit(0);
		}
        
     // Create xpath Object
        XPath xpath = XPathFactory.newInstance().newXPath(); 
        
        try {
			filterNode = (Node)xpath.evaluate("/FileFilter/Filter[@name='"+filterName + "']", document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			System.out.println("[Error] An error occurred while parsing the file.Please check the <filefilter.xml>.");
			e.printStackTrace();
			System.exit(0);
		}
        
        if(filterNode != null) {
        	 
        	 try {
        		 childNodeList = (NodeList)xpath.evaluate("./ExcludedPathPatterns/Path", filterNode, XPathConstants.NODESET);
     		} catch (XPathExpressionException e) {
     			System.out.println("[Error] An error occurred while parsing the file.Please check the <filefilter.xml>.");
     			e.printStackTrace();
     			System.exit(0);
     		}
        	 
        	 if(childNodeList != null && childNodeList.getLength() > 0) {
        		 this.excluded_path_patterns = new ArrayList<String>();
        		 int childSize = childNodeList.getLength();
        		 
        		 for(int i = 0; i < childSize; i++) {        			 
        			 this.excluded_path_patterns.add(childNodeList.item(i).getTextContent().trim().replace('/', '\\')); 			 
        		 }
        	 }
        	 
        	 try {
        		 childNodeList = (NodeList)xpath.evaluate("./Extentions/Extention", filterNode, XPathConstants.NODESET);
     		} catch (XPathExpressionException e) {
     			System.out.println("[Error] An error occurred while parsing the file.Please check the <filefilter.xml>.");
     			e.printStackTrace();
     			System.exit(0);
     		}
        	 
        	 if(childNodeList != null && childNodeList.getLength() > 0) {
        		 this.extension = new HashMap<String,String>();
        		 int childSize = childNodeList.getLength();
        		 
        		 for(int i = 0; i < childSize; i++) {        			 
        			 this.extension.put(childNodeList.item(i).getTextContent().trim().toLowerCase(), 
        					 childNodeList.item(i).getAttributes().getNamedItem("encoding").getTextContent().trim()); 			 
        		 }
        	 }
        }
		
	}

	/**
	 * 분석 대상 경로에 포함된(하위 경로 포함) Java Code에 대한 분석을 수행하는 메인 메소드이다.
	 * <pre>
	 * 1. 분석 대상 경로, 결과 파일 저장 경로를 입력받아 경로 유무 확인  <br>
	 * 2. 결과 파일 출력을 위한 객체 생성   <br>
	 * 3. 대상 파일 분석   <br>  
	 * </pre>
	 * @param contextRoot  현재 요청한 Context Root 경로
	 * @throws Exception 
	 */
	public static void main(java.lang.String[] args) throws Exception {
		String targetPath = null, resultPath = null, filterName = null;
		String depth = null, prefix = null;
		
		// 1. Validating target path and result file path 
		Options options = new Options();
		options.addOption("t", "targetPath", true, "The absolute path of target(Can be omitted in jenkins)");
		options.addOption("r", "resultingPath", true, "The path of result file(Can be omitted in jenkins)");
		options.addOption("f", "filterName", true, "The name of filter(Default value is 'default')");
		options.addOption("i", "ignoreDepth", true, "The number of ignore depth(Default value is '0')");
	  
		options.addOption("p", "projectName", true, "The name of the project in SCM(ex. SVN)(Analyzed File Path's Prefix, Default value is '')");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
		  
		targetPath = cmd.getOptionValue('t');
		resultPath = cmd.getOptionValue('r');
		filterName =  cmd.getOptionValue('f');
		depth = cmd.getOptionValue('i');
		prefix = cmd.getOptionValue('p');
		
		if(targetPath == null || resultPath == null) {
			targetPath = System.getenv("WORKSPACE");
			resultPath = System.getenv("JENKINS_HOME") + "\\jobs\\" + 
						System.getenv("JOB_NAME") + "\\builds\\" +
						System.getenv("BUILD_ID");
		}
		
		if(filterName == null) {
			filterName = "default";
		}
		
		if(depth == null) {
			depth = new String("0");
		}
		
		if(targetPath == null || resultPath == null) {
			 HelpFormatter formatter = new HelpFormatter();
			 formatter.printHelp("Usage ....", options);
			
			 System.exit(0);
		}
	
		
		System.out.println("Target Path :" + targetPath);
		
		File filePath = new File(resultPath);
		
		if(!filePath.exists()) { System.exit(0);}
			
		JavaCodeAnalyzerMain analyer = new JavaCodeAnalyzerMain(targetPath, resultPath, filterName, Integer.parseInt(depth), prefix);
		
		analyer.process();
	}
	
	/**
	 * Processing code analyze
	 * 
	 * @throws Exception
	 */
	private void process() throws Exception {
		// 1. Created Result File Object
		File file = new File(this.resultPath + "\\code_analysis_result.xml");
		
		OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		
		fout.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		fout.write("<!-- JavaCodeAnalyzer_1.8  Java 1.8 Support -->\n");
		
		fout.write("<files>\n");
		
		// 2. Analyzing target
		anayzeFile(new File(this.targetPath), fout);
		
		fout.write("</files>\n");
		fout.flush();
		fout.close();
		
		System.out.println("[INFO] Code analyzing Finished. Result File Path : "+this.resultPath + "\\code_analysis_result.xml");
	}

	/**
	 * 주어진 경로에 위치한 파일을 분석한다.
	 * <pre>
	 * 1. 분석 대상 경로 하위의 파일 확인  <br>
	 * 2-1. 디렉토리 인 경우 재귀 호출   <br>
	 * 2-2. 파일인 경우 해당 파일 분석  <br>  
	 * 3. 파일 분석 결과 출력   <br>  
	 * </pre>
	 * @param f 분석 경로 파일
	 * @param fout 결과 파일 출력 스트림 객체
	 */
	private void anayzeFile(File f, OutputStreamWriter fout) {	
		
		// 1. 분석 대상 경로 하위의 파일 확인
		String[] child = f.list();
		if(child == null) {
			System.out.println("[Error] Target Path '"+ f.getAbsolutePath() + "' dose not exist file.");
			return;
		}
		
		int childsize = child.length;
		File childFile = null;
		FileInputStream in = null;
		CompilationUnit cu;
		LocInfo li;
		String path = null;
		String filetype = null;
		int index = -1;
		boolean excludeFlag = true, containFlag = true;
		Charset encoding = null;

		for(int i = 0; i < childsize; i++) {
			excludeFlag = true;
			for(String epp:this.excluded_path_patterns) {
				if(epp.equals("\\"+child[i]+"\\"))
					excludeFlag = false;
			}
			
			if(excludeFlag) {
				childFile = new File(f.getPath()+"\\"+child[i]);
			
				// 2-1. 디렉토리 인 경우 재귀 호출  
				if(childFile.isDirectory()) {
					anayzeFile(childFile, fout);
				} else {		
					
					try {
						in = new FileInputStream(childFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						if(in != null)
							try {
								in.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						return;
					}
						
					index = childFile.getName().lastIndexOf(".");
				
					if(index > 0)
						filetype = childFile.getName().substring(childFile.getName().lastIndexOf(".")+1).toLowerCase();
					else
						filetype = "";
					
					path = getPath(childFile.getAbsolutePath().replace("\\", "/").substring(targetPath.length()));
					
					// 2-2. 파일인 경우 해당 파일 파싱
					// java 파일에 대한 파싱 - JavaParser 사용
					if(extension.containsKey("java") && "java".equals(filetype)) {
						encoding = Charset.forName(extension.get("java"));
						if(encoding == null) encoding = Charset.forName("utf-8");

						cu = null;
				        try {
				            cu = JavaParser.parse(in, encoding);
		
				            // 3. 파일 분석 결과 출력
				            fout.write("<file type='"+filetype+"' path='"+ path +"' loc='"+cu.getLoc()+"' codeLoc='"+cu.getCodeLoc()+"' commentLoc='"+cu.getCommentLoc()+"'>\n");

				        	printMethods(cu, fout);
							
							fout.write("</file>\n");
										        	
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
				            try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
								return;
							}
				        }
				        
					}
					// java 이외 파일에 대한 파싱 - DefaultLocCounter 사용					
					else if(!"".equals(filetype) && extension.containsKey(filetype)){
						
						encoding = Charset.forName(extension.get(filetype));
						
						if(encoding == null) {
							containFlag = false;
							encoding = Charset.forName("utf-8");
						}
						li = null;
						if(containFlag) {
							try {
								li = DefaultLocCounter.parse(in, encoding);
							} catch (IOException e) {
								e.printStackTrace();
								return;
							} finally {
					            try {
									in.close();
								} catch (IOException e) {
									e.printStackTrace();
									return;
								}
					        }
							
					        // 3. 파일 분석 결과 출력
					        try {
					        	
					        	fout.write("<file type='"+filetype+"' path='"+ path +"' loc='"+li.getLoc()+"' codeLoc='"+li.getCodeLoc()+"' commentLoc='0'/>\n");						
							
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
					}				
				}
			}
		}
		
	}

	/**
	 * 파일이 포함 한 함수에 대해 분석한다.
	 * <pre>
	 * 1. 함수(생성자, 기타 함수)에 대한 분석 결과 출력  <br>
	 * 2. 내부 클래스 인 경우 클래스 분석 함수 호출   <br>
	 * </pre>
	 * @param f 분석 경로 파일
	 * @param fout 결과 파일 출력 스트림 객체
	 */
	 private void printMethods(CompilationUnit cu, OutputStreamWriter fout) throws IOException { 
		 fout.write("<functions>\n");		
		 com.github.javaparser.ast.NodeList<TypeDeclaration<?>> types = cu.getTypes(); 
	     if(types != null) {
		     for (TypeDeclaration<?> type : types) {
		    	 com.github.javaparser.ast.NodeList<BodyDeclaration<?>> members = type.getMembers(); 
		    	 if(members != null) {
			    	 for (BodyDeclaration<?> member : members) { 
			    		 // 1. 함수(생성자, 기타 함수)에 대한 분석 결과 출력 
			    		 if (member instanceof MethodDeclaration) { 
			    			 MethodDeclaration method = (MethodDeclaration) member; 
			    			 fout.write("<function name='"+method.getName()+ "' " +
			    					 "signature='"+method.getSignature()+"' "+
			    					 "complexity='"+method.getComplexity()+"' "+
			    					 "loc='" + method.getLoc()+"' " +
			    					 "codeLoc='" + method.getCodeLoc()+"' " +
			    					 "commentLoc='" + method.getCommentLoc()+"' " +
			    					 "startLine='" + method.getBeginLine()+"' " +
			    					 "endLine='" + method.getEndLine() + "'/>\n");
			    		 } else if(member instanceof ConstructorDeclaration) {
			    			 ConstructorDeclaration method = (ConstructorDeclaration) member; 
			    			 fout.write("<function name='"+method.getName()+ "' " +
		                    		"signature='"+method.getSignature()+"' "+
		                    		"complexity='"+method.getComplexity()+"' "+
		                    		"loc='" + method.getLoc()+"' " +
		                    		"codeLoc='" + method.getCodeLoc()+"' " +
		                    		"commentLoc='" + method.getCommentLoc()+"' " +
		                    		"startLine='" + method.getBeginLine()+"' " +
		                    		"endLine='" + method.getEndLine() + "'/>\n");
		                }
			    		 // 2. 내부 클래스 인 경우 클래스 분석 함수 호출
			    		 else if(member instanceof ClassOrInterfaceDeclaration) { 
		                	printInnerClassMedhods(null, (ClassOrInterfaceDeclaration)member, fout);
		    	    	}
		            } 
		    	 }
		     }
	     }
	     fout.write("</functions>\n");
	 } 

	/**
	 * 내부 클래스가 포함 한 함수에 대해 분석한다.
	 * <pre>
	 * 1. 함수(생성자, 기타 함수)에 대한 분석 결과 출력  <br>
	 * 2. 내부 클래스 인 경우 클래스 분석 함수 호출   <br>
	 * </pre>
	 * @param f 분석 경로 파일
	 * @param fout 결과 파일 출력 스트림 객체
	 */	 
	private void printInnerClassMedhods(String superClassName, ClassOrInterfaceDeclaration clazz, OutputStreamWriter fout) throws IOException {
//		fout.write("============InnerClassMedhods\n");
		String className = clazz.getName().toString();
		com.github.javaparser.ast.NodeList<BodyDeclaration<?>> members = clazz.getMembers(); 
		if(members != null) {
	    	for (BodyDeclaration<?> member : members) { 
	    		if(superClassName != null) {
		    		// 1. 함수(생성자, 기타 함수)에 대한 분석 결과 출력
		    		if (member instanceof MethodDeclaration) { 
		    			MethodDeclaration method = (MethodDeclaration) member;
		    			fout.write("<function name='"+superClassName+"."+className+"."+method.getName()+ "' " +
		               		"signature='"+method.getSignature()+"' "+
		               		"complexity='"+method.getComplexity()+"' "+
		               		"loc='" + method.getLoc()+"' " +
		               		"codeLoc='" + method.getCodeLoc()+"' " +
		               		"commentLoc='" + method.getCommentLoc()+"' " +
		               		"startLine='" + method.getBeginLine()+"' " +
		               		"endLine='" + method.getEndLine() + "'/>\n");
		    		} else if(member instanceof ConstructorDeclaration) {
		    			ConstructorDeclaration method = (ConstructorDeclaration) member; 
		    		   	fout.write("<function name='"+superClassName+"."+className+"."+method.getName()+ "' " +
		               		"signature='"+method.getSignature()+"' "+
		               		"complexity='"+method.getComplexity()+"' "+
		               		"loc='" + method.getLoc()+"' " +
		               		"codeLoc='" + method.getCodeLoc()+"' " +
		               		"commentLoc='" + method.getCommentLoc()+"' " +
		               		"startLine='" + method.getBeginLine()+"' " +
		               		"endLine='" + method.getEndLine() + "'/>\n");
		           } 
		    		// 2. 내부 클래스 인 경우 클래스 분석 함수 호출 
		    		else if(member instanceof ClassOrInterfaceDeclaration) {
		    			printInnerClassMedhods(new String(superClassName+"."+className), (ClassOrInterfaceDeclaration)member, fout);
		           } 
	    		} else {
	    			// 1. 함수(생성자, 기타 함수)에 대한 분석 결과 출력
		    		if (member instanceof MethodDeclaration) { 
		    			MethodDeclaration method = (MethodDeclaration) member; 
		    			fout.write("<function name='"+className+"."+method.getName()+ "' " +
		               		"signature='"+method.getSignature()+"' "+
		               		"complexity='"+method.getComplexity()+"' "+
		               		"loc='" + method.getLoc()+"' " +
		               		"codeLoc='" + method.getCodeLoc()+"' " +
		               		"commentLoc='" + method.getCommentLoc()+"' " +
		               		"startLine='" + method.getBeginLine()+"' " +
		               		"endLine='" + method.getEndLine() + "'/>\n");
		    		} else if(member instanceof ConstructorDeclaration) {
		    			ConstructorDeclaration method = (ConstructorDeclaration) member; 
		    		   	fout.write("<function name='"+className+"."+method.getName()+ "' " +
		               		"signature='"+method.getSignature()+"' "+
		               		"complexity='"+method.getComplexity()+"' "+
		               		"loc='" + method.getLoc()+"' " +
		               		"codeLoc='" + method.getCodeLoc()+"' " +
		               		"commentLoc='" + method.getCommentLoc()+"' " +
		              		"startLine='" + method.getBeginLine()+"' " +
		               		"endLine='" + method.getEndLine() + "'/>\n");
		           } 
		    		// 2. 내부 클래스 인 경우 클래스 분석 함수 호출 
		    		else if(member instanceof ClassOrInterfaceDeclaration) {
		    			printInnerClassMedhods(new String(className), (ClassOrInterfaceDeclaration)member, fout);
		           } 
	    		}
	    	}
		}
	}
	
	private String getPath(String fileName) {
		String result = fileName;
		
		if(this.depth > 0) {
			result = result.substring(1);
			for(int i = 0; i < this.depth; i++) {
				result = result.substring(result.indexOf('/')+1);
			}
			result = "/"+result;
		} else if(this.depth < 0 ){
			if(this.prefix != null) {
				result =  "/" + this.prefix + result;
			}
		}
		
		return result;
	}
}