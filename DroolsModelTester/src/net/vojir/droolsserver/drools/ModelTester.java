package net.vojir.droolsserver.drools;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import net.vojir.droolsserver.xml.AssociationRulesXmlParser;

import org.drools.core.StatelessSession;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;

/**
 * T��da slou��c� ke kontrole p�esnosti a �plnosti modelu tvo�en�ho asocia�n�mi pravidly (na��taj�c� data z DB, vyu��vaj�c� znalostn� b�zi v drools)
 * @author Standa
 *
 */
@SuppressWarnings("restriction")
public class ModelTester {
	
	private StatelessSession droolsSession;
	//��ta�e
	private int rowsPositiveMatch;
	private int rowsNegativeMatch;
	private int rowsError;
	private int rowsTotalCount;
	final static String xslTemlateResourceName="ar2drl.xslt";
	
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
			
<<<<<<< HEAD
			//System.out.println(drlAR.getConsequentValue()+" : "+drlAR.isCheckedOk()+" = "+drlAR.getBestId());
=======
			System.out.println(drlAR.getConsequentValue()+" : "+drlAR.isCheckedOk());
>>>>>>> refs/remotes/origin/master
			
			if (drlAR.isCheckedOk()){
				setRowsPositiveMatch(getRowsPositiveMatch() + 1);
			}else if(!drlAR.getBestId().equals("")){
				setRowsNegativeMatch(getRowsNegativeMatch() + 1);
			}
			
			//--kontrola jednoho ��dku z datov� matice			
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
	public static ModelTester prepareFromXml(String xmlString) throws Exception{
		String drlString;
		try {
			drlString = AssociationRulesXmlParser.transformBigXml(xmlString, xslTemlateResourceName);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			throw new Exception("Transformation from XML to DRL failed!",e);
		}
    	
    	drlString = ModelTesterSessionHelper.prepareDrlString(drlString);
    	
    	PrintWriter writer = new PrintWriter("drl.txt", "UTF-8");
    	writer.println(drlString);
    	writer.close();
    	
    	//System.out.println("DRL string prepared");
    	
    	StatelessSession statelessSession = ModelTesterSessionHelper.prepareStatelessSession(drlString);

    	//System.out.println("StatelessSession created");
    	
    	return new ModelTester(statelessSession);
    }
}
