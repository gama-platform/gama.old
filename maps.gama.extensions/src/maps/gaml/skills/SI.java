package maps.gaml.skills;

import msi.gama.util.GamaList;

public class SI extends SystemEDP {
	
	private double beta;
	private double gamma;
	private double N;
	
	public SI(double _beta, double _gamma, double _n){
		super.numberEquation = 2;

		beta = _beta;
		gamma = _gamma;
		N = _n;
	}
	
	GamaList<Double> compute(final GamaList<Double> si) {

		double ds = (- beta * si.get(0) * si.get(1) / N) + (gamma * si.get(1)) ;
		double di = (beta * si.get(0) * si.get(1) / N) - (gamma * si.get(1));
		
		GamaList<Double> siReturn = new GamaList<Double>();
		siReturn.add(ds);
		siReturn.add(di);
		
		return siReturn;
	}
}
