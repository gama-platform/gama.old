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
package msi.gaml.types;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.*;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.metamodel.topology.grid.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

/**
 * The type topology.
 * 
 * @author Alexis Drogoul
 * @since 26 nov. 2011
 * 
 */
@type(value = IType.TOPOLOGY_STR, id = IType.TOPOLOGY, wraps = { ITopology.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaTopologyType extends GamaType<ITopology> {

	public static ITopology staticCast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		// Many cases.
		if ( obj == null ) { return null; }
		if ( obj instanceof ISpatialGraph ) { return ((ISpatialGraph) obj).getTopology(); }
		if ( obj instanceof ITopology ) { return (ITopology) obj; }
		if ( obj instanceof IPopulation ) { return ((IPopulation) obj).getTopology(); }
		if ( obj instanceof ISpecies ) { return staticCast(scope, scope.getAgentScope()
			.getPopulationFor((ISpecies) obj), param); }
		if ( obj instanceof IShape ) { return from(scope, (IShape) obj); }
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

	public static ITopology from(final IScope scope, final IShape obj) {
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
			return ((GamaSpatialGraph) obj).getTopology();
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
