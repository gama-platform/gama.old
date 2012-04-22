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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.util.GamaList;
import msi.gaml.commands.Facets;
import msi.gaml.factories.ModelFactory;

public class SpeciesDescription extends ExecutionContextDescription {

	/**
	 * Micro-species of a species includes species explicitly declared inside it
	 * and micro-species of its parent.
	 * 
	 * The following map contains micro-species explicitly declared inside this species.
	 */
	private Map<String, SpeciesDescription> microSpecies;

	private List<CommandDescription> inits;

	private boolean isSuper = false;

	//
	/** A list of user redefined or new variables compare to the parent species. */
	private Set<String> userRedefinedOrNewVars;

	public SpeciesDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final Class base,
		final ISyntacticElement source, final SymbolMetaDescription md) {
		super(keyword, superDesc, facets, children, source, md);
		setSkills(facets.get(IKeyword.SKILLS));
		setJavaBase(base);
	}

	@Override
	protected void initFields() {
		super.initFields();
		inits = new ArrayList<CommandDescription>();
		microSpecies = new HashMap<String, SpeciesDescription>();
	}

	@Override
	public IDescription addChild(final IDescription child) {
		IDescription desc = super.addChild(child);
		if ( desc.getKeyword().equals(IKeyword.INIT) ) {
			addInit((CommandDescription) desc);
		} else if ( ModelFactory.SPECIES_NODES.contains(desc.getKeyword()) ) {
			this.getModelDescription().addType((SpeciesDescription) desc);
			microSpecies.put(desc.getName(), (SpeciesDescription) desc);
		}

		return desc;
	}

	private void addInit(final CommandDescription init) {
		inits.add(0, init); // Added at the beginning
	}

	/**
	 * Returns all the direct&in-direct micro-species of this species.
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getAllMicroSpecies() {
		List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		retVal.addAll(microSpecies.values());

		for ( SpeciesDescription micro : microSpecies.values() ) {
			retVal.addAll(micro.getAllMicroSpecies());
		}

		return retVal;
	}

	// /**
	// *
	// *
	// * @return
	// */
	// private void fillParentTrace(final List<SpeciesDescription> parentTrace) {
	// if ( !parentTrace.contains(this) ) {
	// parentTrace.add(this);
	//
	// SpeciesDescription parentSpec = this.getParentSpecies();
	// if ( parentSpec != null ) {
	// parentSpec.fillParentTrace(parentTrace);
	// }
	//
	// for ( SpeciesDescription micro : microSpecies.values() ) {
	// micro.fillParentTrace(parentTrace);
	// }
	// }
	// }

	/**
	 * Verifies if the specified species can be a parent of this species.
	 * 
	 * A species can be parent of other if the following conditions are hold
	 * 1. A parent species is visible to the sub-species.
	 * 2. A species can' be a sub-species of itself.
	 * 3. 2 species can't be parent of each other.
	 * 5. A species can't be a sub-species of its direct/in-direct micro-species.
	 * 6. A species and its direct/indirect micro/macro-species can't share one/some direct/indirect
	 * parent-species having micro-species.
	 * 7. The inheritance between species from different branches doesn't form a "circular"
	 * inheritance.
	 * 
	 * @param parentName the name of the potential parent
	 * @throws GamlException if the species with the specified name can not be a parent of this
	 *             species.
	 */
	private SpeciesDescription verifyParent(final String parentName) {

		if ( this.getName().equals(parentName) ) {
			flagError(getName() + " species can't be a sub-species of itself");
			return null;
		}

		SpeciesDescription potentialParent = findPotentialParent(parentName);
		if ( potentialParent == null ) {

			List<SpeciesDescription> potentialParents = this.getPotentialParentSpecies();

			List<String> availableSpecies = new GamaList<String>();

			for ( SpeciesDescription p : potentialParents ) {
				availableSpecies.add(p.getName());
				availableSpecies.add(", ");
			}
			availableSpecies.remove(availableSpecies.size() - 1);
			StringBuffer availableSpecsStr = new StringBuffer();
			availableSpecsStr.append("[");
			for ( String s : availableSpecies ) {
				availableSpecsStr.append(s);
			}
			availableSpecsStr.append("]");
			flagError(parentName + " can't be a parent-species of " + this.getName() +
				" species. Available parent species are: " + availableSpecsStr.toString());

			return null;
		}

		List<SpeciesDescription> parentsOfParent = potentialParent.getSelfWithParents();
		if ( parentsOfParent.contains(this) ) {
			flagError(this.getName() + " species and " + potentialParent.getName() +
				" species can't be sub-species of each other.");
			return null;
		}

		//
		// if ( this.getAllMacroSpecies().contains(parentSpec) ) {
		// flagError(this.getName() + " species can't be a sub-species of " +
		// parentSpec.getName() +
		// " species because a species can't be sub-species of its direct or indirect macro-species.");
		// }

		if ( this.getAllMicroSpecies().contains(parentsOfParent) ) {
			flagError(this.getName() + " species can't be a sub-species of " +
				potentialParent.getName() +
				" species because a species can't be sub-species of its direct or indirect micro-species.");
			return null;
		}
		//
		// List<SpeciesDescription> allMacroSpecies = this.getAllMacroSpecies();
		// List<SpeciesDescription> parentsOfMacro;
		// for ( SpeciesDescription macro : allMacroSpecies ) {
		// parentsOfMacro = macro.getSelfWithParents();
		// parentsOfMacro.remove(macro);
		//
		// parentsOfMacro.retainAll(parentsOfParent);
		//
		// List<SpeciesDescription> sharedParents = new GamaList<SpeciesDescription>();
		// sharedParents.addAll(parentsOfMacro);
		// sharedParents.retainAll(parentsOfParent);
		//
		// if ( !sharedParents.isEmpty() ) {
		// SpeciesDescription parentOfMacro = macro.getParentSpecies();
		//
		// List<String> microSpecsNames = new GamaList<String>();
		//
		// for ( SpeciesDescription sParent : sharedParents ) {
		// for ( SpeciesDescription inheritedMicro : sParent.getMicroSpecies() ) {
		// microSpecsNames.add(inheritedMicro.getName());
		// microSpecsNames.add(", ");
		// }
		// }
		//
		// if ( !microSpecsNames.isEmpty() ) {
		// microSpecsNames.remove(microSpecsNames.size() - 1);
		//
		// StringBuffer microSpecsStr = new StringBuffer();
		// microSpecsStr.append("[");
		// for ( String msN : microSpecsNames ) {
		// microSpecsStr.append(msN);
		// }
		// microSpecsStr.append("]");
		//
		// String message;
		// if ( parentSpec.equals(parentOfMacro) ) {
		// message =
		// new String(this.getName() + " and " + macro.getName() +
		// " species can't share " + parentSpec.getName() +
		// " as parent-species because ");
		// } else {
		// message =
		// new String(this.getName() + " and " + macro.getName() +
		// " species can't have " + parentSpec.getName() + " and " +
		// parentOfMacro.getName() + " as parent-species because ");
		// }
		//
		// flagError(message + "\n\t1. " + this.getName() + " and " + macro.getName() +
		// " have micro-macro species relationship;" +
		// "\n\t2. They will both inherit the micro-species " + microSpecsStr +
		// " which will ambiguate the reference to " + microSpecsStr +
		// " species in the context of " + this.getName() + " species.");
		// }
		// }
		// }
		//
		// List<SpeciesDescription> parentTrace = new GamaList<SpeciesDescription>();
		// parentSpec.fillParentTrace(parentTrace);
		// if ( parentTrace.contains(this) ) {
		// flagError(this.getName() + " species can't be a sub-species of " +
		// parentSpec.getName() +
		// " species because this forms a circular inheritance between species of different branches.");
		// }
		//
		return potentialParent;
	}

	@Override
	protected void copyItemsFromParent() {
		SpeciesDescription parent = getParentSpecies();

		if ( parent != null ) {
			if ( !parent.javaBase.isAssignableFrom(javaBase) ) {
				if ( javaBase == GamlAgent.class ) { // default base class
					javaBase = parent.javaBase;
					agentConstructor = parent.agentConstructor;
				} else {
					flagError("Species " + getName() + " Java base class (" +
						javaBase.getSimpleName() + ") is not a subclass of its parent species " +
						parent.getName() + " base class (" + parent.getJavaBase().getSimpleName() +
						")");
				}
			}
			skillsClasses.addAll(parent.skillsClasses);
			skillsMethods.putAll(parent.skillsMethods);

			// We only copy the behaviors that are not redefined in this species
			for ( final CommandDescription b : parent.behaviors.values() ) {
				if ( !hasBehavior(b.getName()) ) {
					addChild(b);
				}
			}

			for ( final CommandDescription init : parent.inits ) {
				addChild(init);
			}

			// We only copy the actions that are not redefined in this species
			for ( final String aName : parent.actions.keySet() ) {
				if ( !hasAction(aName) ) {
					CommandDescription action = parent.actions.get(aName);
					if ( action.isAbstract() ) {
						this.flagWarning("Abstract action '" + aName +
							"', which is inherited from " + parent.getName() +
							", should be redefined.");
					}
					addChild(parent.actions.get(aName));
				}
			}

			for ( final String aName : parent.aspects.keySet() ) {
				// if ( aName.equals(ISymbol.DEFAULT) || !hasAspect(aName) ) {
				if ( !hasAspect(aName) ) {
					addChild(parent.aspects.get(aName));
				}
			}

			// We only copy the variables that are not redefined in this species
			for ( final VariableDescription v : parent.variables.values() ) {
				if ( v.isBuiltIn() ) {
					final VariableDescription var = getVariable(v.getName());
					if ( var == null ) { // || ! isUserDefined ???
						addChild(v);
					}
				} else if ( !hasVar(v.getName()) ) {
					addChild(v);
				}
			}
		}
		sortVars();

	}

	public boolean isArgOf(final String op, final String arg) {
		if ( hasAction(op) ) { return actions.get(op).containsArg(arg); }
		return false;
	}

	/**
	 * @return
	 */
	@Override
	public String getParentName() {
		return facets.getLabel(IKeyword.PARENT);
	}

	public void verifyAndSetParent() {
		String parentName = getParentName();
		if ( parentName == null ) { return; }
		parentSpecies = verifyParent(parentName);;
	}

	/**
	 * Returns the parent species.
	 * 
	 * @return
	 */
	public SpeciesDescription getParentSpecies() {
		return parentSpecies;
	}

	/**
	 * @return
	 */
	public List<SpeciesDescription> getSelfWithParents() {

		// returns a reversed list of parents + self
		List<SpeciesDescription> result = new GamaList<SpeciesDescription>();
		SpeciesDescription currentSpeciesDesc = this;
		while (currentSpeciesDesc != null) {
			result.add(0, currentSpeciesDesc);
			currentSpeciesDesc = currentSpeciesDesc.getParentSpecies();
		}

		return result;
	}

	// public void complementWith(final SpeciesDescription sd) {
	// // addChildren(sd.getChildren());
	// skillsClasses.addAll(sd.skillsClasses);
	// skillsMethods.putAll(sd.skillsMethods);
	// if ( agentConstructor == null ) {
	// agentConstructor = sd.agentConstructor;
	// }
	// if ( javaBase == null ) {
	// javaBase = sd.javaBase;
	// }
	// if ( getSource() == null ) {
	// setSource(sd.getSource());
	// }
	//
	// // We only copy the behaviors that are not redefined in this species
	// for ( final CommandDescription b : sd.behaviors.values() ) {
	// if ( !hasBehavior(b.getName()) ) {
	// addChild(b);
	// }
	// }
	//
	// for ( final CommandDescription init : sd.inits ) {
	// addChild(init);
	// }
	//
	// // We only copy the actions that are not redefined in this species
	// for ( final String aName : sd.actions.keySet() ) {
	// if ( !hasAction(aName) ) {
	// addChild(sd.actions.get(aName));
	// }
	// }
	//
	// for ( final String aName : sd.aspects.keySet() ) {
	// // if ( aName.equals(ISymbol.DEFAULT) || !hasAspect(aName) ) {
	// if ( !hasAspect(aName) ) {
	// addChild(sd.aspects.get(aName));
	// }
	// }
	//
	// // We only copy the variables that are not redefined in this species
	// for ( final VariableDescription v : sd.variables.values() ) {
	// if ( v.isBuiltIn() ) {
	// final VariableDescription var = getVariable(v.getName());
	// if ( var == null ) { // || ! isUserDefined ???
	// addChild(v);
	// }
	// } else if ( !hasVar(v.getName()) ) {
	// addChild(v);
	// }
	// }
	//
	// sortVars();
	//
	// }

	public Map<String, Class> getSkillsMethods() {
		return skillsMethods;
	}

	public boolean isSuperSpecies() {
		return isSuper;
	}

	public void setIsSuperSpecies() {
		isSuper = true;
	}

	@Override
	protected boolean hasAspect(final String a) {
		return aspects.containsKey(a);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return this;
	}

	/**
	 * Returns all the direct micro-species of this species.
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getMicroSpecies() {
		GamaList<SpeciesDescription> retVal =
			new GamaList<SpeciesDescription>(microSpecies.values());
		if ( parentSpecies != null ) {
			retVal.addAll(parentSpecies.getMicroSpecies());
		}

		return retVal;
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		SpeciesDescription retVal = microSpecies.get(name);
		if ( retVal != null ) { return retVal; }

		if ( this.parentSpecies != null ) { return parentSpecies.getMicroSpecies(name); }
		return null;
	}

	// /**
	// * Returns all the species sharing the same direct macro-species with this species.
	// *
	// * @return
	// */
	// public List<SpeciesDescription> getPeerSpecies() {
	// if ( macroSpecies == null ) { return GamaList.EMPTY_LIST; }
	//
	// if ( peerSpecies == null ) {
	// peerSpecies = new GamaList<SpeciesDescription>();
	// peerSpecies.addAll(macroSpecies.getMicroSpecies());
	// peerSpecies.remove(this);
	// }
	//
	// return peerSpecies;
	// }

	//
	// /**
	// * Returns the peer species with the specified name.
	// *
	// * @param peerName
	// * @return
	// */
	// public SpeciesDescription getPeerSpecies(final String peerName) {
	// if ( macroSpecies == null ) { return null; }
	//
	// if ( peerSpecies == null ) {
	// getPeerSpecies();
	// }
	//
	// for ( SpeciesDescription sd : peerSpecies ) {
	// if ( sd.getName().equals(peerName) ) { return sd; }
	// }
	//
	// return null;
	// }

	//
	// /**
	// * Finds and returns a species situated higher in the species hierarchy.
	// *
	// * @param macroSpeciesName
	// * @return
	// */
	// public SpeciesDescription getMacroSpeciesContext(final String macroSpeciesName) {
	// if ( macroSpecies == null ) { return null; }
	//
	// if ( macroSpecies.getName().equals(macroSpeciesName) ) { return macroSpecies; }
	//
	// SpeciesDescription peerOfMacro = macroSpecies.getPeerSpecies(macroSpeciesName);
	// if ( peerOfMacro != null ) { return peerOfMacro; }
	//
	// return macroSpecies.getMacroSpeciesContext(macroSpeciesName);
	// }

	/**
	 * Finalizes the species description
	 * + Copy the behaviors, attributes from parent;
	 * + Creates the control if necessary.
	 * 
	 * @throws GamlException
	 */
	@Override
	public void finalizeDescription() {
		super.finalizeDescription();

		// recursively finalize the sorted micro-species
		for ( SpeciesDescription microSpec : sortedMicroSpecies() ) {
			microSpec.finalizeDescription();
		}
	}

	/**
	 * Returns a list of visible species from this species.
	 * 
	 * A species can see the following species:
	 * 1. Its direct micro-species.
	 * 2. Its peer species.
	 * 3. Its direct&in-direct macro-species and their peers.
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getVisibleSpecies() {
		List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();

		SpeciesDescription currentSpec = this;
		while (currentSpec != null) {
			retVal.addAll(currentSpec.getMicroSpecies());

			// "world" species
			if ( currentSpec.getMacroSpecies() == null ) {
				retVal.add(currentSpec);
			}

			currentSpec = currentSpec.getMacroSpecies();
		}

		return retVal;
	}

	/**
	 * Returns a visible species from the view point of this species.
	 * If the visible species list contains a species with the specified name.
	 * 
	 * @param speciesName
	 */
	public SpeciesDescription getVisibleSpecies(final String speciesName) {
		for ( SpeciesDescription visibleSpec : getVisibleSpecies() ) {
			if ( visibleSpec.getName().equals(speciesName) ) { return visibleSpec; }
		}

		return null;
	}

	/*
	 * @Override
	 * public IDescription getSpeciesDescription(final String actualSpecies) {
	 * 
	 * }
	 */

	/**
	 * Returns a list of SpeciesDescription that can be the parent of this species.
	 * A species can be a sub-species of its "peer" species ("peer" species are species sharing the
	 * same direct macro-species).
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getPotentialParentSpecies() {
		List<SpeciesDescription> retVal = getVisibleSpecies();
		retVal.removeAll(this.getMicroSpecies());
		retVal.remove(this);

		return retVal;
	}

	private SpeciesDescription findPotentialParent(final String parentName) {
		List<SpeciesDescription> candidates = this.getPotentialParentSpecies();
		for ( SpeciesDescription c : candidates ) {
			if ( c.getName().equals(parentName) ) { return c; }
		}

		return null;
	}

	/**
	 * Sorts the micro-species.
	 * Parent micro-species are ahead of the list followed by sub micro-species.
	 * 
	 * @return
	 */
	private List<SpeciesDescription> sortedMicroSpecies() {

		Collection<SpeciesDescription> allMicroSpecies = microSpecies.values();
		// validate and set the parent parent of each micro-species
		for ( SpeciesDescription microSpec : allMicroSpecies ) {
			microSpec.verifyAndSetParent();
		}

		List<SpeciesDescription> sortedMicroSpecs = new GamaList<SpeciesDescription>();
		for ( SpeciesDescription microSpec : allMicroSpecies ) {
			List<SpeciesDescription> parents = microSpec.getSelfWithParents();

			for ( SpeciesDescription p : parents ) {
				if ( !sortedMicroSpecs.contains(p) && allMicroSpecies.contains(p) ) {
					sortedMicroSpecs.add(p);
				}
			}
		}

		return sortedMicroSpecs;
	}

	//
	// private List<IDescription> getChildrenWithoutMicroSpec(final SpeciesDescription specDesc) {
	// List<IDescription> retVal = new GamaList<IDescription>();
	// retVal.addAll(specDesc.getChildren());
	//
	// for ( SpeciesDescription microSpec : specDesc.getMicroSpecies() ) {
	// retVal.remove(microSpec);
	// }
	//
	// return retVal;
	// }

	public void setUserRedefinedAndNewVars(final Set<String> userRedefinedOrNewVars) {
		this.userRedefinedOrNewVars = userRedefinedOrNewVars;
	}

	/**
	 * Return a set of user re-defined or new variable names of the species compare to the parent
	 * species.
	 * 
	 * @return
	 */
	public Set<String> userRedefinedOrNewVars() {
		return userRedefinedOrNewVars;
	}

	public boolean isGrid() {
		return getKeyword().equals(IKeyword.GRID);
	}
}
