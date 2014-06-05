package net.vojir.droolsserver.modeltester.rest;

import java.net.URL;
import java.util.Map.Entry;

import javax.ws.rs.*;

import net.vojir.droolsserver.drools.ModelTester;


/**
 * Trida pro otestovani asociacnich pravidel pomoci modeltesteru
 * @author Stanislav Vojir
 */
@Path("association-rules")
public class AssociationRules {

	@GET
	@Path("status")
	public String status(){
		return "<status>OK</status>";
	}
	
	@GET
	@Path("test-files")
	public String testFiles(@QueryParam("rulesXml") String rulesXmlUrl,@QueryParam("dataCsv") String dataCsvUrl,@DefaultValue("confidenceSupportAntecedentLength") @QueryParam("selectionMethod") String selectionMethod, @DefaultValue("") @QueryParam("complexResults") String complexResults) throws Exception{
		ModelTester modelTester = new ModelTester();
		modelTester.prepareFromXml(new URL(rulesXmlUrl).openStream());
		modelTester.testAllRows(new URL(dataCsvUrl).openStream(), selectionMethod);
		
		//TODO return method form modelTester
		StringBuffer output=new StringBuffer();
		output.append("<testResults>");
		output.append("<rulesXml>"+rulesXmlUrl+"</rulesXml>");
		output.append("<dataCsv>"+dataCsvUrl+"</dataCsv>");
		output.append("<truePositive>"+modelTester.getRowsPositiveMatch()+"</truePositive>");
		output.append("<falsePositive>"+modelTester.getRowsNegativeMatch()+"</falsePositive>");
		output.append("<rowsCount>"+modelTester.getRowsTotalCount()+"</rowsCount>");
		
		if (complexResults.equalsIgnoreCase("ok")){
			//vrac�me komplexn� v�sledky - po�ty jednotliv�ch pravidel
			output.append("<rulesMatches>");
			if (modelTester.getPositiveMathes().size()>0){
				for ( Entry<String, Integer> entry : modelTester.getPositiveMathes().entrySet()) {
					String id=entry.getKey();
					Integer positiveMatches=entry.getValue();
					Integer negativeMatches=modelTester.getNegativeMathes().get(id);
					output.append("<rule id=\""+id+"\" truePositive=\""+positiveMatches.toString()+"\" falsePositive=\""+negativeMatches.toString()+"\" />");
				}
				for( Entry<String, Integer> entry: modelTester.getNegativeMathes().entrySet()){
					String id = entry.getKey();
					if (!modelTester.getPositiveMathes().containsKey(id)){
						Integer negativeMatches=entry.getValue();
						Integer positiveMatches=modelTester.getPositiveMathes().get(id);
						output.append("<rule id=\""+id+"\" truePositive=\""+positiveMatches.toString()+"\" falsePositive=\""+negativeMatches.toString()+"\" />");
					}
				}
			}
			output.append("</rulesMatches>");
		}
		
		output.append("</testResults>");
		
		return output.toString();
	}
}
