/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.descriptions;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.Facets;
import msi.gama.kernel.ModelFileManager;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.util.GamaList;

public class SpeciesDescription extends ExecutionContextDescription {

	/** Micro-species of a species. */
	private Map<String, SpeciesDescription> microSpecies;

	private List<CommandDescription> inits;

	private boolean isSuper = false;

	/** peer species are species sharing the same direct macro-species */
	private List<SpeciesDescription> peerSpecies;

	/** A list of user redefined or new variables compare to the parent species. */
	private Set<String> userRedefinedOrNewVars;

	public SpeciesDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final Class base,
		final ISyntacticElement source) throws GamlException {
		super(keyword, superDesc, facets, children, source);
		setSkills(facets.getTokens(ISpecies.SKILLS));
		setJavaBase(base);
	}

	/**
	 * This constructor is used to copy a species and make it be the micro-species of another
	 * species.
	 * 
	 * @param originalSpecies The species to copy.
	 * @param childrenWithoutMicroSpecies The children of the copied species are children of the
	 *            original species excluding the micro-species cause they will be recursively added
	 *            later on.
	 * @param newMacroSpecies The macro-species of the copied species.
	 * @throws GamlException
	 */
	public SpeciesDescription(final SpeciesDescription originalSpecies,
		final List<IDescription> childrenWithoutMicroSpecies,
		final SpeciesDescription newMacroSpecies) throws GamlException {

		super(originalSpecies.getKeyword(), newMacroSpecies, originalSpecies.getFacets(),
			childrenWithoutMicroSpecies, originalSpecies.getSourceInformation());
		isCopy = true;
		skillsClasses.addAll(originalSpecies.skillsClasses);
		skillsMethods.putAll(originalSpecies.skillsMethods);
		javaBase = originalSpecies.javaBase;
		agentConstructor = originalSpecies.agentConstructor;
		control = originalSpecies.control;

		skillInstancesByClass.putAll(originalSpecies.skillInstancesByClass);
		skillInstancesByMethod.putAll(originalSpecies.skillInstancesByMethod);
		sortedVariableNames.addAll(originalSpecies.sortedVariableNames);
		updatableVariableNames.addAll(originalSpecies.updatableVariableNames);
		setUserRedefinedAndNewVars(originalSpecies.userRedefinedOrNewVars);

		// recursively copy micro-species
		for ( SpeciesDescription microSpec : originalSpecies.sortedMicroSpecies() ) {
			this.addMicroSpecies(new SpeciesDescription(microSpec,
				getChildrenWithoutMicroSpec(microSpec), this));
		}
	}

	@Override
	protected void initFields() {
		super.initFields();

		inits = new ArrayList<CommandDescription>();
		microSpecies = new HashMap<String, SpeciesDescription>();
	}

	@Override
	public SpeciesDescription shallowCopy(final IDescription superDesc) throws GamlException {
		return this; // SpeciesDescription sd =
		// new SpeciesDescription(getKeyword(), superDesc, facets, children, javaBase, source);
		// return sd;
	}

	@Override
	public IDescription addChild(final IDescription child) throws GamlException {
		IDescription desc = super.addChild(child);

		if ( desc.getKeyword().equals(ISymbol.INIT) ) {
			addInit((CommandDescription) desc);
		} else if ( ModelFileManager.SPECIES_NODES.contains(desc.getKeyword()) ) {

			/*
			 * if ( microSpecies.containsKey(child.getName()) ) { // TODO remove this!!! why do we
			 * need this?
			 * SpeciesDescription partial = microSpecies.get(child.getName());
			 * partial.complementWith((SpeciesDescription) child);
			 * return partial;
			 * }
			 */
			addMicroSpecies((SpeciesDescription) desc);
		}

		return desc;
	}

	@Override
	protected void copyChildren(final List<IDescription> originalChildren) throws GamlException {
		// if ( type == null ) {
		// type = getTypeOf(getName());
		// Necessary to fix the type before adding children...
		// }
		super.copyChildren(originalChildren);
	}

	private void addInit(final CommandDescription init) {
		inits.add(0, init); // Added at the beginning
	}

	/**
	 * Adds a micro-species.
	 * 
	 * A micro-species may be declared in this species or may be inherited from the parent species.
	 * If the micro-species is declared in this species, then we need to add it to the type manager.
	 * Otherwise, if the micro-species is inherited from the parent species, then it has already
	 * added
	 * to the type manager by the species in which it is declared. Thus we don't need to re-add it
	 * to the type manager.
	 * 
	 * @param microSpec
	 * @throws GamlException
	 */
	private void addMicroSpecies(final SpeciesDescription microSpec) throws GamlException {

		// this micro-species is inherited from the parent species, so we don't need to re-add it to
		// the type manager.
		if ( !microSpec.isCopy ) {
			this.getModelDescription().addType(microSpec);
		}
		microSpecies.put(microSpec.getName(), microSpec);
	}

	/**
	 * Returns all the direct&in-direct macro-species of this species.
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getAllMacroSpecies() {
		List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		SpeciesDescription macro = this.macroSpecies;
		while (macro != null) {
			retVal.add(macro);
			macro = macro.macroSpecies;
		}

		return retVal;
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

	/**
	 * 
	 * 
	 * @return
	 */
	private void fillParentTrace(final List<SpeciesDescription> parentTrace) {
		if ( !parentTrace.contains(this) ) {
			parentTrace.add(this);

			SpeciesDescription parentSpec = this.getParentSpecies();
			if ( parentSpec != null ) {
				parentSpec.fillParentTrace(parentTrace);
			}

			for ( SpeciesDescription micro : microSpecies.values() ) {
				micro.fillParentTrace(parentTrace);
			}
		}
	}

	/**
	 * Verifies if the specified species can be a parent of this species.
	 * 
	 * A species can be parent of other if the following conditions are hold
	 * 1. A parent species is visible to the sub-species.
	 * 2. A species can' be a sub-species of itself.
	 * 3. 2 species can't be parent of each other.
	 * 4. A species can't be a sub-species of its direct/in-direct macro-species.
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
	private SpeciesDescription verifyParent(final String parentName) throws GamlException {
		// TODO catch this method to avoid being run 2 times (use a boolean for example)
		// TODO how to make these validations available at the IDE level?

		SpeciesDescription parentSpec = this.getVisibleSpecies(parentName);
		if ( parentSpec == null ) { throw new GamlException(getName() +
			" species can't be a sub-species of " + parentName + " species because " + parentName +
			" is not defined or is not visible to " + getName() + " species.",
			this.getSourceInformation()); }

		if ( parentSpec.equals(this) ) { throw new GamlException(getName() +
			" species can't be a sub-species of itself", this.getSourceInformation()); }

		List<SpeciesDescription> parentsOfParent = parentSpec.getSelfWithParents();
		if ( parentsOfParent.contains(this) ) { throw new GamlException(
			this.getName() + " species and " + parentSpec.getName() +
				" species can't be sub-species of each other.", this.getSourceInformation()); }

		if ( this.getAllMacroSpecies().contains(parentSpec) ) { throw new GamlException(
			this.getName() + " species can't be a sub-species of " + parentSpec.getName() +
				" species because a species can't be sub-species of its direct or indirect macro-species.",
			this.getSourceInformation()); }

		// TODO test this with copied-micro-species!!!
		if ( this.getAllMicroSpecies().contains(parentSpec) ) { throw new GamlException(
			this.getName() + " species can't be a sub-species of " + parentSpec.getName() +
				" species because a species can't be sub-species of its direct or indirect micro-species.",
			this.getSourceInformation()); }

		List<SpeciesDescription> allMacroSpecies = this.getAllMacroSpecies();
		List<SpeciesDescription> parentsOfMacro;
		for ( SpeciesDescription macro : allMacroSpecies ) {
			parentsOfMacro = macro.getSelfWithParents();
			parentsOfMacro.remove(macro);

			parentsOfMacro.retainAll(parentsOfParent);

			List<SpeciesDescription> sharedParents = new GamaList<SpeciesDescription>();
			sharedParents.addAll(parentsOfMacro);
			sharedParents.retainAll(parentsOfParent);

			if ( !sharedParents.isEmpty() ) {
				SpeciesDescription parentOfMacro = macro.getParentSpecies();

				List<String> microSpecsNames = new GamaList<String>();

				for ( SpeciesDescription sParent : sharedParents ) {
					for ( SpeciesDescription inheritedMicro : sParent.getMicroSpecies() ) {
						microSpecsNames.add(inheritedMicro.getName());
						microSpecsNames.add(", ");
					}
				}

				if ( !microSpecsNames.isEmpty() ) {
					microSpecsNames.remove(microSpecsNames.size() - 1);

					StringBuffer microSpecsStr = new StringBuffer();
					microSpecsStr.append("[");
					for ( String msN : microSpecsNames ) {
						microSpecsStr.append(msN);
					}
					microSpecsStr.append("]");

					String message;
					if ( parentSpec.equals(parentOfMacro) ) {
						message =
							new String(this.getName() + " and " + macro.getName() +
								" species can't share " + parentSpec.getName() +
								" as parent-species because ");
					} else {
						message =
							new String(this.getName() + " and " + macro.getName() +
								" species can't have " + parentSpec.getName() + " and " +
								parentOfMacro.getName() + " as parent-species because ");
					}

					throw new GamlException(message + "\n\t1. " + this.getName() + " and " +
						macro.getName() + " have micro-macro species relationship;" +
						"\n\t2. They will both inherit the micro-species " + microSpecsStr +
						" which will ambiguate the reference to " + microSpecsStr +
						" species in the context of " + this.getName() + " species.",
						parentSpec.getSourceInformation());
				}
			}
		}

		List<SpeciesDescription> parentTrace = new GamaList<SpeciesDescription>();
		parentSpec.fillParentTrace(parentTrace);
		if ( parentTrace.contains(this) ) { throw new GamlException(
			this.getName() + " species can't be a sub-species of " + parentSpec.getName() +
				" species because this forms a circular inheritance between species of different branches.",
			this.getSourceInformation()); }

		return parentSpec;
	}

	@Override
	protected void copyItemsFromParent() throws GamlException {
		SpeciesDescription parent = getParentSpecies();

		if ( parent != null ) {
			skillsClasses.addAll(parent.skillsClasses);
			skillsMethods.putAll(parent.skillsMethods);

			// We only copy the reflexes that are not redefined in this species
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
				if ( v.isBuiltIn() && v.isUserDefined() ) {
					final VariableDescription var = getVariable(v.getName());
					if ( var == null || !var.isUserDefined() ) {
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
		return facets.getString(ISpecies.PARENT);
	}

	public void verifyAndSetParent() throws GamlException {
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

	public void complementWith(final SpeciesDescription sd) throws GamlException {
		addChildren(sd.getChildren());
		skillsClasses.addAll(sd.skillsClasses);
		skillsMethods.putAll(sd.skillsMethods);
		if ( agentConstructor == null ) {
			agentConstructor = sd.agentConstructor;
		}
		if ( javaBase == null ) {
			javaBase = sd.javaBase;
		}
		if ( source == null ) {
			source = sd.source;
		}
	}

	public Map<String, Class> getSkillsMethods() {
		return skillsMethods;
	}

	public boolean isSuperSpecies() {
		return isSuper;
	}

	/**
	 * Indicates that this species is copied from the parent species or not.
	 * 
	 * @return
	 */
	public boolean isCopy() {
		return isCopy;
	}

	public void setIsSuperSpecies() {
		isSuper = true;
	}

	@Override
	protected boolean hasAspect(final String a) {
		return aspects.containsKey(a);
		// return a.equals(ISymbol.DEFAULT) || aspects.containsKey(a);
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
		return new GamaList<SpeciesDescription>(microSpecies.values());
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		return microSpecies.get(name);
	}

	/**
	 * Returns all the species sharing the same direct macro-species with this species.
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getPeerSpecies() {
		if ( macroSpecies == null ) { return GamaList.EMPTY_LIST; }

		if ( peerSpecies == null ) {
			peerSpecies = new GamaList<SpeciesDescription>();
			peerSpecies.addAll(macroSpecies.getMicroSpecies());
			peerSpecies.remove(this);
		}

		return peerSpecies;
	}

	/**
	 * Returns the peer species with the specified name.
	 * 
	 * @param peerName
	 * @return
	 */
	public SpeciesDescription getPeerSpecies(final String peerName) {
		if ( macroSpecies == null ) { return null; }

		if ( peerSpecies == null ) {
			getPeerSpecies();
		}

		for ( SpeciesDescription sd : peerSpecies ) {
			if ( sd.getName().equals(peerName) ) { return sd; }
		}

		return null;
	}

	/**
	 * Finds and returns a species situated higher in the species hierarchy.
	 * 
	 * @param macroSpeciesName
	 * @return
	 */
	public SpeciesDescription getMacroSpeciesContext(final String macroSpeciesName) {
		if ( macroSpecies == null ) { return null; }

		if ( macroSpecies.getName().equals(macroSpeciesName) ) { return macroSpecies; }

		SpeciesDescription peerOfMacro = macroSpecies.getPeerSpecies(macroSpeciesName);
		if ( peerOfMacro != null ) { return peerOfMacro; }

		return macroSpecies.getMacroSpeciesContext(macroSpeciesName);
	}

	/**
	 * Finalizes the species description
	 * + Copy the behaviors, attributes from parent;
	 * + Creates the control if necessary.
	 * 
	 * @throws GamlException
	 */
	@Override
	public void finalizeDescription() throws GamlException {
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

	/**
	 * Sorts the micro-species.
	 * Parent micro-species are ahead of the list followed by sub micro-species.
	 * 
	 * @return
	 */
	private List<SpeciesDescription> sortedMicroSpecies() throws GamlException {

		Collection<SpeciesDescription> allMicroSpecies = microSpecies.values();
		// validate and set the parent parent of each micro-species
		for ( SpeciesDescription microSpec : allMicroSpecies ) {
			microSpec.verifyAndSetParent();
		}

		List<SpeciesDescription> sortedMicroSpecs = new GamaList<SpeciesDescription>();;
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

	/**
	 * Inheritance of micro-species from the parent-species.
	 */
	public void inheritMicroSpecies() throws GamlException {
		if ( parentSpecies != null ) {

			// copy the micro-species from the parent species.
			SpeciesDescription copiedSpec;
			List<SpeciesDescription> copiedMicroSpecs = parentSpecies.copyMicroSpecies(this);
			for ( SpeciesDescription copied : copiedMicroSpecs ) {
				copiedSpec = (SpeciesDescription) super.addChild(copied);
				this.addMicroSpecies(copiedSpec);
			}

		}

		for ( SpeciesDescription microSpec : microSpecies.values() ) {
			microSpec.inheritMicroSpecies();
		}
	}

	/**
	 * Copies all the micro-species of this species to the new macro-species.
	 * 
	 * @param newMacroSpecies
	 * @return
	 */
	public List<SpeciesDescription> copyMicroSpecies(final SpeciesDescription newMacroSpecies)
		throws GamlException {

		List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		SpeciesDescription copiedMicroSpec;

		// recursively copy the sorted micro-species
		for ( SpeciesDescription microSpec : this.sortedMicroSpecies() ) {
			copiedMicroSpec =
				new SpeciesDescription(microSpec, getChildrenWithoutMicroSpec(microSpec),
					newMacroSpecies);

			retVal.add(copiedMicroSpec);
		}

		return retVal;
	}

	private List<IDescription> getChildrenWithoutMicroSpec(final SpeciesDescription specDesc) {
		List<IDescription> retVal = new GamaList<IDescription>();
		retVal.addAll(specDesc.getChildren());

		for ( SpeciesDescription microSpec : specDesc.getMicroSpecies() ) {
			retVal.remove(microSpec);
		}

		return retVal;
	}

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
}
