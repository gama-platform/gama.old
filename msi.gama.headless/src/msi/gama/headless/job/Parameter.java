/*********************************************************************************************
 * 
 *
 * 'Parameter.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.job;

import msi.gama.headless.common.DataType;

public class Parameter {

	private String name;
	private Object value;

	// private DataType type;

	public Parameter(final String name, final Object value, final DataType type) {
		super();
		this.name = name;
		this.value = value;
		// this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(final Object value) {
		// this.type=DataTypeFactory.getObjectMetaData(value);
		this.value = value;
	}

}
