/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.text.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.graph.*;

/**
 * Written by drogoul Modified on 15 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Cast {

	public static IAgent asAgent(final IScope scope, final Object val) throws GamaRuntimeException {
		return (IAgent) Types.typeToIType[IType.AGENT].cast(scope, val);
	}

	public static IAgent asAgent(final Object val) throws GamaRuntimeException {
		return asAgent(GAMA.getDefaultScope(), val);
	}

	public static Boolean asBool(final IScope scope, final Object val) {
		return GamaBoolType.staticCast(scope, val, null);
	}

	public static Boolean asBool(final Object val) {
		return asBool(GAMA.getDefaultScope(), val);
	}

	public static GamaColor asColor(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return GamaColorType.staticCast(scope, val, null);
	}

	public static GamaColor asColor(final Object val) throws GamaRuntimeException {
		return asColor(GAMA.getDefaultScope(), val);
	}

	public static Double asFloat(final IScope scope, final Object val) {
		return GamaFloatType.staticCast(scope, val, null);
	}

	public static Double asFloat(final Object val) {
		return asFloat(GAMA.getDefaultScope(), val);
	}

	public static GamaGeometry asGeometry(final IScope scope, final Object s)
		throws GamaRuntimeException {
		return (GamaGeometry) Types.get(IType.GEOMETRY).cast(scope, s);
	}

	public static GamaGeometry asGeometry(final Object val) throws GamaRuntimeException {
		return asGeometry(GAMA.getDefaultScope(), val);
	}

	public static Integer asInt(final IScope scope, final Object val) {
		return GamaIntegerType.staticCast(scope, val, null);
	}

	public static Integer asInt(final Object val) {
		return asInt(GAMA.getDefaultScope(), val);
	}

	public static GamaList asList(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaListType.staticCast(scope, val, null);
	}

	public static GamaList asList(final Object val) throws GamaRuntimeException {
		return asList(GAMA.getDefaultScope(), val);
	}

	public static GamaMap asMap(final IScope scope, final Object val) throws GamaRuntimeException {
		return (GamaMap) Types.get(IType.MAP).cast(scope, val);
	}

	public static IMatrix asMatrix(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return (IMatrix) Types.typeToIType[IType.MATRIX].cast(scope, val);
	}

	public static IMatrix asMatrix(final IScope scope, final Object val, final GamaPoint size)
		throws GamaRuntimeException {
		return (IMatrix) Types.typeToIType[IType.MATRIX].cast(scope, val, size);
	}

	public static GamaMap asMap(final Object val) throws GamaRuntimeException {
		return (GamaMap) Types.get(IType.MAP).cast(null, val);
	}

	public static IMatrix asMatrix(final Object val) throws GamaRuntimeException {
		return (IMatrix) Types.typeToIType[IType.MATRIX].cast(null, val);
	}

	public static IMatrix asMatrix(final Object val, final GamaPoint size)
		throws GamaRuntimeException {
		return (IMatrix) Types.typeToIType[IType.MATRIX].cast(null, val, size);
	}

	public static Object asObject(final Object obj) {
		return obj;
	}

	public static GamaPair asPair(final IScope scope, final Object val) throws GamaRuntimeException {
		return (GamaPair) Types.get(IType.PAIR).cast(scope, val);
	}

	public static GamaPoint asPoint(final IScope scope, final Object val) {
		return GamaPointType.staticCast(scope, val, null);
	}

	public static String asString(final Object val) throws GamaRuntimeException {
		return GamaStringType.staticCast(val, null);
	}

	public static GamaPair asPair(final Object val) throws GamaRuntimeException {
		return (GamaPair) Types.get(IType.PAIR).cast(null, val);
	}

	public static GamaPoint asPoint(final Object val) {
		return GamaPointType.staticCast(null, val, null);
	}

	public static GamaGraph asGraph(final Object val) throws GamaRuntimeException {
		return (GamaGraph) Types.get(IType.GRAPH).cast(val);
	}

	public static IGraph asGraph(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return (IGraph) Types.get(IType.GRAPH).cast(scope, val);
	}

	public static String toGaml(final Object val) {
		if ( val == null ) { return "nil"; }
		if ( val instanceof IValue ) { return ((IValue) val).toGaml(); }
		if ( val instanceof String ) { return GamaStringType.toGamlString((String) val); }
		if ( val instanceof Double ) { return DEFAULT_DECIMAL_FORMAT.format(val); }
		return String.valueOf(val);
	}

	public static final DecimalFormatSymbols	SYMBOLS;
	public static final DecimalFormat			DEFAULT_DECIMAL_FORMAT;

	static {
		SYMBOLS = new DecimalFormatSymbols();
		SYMBOLS.setDecimalSeparator('.');
		DEFAULT_DECIMAL_FORMAT = new DecimalFormat("##0.0################", SYMBOLS);
	}

	public static String toJava(final Object val) {
		if ( val == null ) { return "null"; }
		if ( val instanceof IValue ) { return ((IValue) val).toJava(); }
		if ( val instanceof String ) { return GamaStringType.toJavaString((String) val); }
		return val.toString();
	}

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

}
