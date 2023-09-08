/*******************************************************************************************************
 *
 * GamaMetaType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class GamaMetaType.
 */
@type (
		name = "gaml_type",
		id = IType.TYPE,
		wraps = { IType.class },
		doc = @doc ("Metatype of all types in GAML"))
public class GamaMetaType extends GamaType<IType<?>> {

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@doc ("The type of all types")
	@no_test
	@Override
	public IType<?> cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(obj);
	}

	/**
	 * Static cast.
	 *
	 * @param obj
	 *            the obj
	 * @return the i type
	 */
	public static IType<?> staticCast(final Object obj) {
		return GamaType.of(obj);
	}

	@Override
	public IType<?> getDefault() { return Types.NO_TYPE; }

	/**
	 * Type of.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @return the i type
	 */
	@operator (
			value = { "type_of" },
			can_be_const = true,
			doc = @doc ("Returns the GAML type of the operand"))
	@doc (
			value = "Returns the GAML type of the operand",
			examples = { @example (
					value = "string(type_of(\"a string\"))",
					equals = "\"string\"",
					returnType = "string"),
					@example (
							value = "string(type_of([1,2,3,4,5]))",
							equals = "\"list<int>\"",
							returnType = "string"),
					@example ("geometry g0 <- to_GAMA_CRS({121,14}, \"EPSG:4326\"); "), @example (
							value = "string(type_of(g0))",
							equals = "\"point\"",
							returnType = "string")

			})
	@no_test
	public static IType<?> typeOf(final IScope scope, final Object obj) {
		return staticCast(obj);
	}

}
