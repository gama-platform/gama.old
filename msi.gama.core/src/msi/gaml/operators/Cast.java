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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 15 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Cast {

	@operator(value = { IKeyword.IS }, priority = IPriority.COMPARATOR)
	public static Boolean isA(final IScope scope, final IExpression a, final IExpression b)
		throws GamaRuntimeException {
		// TODO Verify this method. And see if the treatment of species and types cannot be unifiedé
		IType type = asType(scope, b);
		if ( type.isSpeciesType() ) {
			ISpecies s = scope.getAgentScope().getVisibleSpecies(type.getSpeciesName());
			Object v = a.value(scope);
			if ( v instanceof IAgent ) { return ((IAgent) v).isInstanceOf(s, false); }
			return false;
		}
		return type.isAssignableFrom(a.type());
	}

	//
	// public static String toJava(final Object val) {
	// if ( val == null ) { return "null"; }
	// if ( val instanceof IValue ) { return ((IValue) val).toJava(); }
	// if ( val instanceof String ) { return StringUtils.toJavaString((String) val); }
	// return val.toString();
	// }

	public static IType asType(final IScope scope, final IExpression expr)
		throws GamaRuntimeException {
		Object value = expr.value(scope);
		if ( value instanceof String ) {
			IModel m = scope.getSimulationScope().getModel();
			return m.getDescription().getTypeOf((String) value);
		} else if ( value instanceof ISpecies ) {
			return ((ISpecies) value).getAgentType();
		} else {
			return expr.type();
		}
	}

	@operator(value = IType.CONTAINER_STR, content_type = ITypeProvider.CHILD_CONTENT_TYPE, priority = IPriority.CAST)
	public static IContainer asContainer(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaContainerType.staticCast(scope, val, null);
	}

	@operator(value = IType.GRAPH_STR, content_type = ITypeProvider.CHILD_CONTENT_TYPE, priority = IPriority.CAST)
	public static IGraph asGraph(final IScope scope, final Object val) {
		return GamaGraphType.staticCast(scope, val, null);
	}

	@operator(value = IType.TOPOLOGY_STR, content_type = IType.GEOMETRY, priority = IPriority.CAST)
	public static ITopology asTopology(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaTopologyType.staticCast(scope, val, null);
	}

	@operator(value = IType.AGENT_STR)
	public static IAgent asAgent(final IScope scope, final Object val) throws GamaRuntimeException {
		return (IAgent) Types.get(IType.AGENT).cast(scope, val, null);
	}

	@operator(value = "as", type = ITypeProvider.RIGHT_CONTENT_TYPE, content_type = ITypeProvider.RIGHT_CONTENT_TYPE, priority = IPriority.CAST)
	public static IAgent asAgent(final IScope scope, final Object val, final ISpecies species)
		throws GamaRuntimeException {
		if ( val == null ) { return null; }
		if ( species == null ) { return asAgent(scope, val); }
		if ( val instanceof IAgent ) {
			// if ( ((IAgent) val).dead() ) { return null; }
			return ((IAgent) val).isInstanceOf(species, false) ? (IAgent) val : null;
		}
		// if ( val instanceof String ) { return species.getAgent((String) val); }
		if ( val instanceof Integer ) { return scope.getAgentScope().getPopulationFor(species)
			.getAgent((Integer) val); }
		if ( val instanceof ILocation ) { return scope.getAgentScope().getPopulationFor(species)
			.getAgent((GamaPoint) val); }
		return null;
	}

	@operator(value = IType.BOOL_STR, can_be_const = true)
	public static Boolean asBool(final IScope scope, final Object val) {
		return GamaBoolType.staticCast(scope, val, null);
	}

	@operator(value = IType.COLOR_STR, can_be_const = true)
	public static GamaColor asColor(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaColorType.staticCast(scope, val, null);
	}

	@operator(value = IType.FLOAT_STR, can_be_const = true)
	public static Double asFloat(final IScope scope, final Object val) {
		return GamaFloatType.staticCast(scope, val, null);
	}

	@operator(IType.GEOM_STR)
	public static IShape asGeometry(final IScope scope, final Object s) throws GamaRuntimeException {
		return GamaGeometryType.staticCast(scope, s, null);
	}

	@operator(value = IType.INT_STR, can_be_const = true)
	public static Integer asInt(final IScope scope, final Object val) {
		return GamaIntegerType.staticCast(scope, val, null);
	}

	@operator(value = IType.LIST_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IList asList(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaListType.staticCast(scope, val, null);
	}

	@operator(value = IType.MAP_STR, can_be_const = true)
	public static GamaMap asMap(final IScope scope, final Object val) throws GamaRuntimeException {
		return (GamaMap) Types.get(IType.MAP).cast(scope, val, null);
	}

	@operator(value = IType.MATRIX_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IMatrix asMatrix(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return asMatrix(scope, val, null);
	}

	@operator(value = "as_matrix", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public static IMatrix asMatrix(final IScope scope, final Object val, final ILocation size)
		throws GamaRuntimeException {
		return (IMatrix) Types.get(IType.MATRIX).cast(scope, val, size);
	}

	@operator(value = IType.NONE_STR, can_be_const = true)
	public static Object asObject(final Object obj) {
		return obj;
	}

	@operator(value = IType.PAIR_STR, can_be_const = true)
	public static GamaPair asPair(final IScope scope, final Object val) throws GamaRuntimeException {
		return (GamaPair) Types.get(IType.PAIR).cast(scope, val, null);
	}

	@operator(value = IType.POINT_STR, can_be_const = true)
	public static ILocation asPoint(final IScope scope, final Object val) {
		return GamaPointType.staticCast(scope, val, null);
	}

	@operator(value = { IType.SPECIES_STR, "species_of" }, content_type = ITypeProvider.CHILD_TYPE)
	public static ISpecies asSpecies(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return (ISpecies) Types.get(IType.SPECIES).cast(scope, val, null);
	}

	@operator(value = IType.STRING_STR, can_be_const = true)
	public static String asString(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaStringType.staticCast(val, null);
	}

	@operator(value = "to_gaml")
	public static String toGaml(final Object val) {
		return StringUtils.toGaml(val);
	}

	@operator(value = "to_java")
	public static String toJava(final Object val) throws GamaRuntimeException {
		throw new GamaRuntimeException("to_java is not yet implemented")/* Cast.toJava(val) */;
	}

}
