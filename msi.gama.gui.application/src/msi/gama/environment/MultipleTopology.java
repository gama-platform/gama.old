/**
 * Created by drogoul, 30 nov. 2011
 * 
 */
package msi.gama.environment;

import msi.gama.interfaces.*;
import msi.gama.internal.types.GamaGeometryType;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;

/**
 * The class GamaMultipleTopology.
 * 
 * @author drogoul
 * @since 30 nov. 2011
 * 
 */
public class MultipleTopology extends ContinuousTopology {

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param environment
	 */
	public MultipleTopology(final IScope scope, final IGamaContainer<?, IGeometry> places)
		throws GamaRuntimeException {
		// For the moment, use the geometric envelope in order to simplify the "environment".
		super(scope, GamaGeometryType.geometriesToGeometry(places).getGeometricEnvelope());
		this.places = places;
	}

	@Override
	protected boolean createAgents() {
		return true;
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue() throws GamaRuntimeException {
		return "Multiple topology in " + environment.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return ISymbol.TOPOLOGY + "(" + places.toGaml() + ")";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy() {
		try {
			return new MultipleTopology(scope, places/* , isTorus */);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			e.printStackTrace();
			return null;
		}
	}

}
