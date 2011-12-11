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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.environment.ITopology;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IExpressionParser;
import msi.gama.internal.types.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 15 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Casting {

	@operator(value = { IExpressionParser.IS }, priority = IPriority.COMPARATOR)
	public static Boolean isA(final IScope scope, final IExpression a, final IExpression b)
		throws GamaRuntimeException {
		// TODO Verify this method. And see if the treatment of species and types cannot be unifiedé
		IType type = Cast.asType(scope, b);
		if ( type.isSpeciesType() ) {
			ISpecies s = scope.getAgentScope().getVisibleSpecies(type.getSpeciesName());
			Object v = a.value(scope);
			if ( v instanceof IAgent ) { return ((IAgent) v).isInstanceOf(s, false); }
			return false;
		}
		return a.type().isSubTypeOf(type);
	}

	@operator(value = IType.CONTAINER_STR, content_type = ITypeProvider.CHILD_CONTENT_TYPE, priority = IPriority.CAST)
	public static IContainer asContainer(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaContainerType.staticCast(scope, val, null);
	}

	@operator(value = IType.TOPOLOGY_STR, content_type = IType.GEOMETRY, priority = IPriority.CAST)
	public static ITopology asTopology(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaTopologyType.staticCast(scope, val, null);
	}

	@operator(value = IType.AGENT_STR)
	public static IAgent asAgent(final IScope scope, final Object val) throws GamaRuntimeException {
		return Cast.asAgent(scope, val);
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
		if ( val instanceof GamaPoint ) { return scope.getAgentScope().getPopulationFor(species)
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
		return Cast.asFloat(scope, val);
	}

	@operator(IType.GEOM_STR)
	public static GamaGeometry asGeometry(final IScope scope, final Object s)
		throws GamaRuntimeException {
		return Cast.asGeometry(scope, s);
	}

	@operator(value = IType.INT_STR, can_be_const = true)
	public static Integer asInt(final IScope scope, final Object val) {
		return Cast.asInt(scope, val);
	}

	@operator(value = IType.LIST_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static GamaList asList(final IScope scope, final Object val) throws GamaRuntimeException {
		return Cast.asList(scope, val);
	}

	@operator(value = IType.MAP_STR, can_be_const = true)
	public static GamaMap asMap(final IScope scope, final Object val) throws GamaRuntimeException {
		return Cast.asMap(scope, val);
	}

	@operator(value = IType.MATRIX_STR, can_be_const = true, content_type = ITypeProvider.CHILD_CONTENT_TYPE)
	public static IMatrix asMatrix(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return Cast.asMatrix(scope, val);
	}

	@operator(value = "as_matrix", content_type = ITypeProvider.LEFT_CONTENT_TYPE, can_be_const = true)
	public static IMatrix asMatrix(final IScope scope, final Object val, final GamaPoint size)
		throws GamaRuntimeException {
		return Cast.asMatrix(scope, val, size);
	}

	@operator(value = IType.NONE_STR, can_be_const = true)
	public static Object asObject(final Object obj) {
		return Cast.asObject(obj);
	}

	@operator(value = IType.PAIR_STR, can_be_const = true)
	public static GamaPair asPair(final IScope scope, final Object val) throws GamaRuntimeException {
		return Cast.asPair(scope, val);
	}

	@operator(value = IType.POINT_STR, can_be_const = true)
	public static GamaPoint asPoint(final IScope scope, final Object val) {
		return Cast.asPoint(scope, val);
	}

	@operator(value = { IType.SPECIES_STR, "species_of" }, content_type = ITypeProvider.CHILD_TYPE)
	public static ISpecies asSpecies(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return (ISpecies) Types.typeToIType[IType.SPECIES].cast(scope, val);

		// TODO get the
		// appropriate
		// TypesManager?

		// TypesManager typesManager = ((ExecutionContextDescription)
		// scope.getAgentScope().getSpecies().getDescription()).getTypesManager();
		// return (ISpecies) typesManager.get(IType.SPECIES).cast(scope, val);
	}

	@operator(value = IType.STRING_STR, can_be_const = true)
	public static String asString(final IScope scope, final Object val) throws GamaRuntimeException {
		return Cast.asString(val);
	}

	@operator(value = "to_gaml")
	public static String toGaml(final Object val) {
		return Cast.toGaml(val);
	}

	@operator(value = "to_java")
	public static String toJava(final Object val) {
		return Cast.toJava(val);
	}

}
