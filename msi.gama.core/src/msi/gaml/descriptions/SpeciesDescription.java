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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.MinimalGridAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.*;
import msi.gaml.skills.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import org.eclipse.emf.ecore.EObject;

public class SpeciesDescription extends TypeDescription {

	private Map<String, StatementDescription> behaviors, aspects;
	private List<StatementDescription> inits;
	protected final Map<Class, ISkill> skills = new HashMap();
	protected IArchitecture control;
	private Map<String, SpeciesDescription> microSpecies;
	private IAgentConstructor agentConstructor;

	public SpeciesDescription(final String keyword, final IDescription macroDesc, final ChildrenProvider cp,
		final EObject source, final Facets facets) {
		this(keyword, null, macroDesc, null, cp, source, facets);
	}

	public SpeciesDescription(final String keyword, final Class clazz, final IDescription macroDesc,
		final IDescription parent, final ChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, clazz, macroDesc, parent, cp, source, facets);
		setSkills(facets.get(SKILLS), Collections.EMPTY_SET);
	}

	/**
	 * This constructor is only called to build built-in species. The parent is passed directly as there is no
	 * ModelFactory to provide it
	 */
	public SpeciesDescription(final String name, final Class clazz, final IDescription superDesc,
		final SpeciesDescription parent, final IAgentConstructor helper, final Set<String> skills2, final Facets ff) {
		super(SPECIES, clazz, superDesc, null, ChildrenProvider.NONE, null, new Facets(KEYWORD, SPECIES, NAME, name));
		if ( ff.containsKey(CONTROL) ) {
			facets.putAsLabel(CONTROL, ff.get(CONTROL).toString());
		}
		setSkills(ff.get(SKILLS), skills2);
		setParent(parent);
		setAgentConstructor(helper);
	}

	@Override
	public void dispose() {
		if ( isBuiltIn() ) { return; }
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
		// macroSpecies = null;

		if ( microSpecies != null ) {
			microSpecies.clear();
		}
		if ( inits != null ) {
			inits.clear();
		}
		super.dispose();
		// isDisposed = true;
	}

	protected void setSkills(final IExpressionDescription userDefinedSkills, final Set<String> builtInSkills) {
		final Set<String> skillNames = new LinkedHashSet();
		/* We try to add the control architecture if any is defined */
		if ( facets.containsKey(CONTROL) ) {
			String control = facets.getLabel(CONTROL);
			if ( control == null ) {
				warning("This control  does not belong to the list of known agent controls (" +
					AbstractGamlAdditions.ARCHITECTURES + ")", IGamlIssue.WRONG_CONTEXT, CONTROL);
			} else {
				ISkill skill = AbstractGamlAdditions.getSkillInstanceFor(control);
				if ( skill == null ) {
					warning("The control " + control + " does not belong to the list of known agent controls (" +
						AbstractGamlAdditions.ARCHITECTURES + ")", IGamlIssue.WRONG_CONTEXT, CONTROL);
				}
			}
			skillNames.add(control);
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
		for ( final String skillName : skillNames ) {
			final Class skillClass = AbstractGamlAdditions.getSkillClasses().get(skillName);
			if ( skillClass != null ) {
				addSkill(skillClass);
			}
		}

	}

	public String getControlName() {
		String controlName = facets.getLabel(CONTROL);
		// if the "control" is not explicitly declared then inherit it from the parent species.
		// Takes care of invalid species (see Issue 711)
		if ( controlName == null ) {
			if ( parent != null && parent != this ) {
				controlName = getParent().getControlName();
			} else {
				controlName = REFLEX;
			}
		}
		return controlName;
	}

	public ISkill getSkillFor(final Class clazz) {
		final ISkill skill = skills.get(clazz);
		if ( skill == null && clazz != null ) {
			for ( final Map.Entry<Class, ISkill> entry : skills.entrySet() ) {
				if ( clazz.isAssignableFrom(entry.getKey()) ) { return entry.getValue(); }
			}
		}
		return skill;
	}

	private void buildSharedSkills() {
		// Necessary in order to prevent concurrentModificationExceptions
		final Set<Class> classes = new HashSet(skills.keySet());
		for ( final Class c : classes ) {
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
		final IDescription desc = super.addChild(child);
		if ( desc == null ) { return null; }
		if ( desc instanceof StatementDescription ) {
			// FIXME Move this to TypeDescription !
			final StatementDescription statement = (StatementDescription) desc;
			final String kw = desc.getKeyword();
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
			final ModelDescription md = getModelDescription();
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
		final String behaviorName = r.getName();
		if ( behaviors == null ) {
			behaviors = new LinkedHashMap<String, StatementDescription>();
		}
		final StatementDescription existing = behaviors.get(behaviorName);
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
		final List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		if ( hasMicroSpecies() ) {
			retVal.addAll(getMicroSpecies().values());

			for ( final SpeciesDescription micro : getMicroSpecies().values() ) {
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
		final GamaList<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();
		if ( hasMicroSpecies() ) {
			retVal.addAll(getMicroSpecies().values());
		}
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this ) {
			retVal.addAll(getParent().getSelfAndParentMicroSpecies());
		}

		return retVal;
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		if ( hasMicroSpecies() ) {
			final SpeciesDescription retVal = microSpecies.get(name);
			if ( retVal != null ) { return retVal; }
		}
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this ) { return getParent().getMicroSpecies(name); }
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

	protected void setAgentConstructor(final IAgentConstructor agentConstructor) {
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
		final IDescription d = getEnclosingDescription();
		if ( d instanceof SpeciesDescription ) { return (SpeciesDescription) d; }
		return null;
	}

	@Override
	public SpeciesDescription getParent() {
		return (SpeciesDescription) super.getParent();
	}

	@Override
	public void inheritFromParent() {
		final SpeciesDescription parent = getParent();
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this ) {
			if ( !parent.getJavaBase().isAssignableFrom(getJavaBase()) ) {
				error("Species " + getName() + " Java base class (" + getJavaBase().getSimpleName() +
					") is not a subclass of its parent species " + parent.getName() + " base class (" +
					parent.getJavaBase().getSimpleName() + ")", IGamlIssue.GENERAL);
				// }
			}
			// GuiUtils.debug(" **** " + getName() + " inherits from " + parent.getName());
			inheritMicroSpecies(parent);
			inheritSkills(parent);
			inheritBehaviors(parent);
			inheritInits(parent);
			inheritAspects(parent);
			super.inheritFromParent();
		}

	}

	// FIXME HACK !
	private void inheritMicroSpecies(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if ( parent == null || parent == this ) { return; }
		for ( final Map.Entry<String, SpeciesDescription> entry : parent.getMicroSpecies().entrySet() ) {
			if ( !getMicroSpecies().containsKey(entry.getKey()) ) {
				getMicroSpecies().put(entry.getKey(), entry.getValue());
				children.add(entry.getValue());
			}
		}
	}

	private void inheritAspects(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this && parent.aspects != null ) {
			for ( final String aName : parent.aspects.keySet() ) {
				if ( !hasAspect(aName) ) {
					addChild(parent.getAspect(aName).copy(this));
				}
			}
		}
	}

	private void inheritInits(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this && parent.inits != null ) {
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
		for ( final Map.Entry<Class, ISkill> entry : parent.skills.entrySet() ) {
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
		final List<SpeciesDescription> result = new GamaList<SpeciesDescription>();
		SpeciesDescription currentSpeciesDesc = this;
		while (currentSpeciesDesc != null) {
			result.add(0, currentSpeciesDesc);
			SpeciesDescription parent = currentSpeciesDesc.getParent();
			// Takes care of invalid species (see Issue 711)
			if ( parent == currentSpeciesDesc ) {
				break;
			} else {
				currentSpeciesDesc = parent;
			}
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
		final String parentName = getParent() == null ? "nil" : getParent().getName();
		final String hostName = getMacroSpecies() == null ? null : getMacroSpecies().getName();
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
		final Set<String> names = new LinkedHashSet();
		for ( final ISkill skill : skills.values() ) {
			if ( skill != null ) {
				names.add(AbstractGamlAdditions.getSkillNameFor(skill.getClass()));
			}
		}
		// Takes care of invalid species (see Issue 711)
		if ( getParent() != null && getParent() != this ) {
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
		final List<SpeciesDescription> retVal = getVisibleSpecies();
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
		final Collection<SpeciesDescription> allMicroSpecies = getMicroSpecies().values();
		// validate and set the parent parent of each micro-species
		for ( final SpeciesDescription microSpec : allMicroSpecies ) {
			microSpec.verifyParent();
		}

		final List<SpeciesDescription> sortedMicroSpecs = new GamaList<SpeciesDescription>();
		for ( final SpeciesDescription microSpec : allMicroSpecies ) {
			final List<SpeciesDescription> parents = microSpec.getSelfWithParents();

			for ( final SpeciesDescription p : parents ) {
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
		final List<SpeciesDescription> retVal = new GamaList<SpeciesDescription>();

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
		for ( final TypeDescription visibleSpec : getVisibleSpecies() ) {
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
		final List<SpeciesDescription> candidates = this.getPotentialParentSpecies();
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

		final List<SpeciesDescription> parentsOfParent = ((SpeciesDescription) potentialParent).getSelfWithParents();
		if ( parentsOfParent.contains(this) ) {
			final String error =
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
		// super.finalizeDescription();
		if ( isMirror() ) {
			// TODO Try to find automatically the type given the "MIRROR" expression
			IExpression expr = facets.getExpr(MIRRORS);
			addChild(DescriptionFactory.create(AGENT, this, NAME, TARGET));
		}

		control = (IArchitecture) AbstractGamlAdditions.getSkillInstanceFor(getControlName());
		buildSharedSkills();
		// recursively finalize the sorted micro-species
		for ( final SpeciesDescription microSpec : sortedMicroSpecies() ) {
			microSpec.finalizeDescription();
			if ( !microSpec.isExperiment() ) {
				final VariableDescription var =
					(VariableDescription) DescriptionFactory.create(CONTAINER, this, NAME, microSpec.getName(), OF,
						microSpec.getName()); // CONST = TRUE ?
				// FIXME : OF, microSpec.getName() ??
				// var.setContentType(microSpec.getType());
				// We compute the dependencies of micro species with respect to the variables
				// defined in the macro species.
				final IExpressionDescription exp = microSpec.getFacets().get(DEPENDS_ON);
				final Set<String> dependencies = exp == null ? new LinkedHashSet() : exp.getStrings(this, false);
				for ( final VariableDescription v : microSpec.getVariables().values() ) {
					dependencies.addAll(v.getExtraDependencies());
				}
				dependencies.add(SHAPE);
				dependencies.add(LOCATION);
				var.getFacets().put(DEPENDS_ON, new StringListExpressionDescription(dependencies));
				// GuiUtils.debug("The population of " + microSpec.getName() + " depends on: " + dependencies + " in " +
				// getName());
				final GamaHelper get = new GamaHelper() {

					@Override
					public Object run(final IScope scope, final IAgent agent, final ISkill skill,
						final Object ... values) throws GamaRuntimeException {
						// TODO Make a test ?
						return ((IMacroAgent) agent).getMicroPopulation(microSpec.getName());
					}
				};
				final GamaHelper set = new GamaHelper() {

					@Override
					public Object run(final IScope scope, final IAgent agent, final ISkill target,
						final Object ... value) throws GamaRuntimeException {
						return null;
					}

				};
				final GamaHelper init = new GamaHelper(null) {

					@Override
					public Object run(final IScope scope, final IAgent agent, final ISkill skill,
						final Object ... values) throws GamaRuntimeException {
						((IMacroAgent) agent).initializeMicroPopulation(scope, microSpec.getName());
						return ((IMacroAgent) agent).getMicroPopulation(microSpec.getName());
					}

				};
				var.addHelpers(get, init, set);
				addChild(var);
			}
		}
		sortVars();
	}

	@Override
	protected void validateChildren() {
		IExpression mirrors = getFacets().getExpr(MIRRORS);
		if ( mirrors != null ) {
			// We try to change the type of the 'target' variable if the expression contains only agents from the
			// same species
			IType t = mirrors.getContentType();
			if ( t.isSpeciesType() && t.id() != IType.AGENT ) {
				VariableDescription v = getVariable(TARGET);
				if ( v != null ) {
					// In case, but should not be null
					v.setType(t);
					info("The 'target' variable will be of type " + t.getSpeciesName(), IGamlIssue.GENERAL, MIRRORS);
				}
			} else {
				info("No common species detected in 'mirrors'. The 'target' variable will be of generic type 'agent'",
					IGamlIssue.WRONG_TYPE, MIRRORS);
			}
		}
		super.validateChildren();
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

	@Override
	public Class getJavaBase() {
		// FIXME HACK Remove at some point in the future
		if ( isGrid() ) {
			if ( !facets.containsKey("use_regular_agents") || TRUE.equals(facets.getLabel("use_regular_agents")) ) {
				javaBase = GamlAgent.class;
			} else {
				javaBase = MinimalGridAgent.class;
			}
			return javaBase;
		}
		if ( getName().equals(AGENT) ) {
			javaBase = MinimalAgent.class;
			return javaBase;
		}
		// Takes care of invalid species (see Issue 711)
		if ( javaBase == null && parent != null && parent != this ) {
			javaBase = getParent().getJavaBase();
		}
		if ( javaBase == MinimalAgent.class ) {
			javaBase = GamlAgent.class;
		}
		return javaBase;
	}

	/**
	 * @param found_sd
	 * @return
	 */
	public boolean hasMacroSpecies(final SpeciesDescription found_sd) {
		SpeciesDescription sd = getMacroSpecies();
		if ( sd == null ) { return false; }
		if ( sd.equals(found_sd) ) { return true; }
		return sd.hasMacroSpecies(found_sd);
	}

	/**
	 * @param macro
	 * @return
	 */
	public boolean hasParent(final SpeciesDescription p) {
		SpeciesDescription sd = getParent();
		// Takes care of invalid species (see Issue 711)
		if ( sd == null || sd == this ) { return false; }
		if ( sd.equals(p) ) { return true; }
		return sd.hasMacroSpecies(p);
	}

}
