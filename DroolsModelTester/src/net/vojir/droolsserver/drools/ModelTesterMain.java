package net.vojir.droolsserver.drools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.vojir.droolsserver.xml.XmlParser;

public class ModelTesterMain {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		if (args.length!=2){
			throw new Exception("This application requires 2 params - XML file name, CSV file name");
		}
		


		String xmlString = readFileToString(args[0]);
		String csvString = readFileToString(args[1]);
    	
    	//Pøipravení drools stateless session
    	ModelTester modelTester = ModelTester.prepareFromXml(xmlString);
    	modelTester.testAllRows(csvString);
    	
    	System.out.println("Rows total: "+modelTester.getRowsTotalCount());
    	System.out.println("Rows positive: "+modelTester.getRowsPositiveMatch());
    	System.out.println("Rows negative: "+modelTester.getRowsNegativeMatch());
    	System.out.println("Rows errors: "+modelTester.getRowsError());
    	
    	//TODO vypsání výsledkù
    	
	}
	
	@SuppressWarnings("resource")
	private static String readFileToString(String fileName) throws IOException{
		StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
		br = new BufferedReader(new FileReader(fileName));
    	for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
		
    	return sb.toString();
	}

	@SuppressWarnings("unused")
	private static String readResourceToString(String resourceFileName,String encoding) throws IOException{
		StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(XmlParser.class.getResourceAsStream("rules big file.xml"), "UTF-8"));
    	for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
    	return sb.toString();
	}

}
