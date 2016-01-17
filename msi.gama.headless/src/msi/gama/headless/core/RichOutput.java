package msi.gama.headless.core;

public class RichOutput {

	private final String name;
	private final Object value;
	private final long step;
	
	RichOutput(final String n, final long sp, final Object val )
	{
		this.name=n;
		this.value=val;
		this.step=sp;
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
}