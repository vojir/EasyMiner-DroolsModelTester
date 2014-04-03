package net.vojir.droolsserver.xml;


import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
//import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;




public class AssociationRulesXmlParser {
	public static int counter =  0;
	
	/**
	 * Statická funkce pro rozdìlení XML øetìzce AssociationRules na jednotlivé èásti, které je poté možné transformovat
	 * @param xmlString
	 * @return
	 */
	public static ArrayList<String> splitXMLIntoFragments(String xmlString){
		ArrayList<String> fragments = new ArrayList<String>();
		//java.util.Date date= new java.util.Date();
		 //System.out.println(new Timestamp(date.getTime()));
		int endPosition=0;
		int firstPartEnd=xmlString.indexOf(">",xmlString.indexOf("<AssociationRules"))+1;
		String firstPart=xmlString.substring(0,firstPartEnd);
		int startPosition=firstPartEnd;
		
		StringBuilder fragment = new StringBuilder(firstPart);
		int fragmentCounter= 0;
		
		startPosition=xmlString.indexOf("<AssociationRule",firstPartEnd+3);
		while(startPosition>-1){			
			//System.out.println(startPosition);
			endPosition=xmlString.indexOf("</AssociationRule",startPosition);
			
			fragment.append(xmlString.substring(startPosition, endPosition));
			fragment.append("</AssociationRule>");
			if (fragmentCounter>100){
				fragmentCounter=0;
				fragment.append("</AssociationRules>");
				fragments.add(fragment.toString());
				fragment = new StringBuilder(firstPart);
			}else{
				fragmentCounter++;
			}
			
			startPosition=xmlString.indexOf("<AssociationRule",endPosition);
		}
		if (!fragment.toString().equals("")){
			fragments.add(fragment.append("</AssociationRules>").toString());
		}

		return fragments;
	}
	
	/**
	 * Statická funkce pro vytvoøení transformeru pro XML transformace
	 * @return
	 * @throws TransformerConfigurationException
	 */
	private static Transformer prepareTransformer(String resourceName) throws TransformerConfigurationException{
		TransformerFactory transFact = TransformerFactory.newInstance();
		Source xsltSource = new StreamSource(AssociationRulesXmlParser.class.getResourceAsStream(resourceName));
		Templates cachedXSLT = transFact.newTemplates(xsltSource);
		return cachedXSLT.newTransformer();
	}
	
	/**
	 * Funkce pro transformaci XML stringu pomocí existujícího transformátoru
	 * 
	 * @param xmlString
	 * @param transformer
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String transformXML(String xmlString,Transformer transformer) throws TransformerFactoryConfigurationError, TransformerException{
		Source inputXml = new StreamSource(new StringReader(xmlString));
		
		Writer stringWriter=new StringWriter();
		Result xmlOutput = new StreamResult(stringWriter);
		
		transformer.transform(inputXml, xmlOutput);
		return stringWriter.toString();
	}
	
	/**
	 * Statická funkce pro XSL transformaci velkého XML
	 * @param xmlString
	 * @param xslTemlate
	 * @return
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String transformBigXml(String xmlString,String xslTemlateResourceName) throws TransformerFactoryConfigurationError, TransformerException{
		StringBuffer outputString = new StringBuffer();
		int maxParts = 60;
		
		ArrayList<String> parts = splitXMLIntoFragments(xmlString);
		//System.out.println("parts "+parts.size());
    	Transformer transformer = prepareTransformer(xslTemlateResourceName);
    	
    	for (String polozka : parts){
			String str = transformXML(polozka,transformer);
			outputString.append(str);
			maxParts--;
			if (maxParts<=0){
				break;
			}
    	}
    	return outputString.toString();
	}
   
}
