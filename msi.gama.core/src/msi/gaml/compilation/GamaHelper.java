/*******************************************************************************************************
 *
 * msi.gaml.compilation.GamaHelper.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import msi.gama.common.interfaces.INamed;
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
public class GamaHelper<T> implements IGamaHelper<T>, INamed {

	final String name;
	final Class skillClass;
	final IGamaHelper<T> delegate;

	public GamaHelper(final Class clazz, final IGamaHelper<T> delegate) {
		this(null, clazz, delegate);
	}

	public GamaHelper(final String name, final Class clazz, final IGamaHelper<T> delegate) {
		this.name = name;
		skillClass = clazz;
		this.delegate = delegate;
	}

	@Override
	public Class getSkillClass() {
		return skillClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public T run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill, final Object values) {
		if (delegate == null) return null;
		return delegate.run(scope, agent, skill, values);
	}

}