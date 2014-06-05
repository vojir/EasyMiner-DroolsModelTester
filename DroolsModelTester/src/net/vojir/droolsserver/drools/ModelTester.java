package net.vojir.droolsserver.drools;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.vojir.droolsserver.xml.AssociationRulesXmlParser;

import org.drools.core.StatelessSession;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

/**
 * Třída sloužící ke kontrole přesnosti a úplnosti modelu tvořeného asociačními pravidly (na��taj�c� data z DB, vyu��vaj�c� znalostn� b�zi v drools)
 * @author Standa
 *
 */
@SuppressWarnings("restriction")
public class ModelTester implements IModelTester {
	
	private StatelessSession droolsSession;
	//��ta�e
	private int rowsPositiveMatch;
	private int rowsNegativeMatch;
	private int rowsError;
	private int rowsTotalCount;
	private HashMap<String, Integer> positiveMathes = new HashMap<String, Integer>();
	public HashMap<String, Integer> getPositiveMathes() {
		return positiveMathes;
	}

	public HashMap<String, Integer> getNegativeMathes() {
		return negativeMathes;
	}

	private HashMap<String, Integer> negativeMathes = new HashMap<String, Integer>();
	final static String xslTemlateResourceName="ar2drl.xslt";
	
	public ModelTester(){}
	
	public ModelTester(StatelessSession droolsSession){
		this.droolsSession=droolsSession;
	}
	
	public void testAllRows(String csvContent,String betterRuleSelectionMethod){
		ModelTesterSessionHelper.setBetterARMethod(betterRuleSelectionMethod);
		testAllRows(csvContent);
	}
	
	/**
	 * Funkce pro projit� jednotliv�ch ��dk� a otestov�n�, jestli odpov�daj� asocia�n�m pravidl�m v drools stateless session
	 * @param csvContent
	 */
	public void testAllRows(String csvContent){
		Reader reader = new StringReader(csvContent);
		resetCounters();
		String[] header;
		int columnsCount;
		String[] row;
		Collection<Object> rowsObjects=new LinkedList<Object>();
		
		CSVReader<String[]> csvParser = CSVReaderBuilder.newDefaultReader(reader);
		Iterator<String[]> iterator = csvParser.iterator();
		//p�ipraven� p�ehledu z�hlav�
		header = iterator.next();
		columnsCount=header.length;
		
		//jednotliv� ��dky
		while(iterator.hasNext()){
			//kontrola jednoho ��dku z datov� matice
			row=iterator.next();
			setRowsTotalCount(getRowsTotalCount() + 1);
			if (row.length != columnsCount){
				setRowsError(getRowsError() + 1);
				continue;
			}
			//vytvo�en� kolekce a test pomoc� drools
			DrlAR drlAR= new DrlAR();
			rowsObjects.clear();
			for (int i=0;i<columnsCount;i++){
				rowsObjects.add(new DrlObj(header[i],row[i]));
			}
			rowsObjects.add(drlAR);

			droolsSession.execute(rowsObjects);
			
			//System.out.println(drlAR.getConsequentValue()+" : "+drlAR.isCheckedOk()+" = "+drlAR.getBestId());
			
			if (drlAR.isCheckedOk()){
				setRowsPositiveMatch(getRowsPositiveMatch() + 1);
				addPositiveMatch(drlAR.getBestId().substring(5));
			}else if(!drlAR.getBestId().equals("")){
				setRowsNegativeMatch(getRowsNegativeMatch() + 1);
				addNegativeMatch(drlAR.getBestId().substring(5));
			}
			
			//--kontrola jednoho ��dku z datov� matice			
		}
		
	}
	
	public void addPositiveMatch(String id){
		if (this.positiveMathes.containsKey(id)){
			this.positiveMathes.put(id, this.positiveMathes.get(id)+1);
		}else{
			this.positiveMathes.put(id,1);
		}
	}
	public void addNegativeMatch(String id){
		if (this.negativeMathes.containsKey(id)){
			this.negativeMathes.put(id, this.negativeMathes.get(id)+1);
		}else{
			this.negativeMathes.put(id,1);
		}
	}
	
	/**
	 * Funkce pro vynulov�n� ��ta��
	 */
	private void resetCounters(){
		this.setRowsPositiveMatch(0);
		this.setRowsNegativeMatch(0);
		this.setRowsError(0);
		this.setRowsTotalCount(0);
		this.positiveMathes.clear();
		this.negativeMathes.clear();
	}


	/**
	 * @return the rowsPositiveMatch
	 */
	public int getRowsPositiveMatch() {
		return rowsPositiveMatch;
	}


	/**
	 * @param rowsPositiveMatch the rowsPositiveMatch to set
	 */
	public void setRowsPositiveMatch(int rowsPositiveMatch) {
		this.rowsPositiveMatch = rowsPositiveMatch;
	}


	/**
	 * @return the rowsNegativeMatch
	 */
	public int getRowsNegativeMatch() {
		return rowsNegativeMatch;
	}


	/**
	 * @param rowsNegativeMatch the rowsNegativeMatch to set
	 */
	public void setRowsNegativeMatch(int rowsNegativeMatch) {
		this.rowsNegativeMatch = rowsNegativeMatch;
	}


	/**
	 * @return the rowsError
	 */
	public int getRowsError() {
		return rowsError;
	}


	/**
	 * @param rowsError the rowsError to set
	 */
	public void setRowsError(int rowsError) {
		this.rowsError = rowsError;
	}


	/**
	 * @return the rowsTotalCount
	 */
	public int getRowsTotalCount() {
		return rowsTotalCount;
	}


	/**
	 * @param rowsTotalCount the rowsTotalCount to set
	 */
	public void setRowsTotalCount(int rowsTotalCount) {
		this.rowsTotalCount = rowsTotalCount;
	}
	
	/**
	 * Statick� funkce pro vytvo�en� nov�ho modeltesteru na z�klad� XML �et�zce a transforma�n� �ablony
	 * @param xmlString
	 * @param xslTemlateResourceName
	 * @return
	 * @throws Exception
	 */
	public void prepareFromXml(String xmlString) throws Exception{
		String drlString;
		try {
			drlString = AssociationRulesXmlParser.transformBigXml(xmlString, xslTemlateResourceName);
		} catch (TransformerFactoryConfigurationError e){
			throw new Exception("Transformation from XML to DRL failed!",e);
		}	catch (TransformerException e) {
			throw new Exception("Transformation from XML to DRL failed!",e);
		}

    	drlString = ModelTesterSessionHelper.prepareDrlString(drlString);
    	
    	
    	/*PrintWriter writer = new PrintWriter("drl.txt", "UTF-8");
    	writer.println(drlString);
    	writer.close();
    	*/
    	//System.out.println("DRL string prepared");
    	
    	StatelessSession statelessSession = ModelTesterSessionHelper.prepareStatelessSession(drlString);

    	//System.out.println("StatelessSession created");
    	
    	this.droolsSession=statelessSession;
    	//return new ModelTester(statelessSession);
    }

	public void prepareFromXml(InputStream inputStream) throws IOException, Exception {
		this.prepareFromXml(readInputToString(inputStream));
	}
	
	
	private static String readInputToString(InputStream inputStream) throws IOException{
		StringBuffer sb = new StringBuffer();
    	for (int c = inputStream.read(); c != -1; c = inputStream.read()) sb.append((char)c);
    	return sb.toString();
	}

	@Override
	public void testAllRows(InputStream csvInputStream, String selectionMethod) throws IOException {
		this.testAllRows(readInputToString(csvInputStream),selectionMethod);
		
	}

	
}
