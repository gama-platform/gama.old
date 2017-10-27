package msi.gama.headless.core;

import msi.gama.headless.common.DataType;

public class RichOutput {

	private final String name;
	private final Object value;
	private final long step;
	private final DataType type;
	
	RichOutput(final String n, final long sp, final Object val,final DataType mtype )
	{
		this.name=n;
		this.value=val;
		this.step=sp;
		this.type = mtype;
	}
	
	public String getName() {
		return name;
	}
	public Object getValue() {
		return value;
	}
	public long getStep() {
		return step;
	}
	public DataType getType() {
		return this.type;
	}
}