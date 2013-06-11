package maps.gaml.edpSystem;

import msi.gama.util.GamaList;

public class SI extends SystemEDP {

	private final double beta;
	private final double nu;
	private final double N;

	public SI(final double _beta, final double _nu, final double _n) {
		super.numberEquation = 2;

		beta = _beta;
		nu = _nu;
		N = _n;
	}

	@Override
	public GamaList<Double> compute(final GamaList<Double> si) {

		double ds = -beta * si.get(0) * si.get(1) / N + nu * si.get(1);
		double di = beta * si.get(0) * si.get(1) / N - nu * si.get(1);

		GamaList<Double> siReturn = new GamaList<Double>();
		siReturn.add(ds);
		siReturn.add(di);

		return siReturn;
	}
}
