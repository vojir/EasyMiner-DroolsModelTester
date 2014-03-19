package net.vojir.droolsserver.drools;


public class DrlObj {
	private String name;
	private Float numVal;
	private String value;
	
	public DrlObj(){
		
	}
	
	public DrlObj(String name,String value){
		this.setName(name);
		this.setValue(value);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the numVal - numerical value of "value" 
	 */
	public Float getNumVal() {
		return numVal;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		try{
			this.numVal = Float.parseFloat(value);
		}catch (Exception e){
			this.numVal=null;
		}
		this.value = value;
	}

	
}
