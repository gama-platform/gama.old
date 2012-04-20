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
import msi.gama.runtime.GAMA;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.*;
import msi.gaml.expressions.*;
import msi.gaml.skills.*;
import msi.gaml.types.IType;

public abstract class ExecutionContextDescription extends SymbolDescription {

	protected Map<String, CommandDescription> behaviors;
	protected Map<String, CommandDescription> aspects;
	protected Map<String, CommandDescription> actions;
	protected Map<String, VariableDescription> variables;
	protected final IList<String> sortedVariableNames;
	protected final IList<String> updatableVariableNames;
	protected Set<Class> skillsClasses;
	protected Map<String, Class> skillsMethods;
	protected final Map<Class, ISkill> skillInstancesByClass;
	protected final Map<String, ISkill> skillInstancesByMethod;

	protected Class javaBase;
	protected IAgentConstructor agentConstructor;

	protected IArchitecture control;

	protected int varCount = 0;
	protected SpeciesDescription macroSpecies;
	protected SpeciesDescription parentSpecies;

	public ExecutionContextDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final ISyntacticElement source,
		final SymbolMetaDescription md) {
		super(keyword, superDesc, children, source, md);
		skillInstancesByClass = new HashMap();
		skillInstancesByMethod = new HashMap();
		sortedVariableNames = new GamaList();
		updatableVariableNames = new GamaList();

		// "world_species" has ModelDescription as superDesc
		if ( superDesc instanceof SpeciesDescription ) {
			macroSpecies = (SpeciesDescription) superDesc;
		}
	}

	@Override
	protected void initFields() {
		super.initFields();

		behaviors = new HashMap<String, CommandDescription>();
		aspects = new HashMap<String, CommandDescription>();
		actions = new HashMap<String, CommandDescription>();
		variables = new HashMap<String, VariableDescription>();
		skillsClasses = new HashSet();
		skillsMethods = new HashMap();
	}

	protected void setSkills(final IExpressionDescription s) {
		if ( s != null ) {
			List<String> skills = GAMA.getExpressionFactory().parseLiteralArray(s, this);
			for ( String skill : skills ) {
				final Class c = Skill.getSkillClassFor(skill.trim());
				if ( c != null ) {
					addSkill(c);
				} // else {
					// flagError("Unknown skill: " + skill);
				// }
			}
		}
		Class c = Skill.getSkillClassFor(getKeyword());
		if ( c != null ) {
			addSkill(c);
		}
		addSkill(Skill.getSkillClassFor(facets.getLabel(IKeyword.CONTROL)));
		// ADDED HERE, BUT SHOULD BE MOVED ELSEWHERE
	}

	/**
	 * Finalizes the description:
	 * + Copy the behaviors, attributes from parent;
	 * + Creates the control if necessary;
	 * 
	 * @throws GamlException
	 */
	public void finalizeDescription() {
		copyItemsFromParent();
		createControl();
		buildSharedSkills();
	}

	protected abstract void copyItemsFromParent();

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

		return ((ExecutionContextDescription) enclosing).getLevel() + 1;
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
		IDescription desc = super.addChild(child);
		if ( desc.getKeyword().equals(IKeyword.REFLEX) ) {
			addBehavior((CommandDescription) desc);
		} else if ( desc.getKeyword().equals(IKeyword.ACTION) ) {
			addAction((CommandDescription) desc);
		} else if ( desc.getKeyword().equals(IKeyword.ASPECT) ) {
			addAspect((CommandDescription) desc);
		} else if ( desc.getKeyword().equals(IKeyword.PRIMITIVE) ) {
			addAction((CommandDescription) desc);
		} else if ( desc instanceof CommandDescription && !IKeyword.INIT.equals(desc.getKeyword()) ) {
			addBehavior((CommandDescription) desc);
		} else if ( desc instanceof VariableDescription ) {
			addVariable((VariableDescription) desc);
		}
		return desc;
	}

	private void addBehavior(final CommandDescription r) {
		String behaviorName = r.getName();
		CommandDescription existing = behaviors.get(behaviorName);
		if ( existing != null ) {
			if ( !existing.getKeyword().equals(r.getKeyword()) ) {
				r.flagWarning(r.getKeyword() + " " + behaviorName + " replaces the " +
					existing.getKeyword() + " declared in the parent species.");
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
			if ( existing.getKeyword().equals(IKeyword.PRIMITIVE) &&
				ce.getKeyword().equals(IKeyword.PRIMITIVE) ) {
				return;
			} else if ( existing.getKeyword().equals(IKeyword.PRIMITIVE) &&
				ce.getKeyword().equals(IKeyword.ACTION) ) {
				ce.flagError("action name already declared as a primitive : " + actionName);
			} else if ( !ce.getArgNames().containsAll(existing.getArgNames()) ) {
				String error =
					"The list of arguments differ in the two implementations of " + actionName;
				ce.flagError(error);
				existing.flagWarning(error);
			} else {
				children.remove(existing);
			}
		} else {

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
			ce.flagError("aspect name already declared : " + aspectName);
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
	protected boolean hasAction(final String a) {
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
			IDescription builtIn = removeChild(variables.get(vName));
			IType bType = builtIn.getTypeOf(builtIn.getFacets().getLabel(IKeyword.TYPE));
			IType vType = v.getTypeOf(v.getFacets().getLabel(IKeyword.TYPE));
			if ( bType != vType ) {
				String builtInType = bType.toString();
				String varType = vType.toString();
				v.flagError("variable " + vName + " is of type " + builtInType +
					" and cannot be redefined as a " + varType);
			}
			v.copyFrom((VariableDescription) builtIn);
		}
		v.setDefinitionOrder(varCount++);
		variables.put(vName, v);
	}

	protected IDescription removeChild(final IDescription builtIn) {
		children.remove(builtIn);
		return builtIn;
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
	public IExpression getVarExpr(final String n, final IExpressionFactory factory) {
		VariableDescription vd = getVariable(n);
		if ( vd == null ) { return null; }
		return vd.getVarExpr(factory);
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

	public Set<String> getBehaviorsNames() {
		return behaviors.keySet();
	}

	public Collection<CommandDescription> getBehaviors() {
		return behaviors.values();
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
			List<String> sss = GamlCompiler.getSkillMethods(c1);
			for ( String s : sss ) {
				addSkillMethod(c1, s);
			}

		}
		for ( IDescription v : children ) {
			v.setSuperDescription(this);
			addChild(v);
		}
		cleanDeclarationClasses();
		agentConstructor = GamlCompiler.getAgentConstructor(javaBase);

		if ( agentConstructor == null ) {
			flagError("The base class " + getJavaBase().getName() +
				" cannot be used as an agent class");
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

	public void addSkillMethod(final Class c, final String m) {
		// if ( !ISkill.class.isAssignableFrom(c) ) {
		// ;
		// return;
		// }
		final Class clazz =
		/* c.isInterface() || !Skill.class.isAssignableFrom(c) ? getJavaBase() : */c;
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
		return getTypeOf(this.getName());
	}

	public SpeciesDescription getMacroSpecies() {
		return macroSpecies;
	}

	/**
	 * @return
	 */
	public abstract String getParentName();

}
