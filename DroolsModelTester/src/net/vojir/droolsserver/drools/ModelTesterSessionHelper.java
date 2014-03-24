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

	private static String betterARMethod;
	
	/**
	 * Funkce pro vytvoøení nového DLR øetìzce - inicializace pomocí výchozích importù
	 */
	public static String prepareDrlString(String drl){
		StringBuffer drlString=new StringBuffer();
		drlString.append("import net.vojir.droolsserver.drools.DrlObj;");
		drlString.append("import net.vojir.droolsserver.drools.DrlAR;");
		drlString.append("import function net.vojir.droolsserver.drools.ModelTesterSessionHelper.isBetterAR;");
		drlString.append("rule zeroRule salience -1000 when $ar:DrlAR(id!=\"\") then $ar.setId(\"\");update($ar)end");
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
    	if (globalAR.getBestId().equals("")){
    		return true;
    	}
		switch (getBetterARMethod()) {
			case "longerAntecedent":
				return isBetterAR_longerAntecedent(globalAR, currentAR);
			case "shorterAntecedent":
				return isBetterAR_shorterAntecedent(globalAR, currentAR);
			case "support":
				return isBetterAR_support(globalAR, currentAR);
			case "csCombination":
				return isBetterAR_confidenceSupportCombination(globalAR, currentAR);
			default:
				return isBetterAR_confidence(globalAR, currentAR);
		}
    }
    
    //-----------------------------------------------------------------------------------
    public static boolean isBetterAR_confidence(DrlAR globalAR,DrlAR currentAR){
    	if (currentAR.getConfidenceValue()>globalAR.getConfidenceValue()){
			return true;
		}else if(currentAR.getConfidenceValue()==globalAR.getConfidenceValue()){
			return (currentAR.getSupportValue()>globalAR.getSupportValue());
		}
		return false;
    }
    public static boolean isBetterAR_longerAntecedent(DrlAR globalAR,DrlAR currentAR){
    	if (currentAR.getAntecedentLength()>globalAR.getAntecedentLength()){
			return true;
		}else if(currentAR.getAntecedentLength()==globalAR.getAntecedentLength()){
			return isBetterAR_confidence(globalAR, currentAR);
		}
		return false;
    }
    public static boolean isBetterAR_shorterAntecedent(DrlAR globalAR,DrlAR currentAR){
    	if (currentAR.getAntecedentLength()<globalAR.getAntecedentLength()){
			return true;
		}else if(currentAR.getAntecedentLength()==globalAR.getAntecedentLength()){
			return isBetterAR_confidence(globalAR, currentAR);
		}
		return false;
    }
    public static boolean isBetterAR_support(DrlAR globalAR,DrlAR currentAR){
    	if (currentAR.getSupportValue()>globalAR.getSupportValue()){
			return true;
		}else if(currentAR.getSupportValue()==globalAR.getSupportValue()){
			return isBetterAR_confidence(globalAR, currentAR);
		}
		return false;
    }
    public static boolean isBetterAR_confidenceSupportCombination(DrlAR globalAR,DrlAR currentAR){
    	double ar1=globalAR.getConfidenceValue()*Math.log(globalAR.getSupportValue());
    	double ar2=currentAR.getConfidenceValue()*Math.log(currentAR.getSupportValue());
    	return (ar2>ar1);
    }
    //-----------------------------------------------------------------------------------
	public static String getBetterARMethod() {
		return betterARMethod;
	}

	public static void setBetterARMethod(String betterARMethod) {
		if (betterARMethod.equals("confidence")||
				betterARMethod.equals("longerAntecedent")||
				betterARMethod.equals("shorterAntecedent")||
				betterARMethod.equals("support")){
			ModelTesterSessionHelper.betterARMethod = betterARMethod;
		}else{
			System.out.println("NEEXISTUJE metoda "+betterARMethod);
			System.exit(0);
			ModelTesterSessionHelper.betterARMethod="confidence";
		}
	}
    

}
