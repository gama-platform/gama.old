/*******************************************************************************************************
 *
 * GamaSkillType.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.kernel.GamaSkillRegistry;

/**
 * The type used for representing species objects (since they can be manipulated in a model)
 *
 * Written by drogoul Modified on 1 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ("unchecked")
@type (
		name = IKeyword.SKILL,
		id = IType.SKILL,
		wraps = { ISkill.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.SKILL },
		doc = @doc ("Meta-type of the skills present in the GAML language"))
public class GamaSkillType extends GamaType<ISkill> {

	@Override
	@doc ("Tries to convert the parameter to a skill. If it is a skill already, returns it. If it is a string, returns it if it is registered in GAMA. Otherwise return null")
	public ISkill cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj instanceof ISkill) return (ISkill) obj;
		if (obj instanceof String) return GamaSkillRegistry.INSTANCE.getSkillInstanceFor((String) obj);
		return null;
	}

	@Override
	public ISkill getDefault() { return null; }

	@Override
	public boolean canCastToConst() {
		return true;
	}

}
