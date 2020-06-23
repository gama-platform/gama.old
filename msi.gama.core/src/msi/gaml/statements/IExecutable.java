/*******************************************************************************************************
 *
 * msi.gaml.statements.IExecutable.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Class IExecutable.
 *
 * @author drogoul
 * @since 20 août 2013
 *
 */
public interface IExecutable {

	Object executeOn(final IScope scope) throws GamaRuntimeException;

	default void setRuntimeArgs(final IScope executionScope, final Arguments args) {
		// Do nothing
	}

	default void setMyself(final IAgent caller) {
		// Do nothing
	}

}
