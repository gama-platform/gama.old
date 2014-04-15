/*********************************************************************************************
 * 
 *
 * 'Parameter.java', in plugin 'msi.gama.hpc', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.hpc.common;

import msi.gama.headless.common.DataType;
import msi.gama.headless.common.DataTypeFactory;



public class Parameter {
	private String name;
	private Object value;
	
	public Parameter(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Object getValue() {
		return this.value;
	}
	
	
	public void setValue(Object v) {
		this.value = v;
	}

	  public static String castType(Object obj)
	    {
		  	if(obj instanceof Integer)
	    		return "INT";
		  	if(obj instanceof Boolean)
	    		return "BOOLEAN";
	    	if(obj instanceof Float || obj instanceof  Double)
	    		return "FLOAT";
	    	return "STRING";

	    }
}
