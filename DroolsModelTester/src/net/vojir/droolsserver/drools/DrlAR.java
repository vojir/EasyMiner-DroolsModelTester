package net.vojir.droolsserver.drools;


public class DrlAR {
	private String id = "";
	private int antecedentLength = -1;
	private double confidenceValue = -1;
	private double supportValue = -1;
	private boolean checkedOk=false;
	
	
	public DrlAR(){}
	
	public DrlAR(String id){
		setId(id);
	}
	
	public DrlAR(String id,int antecedentLength, double confidenceValue, double supportValue){
		setId(id);
		setAntecedentLength(antecedentLength);
		setConfidenceValue(confidenceValue);
		setSupportValue(supportValue);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		this.setCheckedOk(false);
	}
	

	
}
