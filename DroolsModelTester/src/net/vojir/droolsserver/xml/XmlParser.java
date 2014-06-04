package net.vojir.droolsserver.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class XmlParser {
	public static int counter =  0;
	
	public static ArrayList<String> splitXMLIntoFragments(String xmlString){
		ArrayList<String> fragments = new ArrayList<String>();
		
		int endPosition=0;
		int firstPartEnd=xmlString.indexOf(">",xmlString.indexOf("<AssociationRules"))+1;
		String firstPart=xmlString.substring(0,firstPartEnd);
		int startPosition=firstPartEnd;
		
		StringBuilder fragment = new StringBuilder(firstPart);
		int fragmentCounter= 0;
		
		startPosition=xmlString.indexOf("<AssociationRule",firstPartEnd+3);
		while(startPosition>-1){			
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
		return fragments;
	}
	
	/*
	private static Transformer prepareTransformer() throws TransformerConfigurationException{
		TransformerFactory transFact = TransformerFactory.newInstance();
		Source xsltSource = new StreamSource(XmlParser.class.getResourceAsStream("ar2drl.xslt"));
		//TransformerFactory transFact = TransformerFactory.newInstance();
		Templates cachedXSLT = transFact.newTemplates(xsltSource);
		return cachedXSLT.newTransformer();
	}*/
	
	/**
	 * Funkce pro transformaci XML stringu pomoc� existuj�c�ho transform�toru
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
	
/*
	@SuppressWarnings("restriction")
	public static RuleBase prepareRuleBase(String drlString){
		//read in the source
		Reader source = new StringReader(drlString);
				
		//optionally read in the DSL (if you are using it).
		//Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );

		//Use package builder to build up a rule package.
		//An alternative lower level class called "DrlParser" can also be used...
				
		PackageBuilder builder = new PackageBuilder();
		
		Package pkg = builder.getPackage();
		if (pkg!=null){
			builder.getPackage().clear();
		}
		
		
		//this wil parse and compile in one step
		//NOTE: There are 2 methods here, the one argument one is for normal DRL.
		try {
			builder.addPackageFromDrl( source );
		} catch (DroolsParserException | IOException e) {
			//TODO zalogov�n� chyby
			e.printStackTrace();
		}
		
		//Use the following instead of above if you are using a DSL:
		//builder.addPackageFromDrl( source, dsl );
		
		//get the compiled package (which is serializable)
		pkg = builder.getPackage();
		
		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		return ruleBase;
		
	}
    
    @SuppressWarnings("restriction")
	public static StatelessSession prepareStatelessSession(String drlString){
		RuleBase ruleBase = prepareRuleBase(drlString);
		return ruleBase.newStatelessSession();
	}
   */
}
