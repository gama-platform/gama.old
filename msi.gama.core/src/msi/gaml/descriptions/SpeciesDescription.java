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
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.ModelFactory;
import msi.gaml.skills.*;
import msi.gaml.types.IType;

public class SpeciesDescription extends SymbolDescription {

	protected final Map<String, CommandDescription> behaviors;
	protected final Map<String, CommandDescription> aspects;
	protected final Map<String, CommandDescription> actions;
	protected final Map<String, VariableDescription> variables;
	protected final IList<String> sortedVariableNames;
	protected final IList<String> updatableVariableNames;
	protected final Set<Class> skillsClasses;
	protected final Map<String, Class> skillsMethods;
	protected final Map<Class, ISkill> skillInstancesByClass;
	protected final Map<String, ISkill> skillInstancesByMethod;
	/**
	 * Micro-species of a species includes species explicitly declared inside it
	 * and micro-species of its parent.
	 * 
	 * The following map contains micro-species explicitly declared inside this species.
	 */
	private final Map<String, SpeciesDescription> microSpecies;

	protected final List<CommandDescription> inits;

	protected Class javaBase;
	protected IAgentConstructor agentConstructor;
	protected IArchitecture control;
	protected int varCount = 0;
	protected SpeciesDescription macroSpecies;
	protected SpeciesDescription parentSpecies;

	public SpeciesDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final ISyntacticElement source,
		final Class base, final SymbolMetaDescription md) {
		super(keyword, superDesc, children, source, md);
		skillInstancesByClass = new HashMap();
		skillInstancesByMethod = new HashMap();
		sortedVariableNames = new GamaList();
		updatableVariableNames = new GamaList();
		behaviors = new HashMap<String, CommandDescription>();
		aspects = new HashMap<String, CommandDescription>();
		actions = new HashMap<String, CommandDescription>();
		variables = new HashMap<String, VariableDescription>();
		skillsClasses = new HashSet();
		skillsMethods = new HashMap();
		inits = new ArrayList<CommandDescription>();
		microSpecies = new HashMap<String, SpeciesDescription>();

		setSkills(facets.get(IKeyword.SKILLS));
		setJavaBase(base);
	}

	@Override
	public void dispose() {
		if ( ModelFactory.BUILT_IN_SPECIES.contains(this) ) { return; }
		behaviors.clear();
		aspects.clear();
		actions.clear();
		variables.clear();
		skillInstancesByClass.clear();
		skillInstancesByMethod.clear();
		if ( control != null ) {
			control.dispose();
		}
		macroSpecies = null;
		parentSpecies = null;
		microSpecies.clear();
		inits.clear();
		super.dispose();
	}

	@Override
	public void setSuperDescription(final IDescription desc) {
		super.setSuperDescription(desc);
		if ( desc instanceof SpeciesDescription ) {
			macroSpecies = (SpeciesDescription) desc;
		}

	}

	protected void setSkills(final IExpressionDescription userDefinedSkills) {
		Set<String> skillNames = new LinkedHashSet();
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
		addBuiltInSkills(skillNames);

		// GuiUtils.debug("Skills defined for " + getName() + ": " + skillNames);

		/* We then create the list of classes from this list of names */
		Set<Class> skillClasses = new LinkedHashSet();
		for ( String skillName : skillNames ) {
			final Class skillClass = Skill.getSkillClassFor(skillName.trim());
			if ( skillClass != null ) {
				skillClasses.add(skillClass);
			}
		}
		/* And add them as skills to the species */
		for ( Class skillClass : skillClasses ) {
			addSkill(skillClass);
		}

	}

	/**
	 * Adds to the skills the "built-in" skills described in GamlProperties.SPECIES_SKILLS
	 * properties
	 * 
	 * @param skills
	 */
	private void addBuiltInSkills(final Set<String> skills) {
		Set<String> builtInSkills =
			GamlProperties.loadFrom(GamlProperties.SPECIES_SKILLS).get(getName());
		if ( builtInSkills == null ) { return; }
		for ( String skillName : builtInSkills ) {
			if ( !skills.contains(skillName) ) {
				skills.add(skillName);
			}
		}
	}

	/**
	 * Returns the level of this species.
	 * "world" species is the top level species having 0 as level.
	 * level of a species is equal to level of its direct macro-species plus 1.
	 * 
	 * @return
	 */
	public int getLevel() {
		// "world_species" has ModelDescription as enclosing description.
		if ( enclosing instanceof ModelDescription ) { return 0; }

		return ((SpeciesDescription) enclosing).getLevel() + 1;
	}

	public String getControlName() {
		String controlName = facets.getLabel(IKeyword.CONTROL);

		// if the "control" is not explicitly declared then inherit it from the parent species.
		if ( controlName == null && parentSpecies != null ) {
			controlName = parentSpecies.getControlName();
		}

		return controlName;
	}

	protected void createControl() {
		String keyword = getControlName();
		Class c = Skill.getSkillClassFor(keyword);
		if ( c == null ) {
			control = new ReflexArchitecture();
		} else {
			control = (IArchitecture) Skill.createSharedSkillFor(c);
		}
	}

	public ISkill getSharedSkill(final Class c) {
		return skillInstancesByClass.get(c);
	}

	public ISkill getSkillFor(final String methodName) {
		return skillInstancesByMethod.get(methodName);
	}

	public Class getSkillClassFor(final String getterName) {
		return skillsMethods.get(getterName);
	}

	private void buildSharedSkills() {
		for ( final Class c : new HashSet<Class>(skillsMethods.values()) ) {
			if ( Skill.class.isAssignableFrom(c) ) {
				ISkill skill;
				if ( IArchitecture.class.isAssignableFrom(c) && control != null ) {
					// In order to avoid having two objects of the same class
					skill = control;
				} else {
					skill = Skill.createSharedSkillFor(c);
				}
				skillInstancesByClass.put(c, skill);
				// skill.initializeFor(scope);
			} else {
				skillInstancesByClass.put(c, null);
			}
		}
		for ( final String s : skillsMethods.keySet() ) {
			final Class c = skillsMethods.get(s);
			addSkill(s, skillInstancesByClass.get(c));
		}
	}

	public void addSkill(final String methodName, final ISkill skill) {
		skillInstancesByMethod.put(methodName, skill);
	}

	@Override
	public IDescription addChild(final IDescription child) {
		if ( child == null ) { return null; }
		IDescription desc = super.addChild(child);
		String kw = desc.getKeyword();
		if ( kw.equals(IKeyword.REFLEX) ) {
			addBehavior((CommandDescription) desc);
		} else if ( kw.equals(IKeyword.ACTION) ) {
			addAction((CommandDescription) desc);
		} else if ( kw.equals(IKeyword.ASPECT) ) {
			addAspect((CommandDescription) desc);
		} else if ( kw.equals(IKeyword.PRIMITIVE) ) {
			addAction((CommandDescription) desc);
		} else if ( desc instanceof CommandDescription && !IKeyword.INIT.equals(kw) ) {
			addBehavior((CommandDescription) desc);
		} else if ( desc instanceof VariableDescription ) {
			addVariable((VariableDescription) desc);
		} else if ( kw.equals(IKeyword.INIT) ) {
			addInit((CommandDescription) desc);
		} else if ( ModelFactory.SPECIES_NODES.contains(kw) ) {
			getModelDescription().addType((SpeciesDescription) desc);
			microSpecies.put(desc.getName(), (SpeciesDescription) desc);
		}
		return desc;
	}

	private void addInit(final CommandDescription init) {
		inits.add(0, init); // Added at the beginning
	}

	private void addBehavior(final CommandDescription r) {
		String behaviorName = r.getName();
		CommandDescription existing = behaviors.get(behaviorName);
		if ( existing != null ) {
			if ( !existing.getKeyword().equals(r.getKeyword()) ) {
				r.flagWarning(
					r.getKeyword() + " " + behaviorName + " replaces the " + existing.getKeyword() +
						" declared in the parent species.", IGamlIssue.SHADOWS_NAME, IKeyword.NAME,
					behaviorName);
			}
			children.remove(existing);
		}
		behaviors.put(behaviorName, r);
	}

	public boolean hasBehavior(final String a) {
		return behaviors.containsKey(a);
	}

	private void addAction(final CommandDescription ce) {
		String actionName = ce.getName();
		CommandDescription existing = getAction(actionName);
		if ( existing != null ) {
			String previous = existing.getKeyword();
			if ( previous.equals(IKeyword.PRIMITIVE) && ce.getKeyword().equals(IKeyword.ACTION) ) {
				ce.flagError("action name already declared as a primitive : " + actionName,
					IGamlIssue.GENERAL);
			} else if ( !ce.getArgNames().containsAll(existing.getArgNames()) ) {
				String error =
					"The list of arguments differ in the two implementations of " + actionName;
				ce.flagError(error, IGamlIssue.DIFFERENT_ARGUMENTS);
				existing.flagWarning(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			} else {
				children.remove(existing);
			}
		}
		actions.put(actionName, ce);
		GamaCompiler
			.registerFunction(actionName, getSpeciesContext().getSpeciesContext().getType());
	}

	private void addAspect(final CommandDescription ce) {
		String aspectName = ce.getName();
		if ( aspectName == null ) {
			aspectName = IKeyword.DEFAULT;
			ce.getFacets().putAsLabel(IKeyword.NAME, aspectName);
		}
		if ( !aspectName.equals(IKeyword.DEFAULT) && hasAspect(aspectName) ) {
			ce.flagError("aspect name already declared : " + aspectName, IGamlIssue.DUPLICATE_NAME,
				IKeyword.NAME, aspectName);
		}
		aspects.put(aspectName, ce);
	}

	public Set<String> getAspectsNames() {
		return aspects.keySet();
	}

	public CommandDescription getAspect(final String aName) {
		return aspects.get(aName);
	}

	@Override
	public CommandDescription getAction(final String aName) {
		return actions.get(aName);
	}

	@Override
	public boolean hasAction(final String a) {
		return actions.containsKey(a);
	}

	public Collection<CommandDescription> getActions() {
		return actions.values();
	}

	public Set<String> getActionsNames() {
		return actions.keySet();
	}

	protected void addVariable(final VariableDescription v) {
		String vName = v.getName();
		// GuiUtils.debug("Adding var " + v.getName() + " to " + getName());
		if ( hasVar(vName) ) {
			IDescription builtIn = variables.get(vName);
			getChildren().remove(builtIn);
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
		variables.put(vName, v);
	}

	public IArchitecture getControl() {
		return control;
	}

	public VariableDescription getVariable(final String name) {
		return variables.get(name);
	}

	public Map<String, VariableDescription> getVariables() {
		return variables;
	}

	@Override
	public boolean hasVar(final String a) {
		return variables.containsKey(a);
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
		// GuiUtils.debug("***** Sorting variables of " + getNameFacetValue());
		final List<VariableDescription> result = new GamaList();
		final Collection<VariableDescription> vars = variables.values();
		for ( final VariableDescription var : vars ) {
			var.usedVariablesIn(variables);
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

		for ( int i = 0; i < result.size(); i++ ) {
			VariableDescription v = result.get(i);
			String s = v.getName();
			sortedVariableNames.add(s);
			if ( v.isUpdatable() ) {
				updatableVariableNames.add(s);
			}
		}

		// GuiUtils.debug("Sorted variable names of " + facets.getLabel(IKeyword.NAME) + " are " +
		// sortedVariableNames);
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

	public Map<String, Class> getSkillsMethods() {
		return skillsMethods;
	}

	@Override
	protected boolean hasAspect(final String a) {
		return aspects.containsKey(a);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return this;
	}

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

	@Override
	public String toString() {
		return "Description of " + getName();
	}

	public void setJavaBase(final Class c) {
		if ( javaBase != null ) { return; }
		javaBase = c;
		final List<Class> classes =
			GamaCompiler.collectImplementationClasses(javaBase, getSkillClasses());
		final List<IDescription> children = new ArrayList();
		for ( final Class c1 : classes ) {
			children.addAll(GamlCompiler.getVarDescriptions(c1));
			children.addAll(GamlCompiler.getCommandDescriptions(c1));
			for ( String s : GamlCompiler.getSkillMethods(c1) ) {
				addSkillMethod(c1, s);
			}

		}
		for ( IDescription v : children ) {
			addChild(((SymbolDescription) v).copy());
		}
		cleanDeclarationClasses();
		agentConstructor = GamlCompiler.getAgentConstructor(javaBase);

		if ( agentConstructor == null ) {
			flagError("The base class " + getJavaBase().getName() +
				" cannot be used as an agent class", IGamlIssue.GENERAL);
		}
	}

	public IAgentConstructor getAgentConstructor() {
		return agentConstructor;
	}

	public void cleanDeclarationClasses() {
		final Set<Class> allClasses = new HashSet();
		allClasses.addAll(skillsMethods.values());
		for ( final Class c : allClasses ) {
			for ( final String s : skillsMethods.keySet() ) {
				final Class old = skillsMethods.get(s);
				if ( old != c && old.isAssignableFrom(c) ) {
					// OutputManager.debug("Change: " + old.getSimpleName() + " replaced by " +
					// c.getSimpleName() + " in the definition of " + s);
					skillsMethods.put(s, c);
				}
			}
		}
	}

	public void addSkill(final Class c) {
		if ( c != null && ISkill.class.isAssignableFrom(c) && !c.isInterface() ) {
			skillsClasses.add(c);
		}
	}

	public void addSkillMethod(final Class clazz, final String m) {
		addSkill(clazz);
		Class old = skillsMethods.get(m);
		if ( old == null || old.isAssignableFrom(clazz) ) {
			skillsMethods.put(m, clazz);
			return;
		}
	}

	public Set<Class> getSkillClasses() {
		return skillsClasses;
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
			skillsClasses.addAll(parent.skillsClasses);
			skillsMethods.putAll(parent.skillsMethods);

			// We only copy the behaviors that are not redefined in this species
			for ( final CommandDescription b : parent.behaviors.values() ) {
				if ( !hasBehavior(b.getName()) ) {
					// Copy done here
					addChild(b.copy());
				}
			}

			for ( final CommandDescription init : parent.inits ) {
				addChild(init.copy());
			}

			// We only copy the actions that are not redefined in this species
			for ( final String aName : parent.actions.keySet() ) {
				if ( !hasAction(aName) ) {
					CommandDescription action = parent.actions.get(aName);
					if ( action.isAbstract() ) {
						this.flagWarning("Abstract action '" + aName +
							"', which is inherited from " + parent.getName() +
							", should be redefined.", IGamlIssue.GENERAL);
					}
					// GuiUtils.debug("Copying action " + aName + " from " + parent + " to " +
					// this);
					addChild(parent.actions.get(aName).copy());
				}
			}

			for ( final String aName : parent.aspects.keySet() ) {
				// if ( aName.equals(ISymbol.DEFAULT) || !hasAspect(aName) ) {
				if ( !hasAspect(aName) ) {
					addChild(parent.aspects.get(aName).copy());
				}
			}

			// We only copy the variables that are not redefined in this species
			for ( final VariableDescription v : parent.variables.values() ) {
				if ( v.isBuiltIn() ) {
					final VariableDescription var = getVariable(v.getName());
					if ( var == null ) { // || ! isUserDefined ???
						addChild(v.copy());
					}
				} else if ( !hasVar(v.getName()) ) {
					addChild(v.copy());
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

		SpeciesDescription potentialParent = findPotentialParent(parentName);
		if ( potentialParent == null ) {

			List<SpeciesDescription> potentialParents = this.getPotentialParentSpecies();

			List<String> availableSpecies = new GamaList<String>();

			for ( SpeciesDescription p : potentialParents ) {
				availableSpecies.add(p.getName());
				availableSpecies.add(", ");
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
		copyItemsFromParent();
		createControl();
		buildSharedSkills();
		// recursively finalize the sorted micro-species
		for ( SpeciesDescription microSpec : sortedMicroSpecies() ) {
			microSpec.finalizeDescription();
		}
	}

}
