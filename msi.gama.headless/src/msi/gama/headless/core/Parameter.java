package msi.gama.headless.core;

import msi.gama.headless.common.DataType;
import msi.gama.headless.common.DataTypeFactory;



public class Parameter {
	private String name;
	private Object value;
	private DataType type;
	
	
	public Parameter(String name, Object value, DataType type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
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
	
	
	public void setValue(Object value) {
		this.type=DataTypeFactory.getObjectMetaData(value);
		this.value = value;
	}


}
