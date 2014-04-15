/*********************************************************************************************
 * 
 *
 * 'IExperimentController.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.experimentHandler;

public interface IExperimentController {
	
	public void setParameterWithName(String name, Object value);
	public void mergeElement(Object value);
	
	void setup(long seed);
	void setup();
	
	void step();
	
	void dispose();
	
}
