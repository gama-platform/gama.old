/*******************************************************************************************************
 *
 * IArchitecture.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture;

import msi.gama.common.interfaces.ISkill;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */
public interface IArchitecture extends ISkill, IStatement {

	/**
	 * Inits the.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public abstract boolean init(IScope scope) throws GamaRuntimeException;

	/**
	 * Abort.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public abstract boolean abort(IScope scope) throws GamaRuntimeException;

	/**
	 * Verify behaviors.
	 *
	 * @param context the context
	 */
	public abstract void verifyBehaviors(ISpecies context);

	/**
	 * Pre step.
	 *
	 * @param scope the scope
	 * @param gamaPopulation the gama population
	 */
	public abstract void preStep(final IScope scope, IPopulation<? extends IAgent> gamaPopulation);

	@Override
	public default int getOrder() {
		return 0;
	}

	@Override
	public default void setOrder(final int o) {}
}