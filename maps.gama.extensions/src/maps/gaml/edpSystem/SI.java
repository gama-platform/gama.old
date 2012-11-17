package maps.gaml.edpSystem;

import maps.gaml.edpSystem.SystemEDP;
import msi.gama.util.GamaList;

public class SI extends SystemEDP {
	
	private double beta;
	private double nu;
	private double N;
	
	public SI(double _beta, double _nu, double _n){
		super.numberEquation = 2;

		beta = _beta;
		nu = _nu;
		N = _n;
	}
	
	public GamaList<Double> compute(final GamaList<Double> si) {

		double ds = (- beta * si.get(0) * si.get(1) / N) + (nu * si.get(1)) ;
		double di = (beta * si.get(0) * si.get(1) / N) - (nu * si.get(1));
		
		GamaList<Double> siReturn = new GamaList<Double>();
		siReturn.add(ds);
		siReturn.add(di);
		
		return siReturn;
	}
}
