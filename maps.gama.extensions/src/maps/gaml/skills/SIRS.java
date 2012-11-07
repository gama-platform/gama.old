package maps.gaml.skills;

import msi.gama.util.GamaList;

public class SIRS extends SystemEDP {
	
	private double alpha; 
	private double beta;
	private double gamma;
	private double b;
	private double d1;
	private double d2;
	private double N;
	
	public SIRS(double _alpha ,double _beta, double _gamma, double _b, double _d1, double _d2, double _n){
		super.numberEquation = 3;
		alpha = _alpha;
		beta = _beta;
		gamma = _gamma;
		b = _b;
		d1 = _d1;
		d2 = _d2;
		N = _n;
	}
	
	GamaList<Double> compute(final GamaList<Double> sirs) {

		double ds = (- beta * sirs.get(0) * sirs.get(1) / N) + (gamma * sirs.get(2)) + (b * N)  - (d1 * sirs.get(0));
		double di = (beta * sirs.get(0) * sirs.get(1) / N) - (alpha * sirs.get(1)) - (d2 * sirs.get(1));
		double dr = (alpha * sirs.get(1)) - (gamma * sirs.get(2)) - (d1 * sirs.get(2));
		
		GamaList<Double> sirsReturn = new GamaList<Double>();
		sirsReturn.add(ds);
		sirsReturn.add(di);
		sirsReturn.add(dr);
		
		return sirsReturn;
	}
}
