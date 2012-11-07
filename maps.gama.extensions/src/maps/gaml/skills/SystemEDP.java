package maps.gaml.skills;

import msi.gama.util.GamaList;

public abstract class SystemEDP {
	int numberEquation;
	
	public int getNumberEquation(){
		return numberEquation;
	}
	
	abstract GamaList<Double> compute(final GamaList<Double> variables);
}
