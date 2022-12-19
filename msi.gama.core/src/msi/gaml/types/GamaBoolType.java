/*******************************************************************************************************
 *
 * GamaBoolType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.io.File;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.file.GamaFile;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.BOOL,
		id = IType.BOOL,
		wraps = { Boolean.class, boolean.class },
		kind = ISymbolKind.Variable.REGULAR,
		doc = { @doc ("Represents boolean values, either true or false") },
		concept = { IConcept.TYPE, IConcept.LOGICAL, IConcept.CONDITION })
public class GamaBoolType extends GamaType<Boolean> {

	@Override
	@doc ("Casts parameter into a bool. false if the parameter is nil, equal to zero, empty or dead, depending on its type")
	public Boolean cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param obj
	 *            the obj
	 * @param param
	 *            the param
	 * @param copy
	 *            the copy
	 * @return the boolean
	 */
	@SuppressWarnings ("rawtypes")
	public static Boolean staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj == null) return false;
		if (obj instanceof Boolean) return (Boolean) obj;
		if (obj instanceof IAgent) return !((IAgent) obj).dead();
		if (obj instanceof GamaFile) return ((GamaFile) obj).exists(scope);
		if (obj instanceof IContainer) return !((IContainer) obj).isEmpty(scope);
		if (obj instanceof File) return ((File) obj).exists();
		if (obj instanceof Integer) return (Integer) obj != 0;
		if (obj instanceof Double) return (Double) obj != 0d;
		if (obj instanceof String) return "true".equals(obj);
		return false;
	}

	@Override
	public Boolean getDefault() { return false; }

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
