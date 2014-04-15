/*********************************************************************************************
 * 
 *
 * 'Variable.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.common;

import java.io.Serializable;



public class Variable  {
	
	
	static int MAX_VAR_ID=0;
	
	private int variableid;

	private String name;
	
	private int step;

	private Object value;

	private DataType type;

	private static final long serialVersionUID = 1L;

	public Variable() {
		super();
		this.variableid=MAX_VAR_ID++;
	}

	public Variable(String name, int step, Object value) {
		super();
		this.variableid=MAX_VAR_ID++;
		this.name = name;
		this.step = step;
		this.value = value;
		this.type = DataTypeFactory.getObjectMetaData(this.value);
	}

	public int getVariableid() {
		return this.variableid;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStep() {
		return this.step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Object getValue() {
		return this.value;
	}

	public void setObjectValue(Object value) {
		this.value = value;
		this.type = DataTypeFactory.getObjectMetaData(this.value);
	}

	public DataType getType() {
		return this.type;
	}
	
	public String asString()
	{
		if(value==null)
			return "";
		return value.toString();
	}
}
