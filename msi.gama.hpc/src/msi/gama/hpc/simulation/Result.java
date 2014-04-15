/*********************************************************************************************
 * 
 *
 * 'Result.java', in plugin 'msi.gama.hpc', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.hpc.simulation;

public class Result {
	private String name;
	private Double value;

	
	
	public Result(String name, Double value) {
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
	
	
	public Double getValue() {
		return this.value;
	}
	
	
	public void setValue(Double value) {
		this.value = value;
	}


}
