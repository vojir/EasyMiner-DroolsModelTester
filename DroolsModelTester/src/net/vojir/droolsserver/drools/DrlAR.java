package net.vojir.droolsserver.drools;


public class DrlAR {
	
	//TODO jen pracovní
	private String consequentValue;
	public void setConsequentValue(String consequentValue){
		this.consequentValue=consequentValue;
	}
	public String getConsequentValue(){
		return consequentValue;
	}
	public DrlAR(String id,int antecedentLength, double confidenceValue, double supportValue, double acValue, String consequentValue){
		setId(id);
		setAntecedentLength(antecedentLength);
		setConfidenceValue(confidenceValue);
		setSupportValue(supportValue);
		setAcValue(acValue);
		setConsequentValue(consequentValue);
	}
	//--jen pracovní
	
	private String id = "";
	private String bestId = "";
	private int antecedentLength = -1;
	private double confidenceValue = -1;
	private double supportValue = -1;
	private double acValue = -1;
	private boolean checkedOk=false;
	
	
	public DrlAR(){}
	
	public DrlAR(String id){
		setId(id);
	}
	
	
	public DrlAR(String id,int antecedentLength, double confidenceValue, double supportValue, double acValue){
		setId(id);
		setAntecedentLength(antecedentLength);
		setConfidenceValue(confidenceValue);
		setSupportValue(supportValue);
		setAcValue(acValue);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getBestId() {
		return bestId;
	}

	public void setBestId(String bestId) {
		this.bestId = bestId;
	}
	
	public int getAntecedentLength() {
		return antecedentLength;
	}

	public void setAntecedentLength(int antecedentLength) {
		this.antecedentLength = antecedentLength;
	}

	public double getConfidenceValue() {
		return confidenceValue;
	}

	public void setConfidenceValue(double confidenceValue2) {
		this.confidenceValue = confidenceValue2;
	}

	public double getSupportValue() {
		return supportValue;
	}

	public void setSupportValue(double supportValue2) {
		this.supportValue = supportValue2;
	}

	public boolean isCheckedOk() {
		return checkedOk;
	}

	public void setCheckedOk(boolean checkedOk) {
		this.checkedOk = checkedOk;
	}
	
	public void updateFromAR(DrlAR ar){
		this.setId(ar.getId());
		this.setAntecedentLength(ar.getAntecedentLength());
		this.setConfidenceValue(ar.getConfidenceValue());
		this.setSupportValue(ar.getSupportValue());
		this.setAcValue(ar.getAcValue());
		this.setCheckedOk(false);
		this.setBestId(ar.getId());
		this.setConsequentValue(ar.getConsequentValue());
	}

	public double getAcValue() {
		return acValue;
	}

	public void setAcValue(double acValue) {
		this.acValue = acValue;
	}
	

	
}
