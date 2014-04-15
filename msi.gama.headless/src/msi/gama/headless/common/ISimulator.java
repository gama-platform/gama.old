/*********************************************************************************************
 * 
 *
 * 'ISimulator.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.common;


public interface ISimulator {
	public void initialize();
	public void nextStep(int currentStep) ;
	public void free() ;
	/**
	 * set a value to a model variable
	 * @param name name of the variable
	 * @param value the value
	 */
    public void   setParameterWithName(java.lang.String name, Object value );
    /**
     * get the current value of the model variable
     * @param name name of the variable
     * @return value
     */
    public Object getVariableWithName(java.lang.String name) ;
    public DataType getVariableTypeWithName(java.lang.String name) ;
    public boolean containVariableWithName(java.lang.String name) ;
    public void load(String var, String exp, String expName);
    public void setSeed(long seed); 
}
