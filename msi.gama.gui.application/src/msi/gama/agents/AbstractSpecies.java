/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.agents;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.interfaces.ICommand.WithArgs;
import msi.gama.internal.compilation.IAgentConstructor;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.util.*;
import msi.gaml.commands.*;
import msi.gaml.control.IControl;

/**
 * Written by drogoul Modified on 29 déc. 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractSpecies extends Symbol implements ISpecies {

	protected boolean isGrid;

	protected Map<String, ISpecies> microSpecies;

	private boolean isCopy = false;

	protected ISpecies parentSpecies;

	private List<ISpecies> peerSpecies;

	private Map<String, ISpecies> visibleSpecies;

	private IScope ownStack;

	private Map<String, IVariable> variables;

	private Map<String, AspectCommand> aspects;

	private Map<String, ActionCommand> actions;

	private List<ICommand> behaviors;

	protected ISpecies macroSpecies;

	public AbstractSpecies(final IDescription description) {
		super(description);

		setName(description.getName());
		setOwnScope(GAMA.obtainNewScope());
		isGrid = description.getFacets().equals(ISymbol.KEYWORD, ISymbol.GRID);
		isCopy = ((SpeciesDescription) description).isCopy();
	}

	@Override
	public IType type() {
		return Types.get(IType.SPECIES);
	}

	@Override
	public GamaList listValue(final IScope scope) {
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
	public GamaMap mapValue(final IScope scope) {
		List<IAgent> agents;
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

	@Override
	public boolean isCopy() {
		return isCopy;
	}

	@Override
	public String toJava() {
		return "SimulationManager.getFrontmostSimulation().getSpecies(" + name + ")";
	}

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

	/**
	 * @return allPureMicroSpecies + delegations.
	 */
	@Override
	public List<ISpecies> getMicroSpecies() {
		List<ISpecies> retVal = new GamaList<ISpecies>();
		retVal.addAll(microSpecies.values());
		return retVal;
	}

	@Override
	public List<String> getMicroSpeciesNames() {
		List<String> retVal = new GamaList<String>();
		retVal.addAll(microSpecies.keySet());
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
		for ( ISpecies microSpec : microSpecies.values() ) {
			if ( microSpec.getName().equals(microSpeciesName) ) { return microSpec; }
		}

		return null;
	}

	/**
	 * @see ISpecies
	 */
	@Override
	public boolean containMicroSpecies(final ISpecies species) {
		return microSpecies.values().contains(species);
	}

	@Override
	public boolean hasMicroSpecies() {
		return !microSpecies.isEmpty();
	}

	@Override
	public SpeciesDescription getDescription() {
		return (SpeciesDescription) description;
	}

	@Override
	public List<ISpecies> getPeersSpecies() {
		if ( macroSpecies == null ) { return GamaList.EMPTY_LIST; }

		if ( peerSpecies == null ) {
			peerSpecies = new GamaList<ISpecies>();
			for ( ISpecies microSpec : macroSpecies.getMicroSpecies() ) {
				if ( !microSpec.equals(this) ) {
					peerSpecies.add(microSpec);
				}
			}
		}

		return peerSpecies;
	}

	/**
	 * Finds the corresponding ISpecies of a SpeciesDescription.
	 * 
	 * This method is invoked only by the "getParent()" method to find the parent species of this
	 * species.
	 * A species can be a sub species of
	 * either its peer species
	 * or one species situated higher in the species tree.
	 * 
	 * @param speciesDesc
	 * @return
	 */
	private ISpecies findParentSpecies(final SpeciesDescription speciesDesc) {
		getPeersSpecies();

		// Verify the peer species.
		for ( ISpecies p : peerSpecies ) {
			if ( p.getDescription().equals(speciesDesc) ) { return p; }
		}

		ISpecies macroSpecLevel = macroSpecies;
		while (macroSpecLevel != null) {
			// Verify the macro-species
			if ( macroSpecLevel.getDescription().equals(speciesDesc) ) { return macroSpecLevel; }

			// Verify the peers of macro-species
			List<ISpecies> macroLevelPeers = macroSpecLevel.getPeersSpecies();
			for ( ISpecies p : macroLevelPeers ) {
				if ( p.getDescription().equals(speciesDesc) ) { return p; }
			}

			macroSpecLevel = macroSpecLevel.getMacroSpecies();
		}

		return null;
	}

	@Override
	public ISpecies getParentSpecies() {
		if ( parentSpecies != null ) { return parentSpecies; }

		SpeciesDescription parentSpecDesc = getDescription().getParentSpecies();
		if ( parentSpecDesc == null ) { return null; }

		parentSpecies = findParentSpecies(parentSpecDesc);
		return parentSpecies;
	}

	@Override
	public ISpecies getPeerSpecies(final String peerName) {
		for ( ISpecies p : getPeersSpecies() ) {
			if ( p.getName().equals(peerName) ) { return p; }
		}

		return null;
	}

	@Override
	public Collection<ISpecies> getVisibleSpecies() {
		if ( visibleSpecies == null ) {
			visibleSpecies = new HashMap<String, ISpecies>();

			ISpecies currentSpecies = this;
			while (!currentSpecies.getName().equals(ISymbol.WORLD_SPECIES_NAME)) {
				for ( ISpecies microSpec : currentSpecies.getMicroSpecies() ) {
					visibleSpecies.put(microSpec.getName(), microSpec);
				}

				currentSpecies = currentSpecies.getMacroSpecies();
			}

			visibleSpecies.put(currentSpecies.getName(), currentSpecies);
		}

		return visibleSpecies.values();
	}

	@Override
	public ISpecies getVisibleSpecies(final String speciesName) {
		;
		getVisibleSpecies();
		return visibleSpecies.get(speciesName);
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
		actions = new HashMap<String, ActionCommand>();
		aspects = new HashMap<String, AspectCommand>();
		behaviors = new GamaList<ICommand>();
	}

	@Override
	public IControl getControl() {
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
	public List<String> getVarNames() {
		return getDescription().getVarNames();
	}

	@Override
	public Collection<IVariable> getVars() {
		return variables.values();
	}

	@Override
	public void addAction(final ActionCommand ce) {
		actions.put(ce.getName(), ce);
	}

	@Override
	public WithArgs getAction(final String name) {
		return actions.get(name);
	}

	@Override
	public void addAspect(final AspectCommand ce) {
		aspects.put(ce.getName(), ce);
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
	public List<String> getAspectNames() {
		return new GamaList<String>(aspects.keySet());
	}

	@Override
	public void addBehavior(final ICommand c) {
		behaviors.add(c);
	}

	@Override
	public List<ICommand> getBehaviors() {
		return behaviors;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {
		for ( ISymbol s : commands ) {
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
		} else if ( s instanceof AspectCommand ) {
			addAspect((AspectCommand) s);
		} else if ( s instanceof ActionCommand ) {
			addAction((ActionCommand) s);
		} else if ( s instanceof ICommand ) {
			addBehavior((ICommand) s); // reflexes, states or tasks
		}
	}

	private void createControl() {
		IControl control = getControl();
		List<ICommand> behaviors = getBehaviors();
		try {
			control.setChildren(behaviors);
			control.verifyBehaviors(this);
		} catch (GamlException e) {
			e.printStackTrace();
			control = null;
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		for ( IVariable v : variables.values() ) {
			v.dispose();
		}
		variables.clear();
		variables = null;

		for ( AspectCommand ac : aspects.values() ) {
			ac.dispose();
		}
		aspects.clear();
		aspects = null;

		for ( ActionCommand ac : actions.values() ) {
			ac.dispose();
		}
		actions.clear();

		for ( ICommand c : behaviors ) {
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
		return ((ExecutionContextDescription) description).getType();
	}

	@Override
	public IAgentConstructor getAgentConstructor() {
		return ((ExecutionContextDescription) description).getAgentConstructor();
	}

	@Override
	public ISpecies getMacroSpecies() {
		return macroSpecies;
	}

	@Override
	public void setMacroSpecies(final ISpecies macroSpecies) {
		this.macroSpecies = macroSpecies;
	}
}