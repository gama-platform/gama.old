package maps.gaml.edpSystem;

import msi.gama.util.GamaList;

public class SEIR extends SystemEDP {

	private final double alpha;
	private final double beta;
	private final double a;
	private final double b;
	private final double d;
	private final double N;

	public SEIR(final double _alpha, final double _beta, final double _a, final double _b, final double _d,
		final double _n) {
		super.numberEquation = 4;
		alpha = _alpha;
		beta = _beta;
		a = _a;
		b = _b;
		d = _d;
		N = _n;
	}

	@Override
	public GamaList<Double> compute(final GamaList<Double> seir) {

		double ds = b * N - d * seir.get(0) - beta * seir.get(0) * seir.get(2) / N;
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
