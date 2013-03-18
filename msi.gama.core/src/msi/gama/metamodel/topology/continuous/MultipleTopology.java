/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.continuous;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.types.GamaGeometryType;

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
	public MultipleTopology(final IScope scope, final IContainer<?, IShape> places)
		throws GamaRuntimeException {
		// For the moment, use the geometric envelope in order to simplify the "environment".
		super(scope, GamaGeometryType.geometriesToGeometry(scope, places).getGeometricEnvelope(),
			false);
		this.places = places;
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "Multiple topology in " + environment.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return IKeyword.TOPOLOGY + "(" + places.toGaml() + ")";
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(IScope scope) {
		try {
			return new MultipleTopology(scope, places/* , isTorus */);
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			e.printStackTrace();
			return null;
		}
	}

}
