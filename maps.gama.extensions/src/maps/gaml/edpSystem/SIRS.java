package maps.gaml.edpSystem;

import msi.gama.util.GamaList;

public class SIRS extends SystemEDP {

	private final double alpha;
	private final double beta;
	private final double gamma;
	private final double b;
	private final double d1;
	private final double d2;
	private final double N;

	public SIRS(final double _alpha, final double _beta, final double _gamma, final double _b, final double _d1,
		final double _d2, final double _n) {
		super.numberEquation = 3;
		alpha = _alpha;
		beta = _beta;
		gamma = _gamma;
		b = _b;
		d1 = _d1;
		d2 = _d2;
		N = _n;
	}

	@Override
	public GamaList<Double> compute(final GamaList<Double> sirs) {

		double ds = -beta * sirs.get(0) * sirs.get(1) / N + gamma * sirs.get(2) + b * N - d1 * sirs.get(0);
		double di = beta * sirs.get(0) * sirs.get(1) / N - alpha * sirs.get(1) - d2 * sirs.get(1);
		double dr = alpha * sirs.get(1) - gamma * sirs.get(2) - d1 * sirs.get(2);

		GamaList<Double> sirsReturn = new GamaList<Double>();
		sirsReturn.add(ds);
		sirsReturn.add(di);
		sirsReturn.add(dr);

		return sirsReturn;
	}
}
