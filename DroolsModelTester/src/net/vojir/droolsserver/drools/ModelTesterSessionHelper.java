package net.vojir.droolsserver.drools;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.StatelessSession;
import org.drools.core.rule.Package;

@SuppressWarnings("restriction")
public class ModelTesterSessionHelper {
	
	/**
	 * Funkce pro vytvoøení nového DLR øetìzce - inicializace pomocí výchozích importù
	 */
	public static String prepareDrlString(String drl){
		StringBuffer drlString=new StringBuffer();
		drlString.append("import net.vojir.droolsserver.drools.DrlObj;");
		drlString.append("import net.vojir.droolsserver.drools.DrlAR;");
		drlString.append("import function net.vojir.droolsserver.drools.ModelTesterSessionHelper.isBetterAR;");
		drlString.append(drl);
		return drlString.toString();
	}
	
	/**
	 * Statická funkce pro vytvoøení RuleBase na základì DRL øetìzce
	 * @param drlString
	 * @return
	 */
	public static RuleBase prepareRuleBase(String drlString){
		//read in the source
		Reader source = new StringReader(drlString);
				
		PackageBuilder builder = new PackageBuilder();
		
		Package pkg = builder.getPackage();
		if (pkg!=null){
			builder.getPackage().clear();
		}
		
		//this wil parse and compile in one step
		//NOTE: There are 2 methods here, the one argument one is for normal DRL.
		try {
			builder.addPackageFromDrl(source);
		} catch (DroolsParserException | IOException e) {
			//TODO zalogování chyby
			e.printStackTrace();
		}
		
		//get the compiled package (which is serializable)
		pkg = builder.getPackage();
		
		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		return ruleBase;		
	}
    
	/**
	 * Statická funkce pro vytvoøení stateless session
	 * @param drlString
	 * @return
	 */
    public static StatelessSession prepareStatelessSession(String drlString){
		RuleBase ruleBase = prepareRuleBase(drlString);
		return ruleBase.newStatelessSession();
	}
    
    
    /**
     * Statická funkce sloužící k vyhodnocení, jestli je novì vyhodnocované pravidlo lepší, než pøedchozí nalezená varianta
     * @param globalAR
     * @param currentAR
     * @return
     */
    public static boolean isBetterAR(DrlAR globalAR, DrlAR currentAR){
		if (currentAR.getConfidenceValue()>globalAR.getConfidenceValue()){
			return true;
		}else if(currentAR.getConfidenceValue()==globalAR.getConfidenceValue()){
			return (currentAR.getSupportValue()>globalAR.getSupportValue());
		}
		return false;
    	
    }
    
}
