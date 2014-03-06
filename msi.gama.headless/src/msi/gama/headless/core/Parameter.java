package msi.gama.headless.core;

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
