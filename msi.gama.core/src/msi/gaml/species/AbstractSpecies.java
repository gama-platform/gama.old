/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.species;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.*;
import msi.gaml.statements.IStatement.WithArgs;
import msi.gaml.types.*;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 29 déc. 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractSpecies extends Symbol implements ISpecies {

	protected boolean isGrid;

	protected Map<String, ISpecies> microSpecies;

	// TODO remove -> not necessary: this.getMacroSpecies().getMicroSpecies().remove(this);
	private IList<ISpecies> peerSpecies;

	private IScope ownStack;

	private Map<String, IVariable> variables;

	private Map<String, AspectStatement> aspects;

	private Map<String, ActionStatement> actions;

	private Map<String, UserCommandStatement> userCommands;

	private IList<IStatement> behaviors;

	protected ISpecies macroSpecies;

	public AbstractSpecies(final IDescription description) {
		super(description);
		setName(description.getName());
		setOwnScope(GAMA.obtainNewScope());
		isGrid = description.getFacets().equals(IKeyword.KEYWORD, IKeyword.GRID);
	}

	@Override
	public IType type() {
		return Types.get(IType.SPECIES);
	}

	@Override
	public IList listValue(final IScope scope) throws GamaRuntimeException {
		IAgent a = scope.getAgentScope();
		if ( a == null ) { return GamaList.EMPTY_LIST; }
		IPopulation p = a.getPopulationFor(this);
		if ( p == null ) { return GamaList.EMPTY_LIST; }
		return p.getAgentsList();
	}

	@Override
	public String stringValue() {
		return name;
	}

	@Override
	public GamaMap mapValue(final IScope scope) throws GamaRuntimeException {
		IList<IAgent> agents;
		try {
			agents = scope.getAgentScope().getPopulationFor(this).getAgentsList();
		} catch (NullPointerException npe) {
			agents = GamaList.EMPTY_LIST;
		}

		// Default behavior : Returns a map containing the names of agents as keys and the
		// agents themselves as values
		final GamaMap result = new GamaMap();
		for ( IAgent agent : agents ) {
			result.put(agent.getName(), agent);
		}
		return result;
	}

	@Override
	public boolean isGrid() {
		return isGrid;
	}

	// @Override
	// public String toJava() {
	// return "SimulationManager.getFrontmostSimulation().getSpecies(" + name + ")";
	// }

	@Override
	public String toGaml() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public ISpecies copy() {
		return this;
		// Species are immutable
	}

	private void addOneMicroSpecies(final ISpecies oneMicroSpecies) {
		oneMicroSpecies.setMacroSpecies(this);
		microSpecies.put(oneMicroSpecies.getName(), oneMicroSpecies);
	}

	@Override
	public IList<ISpecies> getMicroSpecies() {
		IList<ISpecies> retVal = new GamaList<ISpecies>();
		retVal.addAll(microSpecies.values());

		ISpecies parentSpecies = this.getParentSpecies();
		if ( parentSpecies != null ) {
			retVal.addAll(parentSpecies.getMicroSpecies());
		}

		return retVal;
	}

	@Override
	public IList<String> getMicroSpeciesNames() {
		IList<String> retVal = new GamaList<String>();
		retVal.addAll(microSpecies.keySet());

		ISpecies parentSpecies = this.getParentSpecies();
		if ( parentSpecies != null ) {
			retVal.addAll(parentSpecies.getMicroSpeciesNames());
		}

		return retVal;
	}

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 * 
	 * @param microSpeciesName
	 * @return a species or null
	 */
	@Override
	public ISpecies getMicroSpecies(final String microSpeciesName) {
		ISpecies retVal = microSpecies.get(microSpeciesName);
		if ( retVal != null ) { return retVal; }

		ISpecies parentSpecies = this.getParentSpecies();
		if ( parentSpecies != null ) { return parentSpecies.getMicroSpecies(microSpeciesName); }

		return null;
	}

	/**
	 * @see ISpecies
	 */
	@Override
	public boolean containMicroSpecies(final ISpecies species) {
		ISpecies parentSpecies = this.getParentSpecies();
		return microSpecies.values().contains(species) ||
			(parentSpecies != null ? parentSpecies.containMicroSpecies(species) : false);
	}

	@Override
	public boolean hasMicroSpecies() {
		ISpecies parentSpecies = this.getParentSpecies();
		return !microSpecies.isEmpty() ||
			(parentSpecies != null ? parentSpecies.hasMicroSpecies() : false);
	}

	@Override
	public SpeciesDescription getDescription() {
		return (SpeciesDescription) description;
	}

	@Override
	public IList<ISpecies> getPeersSpecies() {
		if ( macroSpecies == null ) { return GamaList.EMPTY_LIST; }

		if ( peerSpecies == null ) {
			peerSpecies = new GamaList<ISpecies>(macroSpecies.getMicroSpecies());
			peerSpecies.remove(this);
		}

		return peerSpecies;
	}

	@Override
	public boolean isPeer(final ISpecies other) {
		return other != null && other.getMacroSpecies().equals(this.getMacroSpecies());
	}

	@Override
	public List<ISpecies> getSelfWithParents() {
		List<ISpecies> retVal = new GamaList<ISpecies>();
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
		SpeciesDescription parentSpecDesc = getDescription().getParentSpecies();
		if ( parentSpecDesc == null ) { return null; }

		ISpecies currentMacroSpec = this.getMacroSpecies();
		ISpecies potentialParent;
		while (currentMacroSpec != null) {
			potentialParent = currentMacroSpec.getMicroSpecies(parentSpecDesc.getName());
			if ( potentialParent != null ) { return potentialParent; }
			currentMacroSpec = currentMacroSpec.getMacroSpecies();
		}

		return null;

		/*
		 * if ( parentSpecies != null ) { return parentSpecies; }
		 * 
		 * SpeciesDescription parentSpecDesc = getDescription().getParentSpecies();
		 * if ( parentSpecDesc == null ) { return null; }
		 * 
		 * parentSpecies = findParentSpecies(parentSpecDesc);
		 * return parentSpecies;
		 */
	}

	@Override
	public ISpecies getPeerSpecies(final String peerName) {
		for ( ISpecies p : getPeersSpecies() ) {
			if ( p.getName().equals(peerName) ) { return p; }
		}

		return null;
	}

	@Override
	public int getLevel() {
		return this.getDescription().getLevel();
	}

	@Override
	public boolean equals(final Object other) {
		/**
		 * species name is unique
		 */
		if ( other instanceof ISpecies ) { return this.getName().equals(
			((ISpecies) other).getName()); }

		return false;
	}

	@Override
	protected void initFields() {
		super.initFields();
		microSpecies = new HashMap<String, ISpecies>();
		variables = new HashMap<String, IVariable>();
		actions = new HashMap<String, ActionStatement>();
		aspects = new HashMap<String, AspectStatement>();
		userCommands = new LinkedHashMap();
		behaviors = new GamaList<IStatement>();
	}

	@Override
	public IArchitecture getArchitecture() {
		return getDescription().getControl();
	}

	@Override
	public void addVariable(final IVariable v) {
		variables.put(v.getName(), v);
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
	public IList<String> getVarNames() {
		return getDescription().getVarNames();
	}

	@Override
	public Collection<IVariable> getVars() {
		return variables.values();
	}

	@Override
	public void addAction(final IStatement ce) {
		actions.put(ce.getName(), (ActionStatement) ce);
	}

	public void addUserCommand(final IStatement ce) {
		userCommands.put(ce.getName(), (UserCommandStatement) ce);
	}

	public Collection<UserCommandStatement> getUserCommands() {
		return userCommands.values();
	}

	@Override
	public WithArgs getAction(final String name) {
		return actions.get(name);
	}

	@Override
	public void addAspect(final IStatement ce) {
		aspects.put(ce.getName(), (AspectStatement) ce);
	}

	@Override
	public boolean hasAspect(final String n) {
		return aspects.containsKey(n);
	}

	@Override
	public IAspect getAspect(final String n) {
		return aspects.get(n);
	}

	@Override
	public IList<String> getAspectNames() {
		return new GamaList<String>(aspects.keySet());
	}

	@Override
	public void addBehavior(final IStatement c) {
		behaviors.add(c);
	}

	@Override
	public IList<IStatement> getBehaviors() {
		return behaviors;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( ISymbol s : children ) {
			addChild(s);
		}
		createControl();
	}

	@Override
	public void addChild(final ISymbol s) {
		if ( s instanceof ISpecies ) {
			addOneMicroSpecies((ISpecies) s);
		} else if ( s instanceof IVariable ) {
			addVariable((IVariable) s);
		} else if ( s instanceof AspectStatement ) {
			addAspect((AspectStatement) s);
		} else if ( s instanceof ActionStatement ) {
			addAction((ActionStatement) s);
		} else if ( s instanceof UserCommandStatement ) {
			addUserCommand((UserCommandStatement) s); // reflexes, states or tasks
		} else if ( s instanceof IStatement ) {
			addBehavior((IStatement) s); // reflexes, states or tasks
		}
	}

	private void createControl() {
		IArchitecture control = getArchitecture();
		List<IStatement> behaviors = getBehaviors();
		if ( control == null ) {
			this.error("The control of this species cannot be computed", IKeyword.CONTROL);
			return;
		}
		control.setChildren(behaviors);
		control.verifyBehaviors(this);
	}

	@Override
	public void dispose() {
		super.dispose();

		for ( IVariable v : variables.values() ) {
			v.dispose();
		}
		variables.clear();
		variables = null;

		for ( AspectStatement ac : aspects.values() ) {
			ac.dispose();
		}
		aspects.clear();
		aspects = null;

		for ( ActionStatement ac : actions.values() ) {
			ac.dispose();
		}
		actions.clear();

		for ( IStatement c : behaviors ) {
			c.dispose();
		}
		behaviors.clear();
		behaviors = null;

		macroSpecies = null;

		// TODO dispose micro_species first???
		microSpecies.clear();
		microSpecies = null;
	}

	protected void setOwnScope(final IScope ownStack) {
		this.ownStack = ownStack;
	}

	public IScope getOwnScope() {
		return ownStack;
	}

	@Override
	public IType getAgentType() {
		return ((SpeciesDescription) description).getType();
	}

	@Override
	public IAgentConstructor getAgentConstructor() {
		return ((SpeciesDescription) description).getAgentConstructor();
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
}