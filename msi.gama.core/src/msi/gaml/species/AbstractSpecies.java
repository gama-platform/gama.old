/*********************************************************************************************
 *
 *
 * 'AbstractSpecies.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.species;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.kernel.model.GamlModelSpecies;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.graph.AbstractGraphNodeAgent;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.AspectStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.IStatement.WithArgs;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 29 d�c. 2010
 *
 * @todo Description
 *
 */
public abstract class AbstractSpecies extends Symbol implements ISpecies {

	protected final boolean isGrid, isGraph;
	protected final Map<String, ISpecies> microSpecies = new THashMap<String, ISpecies>();
	private final Map<String, IVariable> variables = new TOrderedHashMap<String, IVariable>();
	private final Map<String, AspectStatement> aspects = new TOrderedHashMap<String, AspectStatement>();
	private final Map<String, ActionStatement> actions = new TOrderedHashMap<String, ActionStatement>();
	private final Map<String, UserCommandStatement> userCommands = new TOrderedHashMap();
	private final List<IStatement> behaviors = new ArrayList();
	protected ISpecies macroSpecies, parentSpecies;
	private boolean isInitOverriden, isStepOverriden;
	final IArchitecture control;

	public AbstractSpecies(final IDescription description) {
		super(description);
		setName(description.getName());
		isGrid = getKeyword().equals(IKeyword.GRID);
		isGraph = AbstractGraphNodeAgent.class.isAssignableFrom(((SpeciesDescription) description).getJavaBase());
		control = (IArchitecture) getDescription().getControl().createInstance();
	}

	@Override
	public java.lang.Iterable<? extends IAgent> iterable(final IScope scope) {
		return getPopulation(scope).iterable(scope);
	}

	@Override
	public void addTemporaryAction(final ActionStatement action) {
		actions.put(action.getName(), action);
	}

	@Override
	public IPopulation getPopulation(final IScope scope) {
		final IAgent a = scope.getAgent();
		IPopulation result = null;
		if (a != null) {
			// AD 19/09/13 Patch to allow experiments to gain access to the
			// simulation populations
			result = a.getPopulationFor(this);
		}
		return result;
	}

	@Override
	public IList listValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		// return getPopulation(scope).listValue(scope, contentsType);
		// hqnghi 16/04/14
		IPopulation pop = getPopulation(scope);
		if (pop == null) {
			pop = scope.getSimulation().getPopulationFor(contentsType.getName());
		}
		// AD 20/01/16 : Explicitly passes true in order to obtain a copy of the
		// population
		return pop.listValue(scope, contentsType, true);
		// end-hqnghi
	}

	@Override
	public String stringValue(final IScope scope) {
		return name;
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		final IList<IAgent> agents = listValue(scope, contentsType, false);
		// Default behavior : Returns a map containing the names of agents as
		// keys and the agents themselves as values
		final GamaMap result = GamaMapFactory.create(Types.STRING, scope.getModelContext().getTypeNamed(getName()));
		for (final IAgent agent : agents.iterable(scope)) {
			result.put(agent.getName(), agent);
		}
		return result;
	}

	@Override
	public boolean isGrid() {
		return isGrid;
	}

	@Override
	public boolean isGraph() {
		return isGraph;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public ISpecies copy(final IScope scope) {
		return this;
		// Species are immutable
	}

	@Override
	public IList<ISpecies> getMicroSpecies() {
		final IList<ISpecies> retVal = GamaListFactory.create(Types.SPECIES);
		retVal.addAll(microSpecies.values());
		final ISpecies parentSpecies = this.getParentSpecies();
		if (parentSpecies != null) {
			retVal.addAll(parentSpecies.getMicroSpecies());
		}
		return retVal;
	}

	@Override
	public IList<ISpecies> getSubSpecies(final IScope scope) {
		final IList<ISpecies> subspecies = GamaListFactory.create(Types.SPECIES);
		final GamlModelSpecies model = (GamlModelSpecies) scope.getModel().getSpecies();
		for (final ISpecies s : model.getAllSpecies().values()) {
			if (s.getParentSpecies() == this) {
				subspecies.add(s);
			}
		}
		return subspecies;
	}

	@Override
	public Collection<String> getMicroSpeciesNames() {
		return microSpecies.keySet();
	}

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 *
	 * @param microSpeciesName
	 * @return a species or null
	 */
	@Override
	public ISpecies getMicroSpecies(final String microSpeciesName) {
		final ISpecies retVal = microSpecies.get(microSpeciesName);
		if (retVal != null) {
			return retVal;
		}
		final ISpecies parentSpecies = this.getParentSpecies();
		if (parentSpecies != null) {
			return parentSpecies.getMicroSpecies(microSpeciesName);
		}
		return null;
	}

	@Override
	public boolean containMicroSpecies(final ISpecies species) {
		final ISpecies parentSpecies = this.getParentSpecies();
		return microSpecies.values().contains(species)
				|| (parentSpecies != null ? parentSpecies.containMicroSpecies(species) : false);
	}

	@Override
	public boolean hasMicroSpecies() {
		final ISpecies parentSpecies = this.getParentSpecies();
		return !microSpecies.isEmpty() || (parentSpecies != null ? parentSpecies.hasMicroSpecies() : false);
	}

	@Override
	public SpeciesDescription getDescription() {
		return (SpeciesDescription) description;
	}

	@Override
	public boolean isPeer(final ISpecies other) {
		return other != null && other.getMacroSpecies().equals(this.getMacroSpecies());
	}

	@Override
	public List<ISpecies> getSelfWithParents() {
		final List<ISpecies> retVal = new ArrayList<ISpecies>();
		retVal.add(this);
		ISpecies currentParent = this.getParentSpecies();
		while (currentParent != null) {
			retVal.add(currentParent);
			currentParent = currentParent.getParentSpecies();
		}

		return retVal;
	}

	@Override
	public ISpecies getParentSpecies() {
		if (parentSpecies == null) {
			final TypeDescription parentSpecDesc = getDescription().getParent();
			// Takes care of invalid species (see Issue 711)
			if (parentSpecDesc == null || parentSpecDesc == getDescription()) {
				return null;
			}
			ISpecies currentMacroSpec = this.getMacroSpecies();
			while (currentMacroSpec != null && parentSpecies == null) {
				parentSpecies = currentMacroSpec.getMicroSpecies(parentSpecDesc.getName());
				currentMacroSpec = currentMacroSpec.getMacroSpecies();
			}
		}
		return parentSpecies;
	}

	@Override
	public boolean extendsSpecies(final ISpecies s) {
		final ISpecies parent = getParentSpecies();
		if (parent == null) {
			return false;
		}
		if (parent == s) {
			return true;
		}
		return parent.extendsSpecies(s);
	}

	@Override
	public String getParentName() {
		return getDescription().getParentName();
	}

	@Override
	public boolean equals(final Object other) {
		return this == other;
	}

	@Override
	public IArchitecture getArchitecture() {
		return control;
	}

	@Override
	public IVariable getVar(final String n) {
		return variables.get(n);
	}

	@Override
	public boolean hasVar(final String name) {
		return variables.containsKey(name);
	}

	@Override
	public Collection<String> getVarNames() {
		return getDescription().getAttributeNames();
	}

	@Override
	public IList<String> getAttributeNames(final IScope scope) {
		return GamaListFactory.create(scope, Types.STRING, getVarNames());
	}

	@Override
	public Collection<IVariable> getVars() {
		return variables.values();
	}

	@Override
	public Collection<UserCommandStatement> getUserCommands() {
		return userCommands.values();
	}

	@Override
	public WithArgs getAction(final String name) {
		return actions.get(name);
	}

	@Override
	public Collection<ActionStatement> getActions() {
		return actions.values();
	}

	@Override
	public boolean hasAspect(final String n) {
		return aspects.containsKey(n);
	}

	@Override
	public IExecutable getAspect(final String n) {
		return aspects.get(n);
	}

	@Override
	public Collection<? extends IExecutable> getAspects() {
		return aspects.values();
	}

	@Override
	public List<String> getAspectNames() {
		return new ArrayList<String>(aspects.keySet());
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		// First we verify the control architecture
		if (control == null) {
			throw GamaRuntimeException.error("The control of species " + description.getName() + " cannot be computed");
		}
		// Then we classify the children in their categories
		for (final ISymbol s : children) {
			if (s instanceof ISpecies) {
				final ISpecies oneMicroSpecies = (ISpecies) s;
				oneMicroSpecies.setMacroSpecies(this);
				microSpecies.put(oneMicroSpecies.getName(), oneMicroSpecies);
			} else if (s instanceof IVariable) {
				s.setEnclosing(this);
				variables.put(s.getName(), (IVariable) s);

			} else if (s instanceof AspectStatement) {
				aspects.put(s.getName(), (AspectStatement) s);
			} else if (s instanceof ActionStatement) {
				if (!s.getDescription().isBuiltIn()) {
					final String name = s.getName();
					if (name.equals(initActionName)) {
						isInitOverriden = true;
					} else if (name.equals(stepActionName)) {
						isStepOverriden = true;
					}
				}
				s.setEnclosing(this);
				actions.put(s.getName(), (ActionStatement) s);
			} else if (s instanceof UserCommandStatement) {
				userCommands.put(s.getName(), (UserCommandStatement) s);
			} else if (s instanceof IStatement) {
				behaviors.add((IStatement) s); // reflexes, states or tasks
			}
		}
		control.setChildren(behaviors);
		control.verifyBehaviors(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (final IVariable v : variables.values()) {
			v.dispose();
		}
		variables.clear();
		for (final AspectStatement ac : aspects.values()) {
			ac.dispose();
		}
		aspects.clear();
		for (final ActionStatement ac : actions.values()) {
			ac.dispose();
		}
		actions.clear();
		for (final IStatement c : behaviors) {
			c.dispose();
		}
		behaviors.clear();
		macroSpecies = null;
		parentSpecies = null;
		// TODO dispose micro_species first???
		microSpecies.clear();
	}

	// TODO review this
	// this is the "original" macro-species???
	@Override
	public ISpecies getMacroSpecies() {
		return macroSpecies;
	}

	@Override
	public void setMacroSpecies(final ISpecies macroSpecies) {
		this.macroSpecies = macroSpecies;
	}

	/*
	 * Equation (Huynh Quang Nghi)
	 */

	@Override
	public <T extends IStatement> T getStatement(final Class<T> clazz, final String valueOfFacetName) {
		for (final IStatement s : behaviors) {
			final boolean instance = clazz.isAssignableFrom(s.getClass());
			if (instance) {
				if (valueOfFacetName == null) {
					return (T) s;
				}
				final String t = s.getDescription().getName();
				if (t != null) {
					final boolean named = t.equals(valueOfFacetName);
					if (named) {
						return (T) s;
					}
				}
			}
		}
		return null;
	}

	/*
	 * end-of Equation
	 */

	@Override
	public Boolean implementsSkill(final String skill) {
		return getDescription().implementsSkill(skill);
	}

	/**
	 * Method isInitOverriden()
	 * 
	 * @see msi.gaml.species.ISpecies#isInitOverriden()
	 */
	@Override
	public boolean isInitOverriden() {
		return isInitOverriden;
	}

	/**
	 * Method isStepOverriden()
	 * 
	 * @see msi.gaml.species.ISpecies#isStepOverriden()
	 */
	@Override
	public boolean isStepOverriden() {
		return isStepOverriden;
	}

	@Override
	public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
		return getPopulation(scope).get(scope, index);
	}

	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return getPopulation(scope).contains(scope, o);
	}

	@Override
	public IAgent firstValue(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).firstValue(scope);
	}

	@Override
	public IAgent lastValue(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).lastValue(scope);
	}

	@Override
	public int length(final IScope scope) {
		return getPopulation(scope).length(scope);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return getPopulation(scope).isEmpty(scope);
	}

	@Override
	public IContainer reverse(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).reverse(scope);
	}

	@Override
	public IAgent anyValue(final IScope scope) {
		return getPopulation(scope).anyValue(scope);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy)
			throws GamaRuntimeException {
		return getPopulation(scope).matrixValue(scope, contentsType, copy);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) throws GamaRuntimeException {
		return getPopulation(scope).matrixValue(scope, contentsType, preferredSize, copy);
	}

	@Override
	public IAgent getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		return getPopulation(scope).getFromIndicesList(scope, indices);
	}

	@Override
	public boolean isMirror() {
		return getDescription().isMirror();
	}

	@Override
	public Collection<? extends IPopulation> getPopulations(final IScope scope) {
		return Collections.singleton(getPopulation(scope));
	}

	public ISkill getSkillInstanceFor(final Class skillClass) {
		if (skillClass == null)
			return null;
		if (skillClass.isAssignableFrom(control.getClass())) {
			return control;
		}
		return getSkillInstanceFor(getDescription(), skillClass);
	}

	private ISkill getSkillInstanceFor(final SpeciesDescription sd, final Class skillClass) {
		for (final SkillDescription sk : sd.getSkills()) {
			if (skillClass.isAssignableFrom(sk.getJavaBase())) {
				return sk.getInstance();
			}
		}
		if (sd.getParent() != null && sd.getParent() != sd)
			return getSkillInstanceFor(sd.getParent(), skillClass);
		return null;
	}

}