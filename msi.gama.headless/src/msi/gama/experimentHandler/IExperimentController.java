package msi.gama.experimentHandler;

public interface IExperimentController {
	
	public void setParameterWithName(String name, Object value);
	public void mergeElement(Object value);
	
	void setup(long seed);
	void setup();
	
	void step();
	
	void dispose();
	
}
