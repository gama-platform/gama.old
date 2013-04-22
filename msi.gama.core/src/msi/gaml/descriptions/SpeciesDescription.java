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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.skills.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.Types;
import org.eclipse.emf.ecore.EObject;

public class SpeciesDescription extends TypeDescription {

	private Map<String, StatementDescription> behaviors, aspects;
	private List<StatementDescription> inits;
	protected final Map<Class, ISkill> skills = new HashMap();
	protected IArchitecture control;
	private Map<String, SpeciesDescription> microSpecies;
	protected SpeciesDescription macroSpecies;
	private IAgentConstructor agentConstructor;
	private boolean isGlobal = false;

	public SpeciesDescription(final String keyword, final IDescription macroDesc, final IChildrenProvider cp,
		final EObject source, final Facets facets) {
		this(keyword, macroDesc, null, cp, source, facets);
	}

	public SpeciesDescription(final String keyword, final IDescription macroDesc, IDescription parent,
		final IChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, null, macroDesc, parent, cp, source, facets);
		setSkills(facets.get(SKILLS), new HashSet());
		// copyJavaAdditions();
		// GuiUtils.debug("Creation of SpeciesDescription " + getName() + " sub-species of " + String.valueOf(parent));
		// agentConstructor = parent.getAgentConstructor();
	}

	/**
	 * This constructor is only called to build built-in species. The parent is passed directly as there is no
	 * ModelFactory to provide it
	 */
	public SpeciesDescription(final String name, final Class clazz, final IDescription superDesc,
		final SpeciesDescription parent, final IAgentConstructor helper, final Set<String> skills2, final Facets ff) {
		super(SPECIES, clazz, superDesc, null, IChildrenProvider.NONE, null, new Facets(KEYWORD, SPECIES, NAME, name));
		if ( ff.containsKey(CONTROL) ) {
			facets.putAsLabel(CONTROL, ff.get(CONTROL).toString());
		}
		setSkills(ff.get(SKILLS), skills2);
		// copyJavaAdditions();
		setParent(parent);
		setAgentConstructor(helper);
	}

	@Override
	public void dispose() {
		if ( Types.isBuiltIn(getName()) ) { return; }
		if ( behaviors != null ) {
			behaviors.clear();
		}
		if ( aspects != null ) {
			aspects.clear();
		}
		skills.clear();
		if ( control != null ) {
			control.dispose();
		}
		macroSpecies = null;

		if ( microSpecies != null ) {
			microSpecies.clear();
		}
		if ( inits != null ) {
			inits.clear();
		}
		super.dispose();
	}

	@Override
	public void setSuperDescription(final IDescription desc) {
		super.setSuperDescription(desc);
		if ( desc instanceof SpeciesDescription ) {
			macroSpecies = (SpeciesDescription) desc;
		}
	}

	protected void setSkills(final IExpressionDescription userDefinedSkills, final Set<String> builtInSkills) {
		Set<String> skillNames = new LinkedHashSet();
		/* We try to add the control architecture if any is defined */
		if ( facets.containsKey(CONTROL) ) {
			skillNames.add(facets.getLabel(CONTROL));
		}
		/* We add the keyword as a possible skill (used for 'grid' species) */
		skillNames.add(getKeyword());
		/* We add the user defined skills (i.e. as in 'species a skills: [s1, s2...]') */
		if ( userDefinedSkills != null ) {
			skillNames.addAll(userDefinedSkills.getStrings(this, true));
		}
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
		String controlName = facets.getLabel(CONTROL);
		// if the "control" is not explicitly declared then inherit it from the parent species.
		if ( controlName == null && parent != null ) {
			controlName = getParent().getControlName();
		}
		if ( controlName == null ) {
			// Default value
			controlName = REFLEX;
		}
		return controlName;
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

	public String getParentName() {
		return facets.getLabel(PARENT);
	}

	@Override
	public IDescription addChild(final IDescription child) {
		IDescription desc = super.addChild(child);
		if ( desc == null ) { return null; }
		if ( desc instanceof StatementDescription ) {
			StatementDescription statement = (StatementDescription) desc;
			String kw = desc.getKeyword();
			if ( PRIMITIVE.equals(kw) ) {
				addPrimitive(statement);
			} else if ( ACTION.equals(kw) ) {
				addAction(statement);
			} else if ( ASPECT.equals(kw) ) {
				addAspect(statement);
			} else if ( INIT.equals(kw) ) {
				addInit(statement);
			} else {
				addBehavior(statement);
			}
		} else if ( desc instanceof VariableDescription ) {
			addVariable((VariableDescription) desc);
		} else if ( desc instanceof SpeciesDescription ) {
			ModelDescription md = getModelDescription();
			if ( md != null ) {
				md.addSpeciesType((TypeDescription) desc);
			}
			getMicroSpecies().put(desc.getName(), (SpeciesDescription) desc);
		}
		return desc;
	}

	private void addInit(final StatementDescription init) {
		if ( inits == null ) {
			inits = new ArrayList<StatementDescription>();
		}
		inits.add(0, init); // Added at the beginning
	}

	private void addBehavior(final StatementDescription r) {
		String behaviorName = r.getName();
		if ( behaviors == null ) {
			behaviors = new LinkedHashMap<String, StatementDescription>();
		}
		StatementDescription existing = behaviors.get(behaviorName);
		if ( existing != null ) {
			if ( existing.getKeyword().equals(r.getKeyword()) ) {
				duplicateError(r, existing);
				children.remove(existing);
			}
		}
		behaviors.put(behaviorName, r);
	}

	public boolean hasBehavior(final String a) {
		return behaviors != null && behaviors.containsKey(a);
	}

	// FAIRE UN FIX POUR CREER AUTOMATIQUEMENT L'ACTION CORRESPONDANTE

	private void addAspect(final StatementDescription ce) {
		String aspectName = ce.getName();
		if ( aspectName == null ) {
			aspectName = DEFAULT;
			ce.getFacets().putAsLabel(NAME, aspectName);
		}
		if ( !aspectName.equals(DEFAULT) && hasAspect(aspectName) ) {
			duplicateError(ce, getAspect(aspectName));
		}
		if ( aspects == null ) {
			aspects = new LinkedHashMap<String, StatementDescription>();
		}
		aspects.put(aspectName, ce);
	}

	public StatementDescription getAspect(final String aName) {
		return aspects == null ? null : aspects.get(aName);
	}

	public Collection<String> getAspectNames() {
		return aspects == null ? Collections.EMPTY_LIST : aspects.keySet();
	}

	public IArchitecture getControl() {
		return control;
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
		return aspects != null && aspects.containsKey(a);
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
		if ( parent != null ) {
			retVal.addAll(getParent().getSelfAndParentMicroSpecies());
		}

		return retVal;
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		if ( hasMicroSpecies() ) {
			SpeciesDescription retVal = microSpecies.get(name);
			if ( retVal != null ) { return retVal; }
		}
		if ( parent != null ) { return getParent().getMicroSpecies(name); }
		return null;
	}

	@Override
	public String toString() {
		return "Description of " + getName();
	}

	public IAgentConstructor getAgentConstructor() {
		if ( agentConstructor == null && parent != null ) {
			agentConstructor = ((SpeciesDescription) parent).getAgentConstructor();
		}
		return agentConstructor;
	}

	protected void setAgentConstructor(IAgentConstructor agentConstructor) {
		this.agentConstructor = agentConstructor;
	}

	public void addSkill(final Class c) {
		if ( c != null && ISkill.class.isAssignableFrom(c) && !c.isInterface() &&
			!Modifier.isAbstract(c.getModifiers()) ) {
			skills.put(c, null);
		}
	}

	@Override
	public Set<Class> getSkillClasses() {
		return skills.keySet();
	}

	public SpeciesDescription getMacroSpecies() {
		return macroSpecies;
	}

	@Override
	public SpeciesDescription getParent() {
		return (SpeciesDescription) super.getParent();
	}

	@Override
	public void inheritFromParent() {
		SpeciesDescription parent = getParent();
		if ( parent != null ) {
			if ( !parent.getJavaBase().isAssignableFrom(getJavaBase()) ) {
				// if ( javaBase == GamlAgent.class ) { // default base class
				// javaBase = parent.javaBase;
				// agentConstructor = parent.agentConstructor;
				// } else {
				error("Species " + getName() + " Java base class (" + getJavaBase().getSimpleName() +
					") is not a subclass of its parent species " + parent.getName() + " base class (" +
					parent.getJavaBase().getSimpleName() + ")", IGamlIssue.GENERAL);
				// }
			}
			// GuiUtils.debug(" **** " + getName() + " inherits from " + parent.getName());
			inheritSkills(parent);
			inheritBehaviors(parent);
			inheritInits(parent);
			inheritAspects(parent);
			super.inheritFromParent();
		}

	}

	private void inheritAspects(final SpeciesDescription parent) {
		if ( parent.aspects != null ) {
			for ( final String aName : parent.aspects.keySet() ) {
				if ( !hasAspect(aName) ) {
					addChild(parent.getAspect(aName).copy(this));
				}
			}
		}
	}

	private void inheritInits(final SpeciesDescription parent) {
		if ( parent.inits != null ) {
			for ( final StatementDescription init : parent.inits ) {
				addChild(init.copy(this));
			}
		}
	}

	private void inheritBehaviors(final SpeciesDescription parent) {
		// We only copy the behaviors that are not redefined in this species
		if ( parent.behaviors != null ) {
			for ( final StatementDescription b : parent.behaviors.values() ) {
				if ( !hasBehavior(b.getName()) ) {
					// Copy done here
					addChild(b.copy(this));
				}
			}
		}
	}

	private void inheritSkills(final SpeciesDescription parent) {
		for ( Map.Entry<Class, ISkill> entry : parent.skills.entrySet() ) {
			if ( !skills.containsKey(entry.getKey()) ) {
				skills.put(entry.getKey(), entry.getValue());
			}
		}
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
			currentSpeciesDesc = currentSpeciesDesc.getParent();
		}
		return result;
	}

	public boolean isGrid() {
		return getKeyword().equals(GRID);
	}

	@Override
	public String getTitle() {
		return "species " + getName();
	}

	@Override
	public String getDocumentation() {
		String parentName = getParent() == null ? "nil" : getParent().getName();
		String hostName = getMacroSpecies() == null ? null : getMacroSpecies().getName();
		String result = "<b>Subspecies of: </b>" + parentName + "<br>";
		if ( hostName != null ) {
			result += "<b>Microspecies of:</b>" + hostName + "<br>";
		}
		result += "<b>Skills:</b> " + getSkillsNames() + "<br>";
		result += "<b>Attributes:</b> " + getVarNames() + "<br>";
		result += "<b>Actions: </b>" + getActionNames() + "<br>";
		return result;
	}

	public Set<String> getSkillsNames() {
		Set<String> names = new LinkedHashSet();
		for ( ISkill skill : skills.values() ) {
			names.add(AbstractGamlAdditions.getSkillNameFor(skill.getClass()));
		}
		if ( getParent() != null ) {
			names.addAll(getParent().getSkillsNames());
		}
		return names;
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
			microSpec.verifyParent();
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
	public TypeDescription getVisibleSpecies(final String speciesName) {
		for ( TypeDescription visibleSpec : getVisibleSpecies() ) {
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
	protected void verifyParent() {
		if ( parent == null ) { return; }
		if ( this == parent ) {
			error(getName() + " species can't be a sub-species of itself", IGamlIssue.GENERAL);
			return;
		}
		List<SpeciesDescription> candidates = this.getPotentialParentSpecies();
		TypeDescription potentialParent = null;
		if ( candidates.contains(parent) ) {
			potentialParent = parent;
		}
		if ( potentialParent == null ) {

			// List<String> availableSpecies = new GamaList<String>();
			// for ( TypeDescription p : candidates ) {
			// availableSpecies.add(p.getName());
			// }
			// availableSpecies.remove(availableSpecies.size() - 1);

			error(parent.getName() + " can't be a parent species of " + this.getName() + " species.",
				IGamlIssue.WRONG_PARENT, PARENT);

			return;
		}

		List<SpeciesDescription> parentsOfParent = ((SpeciesDescription) potentialParent).getSelfWithParents();
		if ( parentsOfParent.contains(this) ) {
			String error =
				this.getName() + " species and " + potentialParent.getName() +
					" species can't be sub-species of each other.";
			potentialParent.error(error);
			error(error);
			return;
		}

		// TODO Commented because the test does not make sense
		// if ( this.getAllMicroSpecies().contains(parentsOfParent) ) {
		// flagError(
		// this.getName() + " species can't be a sub-species of " + potentialParent.getName() +
		// " species because a species can't be sub-species of its direct or indirect micro-species.",
		// IGamlIssue.GENERAL);
		// return null;
		// }

	}

	/**
	 * Finalizes the species description
	 * + Copy the behaviors, attributes from parent;
	 * + Creates the control if necessary.
	 * Add a variable representing the population of each micro-species
	 * 
	 * @throws GamlException
	 */
	public void finalizeDescription() {
		if ( isMirror() ) {
			// TODO Try to find automatically the type given the "MIRROR" expression
			addChild(DescriptionFactory.create(AGENT, this, NAME, TARGET));
		}
		// GuiUtils.debug("Finalizing the description of :" + getName());
		control = (IArchitecture) AbstractGamlAdditions.getSkillInstanceFor(getControlName());
		buildSharedSkills();
		// recursively finalize the sorted micro-species
		for ( final SpeciesDescription microSpec : sortedMicroSpecies() ) {
			microSpec.finalizeDescription();
			if ( !microSpec.isExperiment() ) {
				VariableDescription var =
					(VariableDescription) DescriptionFactory
						.create(IKeyword.CONTAINER, this, NAME, microSpec.getName()); // CONST = TRUE ?
				var.setContentType(microSpec.getType());
				// We compute the dependencies of micro species with respect to the variables
				// defined in the macro species.
				IExpressionDescription exp = microSpec.getFacets().get(DEPENDS_ON);
				Set<String> dependencies = exp == null ? new LinkedHashSet() : exp.getStrings(this, false);
				for ( VariableDescription v : microSpec.getVariables().values() ) {
					dependencies.addAll(v.getExtraDependencies());
				}
				dependencies.add(SHAPE);
				dependencies.add(LOCATION);
				var.getFacets().put(DEPENDS_ON, new StringListExpressionDescription(dependencies));
				// GuiUtils.debug("The population of " + microSpec.getName() + " depends on: " + dependencies + " in " +
				// getName());
				IVarGetter get = new VarGetter(null) {

					@Override
					public Object run(IScope scope, IAgent agent, ISkill skill) throws GamaRuntimeException {
						return agent.getMicroPopulation(microSpec.getName());
					}
				};
				IVarSetter set = new VarSetter(null) {

					@Override
					public void run(IScope scope, IAgent agent, ISkill target, Object value)
						throws GamaRuntimeException {}

				};
				IVarGetter init = new VarGetter(null) {

					@Override
					public Object run(IScope scope, IAgent agent, ISkill skill) throws GamaRuntimeException {
						agent.initializeMicroPopulation(scope, microSpec.getName());
						return agent.getMicroPopulation(microSpec.getName());
					}

				};
				var.addHelpers(get, init, set);
				addChild(var);
			}
		}
		sortVars();
	}

	public boolean isExperiment() {
		return false;
	}

	boolean hasMicroSpecies() {
		return microSpecies != null;
	}

	public Map<String, SpeciesDescription> getMicroSpecies() {
		if ( microSpecies == null ) {
			microSpecies = new LinkedHashMap<String, SpeciesDescription>();
		}
		return microSpecies;
	}

	public boolean isMirror() {
		return facets.containsKey(MIRRORS);
	}

	public Boolean implementsSkill(final String skill) {
		return skills.containsKey(AbstractGamlAdditions.getSkillClassFor(skill));
	}

	public Map<Class, ISkill> getSkills() {
		return skills;
	}

	public void setGlobal(boolean global) {
		isGlobal = global;
	}

	public boolean isGlobal() {
		return isGlobal;
	}

}
