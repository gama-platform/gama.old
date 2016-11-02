/*********************************************************************************************
 *
 * 'ISpecies.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.species;

import java.util.Collection;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 25 avr. 2010
 *
 * @todo Description
 *
 */
@vars({ @var(name = IKeyword.ATTRIBUTES, type = IType.LIST, of = IType.STRING),
		@var(name = IKeyword.PARENT, type = IType.SPECIES), @var(name = IKeyword.NAME, type = IType.STRING),
		@var(name = ISpecies.SUBSPECIES, type = IType.LIST, of = IType.SPECIES),
		@var(name = ISpecies.POPULATION, type = IType.LIST, of = ITypeProvider.FIRST_CONTENT_TYPE) })
public interface ISpecies
		extends ISymbol, IAddressableContainer<Integer, IAgent, Integer, IAgent>, IPopulationSet<IAgent> {

	public static final String stepActionName = "_step_";
	public static final String initActionName = "_init_";
	public static final String POPULATION = "population";
	public static final String SUBSPECIES = "subspecies";

	public abstract IExpression getFrequency();

	public abstract IExpression getSchedule();

	public abstract IExpression getConcurrency();

	public abstract boolean extendsSpecies(final ISpecies s);

	public abstract boolean isGrid();

	public abstract boolean isGraph();

	/**
	 * Return all the direct subspecies of this species, properly typed for GAMA
	 * 
	 * @return
	 */
	@getter(SUBSPECIES)
	@doc("Returns all the direct subspecies of this species")
	public abstract IList<ISpecies> getSubSpecies(IScope scope);

	@Override
	@getter(IKeyword.NAME)
	public abstract String getName();

	/**
	 * Returns all the micro-species. Micro-species includes: 1. the "direct"
	 * micro-species; 2. the micro-species of the parent-species.
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
	 * Verifies if this species has micro-species or not.
	 *
	 * @return true if this species has micro-species false otherwise
	 */
	public abstract boolean hasMicroSpecies();

	/**
	 * Verifies of the specified species is a micro-species of this species of
	 * not.
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
	@getter(IKeyword.PARENT)
	@doc("Returns the direct parent of the species. Experiments, models and species with no explicit parents will return nil")
	public abstract ISpecies getParentSpecies();

	/**
	 * Verifies that if this species is the peer species of other species.
	 *
	 * @param other
	 * @return
	 */
	public abstract boolean isPeer(ISpecies other);

	public abstract List<ISpecies> getSelfWithParents();

	public abstract Collection<UserCommandStatement> getUserCommands();

	// Huynh Quang Nghi 29/01/13
	public abstract <T extends IStatement> T getStatement(Class<T> clazz, String name);

	public abstract IStatement.WithArgs getAction(final String name);

	public Collection<ActionStatement> getActions();

	public abstract IExecutable getAspect(final String n);

	public Collection<? extends IExecutable> getAspects();

	public abstract List<String> getAspectNames();

	public abstract IArchitecture getArchitecture();

	public abstract String getArchitectureName();

	public abstract ISpecies getMacroSpecies();

	public abstract String getParentName();

	public abstract IVariable getVar(final String n);

	public abstract Collection<String> getVarNames();

	/**
	 * Similar to getVarNames(), but returns a correctly initialized IList of
	 * attribute names
	 * 
	 * @param scope
	 * @return the list of all the attributes defined in this species
	 */
	@getter(IKeyword.ATTRIBUTES)
	@doc("retuns the list of attributes defined in this species (incl. the ones inherited from its parent")
	public IList<String> getAttributeNames(final IScope scope);

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
	 * Returns the population of agents that belong to this species and that are
	 * hosted in the same host
	 * 
	 * @param scope
	 * @return
	 *
	 */
	@getter(POPULATION)
	@doc("Returns the population of agents that belong to this species")
	public abstract IPopulation<? extends IAgent> getPopulation(IScope scope);

	public abstract void addTemporaryAction(ActionStatement a);

	public abstract Collection<IStatement> getBehaviors();

}