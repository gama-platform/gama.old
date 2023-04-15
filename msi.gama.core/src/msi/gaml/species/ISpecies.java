/*******************************************************************************************************
 *
 * ISpecies.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.species;

import java.util.Collection;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Containers;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 25 avr. 2010
 *
 * @todo Description
 *
 */
@vars ({ @variable (
		name = ISpecies.ACTIONS,
		type = IType.LIST,
		of = IType.STRING,
		doc = @doc ("A list of the names of the actions defined in this species")),
		@variable (
				name = ISpecies.ASPECTS,
				type = IType.LIST,
				of = IType.STRING,
				doc = @doc ("A list of the names of the aspects defined in this species")),
		@variable (
				name = IKeyword.ATTRIBUTES,
				type = IType.LIST,
				of = IType.STRING,
				doc = @doc ("A list of the names of the attributes of this species")),
		@variable (
				name = IKeyword.PARENT,
				type = IType.SPECIES,
				doc = @doc ("The parent (if any) of this species")),
		@variable (
				name = IKeyword.NAME,
				type = IType.STRING,
				doc = @doc ("The name of the species")),
		@variable (
				name = ISpecies.SUBSPECIES,
				type = IType.LIST,
				of = IType.SPECIES,
				doc = @doc ("A list of the names of subspecies of this species")),
		@variable (
				name = ISpecies.MICROSPECIES,
				type = IType.LIST,
				of = IType.STRING,
				doc = @doc ("A list of the names of the micro-species declared inside this species")),
		@variable (
				name = ISpecies.POPULATION,
				type = IType.LIST,
				of = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				doc = @doc ("The population that corresponds to this species in an instance of its host")) })
public interface ISpecies
		extends ISymbol, IAddressableContainer<Integer, IAgent, Integer, IAgent>, IPopulationSet<IAgent> {

	/** The step action name. */
	String stepActionName = "_step_";
	
	/** The init action name. */
	String initActionName = "_init_";
	
	/** The population. */
	String POPULATION = "population";
	
	/** The subspecies. */
	String SUBSPECIES = "subspecies";
	
	/** The microspecies. */
	String MICROSPECIES = "microspecies";
	
	/** The actions. */
	String ACTIONS = "actions";
	
	/** The aspects. */
	String ASPECTS = "aspects";

	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	IExpression getFrequency();

	/**
	 * Gets the schedule.
	 *
	 * @return the schedule
	 */
	IExpression getSchedule();

	/**
	 * Gets the concurrency.
	 *
	 * @return the concurrency
	 */
	IExpression getConcurrency();

	/**
	 * Extends species.
	 *
	 * @param s the s
	 * @return true, if successful
	 */
	boolean extendsSpecies(final ISpecies s);

	/**
	 * Checks if is grid.
	 *
	 * @return true, if is grid
	 */
	boolean isGrid();

	/**
	 * Checks if is graph.
	 *
	 * @return true, if is graph
	 */
	boolean isGraph();

	/**
	 * Return all the direct subspecies of this species, properly typed for GAMA
	 *
	 * @return
	 */

	IList<ISpecies> getSubSpecies(IScope scope);

	/**
	 * Gets the sub species names.
	 *
	 * @param scope the scope
	 * @return the sub species names
	 */
	@SuppressWarnings ("unchecked")
	@getter (SUBSPECIES)
	@doc ("Returns all the direct subspecies names of this species")
	default IList<String> getSubSpeciesNames(final IScope scope) {
		return StreamEx.of(getSubSpecies(scope)).map((each) -> each.getName())
				.toCollection(Containers.listOf(Types.STRING));
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	@getter (IKeyword.NAME)
	String getName();

	/**
	 * Returns all the micro-species. Micro-species includes: 1. the "direct" micro-species; 2. the micro-species of the
	 * parent-species.
	 *
	 * @return
	 */
	IList<ISpecies> getMicroSpecies();

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 *
	 * @param microSpeciesName
	 * @return a species or null
	 */
	ISpecies getMicroSpecies(String microSpeciesName);

	/**
	 * Verifies if this species has micro-species or not.
	 *
	 * @return true if this species has micro-species false otherwise
	 */
	boolean hasMicroSpecies();

	/**
	 * Verifies of the specified species is a micro-species of this species of not.
	 *
	 * @param species
	 * @return
	 */
	boolean containMicroSpecies(ISpecies species);

	/**
	 * Returns the parent species.
	 *
	 * @return
	 */
	@getter (IKeyword.PARENT)
	@doc ("Returns the direct parent of the species. Experiments, models and species with no explicit parents will return nil")
	ISpecies getParentSpecies();

	/**
	 * Verifies that if this species is the peer species of other species.
	 *
	 * @param other
	 * @return
	 */
	boolean isPeer(ISpecies other);

	/**
	 * Gets the self with parents.
	 *
	 * @return the self with parents
	 */
	List<ISpecies> getSelfWithParents();

	/**
	 * Gets the user commands.
	 *
	 * @return the user commands
	 */
	Collection<UserCommandStatement> getUserCommands();

	/**
	 * Gets the statement.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @param name the name
	 * @return the statement
	 */
	// Huynh Quang Nghi 29/01/13
	<T extends IStatement> T getStatement(Class<T> clazz, String name);

	/**
	 * Gets the action.
	 *
	 * @param name the name
	 * @return the action
	 */
	IStatement.WithArgs getAction(final String name);

	/**
	 * Gets the action names.
	 *
	 * @param scope the scope
	 * @return the action names
	 */
	@getter (ACTIONS)
	@doc ("retuns the list of actions defined in this species (incl. the ones inherited from its parent)")
	default IList<String> getActionNames(final IScope scope) {
		return GamaListFactory.create(scope, Types.STRING,
				StreamEx.of(getActions()).map((each) -> each.getName()).toList());
	}

	/**
	 * Gets the actions.
	 *
	 * @return the actions
	 */
	Collection<ActionStatement> getActions();

	/**
	 * Gets the aspect.
	 *
	 * @param n the n
	 * @return the aspect
	 */
	IExecutable getAspect(final String n);

	/**
	 * Gets the aspects.
	 *
	 * @return the aspects
	 */
	Collection<? extends IExecutable> getAspects();

	/**
	 * Gets the aspect names.
	 *
	 * @return the aspect names
	 */
	@getter (ASPECTS)
	@doc ("retuns the list of aspects defined in this species")
	IList<String> getAspectNames();

	/**
	 * Gets the architecture.
	 *
	 * @return the architecture
	 */
	IArchitecture getArchitecture();

	/**
	 * Gets the architecture name.
	 *
	 * @return the architecture name
	 */
	String getArchitectureName();

	/**
	 * Gets the macro species.
	 *
	 * @return the macro species
	 */
	ISpecies getMacroSpecies();

	/**
	 * Gets the parent name.
	 *
	 * @return the parent name
	 */
	String getParentName();

	/**
	 * Gets the var.
	 *
	 * @param n the n
	 * @return the var
	 */
	IVariable getVar(final String n);

	/**
	 * Gets the var names.
	 *
	 * @return the var names
	 */
	Collection<String> getVarNames();

	/**
	 * Similar to getVarNames(), but returns a correctly initialized IList of attribute names
	 *
	 * @param scope
	 * @return the list of all the attributes defined in this species
	 */
	@getter (IKeyword.ATTRIBUTES)
	@doc ("retuns the list of attributes defined in this species (incl. the ones inherited from its parent)")
	default IList<String> getAttributeNames(final IScope scope) {
		return GamaListFactory.create(scope, Types.STRING, getVarNames());
	}

	/**
	 * Gets the vars.
	 *
	 * @return the vars
	 */
	Collection<IVariable> getVars();

	/**
	 * Checks for aspect.
	 *
	 * @param n the n
	 * @return true, if successful
	 */
	boolean hasAspect(final String n);

	/**
	 * Checks for var.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	boolean hasVar(final String name);

	/**
	 * Sets the macro species.
	 *
	 * @param macroSpecies the new macro species
	 */
	void setMacroSpecies(final ISpecies macroSpecies);

	/**
	 * Checks if is mirror.
	 *
	 * @return true, if is mirror
	 */
	boolean isMirror();

	/**
	 * Implements skill.
	 *
	 * @param skill the skill
	 * @return the boolean
	 */
	Boolean implementsSkill(String skill);

	/**
	 * Gets the micro species names.
	 *
	 * @return the micro species names
	 */
	@getter (MICROSPECIES)
	@doc ("Returns all the direct microspecies names of this species")
	Collection<String> getMicroSpeciesNames();

	/**
	 * Returns the population of agents that belong to this species and that are hosted in the same host
	 *
	 * @param scope
	 * @return
	 *
	 */
	@Override
	@getter (POPULATION)
	@doc ("Returns the population of agents that belong to this species")
	IPopulation<? extends IAgent> getPopulation(IScope scope);

	/**
	 * Adds the temporary action.
	 *
	 * @param a the a
	 */
	void addTemporaryAction(ActionStatement a);

	/**
	 * Gets the behaviors.
	 *
	 * @return the behaviors
	 */
	Collection<IStatement> getBehaviors();

	/**
	 * Removes the temporary action.
	 */
	void removeTemporaryAction();

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	SpeciesDescription getDescription();

}