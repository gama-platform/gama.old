/*********************************************************************************************
 *
 * 'GamaHelper.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gama.common.interfaces.IVarAndActionSupport;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 14 ao�t 2010. Modified on 23 Apr. 2013. A
 * general purpose helper that can be subclassed like a Runnable.
 *
 * @todo Description
 *
 */
@SuppressWarnings({ "rawtypes" })
public abstract class GamaHelper<T> {

	Class skillClass;
	final IType returnType;

	public GamaHelper() {
		this(null);
	}

	public GamaHelper(final Class clazz) {
		this(Types.NO_TYPE, clazz);
	}

	public GamaHelper(final IType type, final Class clazz) {
		if (clazz != null && Skill.class.isAssignableFrom(clazz)) {
			skillClass = clazz;
		} else {
			skillClass = null;
		}
		returnType = type;
	}

	public Class getSkillClass() {
		return skillClass;
	}

	public IType getReturnType() {
		return returnType;
	}

	public T run(final IScope scope) {
		return null;
	};

	public T run(final IScope scope, final Object... values) {
		return null;
	}

	public T run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill, final Object... values) {
		return null;
	}

	/**
	 * @param clazz
	 */
	public void setSkillClass(final Class clazz) {
		skillClass = clazz;
	}

}
