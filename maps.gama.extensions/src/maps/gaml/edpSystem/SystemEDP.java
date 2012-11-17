package maps.gaml.edpSystem;

import msi.gama.util.GamaList;

public abstract class SystemEDP {
	int numberEquation;
	
	public int getNumberEquation(){
		return numberEquation;
	}
	
	abstract public GamaList<Double> compute(final GamaList<Double> variables);
}
