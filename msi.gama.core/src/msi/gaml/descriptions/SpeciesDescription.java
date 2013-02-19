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

import java.lang.reflect.Modifier;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.*;
import msi.gaml.skills.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

public class SpeciesDescription extends SymbolDescription {

	private Map<String, StatementDescription> behaviors;
	private Map<String, StatementDescription> aspects;
	private Map<String, StatementDescription> actions;
	private Map<String, VariableDescription> variables;
	protected IList<String> sortedVariableNames = new GamaList();
	protected final IList<String> updatableVariableNames = new GamaList();
	protected final Map<Class, ISkill> skills = new HashMap();
	/**
	 * The following map contains micro-species explicitly declared inside this species.
	 */
	private Map<String, SpeciesDescription> microSpecies;
	private List<StatementDescription> inits;

	protected Class javaBase;
	protected IAgentConstructor agentConstructor;
	protected IArchitecture control;
	protected int varCount = 0;
	protected SpeciesDescription macroSpecies;
	protected SpeciesDescription parentSpecies;

	public SpeciesDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final IChildrenProvider cp, final ISyntacticElement source) {
		super(keyword, superDesc, cp, source);
		setSkills(facets.get(IKeyword.SKILLS), new HashSet());
		initJavaBase();
	}

	public SpeciesDescription(final String name, final Class clazz, final IDescription superDesc,
		final IAgentConstructor helper, final Set<String> skills2, final SymbolProto md,
		final Facets ff) {
		super(IKeyword.SPECIES, superDesc, IChildrenProvider.NONE, new SyntheticStatement(
			IKeyword.SPECIES, new Facets(IKeyword.NAME, name)));
		if ( ff.containsKey(IKeyword.CONTROL) ) {
			facets.putAsLabel(IKeyword.CONTROL, ff.get(IKeyword.CONTROL).toString());
		}
		setSkills(ff.get(IKeyword.SKILLS), skills2);
		initJavaBase(clazz, helper);
	}

	@Override
	public void dispose() {
		if ( AbstractGamlAdditions.isBuiltIn(getName()) ) { return; }
		if ( hasBehaviors() ) {
			getBehaviors().clear();
		}
		if ( hasAspects() ) {
			getAspects().clear();
		}
		if ( hasActions() ) {
			getActions().clear();
		}
		if ( hasVariables() ) {
			getVariables().clear();
		}
		skills.clear();
		if ( control != null ) {
			control.dispose();
		}
		macroSpecies = null;
		parentSpecies = null;
		if ( hasMicroSpecies() ) {
			getMicroSpecies().clear();
		}
		getInits().clear();
		super.dispose();
	}

	@Override
	public void setSuperDescription(final IDescription desc) {
		super.setSuperDescription(desc);
		if ( desc instanceof SpeciesDescription ) {
			macroSpecies = (SpeciesDescription) desc;
		}

	}

	private static final Set<String> skillNames = new LinkedHashSet();

	protected void setSkills(final IExpressionDescription userDefinedSkills,
		final Set<String> builtInSkills) {
		skillNames.clear();
		/* We try to add the control architecture if any is defined */
		if ( facets.containsKey(IKeyword.CONTROL) ) {
			skillNames.add(facets.getLabel(IKeyword.CONTROL));
		}
		/* We add the keyword as a possible skill (used for 'grid' species) */
		skillNames.add(getKeyword());
		/* We add the user defined skills (i.e. as in 'species a skills: [s1, s2...]') */
		skillNames.addAll(GAMA.getExpressionFactory().parseLiteralArray(userDefinedSkills, this));
		/*
		 * We add the skills that are defined in Java, either using @species(value='a', skills=
		 * {s1,s2}), or @skill(value="s1", attach_to="a")
		 */
		skillNames.addAll(builtInSkills);

		/* We then create the list of classes from this list of names */
		for ( String skillName : skillNames ) {
			final Class skillClass = AbstractGamlAdditions.getSkillClasses().get(skillName);
			if ( skillClass != null ) {
				addSkill(skillClass);
			}
		}

	}

	public String getControlName() {
		String controlName = facets.getLabel(IKeyword.CONTROL);
		// if the "control" is not explicitly declared then inherit it from the parent species.
		if ( controlName == null && parentSpecies != null ) {
			controlName = parentSpecies.getControlName();
		}
		if ( controlName == null ) {
			// Default value
			controlName = IKeyword.REFLEX;
		}
		return controlName;
	}

	protected void createControl() {
		control = (IArchitecture) AbstractGamlAdditions.getSkillInstanceFor(getControlName());
	}

	public ISkill getSkillFor(final Class clazz) {
		ISkill skill = skills.get(clazz);
		if ( skill == null && clazz != null ) {
			for ( Map.Entry<Class, ISkill> entry : skills.entrySet() ) {
				if ( clazz.isAssignableFrom(entry.getKey()) ) { return entry.getValue(); }
			}
		}
		return skill;
	}

	private void buildSharedSkills() {
		// Necessary in order to prevent concurrentModificationExceptions
		Set<Class> classes = new HashSet(skills.keySet());
		for ( Class c : classes ) {
			Class clazz = c;
			if ( Skill.class.isAssignableFrom(clazz) ) {
				if ( IArchitecture.class.isAssignableFrom(clazz) && control != null ) {
					while (clazz != AbstractArchitecture.class) {
						skills.put(clazz, control);
						clazz = clazz.getSuperclass();
					}
				} else {
					skills.put(clazz, AbstractGamlAdditions.getSkillInstanceFor(c));
				}
			} else {
				skills.put(clazz, null);
			}
		}
	}

	@Override
	public IDescription addChild(final IDescription child) {
		if ( child == null ) { return null; }
		IDescription desc = super.addChild(child);
		String kw = desc.getKeyword();
		if ( kw.equals(IKeyword.ACTION) || kw.equals(IKeyword.PRIMITIVE) ) {
			addAction((StatementDescription) desc);
		} else if ( kw.equals(IKeyword.ASPECT) ) {
			addAspect((StatementDescription) desc);
		} else if ( desc instanceof StatementDescription ) {
			if ( IKeyword.INIT.equals(kw) ) {
				addInit((StatementDescription) desc);
			} else {
				addBehavior((StatementDescription) desc);
			}
		} else if ( desc instanceof VariableDescription ) {
			addVariable((VariableDescription) desc);
		} else if ( desc instanceof SpeciesDescription ) {
			getModelDescription().addSpeciesDescription((SpeciesDescription) desc);
			getMicroSpecies().put(desc.getName(), (SpeciesDescription) desc);
		}
		return desc;
	}

	private void addInit(final StatementDescription init) {
		getInits().add(0, init); // Added at the beginning
	}

	private void addBehavior(final StatementDescription r) {
		String behaviorName = r.getName();
		StatementDescription existing = getBehaviors().get(behaviorName);
		if ( existing != null ) {
			if ( existing.getKeyword().equals(r.getKeyword()) ) {
				duplicateError(r, existing);
				children.remove(existing);
			}

		}
		getBehaviors().put(behaviorName, r);
	}

	public boolean hasBehavior(final String a) {
		return getBehaviors().containsKey(a);
	}

	void duplicateError(final IDescription one, final IDescription two) {
		String name = one.getFacets().getLabel(IKeyword.NAME);
		String key = one.getKeyword();
		String error = key + " " + name + " is declared twice. Only the last will be kept.";
		one.flagWarning(error, IGamlIssue.DUPLICATE_DEFINITION, IKeyword.NAME, name);
		two.flagWarning(error, IGamlIssue.DUPLICATE_DEFINITION, IKeyword.NAME, name);
	}

	// FAIRE UN FIX POUR CREER AUTOMATIQUEMENT L'ACTION CORRESPONDANTE

	private void inheritAction(final SpeciesDescription parent, final StatementDescription a) {
		String name = a.getName();
		if ( !hasAction(name) ) {
			if ( a.isAbstract() ) {
				this.flagError("Abstract action '" + name + "', inherited from " +
					parent.getName() + ", should be redefined.", IGamlIssue.MISSING_ACTION);
				return;
			}
			addChild(a);
			return;
		}
		StatementDescription existing = getAction(name);
		if ( !existing.getArgNames().containsAll(a.getArgNames()) ) {
			String error =
				"The list of arguments is different from that of action " + name + " defined in " +
					parent.getName() + " and redefined here.";
			existing.flagError(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			// a.flagWarning(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			return;
		}
	}

	private void addAction(final StatementDescription ce) {
		String actionName = ce.getName();
		StatementDescription existing = getAction(actionName);
		if ( existing != null ) {
			String previous = existing.getKeyword();
			if ( previous.equals(IKeyword.PRIMITIVE) && ce.getKeyword().equals(IKeyword.ACTION) &&
				!existing.isAbstract() ) {
				ce.flagError("Action " + actionName + " replaces a primitive of the same name.",
					IGamlIssue.GENERAL);
			}
			if ( !ce.getArgNames().containsAll(existing.getArgNames()) ) {
				String error =
					"The list of arguments differ in the two implementations of " + actionName;
				existing.flagError(error, IGamlIssue.DIFFERENT_ARGUMENTS);
				ce.flagWarning(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			} else {
				if ( !existing.isAbstract() ) {
					duplicateError(ce, existing);
				}
				children.remove(existing);
			}
		}
		getActions().put(actionName, ce);
		// Change (june 2012) : removed the necessity to register actions as binary operators. They
		// are now searched directly in the species description and not in the global registry of
		// operators.
		// AbstractGamlAdditions.registerFunction(actionName,
		// getSpeciesContext().getSpeciesContext()
		// .getType());
	}

	private void addAspect(final StatementDescription ce) {
		String aspectName = ce.getName();
		if ( aspectName == null ) {
			aspectName = IKeyword.DEFAULT;
			ce.getFacets().putAsLabel(IKeyword.NAME, aspectName);
		}
		if ( !aspectName.equals(IKeyword.DEFAULT) && hasAspect(aspectName) ) {
			duplicateError(ce, getAspect(aspectName));
		}
		getAspects().put(aspectName, ce);
	}

	public Set<String> getAspectsNames() {
		return aspects == null ? Collections.EMPTY_SET : getAspects().keySet();
	}

	public StatementDescription getAspect(final String aName) {
		return aspects == null ? null : getAspects().get(aName);
	}

	@Override
	public StatementDescription getAction(final String aName) {
		return actions == null ? null : getActions().get(aName);
	}

	@Override
	public boolean hasAction(final String a) {
		return actions != null && getActions().containsKey(a);
	}

	public Set<String> getActionsNames() {
		return actions == null ? Collections.EMPTY_SET : getActions().keySet();
	}

	protected void addVariable(final VariableDescription v) {
		String vName = v.getName();

		if ( hasVar(vName) ) {
			IDescription builtIn = getVariables().get(vName);
			if ( !builtIn.isBuiltIn() ) {
				duplicateError(v, builtIn);
				getChildren().remove(builtIn);
			}
			IType bType = builtIn.getTypeNamed(builtIn.getFacets().getLabel(IKeyword.TYPE));
			IType vType = v.getTypeNamed(v.getFacets().getLabel(IKeyword.TYPE));
			if ( bType != vType ) {
				String builtInType = bType.toString();
				String varType = vType.toString();
				v.flagError("variable " + vName + " is of type " + builtInType +
					" and cannot be redefined as a " + varType, IGamlIssue.WRONG_REDEFINITION);
			}
			v.copyFrom((VariableDescription) builtIn);
		}
		v.setDefinitionOrder(varCount++);
		getVariables().put(vName, v);
	}

	public IArchitecture getControl() {
		return control;
	}

	public VariableDescription getVariable(final String name) {
		return variables == null ? null : getVariables().get(name);
	}

	@Override
	public boolean hasVar(final String a) {
		return variables != null && getVariables().containsKey(a);
	}

	@Override
	public IExpression getVarExpr(final String n) {
		VariableDescription vd = getVariable(n);
		if ( vd == null ) { return null; }
		return vd.getVarExpr();
	}

	public IList<String> getVarNames() {
		return sortedVariableNames;
	}

	public List<String> getUpdatableVarNames() {
		return updatableVariableNames;
	}

	protected void sortVars() {
		if ( variables == null ) { return; }
		// GuiUtils.debug("***** Sorting variables of " + getNameFacetValue());
		final List<VariableDescription> result = new GamaList();
		final Collection<VariableDescription> vars = getVariables().values();
		for ( final VariableDescription var : vars ) {
			var.usedVariablesIn(getVariables());
		}
		for ( final VariableDescription var : vars ) {
			var.expandDependencies(new GamaList());
		}
		for ( final VariableDescription toBePlaced : vars ) {
			boolean found = false;
			int i = 0;
			while (!found && i < result.size()) {
				final VariableDescription alreadyInPlace = result.get(i);
				if ( alreadyInPlace.getDependencies().contains(toBePlaced) ) {
					found = true;
				} else {
					i += 1;
				}
			}
			if ( found ) {
				result.add(i, toBePlaced);
			} else {
				result.add(toBePlaced);
			}
		}

		sortedVariableNames.clear();
		for ( int i = 0; i < result.size(); i++ ) {
			VariableDescription v = result.get(i);
			String s = v.getName();
			sortedVariableNames.add(s);
			if ( v.isUpdatable() ) {
				updatableVariableNames.add(s);
			}
		}
	}

	/**
	 * Returns all the direct&in-direct micro-species of this species.
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getAllMicroSpecies() {
		List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		if ( hasMicroSpecies() ) {
			retVal.addAll(getMicroSpecies().values());

			for ( SpeciesDescription micro : getMicroSpecies().values() ) {
				retVal.addAll(micro.getAllMicroSpecies());
			}
		}
		return retVal;
	}

	@Override
	protected boolean hasAspect(final String a) {
		return aspects != null && getAspects().containsKey(a);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return this;
	}

	public List<SpeciesDescription> getSelfAndParentMicroSpecies() {
		GamaList<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		if ( hasMicroSpecies() ) {
			retVal.addAll(getMicroSpecies().values());
		}
		if ( parentSpecies != null ) {
			retVal.addAll(parentSpecies.getSelfAndParentMicroSpecies());
		}

		return retVal;
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		if ( hasMicroSpecies() ) {
			SpeciesDescription retVal = microSpecies.get(name);
			if ( retVal != null ) { return retVal; }
		}

		if ( this.parentSpecies != null ) { return parentSpecies.getMicroSpecies(name); }
		return null;
	}

	@Override
	public String toString() {
		return "Description of " + getName();
	}

	public void initJavaBase(final Class clazz, final IAgentConstructor helper) {
		if ( javaBase != null ) { return; }
		javaBase = clazz;
		if ( helper == null ) {
			flagError("The base class " + javaBase.getName() + " cannot be used as an agent class",
				IGamlIssue.GENERAL);
			return;
		}
		agentConstructor = helper;
		final List<IDescription> children =
			AbstractGamlAdditions.getAllChildrenOf(javaBase, getSkillClasses());
		for ( IDescription v : children ) {
			addChild(((SymbolDescription) v).copy(this));
		}

	}

	public void initJavaBase() {
		initJavaBase(AbstractGamlAdditions.DEFAULT_AGENT_CLASS,
			AbstractGamlAdditions.DEFAULT_AGENT_CONSTRUCTOR);
	}

	public IAgentConstructor getAgentConstructor() {
		return agentConstructor;
	}

	public void addSkill(final Class c) {
		if ( c != null && ISkill.class.isAssignableFrom(c) && !c.isInterface() &&
			!Modifier.isAbstract(c.getModifiers()) ) {
			skills.put(c, null);
		}
	}

	public Set<Class> getSkillClasses() {
		return skills.keySet();
	}

	public Class getJavaBase() {
		return javaBase;
	}

	@Override
	public IType getType() {
		return getTypeNamed(getName());
	}

	public SpeciesDescription getMacroSpecies() {
		return macroSpecies;
	}

	protected void copyItemsFromParent() {
		SpeciesDescription parent = getParentSpecies();
		if ( parent != null ) {
			if ( !parent.javaBase.isAssignableFrom(javaBase) ) {
				if ( javaBase == GamlAgent.class ) { // default base class
					javaBase = parent.javaBase;
					agentConstructor = parent.agentConstructor;
				} else {
					flagError(
						"Species " + getName() + " Java base class (" + javaBase.getSimpleName() +
							") is not a subclass of its parent species " + parent.getName() +
							" base class (" + parent.getJavaBase().getSimpleName() + ")",
						IGamlIssue.GENERAL);
				}
			}
			for ( Map.Entry<Class, ISkill> entry : parent.skills.entrySet() ) {
				if ( !skills.containsKey(entry.getKey()) ) {
					skills.put(entry.getKey(), entry.getValue());
				}
			}

			// We only copy the behaviors that are not redefined in this species
			if ( parent.hasBehaviors() ) {
				for ( final StatementDescription b : parent.getBehaviors().values() ) {
					if ( !hasBehavior(b.getName()) ) {
						// Copy done here
						addChild(b.copy(this));
					}
				}
			}
			if ( parent.hasInits() ) {
				for ( final StatementDescription init : parent.getInits() ) {
					addChild(init.copy(this));
				}
			}
			// We only copy the actions that are not redefined in this species
			if ( parent.hasActions() ) {
				for ( final StatementDescription action : parent.getActions().values() ) {
					inheritAction(parent, action);
				}
			}
			if ( parent.hasAspects() ) {
				for ( final String aName : parent.getAspects().keySet() ) {
					// if ( aName.equals(ISymbol.DEFAULT) || !hasAspect(aName) ) {
					if ( !hasAspect(aName) ) {
						addChild(parent.getAspects().get(aName).copy(this));
					}
				}
			}
			if ( parent.hasVariables() ) {
				// We only copy the variables that are not redefined in this species
				for ( final VariableDescription v : parent.getVariables().values() ) {
					if ( !hasVar(v.getName()) ) {
						addChild(v.copy(this));
					}
				}
			}
		}
		sortVars();

	}

	public boolean isArgOf(final String op, final String arg) {
		if ( hasAction(op) ) { return getActions().get(op).containsArg(arg); }
		return false;
	}

	/**
	 * @return
	 */
	public String getParentName() {
		return facets.getLabel(IKeyword.PARENT);
	}

	public void verifyAndSetParent() {
		String parentName = getParentName();
		if ( parentName == null ) { return; }
		parentSpecies = verifyParent(parentName);
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

	public boolean isGrid() {
		return getKeyword().equals(IKeyword.GRID);
	}

	@Override
	public String getTitle() {
		return "Species <b>" + getName() + "</a></link></b>";
	}

	/**
	 * Returns a list of SpeciesDescription that can be the parent of this species.
	 * A species can be a sub-species of its "peer" species ("peer" species are species sharing the
	 * same direct macro-species).
	 * 
	 * @return
	 */
	public List<SpeciesDescription> getPotentialParentSpecies() {
		List<SpeciesDescription> retVal = getVisibleSpecies();
		retVal.removeAll(this.getSelfAndParentMicroSpecies());
		retVal.remove(this);

		return retVal;
	}

	/**
	 * Sorts the micro-species.
	 * Parent micro-species are ahead of the list followed by sub micro-species.
	 * 
	 * @return
	 */
	private List<SpeciesDescription> sortedMicroSpecies() {
		if ( !hasMicroSpecies() ) { return Collections.EMPTY_LIST; }
		Collection<SpeciesDescription> allMicroSpecies = getMicroSpecies().values();
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
			retVal.addAll(currentSpec.getSelfAndParentMicroSpecies());

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
			flagError(getName() + " species can't be a sub-species of itself", IGamlIssue.GENERAL);
			return null;
		}
		List<SpeciesDescription> candidates = this.getPotentialParentSpecies();
		SpeciesDescription potentialParent = null;
		for ( SpeciesDescription c2 : candidates ) {
			if ( c2.getName().equals(parentName) ) {
				potentialParent = c2;
				break;
			}
		}

		if ( potentialParent == null ) {

			List<String> availableSpecies = new GamaList<String>();
			for ( SpeciesDescription p : candidates ) {
				availableSpecies.add(p.getName());
			}
			availableSpecies.remove(availableSpecies.size() - 1);

			flagError(parentName + " can't be a parent species of " + this.getName() +
				" species. Available parent species are: " + availableSpecies,
				IGamlIssue.WRONG_PARENT, IKeyword.PARENT, availableSpecies.toArray(new String[] {}));

			return null;
		}

		List<SpeciesDescription> parentsOfParent = potentialParent.getSelfWithParents();
		if ( parentsOfParent.contains(this) ) {
			String error =
				this.getName() + " species and " + potentialParent.getName() +
					" species can't be sub-species of each other.";
			potentialParent.flagError(error);
			flagError(error);
			return null;
		}

		if ( this.getAllMicroSpecies().contains(parentsOfParent) ) {
			flagError(
				this.getName() + " species can't be a sub-species of " + potentialParent.getName() +
					" species because a species can't be sub-species of its direct or indirect micro-species.",
				IGamlIssue.GENERAL);
			return null;
		}

		return potentialParent;
	}

	/**
	 * Finalizes the species description
	 * + Copy the behaviors, attributes from parent;
	 * + Creates the control if necessary.
	 * 
	 * @throws GamlException
	 */
	public void finalizeDescription() {
		if ( isMirror() ) {
			addChild(DescriptionFactory
				.create(IKeyword.AGENT, this, IKeyword.NAME, IKeyword.TARGET));
		}
		copyItemsFromParent();
		createControl();
		buildSharedSkills();
		// recursively finalize the sorted micro-species
		for ( SpeciesDescription microSpec : sortedMicroSpecies() ) {
			microSpec.finalizeDescription();
		}
	}

	/**
	 * Lazy initialization
	 * 
	 * @return the behaviors declared in this species
	 */
	boolean hasBehaviors() {
		return behaviors != null;
	}

	protected Map<String, StatementDescription> getBehaviors() {
		if ( behaviors == null ) {
			behaviors = new LinkedHashMap<String, StatementDescription>();
		}
		return behaviors;
	}

	/**
	 * Lazy initialization
	 * 
	 * @return the aspects declared in this species
	 */

	boolean hasAspects() {
		return aspects != null;
	}

	protected Map<String, StatementDescription> getAspects() {
		if ( aspects == null ) {
			aspects = new LinkedHashMap<String, StatementDescription>();
		}
		return aspects;
	}

	/**
	 * Lazy initialization
	 * 
	 * @return the actions declared in this species
	 */

	boolean hasActions() {
		return actions != null;
	}

	public Map<String, StatementDescription> getActions() {
		if ( actions == null ) {
			actions = new LinkedHashMap<String, StatementDescription>();
		}
		return actions;
	}

	/**
	 * Lazy initialization
	 * 
	 * @return the variables declared in this species
	 */

	boolean hasVariables() {
		return actions != null;
	}

	public Map<String, VariableDescription> getVariables() {
		if ( variables == null ) {
			variables = new LinkedHashMap<String, VariableDescription>();
		}
		return variables;
	}

	/**
	 * Lazy initialization
	 * 
	 * @return the inits declared in this species
	 */

	boolean hasInits() {
		return inits != null;
	}

	protected List<StatementDescription> getInits() {
		if ( inits == null ) {
			inits = new ArrayList<StatementDescription>();
		}
		return inits;
	}

	boolean hasMicroSpecies() {
		return microSpecies != null;
	}

	private Map<String, SpeciesDescription> getMicroSpecies() {
		if ( microSpecies == null ) {
			microSpecies = new LinkedHashMap<String, SpeciesDescription>();
		}
		return microSpecies;
	}

	public boolean isAbstract() {
		for ( StatementDescription a : getActions().values() ) {
			if ( a.isAbstract() ) { return true; }
		}
		return false;
	}

	public boolean isMirror() {
		return facets.containsKey(IKeyword.MIRRORS);
	}

}
