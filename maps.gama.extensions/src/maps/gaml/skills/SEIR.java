package maps.gaml.skills;

import msi.gama.util.GamaList;

public class SEIR extends SystemEDP {
	
	private double alpha; 
	private double beta;
	private double a;
	private double b;
	private double d;
	private double N;
	
	public SEIR(double _alpha ,double _beta, double _a, double _b, double _d, double _n){
		super.numberEquation = 4;
		alpha = _alpha;
		beta = _beta;
		a = _a;		
		b = _b;
		d = _d;
		N = _n;
	}
	
	GamaList<Double> compute(final GamaList<Double> seir) {

		double ds = b * N - d * seir.get(0) - beta * seir.get(0) * seir.get(2) / N ;
		double de = beta * seir.get(0) * seir.get(2) / N - (d + a) * seir.get(1);
		double di = a * seir.get(1) - alpha * seir.get(2) - d * seir.get(2);
		double dr = alpha * seir.get(2) - d * seir.get(2);
		
		GamaList<Double> seirReturn = new GamaList<Double>();
		seirReturn.add(ds);
		seirReturn.add(de);		
		seirReturn.add(di);
		seirReturn.add(dr);
		
		return seirReturn;
	}
}
