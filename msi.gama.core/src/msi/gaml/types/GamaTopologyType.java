/*******************************************************************************************************
 *
 * msi.gaml.types.GamaTopologyType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.continuous.ContinuousTopology;
import msi.gama.metamodel.topology.continuous.MultipleTopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
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
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.TOPOLOGY,
		id = IType.TOPOLOGY,
		wraps = { ITopology.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.TOPOLOGY },
		doc = @doc ("Represents a topology, obtained from agents or geometries, that can be used to compute distances, neighbours, etc."))
public class GamaTopologyType extends GamaType<ITopology> {

	@SuppressWarnings ("rawtypes")
	public static ITopology staticCast(final IScope scope, final Object obj, final boolean copy)
			throws GamaRuntimeException {
		// Many cases.
		if (obj == null) { return null; }
		if (obj instanceof ISpatialGraph) { return ((ISpatialGraph) obj).getTopology(scope); }
		if (obj instanceof ITopology) { return (ITopology) obj; }
		if (obj instanceof IAgent)
			return ((IAgent) obj).getTopology();
		if (obj instanceof IPopulation) { return ((IPopulation) obj).getTopology(); }
		if (obj instanceof ISpecies) { return staticCast(scope, scope.getAgent().getPopulationFor((ISpecies) obj),
				copy); }
		if (obj instanceof IShape) { return from(scope, (IShape) obj); }
		if (obj instanceof IContainer) { return from(scope, (IContainer) obj); }
		return staticCast(scope, Cast.asGeometry(scope, obj, copy), copy);
	}

	/**
	 * @see msi.gama.internal.types.GamaType#cast(msi.gama.interfaces.IScope, java.lang.Object, java.lang.Object)
	 */
	@Override
	public ITopology cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, copy);
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
	private static ITopology from(final IScope scope, final IContainer<?, IShape> obj) throws GamaRuntimeException {
		if (obj instanceof GamaSpatialGraph) {
			return ((GamaSpatialGraph) obj).getTopology(scope);
		} else if (obj instanceof IGrid) {
			return new GridTopology(scope, (IGrid) obj);
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
	public IType<?> getContentType() {
		return Types.GEOMETRY;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

}
