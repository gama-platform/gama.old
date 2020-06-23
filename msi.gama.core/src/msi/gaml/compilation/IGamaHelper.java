/*******************************************************************************************************
 *
 * msi.gaml.compilation.IGamaHelper.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import msi.gama.common.interfaces.IVarAndActionSupport;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * Written by drogoul Modified on 14 ao�t 2010. Modified on 23 Apr. 2013. A general purpose helper that can be
 * subclassed like a Runnable.
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes" })
@FunctionalInterface
public interface IGamaHelper<T> {

	Object[] EMPTY_VALUES = new Object[0];

	default Class getSkillClass() {
		return null;
	}

	default T run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill) {
		return run(scope, agent, skill, EMPTY_VALUES);
	}

	T run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill, final Object values);

}