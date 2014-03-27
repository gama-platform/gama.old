package msi.gama.headless.openmole;


public interface IMoleExperiment { 

	public void setup(final String experimentName);
	public void setup(final String experimentName, final long seed);
	
	public long step();
	
	public void setParameter(final String parameterName, final Object value);
	public Object getOutput(final String parameterName);
	public Object getVariableOutput(final String parameterName);
	
	public void dispose();
	
	
}
