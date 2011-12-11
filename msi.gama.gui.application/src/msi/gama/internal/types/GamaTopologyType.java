/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import msi.gama.environment.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.*;
import msi.gama.util.graph.GamaSpatialGraph;
import msi.gama.util.matrix.GamaSpatialMatrix;

/**
 * The type topology.
 * 
 * @author Alexis Drogoul
 * @since 26 nov. 2011
 * 
 */
@type(value = IType.TOPOLOGY_STR, id = IType.TOPOLOGY, wraps = { ITopology.class })
public class GamaTopologyType extends GamaType<ITopology> {

	public static ITopology staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		// Many cases.
		if ( obj == null ) { return null; }
		if ( obj instanceof ITopology ) { return (ITopology) obj; }
		if ( obj instanceof IPopulation ) { return ((IPopulation) obj).getTopology(); }
		if ( obj instanceof ISpecies ) { return staticCast(scope, scope.getAgentScope()
			.getPopulationFor((ISpecies) obj), param); }
		if ( obj instanceof IGeometry ) { return from(scope, (IGeometry) obj); }
		if ( obj instanceof IContainer ) { return from(scope, (IContainer) obj); }
		return staticCast(scope, Cast.asGeometry(scope, obj), param);
	}

	/**
	 * @see msi.gama.internal.types.GamaType#cast(msi.gama.interfaces.IScope, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public ITopology cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static ITopology from(final IScope scope, final IGeometry obj) {
		return new ContinuousTopology(scope, obj);
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param obj
	 * @return
	 */
	private static ITopology from(final IScope scope, final IContainer obj)
		throws GamaRuntimeException {
		if ( obj instanceof GamaSpatialGraph ) {
			GamaGeometry env = GamaGeometryType.staticCast(scope, obj, null).getGeometricEnvelope();
			return new GraphTopology(scope, env, (GamaSpatialGraph) obj);
		} else if ( obj instanceof GamaSpatialMatrix ) {
			return new GridTopology(scope, (GamaSpatialMatrix) obj);
		} else {
			return new MultipleTopology(scope, obj);
		}
	}

	/**
	 * @see msi.gama.internal.types.GamaType#getDefault()
	 */
	@Override
	public ITopology getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(IType.GEOMETRY);
	}

}
