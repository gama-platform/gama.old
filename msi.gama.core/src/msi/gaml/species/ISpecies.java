/*********************************************************************************************
 * 
 * 
 * 'ISpecies.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.species;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 25 avr. 2010
 * 
 * @todo Description
 * 
 */
public interface ISpecies extends ISymbol, IAddressableContainer<Integer, IAgent, Integer, IAgent>, IPopulationSet {

	public static final String stepActionName = "_step_";
	public static final String initActionName = "_init_";

	public abstract IExpression getFrequency();

	public abstract IExpression getSchedule();

	public abstract boolean extendsSpecies(final ISpecies s);

	public abstract boolean isGrid();

	public abstract boolean isGraph();

	/**
	 * Returns all the micro-species.
	 * Micro-species includes:
	 * 1. the "direct" micro-species;
	 * 2. the micro-species of the parent-species.
	 * 
	 * @return
	 */
	public abstract IList<ISpecies> getMicroSpecies();

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 * 
	 * @param microSpeciesName
	 * @return a species or null
	 */
	public abstract ISpecies getMicroSpecies(String microSpeciesName);

	/**
	 * Returns a list of name of all micro-species.
	 * 
	 * @return
	 */
	// public abstract IList<String> getMicroSpeciesNames();

	/**
	 * Verifies if this species has micro-species or not.
	 * 
	 * @return
	 *         true if this species has micro-species
	 *         false otherwise
	 */
	public abstract boolean hasMicroSpecies();

	/**
	 * Verifies of the specified species is a micro-species of this species of not.
	 * 
	 * @param species
	 * @return
	 */
	public abstract boolean containMicroSpecies(ISpecies species);

	/**
	 * Returns the parent species.
	 * 
	 * @return
	 */
	public abstract ISpecies getParentSpecies();

	/**
	 * Returns all the species sharing the same direct macro-species with this species.
	 * 
	 * @return
	 */
	// public abstract IList<ISpecies> getPeersSpecies();

	/**
	 * Returns a peer species with the specified name.
	 * 
	 * @param peerName name of the peer species.
	 * @return a peer species with the specified name or null.
	 */
	// public abstract ISpecies getPeerSpecies(String peerName);

	/**
	 * Verifies that if this species is the peer species of other species.
	 * 
	 * @param other
	 * @return
	 */
	public abstract boolean isPeer(ISpecies other);

	public abstract List<ISpecies> getSelfWithParents();

	// public abstract void addAction(final IStatement ce);

	// public abstract void addAspect(final IStatement ce);

	// public abstract void addBehavior(IStatement b);

	// public abstract void addChild(final ISymbol s);

	// public abstract void addVariable(final IVariable v);

	public abstract Collection<UserCommandStatement> getUserCommands();

	// Huynh Quang Nghi 29/01/13
	public abstract <T extends IStatement> T getStatement(Class<T> clazz, String name);

	public abstract IStatement.WithArgs getAction(final String name);

	public Collection<ActionStatement> getActions();

	// public abstract IAgentConstructor getAgentConstructor();

	// public abstract IType getAgentType();

	public abstract IExecutable getAspect(final String n);

	public Collection<? extends IExecutable> getAspects();

	public abstract List<String> getAspectNames();

	public abstract Collection<IStatement> getBehaviors();

	public abstract IArchitecture getArchitecture();

	public abstract String getArchitectureName();

	public abstract ISpecies getMacroSpecies();

	public abstract String getParentName();

	public abstract IVariable getVar(final String n);

	public abstract Collection<String> getVarNames();

	public abstract Collection<IVariable> getVars();

	public abstract boolean hasAspect(final String n);

	public abstract boolean hasVar(final String name);

	public abstract void setMacroSpecies(final ISpecies macroSpecies);

	public abstract boolean isMirror();

	public abstract Boolean implementsSkill(String skill);

	/**
	 * @return
	 */
	public abstract Collection<String> getMicroSpeciesNames();

	public abstract boolean isInitOverriden();

	public abstract boolean isStepOverriden();

	/**
	 * @param scope
	 * @return
	 */
	public abstract IPopulation getPopulation(IScope scope);

}