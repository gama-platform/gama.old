/*********************************************************************************************
 *
 *
 * 'SpeciesDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IVarAndActionSupport;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.GamlGridAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.MinimalGridAgent;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.descriptions.SymbolSerializer.SpeciesSerializer;
import msi.gaml.expressions.DenotedActionExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.SpeciesConstantExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.skills.ISkill;
import msi.gaml.statements.Facets;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

public class SpeciesDescription extends TypeDescription {

	private Map<String, StatementDescription> behaviors;
	private Map<String, StatementDescription> aspects;
	private Map<String, SpeciesDescription> microSpecies;
	protected final Map<Class<? extends ISkill>, ISkill> skills = new THashMap();
	protected IArchitecture control;
	private IAgentConstructor agentConstructor;
	private SpeciesConstantExpression speciesExpr;

	public SpeciesDescription(final String keyword, final IDescription macroDesc, final ChildrenProvider cp,
			final EObject source, final Facets facets, final String plugin) {
		this(keyword, null, macroDesc, null, cp, source, facets, plugin);
	}

	public SpeciesDescription(final String keyword, final Class clazz, final IDescription macroDesc,
			final IDescription parent, final ChildrenProvider cp, final EObject source, final Facets facets,
			final String plugin) {
		super(keyword, clazz, macroDesc, parent, cp, source, facets, plugin);
		setSkills(facets.get(SKILLS), Collections.EMPTY_SET);
	}

	/**
	 * This constructor is only called to build built-in species. The parent is
	 * passed directly as there is no ModelFactory to provide it
	 */
	public SpeciesDescription(final String name, final Class clazz, final IDescription superDesc,
			final SpeciesDescription parent, final IAgentConstructor helper, final Set<String> skills2, final Facets ff,
			final String plugin) {
		super(SPECIES, clazz, superDesc, null, ChildrenProvider.NONE, null, new Facets(KEYWORD, SPECIES, NAME, name),
				plugin);
		setParent(parent);
		setSkills(ff.get(SKILLS), skills2);
		setAgentConstructor(helper);
	}

	@Override
	public SymbolSerializer createSerializer() {
		return new SpeciesSerializer();
	}

	@Override
	public void dispose() {
		if (isBuiltIn()) {
			return;
		}
		if (behaviors != null) {
			behaviors.clear();
		}
		if (aspects != null) {
			aspects.clear();
		}
		skills.clear();
		if (control != null) {
			control.dispose();
		}
		// macroSpecies = null;

		if (microSpecies != null) {
			microSpecies.clear();
		}
		// if ( inits != null ) {
		// inits.clear();
		// }
		super.dispose();
		// isDisposed = true;
	}

	protected void setSkills(final IExpressionDescription userDefinedSkills, final Set<String> builtInSkills) {
		final Set<ISkill> skillInstances = new TLinkedHashSet();
		/* We try to add the control architecture if any is defined */
		final IExpressionDescription c = facets.get(CONTROL);
		if (c != null) {
			c.compile(this);
			final Object temp = c.getExpression().value(null);
			if (!(temp instanceof AbstractArchitecture)) {
				warning("This control  does not belong to the list of known agent controls ("
						+ AbstractGamlAdditions.ARCHITECTURES + ")", IGamlIssue.WRONG_CONTEXT, CONTROL);
			} else {
				control = (IArchitecture) temp;
				// We add it explicitly so as to add the variables and actions
				// defined in the control. No need to add it in the other cases
				skillInstances.add(control);
			}
		}
		// else {
		// if ( parent instanceof SpeciesDescription ) {
		// control = ((SpeciesDescription) parent).getControl();
		// if ( control != null ) {
		// control = (IArchitecture) control.duplicate();
		// }
		// }
		// }
		// if ( control == null ) {
		// control = (IArchitecture)
		// AbstractGamlAdditions.getSkillInstanceFor(REFLEX);
		// }

		/* We add the keyword as a possible skill (used for 'grid' species) */
		final ISkill skill = AbstractGamlAdditions.getSkillInstanceFor(getKeyword());
		if (skill != null) {
			skillInstances.add(skill);
		}
		/*
		 * We add the user defined skills (i.e. as in 'species a skills: [s1,
		 * s2...]')
		 */
		if (userDefinedSkills != null) {
			final IExpression expr = userDefinedSkills.compile(this);
			if (expr != null && expr.isConst()) {
				final IList<ISkill> skills = (IList<ISkill>) expr.value(null);
				skillInstances.addAll(skills);
			}
			// skillNames.addAll(userDefinedSkills.getStrings(this, true));
		}
		/*
		 * We add the skills that are defined in Java, either
		 * using @species(value='a', skills= {s1,s2}), or @skill(value="s1",
		 * attach_to="a")
		 */
		for (final String s : builtInSkills) {
			final ISkill sk = AbstractGamlAdditions.getSkillInstanceFor(s);
			if (sk != null) {
				skillInstances.add(sk);
			}
		}
		// skillNames.addAll(builtInSkills);

		/* We then create the list of classes from this list of names */
		for (final ISkill skillInstance : skillInstances) {
			if (skillInstance == null) {
				continue;
			}
			final Class<? extends ISkill> skillClass = skillInstance.getClass();
			addSkill(skillClass, skillInstance);
			// if ( skillInstance == control ) {
			// Class<? extends ISkill> clazz = skillClass;
			// while (clazz != AbstractArchitecture.class) {
			// skills.put(clazz, control);
			// clazz = (Class<? extends ISkill>) clazz.getSuperclass();
			// }
			//
			// }
		}

	}

	public String getControlName() {
		String controlName = facets.getLabel(CONTROL);
		// if the "control" is not explicitly declared then inherit it from the
		// parent species.
		// Takes care of invalid species (see Issue 711)
		if (controlName == null) {
			if (parent != null && parent != this) {
				controlName = getParent().getControlName();
			} else {
				controlName = REFLEX;
			}
		}
		return controlName;
	}

	public ISkill getSkillFor(final Class clazz) {
		final ISkill skill = skills.get(clazz);
		if (skill == null && clazz != null) {
			for (final Map.Entry<Class<? extends ISkill>, ISkill> entry : skills.entrySet()) {
				if (clazz.isAssignableFrom(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		// We go and try to find the skill in the parent
		if (skill == null && parent != null && parent != this) {
			return getParent().getSkillFor(clazz);
		}
		return skill;
	}

	// private void buildSharedSkills() {
	// // Necessary in order to prevent concurrentModificationExceptions
	// final Set<Class<? extends ISkill>> classes = new
	// THashSet(skills.keySet());
	// for ( final Class c : classes ) {
	// Class clazz = c;
	// if ( IArchitecture.class.isAssignableFrom(clazz) && control != null ) {
	// while (clazz != AbstractArchitecture.class) {
	// skills.put(clazz, control);
	// clazz = clazz.getSuperclass();
	// }
	// }
	// // else {
	// // skills.put(clazz, AbstractGamlAdditions.getSkillInstanceFor(c));
	// // }
	// }
	// }

	public String getParentName() {
		return facets.getLabel(PARENT);
	}

	@Override
	public IExpression getVarExpr(final String n, final boolean asField) {
		IExpression result = super.getVarExpr(n, asField);
		if (result == null) {
			IDescription desc = getBehavior(n);
			if (desc != null) {
				result = new DenotedActionExpression(desc);
			}
			desc = getAspect(n);
			if (desc != null) {
				result = new DenotedActionExpression(desc);
			}
		}
		return result;
	}

	@Override
	public IDescription addChild(final IDescription child) {
		final IDescription desc = super.addChild(child);
		if (desc == null) {
			return null;
		}
		if (desc instanceof StatementDescription) {
			final StatementDescription statement = (StatementDescription) desc;
			final String kw = desc.getKeyword();
			if (PRIMITIVE.equals(kw) || ACTION.equals(kw)) {
				addAction(this, statement);
			} else if (ASPECT.equals(kw)) {
				addAspect(statement);
			} else {
				addBehavior(statement);
			}
		} else if (desc instanceof VariableDescription) {
			addOwnVariable((VariableDescription) desc);
		} else if (desc instanceof SpeciesDescription) {
			if (!isModel() && ((SpeciesDescription) desc).isGrid()) {
				desc.error("For the moment, grids cannot be defined as micro-species anywhere else than in the model");
			}
			// final ModelDescription md = getModelDescription();
			// if ( md != null ) {
			// md.addSpeciesType((TypeDescription) desc);
			// }
			getMicroSpecies().put(desc.getName(), (SpeciesDescription) desc);
		}
		return desc;
	}

	private void addBehavior(final StatementDescription r) {
		final String behaviorName = r.getName();
		if (behaviors == null) {
			behaviors = new TOrderedHashMap<String, StatementDescription>();
		}
		final StatementDescription existing = behaviors.get(behaviorName);
		if (existing != null) {
			if (existing.getKeyword().equals(r.getKeyword())) {
				duplicateInfo(r, existing);
				// children.remove(existing);
			}
		}
		behaviors.put(behaviorName, r);
	}

	public boolean hasBehavior(final String a) {
		return behaviors != null && behaviors.containsKey(a);

		// || parent != null &&
		// ((SpeciesDescription) parent).hasBehavior(a);
	}

	public StatementDescription getBehavior(final String s) {
		return behaviors != null ? behaviors.get(s) : null;
	}

	private void addAspect(final StatementDescription ce) {
		String aspectName = ce.getName();
		if (aspectName == null) {
			aspectName = DEFAULT;
			ce.getFacets().putAsLabel(NAME, aspectName);
		}
		if (!aspectName.equals(DEFAULT) && hasAspect(aspectName)) {
			duplicateInfo(ce, getAspect(aspectName));
		}
		if (aspects == null) {
			aspects = new TOrderedHashMap<String, StatementDescription>();
		}
		aspects.put(aspectName, ce);
	}

	public StatementDescription getAspect(final String aName) {
		// if ( aspects != null && aspects.containsKey(aName) ) { return
		// aspects.get(aName); }
		// return parent == null ? null : ((SpeciesDescription)
		// parent).getAspect(aName);
		return aspects == null ? null : aspects.get(aName);
	}

	public Collection<String> getAspectNames() {
		// Set<String> names = new HashSet();
		// if ( aspects != null ) {
		// names.addAll(aspects.keySet());
		// }
		// if ( parent != null ) {
		// names.addAll(((SpeciesDescription) parent).getAspectNames());
		// }
		// return names;
		return aspects == null ? Collections.EMPTY_LIST : aspects.keySet();
	}

	public Collection<StatementDescription> getAspects() {
		return aspects == null ? Collections.EMPTY_LIST : aspects.values();
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
		final List<SpeciesDescription> retVal = new ArrayList<SpeciesDescription>();
		if (hasMicroSpecies()) {
			retVal.addAll(getMicroSpecies().values());

			for (final SpeciesDescription micro : getMicroSpecies().values()) {
				retVal.addAll(micro.getAllMicroSpecies());
			}
		}
		return retVal;
	}

	@Override
	public boolean hasAspect(final String a) {
		return aspects != null && aspects.containsKey(a);

		// || parent != null && parent.hasAspect(a);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return this;
	}

	public List<SpeciesDescription> getSelfAndParentMicroSpecies() {
		final ArrayList<SpeciesDescription> retVal = new ArrayList<SpeciesDescription>();
		if (hasMicroSpecies()) {
			retVal.addAll(getMicroSpecies().values());
		}
		// Takes care of invalid species (see Issue 711)
		if (parent != null && parent != this) {
			retVal.addAll(getParent().getSelfAndParentMicroSpecies());
		}

		return retVal;
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		if (hasMicroSpecies()) {
			final SpeciesDescription retVal = microSpecies.get(name);
			if (retVal != null) {
				return retVal;
			}
		}
		// Takes care of invalid species (see Issue 711)
		if (parent != null && parent != this) {
			return getParent().getMicroSpecies(name);
		}
		return null;
	}

	@Override
	public String toString() {
		return "Description of " + getName();
	}

	public IAgentConstructor getAgentConstructor() {
		if (agentConstructor == null && parent != null) {
			if (parent.getJavaBase() == getJavaBase()) {
				agentConstructor = ((SpeciesDescription) parent).getAgentConstructor();
			} else {
				agentConstructor = IAgentConstructor.CONSTRUCTORS.get(getJavaBase());
			}
			System.out.println("Agent constructor for " + this + " based on :" + getJavaBase());
		}
		return agentConstructor;
	}

	protected void setAgentConstructor(final IAgentConstructor agentConstructor) {
		this.agentConstructor = agentConstructor;
	}

	public void addSkill(final Class<? extends ISkill> c, final ISkill instance) {
		if (c != null && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
			skills.put(c, instance);
		}
	}

	@Override
	public Set<Class<? extends ISkill>> getSkillClasses() {
		return skills.keySet();
	}

	public SpeciesDescription getMacroSpecies() {
		final IDescription d = getEnclosingDescription();
		if (d instanceof SpeciesDescription) {
			return (SpeciesDescription) d;
		}
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
		// built-in parents are not considered as their actions/variables are
		// normally already copied as java additions
		if (parent != null && parent.getJavaBase() == null) {
			error("Species " + parent.getName() + " Java base class can not be found. No validation is possible.",
					IGamlIssue.GENERAL);
			return;
		}
		if (getJavaBase() == null) {
			error("Species " + getName() + " Java base class can not be found. No validation is possible.",
					IGamlIssue.GENERAL);
			return;
		}
		if (parent != null && parent != this && !parent.isBuiltIn() && parent.getJavaBase() != null) {
			if (!parent.getJavaBase().isAssignableFrom(getJavaBase())) {
				error("Species " + getName() + " Java base class (" + getJavaBase().getSimpleName()
						+ ") is not a subclass of its parent species " + parent.getName() + " base class ("
						+ parent.getJavaBase().getSimpleName() + ")", IGamlIssue.GENERAL);
				// }
			}
			// scope.getGui().debug(" **** " + getName() + " inherits from " +
			// parent.getName());
			inheritMicroSpecies(parent);
			inheritBehaviors(parent);
			inheritAspects(parent);
			super.inheritFromParent();
		}

	}

	// FIXME HACK !
	private void inheritMicroSpecies(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if (parent == null || parent == this) {
			return;
		}
		for (final Map.Entry<String, SpeciesDescription> entry : parent.getMicroSpecies().entrySet()) {
			if (!getMicroSpecies().containsKey(entry.getKey())) {
				getMicroSpecies().put(entry.getKey(), entry.getValue());
				// children.add(entry.getValue());
			}
		}
	}

	private void inheritAspects(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if (parent != null && parent != this && parent.aspects != null) {
			for (final String aName : parent.aspects.keySet()) {
				if (!hasAspect(aName)) {
					addChild(parent.getAspect(aName).copy(this));
				}
			}
		}
	}

	private void inheritBehaviors(final SpeciesDescription parent) {
		// We only copy the behaviors that are not redefined in this species
		if (parent.behaviors != null) {
			for (final StatementDescription b : parent.behaviors.values()) {
				if (!hasBehavior(b.getName())) {
					// Copy done here
					addChild(b.copy(this));
				}
			}
		}
	}

	/**
	 * @return
	 */
	public List<SpeciesDescription> getSelfWithParents() {
		// returns a reversed list of parents + self
		final List<SpeciesDescription> result = new ArrayList<SpeciesDescription>();
		SpeciesDescription currentSpeciesDesc = this;
		while (currentSpeciesDesc != null) {
			result.add(0, currentSpeciesDesc);
			final SpeciesDescription parent = currentSpeciesDesc.getParent();
			// Takes care of invalid species (see Issue 711)
			if (parent == currentSpeciesDesc) {
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
		return getKeyword() + " " + getName();
	}

	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append(getDocumentationWithoutMeta());
		sb.append(getMeta().getDocumentation());
		return sb.toString();
	}

	public String getDocumentationWithoutMeta() {
		final StringBuilder sb = new StringBuilder(200);
		final String parentName = getParent() == null ? "nil" : getParent().getName();
		final String hostName = getMacroSpecies() == null ? null : getMacroSpecies().getName();
		sb.append("<b>Subspecies of:</b> ").append(parentName).append("<br>");
		if (hostName != null) {
			sb.append("<b>Microspecies of:</b> ").append(hostName).append("<br>");
		}
		sb.append("<b>Skills:</b> ").append(getSkillsNames()).append("<br>");
		sb.append("<b>Attributes:</b> ").append(getVarNames()).append("<br>");
		sb.append("<b>Actions: </b>").append(getActionNames()).append("<br>");
		sb.append("<br/>");
		return sb.toString();
	}

	public Set<String> getSkillsNames() {
		final Set<String> names = new TLinkedHashSet();
		for (final ISkill skill : skills.values()) {
			if (skill != null) {
				names.add(AbstractGamlAdditions.getSkillNameFor(skill.getClass()));
			}
		}
		// Takes care of invalid species (see Issue 711)
		if (getParent() != null && getParent() != this) {
			names.addAll(getParent().getSkillsNames());
		}
		return names;
	}

	/**
	 * Returns the constant expression representing this species
	 */
	public SpeciesConstantExpression getSpeciesExpr() {
		if (speciesExpr == null) {
			final IType type = GamaType.from(SpeciesDescription.this);
			speciesExpr = GAML.getExpressionFactory().createSpeciesConstant(type);
		}
		return speciesExpr;
	}

	/**
	 * Returns a list of SpeciesDescription that can be the parent of this
	 * species. A species can be a sub-species of its "peer" species ("peer"
	 * species are species sharing the same direct macro-species).
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
	 * Sorts the micro-species. Parent micro-species are ahead of the list
	 * followed by sub micro-species.
	 *
	 * @return
	 */
	private List<SpeciesDescription> sortedMicroSpecies() {
		if (!hasMicroSpecies()) {
			return Collections.EMPTY_LIST;
		}
		final Collection<SpeciesDescription> allMicroSpecies = getMicroSpecies().values();
		// validate and set the parent parent of each micro-species
		for (final SpeciesDescription microSpec : allMicroSpecies) {
			microSpec.verifyParent();
		}

		final List<SpeciesDescription> sortedMicroSpecs = new ArrayList<SpeciesDescription>();
		for (final SpeciesDescription microSpec : allMicroSpecies) {
			final List<SpeciesDescription> parents = microSpec.getSelfWithParents();

			for (final SpeciesDescription p : parents) {
				if (!sortedMicroSpecs.contains(p) && allMicroSpecies.contains(p)) {
					sortedMicroSpecs.add(p);
				}
			}
		}

		return sortedMicroSpecs;
	}

	/**
	 * Returns a list of visible species from this species.
	 *
	 * A species can see the following species: 1. Its direct micro-species. 2.
	 * Its peer species. 3. Its direct&in-direct macro-species and their peers.
	 *
	 * @return
	 */
	public List<SpeciesDescription> getVisibleSpecies() {
		final List<SpeciesDescription> retVal = new ArrayList<SpeciesDescription>();

		SpeciesDescription currentSpec = this;
		while (currentSpec != null) {
			retVal.addAll(currentSpec.getSelfAndParentMicroSpecies());

			// "world" species
			if (currentSpec.getMacroSpecies() == null) {
				retVal.add(currentSpec);
			}

			currentSpec = currentSpec.getMacroSpecies();
		}

		return retVal;
	}

	/**
	 * Returns a visible species from the view point of this species. If the
	 * visible species list contains a species with the specified name.
	 *
	 * @param speciesName
	 */
	public TypeDescription getVisibleSpecies(final String speciesName) {
		for (final TypeDescription visibleSpec : getVisibleSpecies()) {
			if (visibleSpec.getName().equals(speciesName)) {
				return visibleSpec;
			}
		}

		return null;
	}

	/**
	 * Verifies if the specified species can be a parent of this species.
	 *
	 * A species can be parent of other if the following conditions are hold 1.
	 * A parent species is visible to the sub-species. 2. A species can' be a
	 * sub-species of itself. 3. 2 species can't be parent of each other. 5. A
	 * species can't be a sub-species of its direct/in-direct micro-species. 6.
	 * A species and its direct/indirect micro/macro-species can't share
	 * one/some direct/indirect parent-species having micro-species. 7. The
	 * inheritance between species from different branches doesn't form a
	 * "circular" inheritance.
	 *
	 * @param parentName
	 *            the name of the potential parent
	 * @throws GamlException
	 *             if the species with the specified name can not be a parent of
	 *             this species.
	 */
	protected void verifyParent() {
		if (parent == null) {
			return;
		}
		if (this == parent) {
			error(getName() + " species can't be a sub-species of itself", IGamlIssue.GENERAL);
			return;
		}
		final List<SpeciesDescription> candidates = this.getPotentialParentSpecies();
		TypeDescription potentialParent = null;
		if (candidates.contains(parent)) {
			potentialParent = parent;
		}
		if (potentialParent == null) {

			// List<String> availableSpecies =new
			// GamaList<String>(Types.STRING);
			// for ( TypeDescription p : candidates ) {
			// availableSpecies.add(p.getName());
			// }
			// availableSpecies.remove(availableSpecies.size() - 1);

			error(parent.getName() + " can't be a parent species of " + this.getName() + " species.",
					IGamlIssue.WRONG_PARENT, PARENT);

			return;
		}

		final List<SpeciesDescription> parentsOfParent = ((SpeciesDescription) potentialParent).getSelfWithParents();
		if (parentsOfParent.contains(this)) {
			final String error = this.getName() + " species and " + potentialParent.getName()
					+ " species can't be sub-species of each other.";
			// potentialParent.error(error);
			error(error);
			return;
		}

		// TODO Commented because the test does not make sense
		// if ( this.getAllMicroSpecies().contains(parentsOfParent) ) {
		// flagError(
		// this.getName() + " species can't be a sub-species of " +
		// potentialParent.getName() +
		// " species because a species can't be sub-species of its direct or
		// indirect micro-species.",
		// IGamlIssue.GENERAL);
		// return null;
		// }

	}

	/**
	 * Finalizes the species description + Copy the behaviors, attributes from
	 * parent; + Creates the control if necessary. Add a variable representing
	 * the population of each micro-species
	 *
	 * @throws GamlException
	 */
	public void finalizeDescription() {
		if (isMirror()) {
			addChild(DescriptionFactory.create(AGENT, this, NAME, TARGET, TYPE,
					String.valueOf(ITypeProvider.MIRROR_TYPE)));
			// The type of the expression is provided later, in
			// validateChildren();
		}

		// Add the control if it is not already added
		finalizeControl();

		// control = (IArchitecture)
		// AbstractGamlAdditions.getSkillInstanceFor(getControlName());
		// buildSharedSkills();
		// recursively finalize the sorted micro-species
		for (final SpeciesDescription microSpec : sortedMicroSpecies()) {
			microSpec.finalizeDescription();
			if (!microSpec.isExperiment()) {
				final VariableDescription var = (VariableDescription) DescriptionFactory.create(CONTAINER, this, NAME,
						microSpec.getName());
				var.setSyntheticSpeciesContainer();
				var.getFacets().put(OF, GAML.getExpressionFactory()
						.createTypeExpression(getModelDescription().getTypeNamed(microSpec.getName())));
				// We compute the dependencies of micro species with respect to
				// the variables
				// defined in the macro species.
				final IExpressionDescription exp = microSpec.getFacets().get(DEPENDS_ON);
				final Set<String> dependencies = exp == null ? new TLinkedHashSet() : exp.getStrings(this, false);
				for (final VariableDescription v : microSpec.getVariables().values()) {
					dependencies.addAll(v.getExtraDependencies());
				}
				dependencies.add(SHAPE);
				dependencies.add(LOCATION);
				var.getFacets().put(DEPENDS_ON, new StringListExpressionDescription(dependencies));
				final GamaHelper get = new GamaHelper() {

					@Override
					public Object run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill,
							final Object... values) throws GamaRuntimeException {
						// TODO Make a test ?
						return ((IMacroAgent) agent).getMicroPopulation(microSpec.getName());
					}
				};
				final GamaHelper set = new GamaHelper() {

					@Override
					public Object run(final IScope scope, final IAgent agent, final IVarAndActionSupport target,
							final Object... value) throws GamaRuntimeException {
						return null;
					}

				};
				final GamaHelper init = new GamaHelper(null) {

					@Override
					public Object run(final IScope scope, final IAgent agent, final IVarAndActionSupport skill,
							final Object... values) throws GamaRuntimeException {
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

	/**
	 *
	 */
	private void finalizeControl() {

		if (control == null && parent instanceof SpeciesDescription) {
			control = ((SpeciesDescription) parent).getControl();
			if (control != null) {
				control = (IArchitecture) control.duplicate();
			}
		}
		if (control == null) {
			control = (IArchitecture) AbstractGamlAdditions.getSkillInstanceFor(REFLEX);
		}
		Class<? extends ISkill> clazz = control.getClass();
		while (clazz != AbstractArchitecture.class) {
			skills.put(clazz, control);
			clazz = (Class<? extends ISkill>) clazz.getSuperclass();
		}

	}

	@Override
	protected void validateChildren() {
		// We try to issue information about the state of the species: at first,
		// abstract.

		for (final StatementDescription a : getActions()) {
			if (a.isAbstract()) {
				this.info("Action '" + a.getName() + "' is defined or inherited as virtual. In consequence, "
						+ getName() + " is considered as abstract and cannot be instantiated.",
						IGamlIssue.MISSING_ACTION);
			}
		}

		super.validateChildren();
	}

	public boolean isExperiment() {
		return false;
	}

	public boolean isModel() {
		return false;
	}

	boolean hasMicroSpecies() {
		return microSpecies != null;
	}

	public Map<String, SpeciesDescription> getMicroSpecies() {
		if (microSpecies == null) {
			microSpecies = new TOrderedHashMap<String, SpeciesDescription>();
		}
		return microSpecies;
	}

	public boolean isMirror() {
		return facets.containsKey(MIRRORS);
	}

	public Boolean implementsSkill(final String skill) {
		return skills.containsKey(AbstractGamlAdditions.getSkillClassFor(skill));
	}

	public Map<Class<? extends ISkill>, ISkill> getSkills() {
		return skills;
	}

	public Class getJavaBaseOld() {
		// if ( getName().equals(AGENT) ) {
		// javaBase = MinimalAgent.class;
		// return javaBase;
		// }
		// Takes care of invalid species (see Issue 711)
		if (javaBase == null && parent != null && parent != this && !parent.getName().equals(AGENT)) {
			javaBase = getParent().getJavaBase();
		}
		if (javaBase == null) {
			boolean useMinimalAgents = GamaPreferences.AGENT_OPTIMIZATION.getValue()
					|| FALSE.equals(facets.getLabel("use_regular_agents"));
			if (useMinimalAgents && TRUE.equals(facets.getLabel("use_regular_agents"))) {
				useMinimalAgents = false;
			}
			if (useMinimalAgents) {
				for (final SpeciesDescription subSpecies : getSelfWithAllSubSpecies()) {
					if (subSpecies.hasMicroSpecies()) {
						useMinimalAgents = false;
						break;
					}
				}

			}
			if (useMinimalAgents) {
				javaBase = isGrid() ? MinimalGridAgent.class : MinimalAgent.class;
			} else {
				javaBase = isGrid() ? GamlGridAgent.class : GamlAgent.class;
			}
		}
		System.out.println("GetJavaBase() for " + this + " : " + javaBase);
		return javaBase;
	}

	@Override
	public Class<? extends IAgent> getJavaBase() {
		if (javaBase == null) {
			if (parent != null && !parent.getName().equals(AGENT)) {
				javaBase = getParent().getJavaBase();
			} else {
				boolean useMinimalAgents = GamaPreferences.AGENT_OPTIMIZATION.getValue()
						|| FALSE.equals(facets.getLabel("use_regular_agents"));
				if (useMinimalAgents && TRUE.equals(facets.getLabel("use_regular_agents"))) {
					useMinimalAgents = false;
				}
				if (useMinimalAgents) {
					for (final SpeciesDescription subSpecies : getSelfWithAllSubSpecies()) {
						if (subSpecies.hasMicroSpecies()) {
							useMinimalAgents = false;
							break;
						}
					}

				}
				if (useMinimalAgents) {
					javaBase = isGrid() ? MinimalGridAgent.class : MinimalAgent.class;
				} else {
					javaBase = isGrid() ? GamlGridAgent.class : GamlAgent.class;
				}
			}
		}
		// System.out.println("GetJavaBase() for " + this + " : " + javaBase);
		return javaBase;
	}

	public List<SpeciesDescription> getSelfWithAllSubSpecies() {
		final List<SpeciesDescription> result = new ArrayList();
		result.add(this);
		for (final SpeciesDescription sd : getModelDescription().getAllMicroSpecies()) {
			if (sd.hasParent(this)) {
				result.add(sd);
			}
		}
		return result;
	}

	/**
	 * @param found_sd
	 * @return
	 */
	public boolean hasMacroSpecies(final SpeciesDescription found_sd) {
		final SpeciesDescription sd = getMacroSpecies();
		if (sd == null) {
			return false;
		}
		if (sd.equals(found_sd)) {
			return true;
		}
		return sd.hasMacroSpecies(found_sd);
	}

	/**
	 * @param macro
	 * @return
	 */
	public boolean hasParent(final SpeciesDescription p) {
		final SpeciesDescription sd = getParent();
		// Takes care of invalid species (see Issue 711)
		if (sd == null || sd == this) {
			return false;
		}
		if (sd.equals(p)) {
			return true;
		}
		return sd.hasParent(p);
	}

	@Override
	public List<IDescription> getChildren() {
		final List<IDescription> result = super.getChildren();
		if (microSpecies != null) {
			result.addAll(microSpecies.values());
		}
		if (behaviors != null) {
			result.addAll(behaviors.values());
		}
		if (aspects != null) {
			result.addAll(aspects.values());
		}
		return result;
	}

	/**
	 * @return
	 */
	public Collection<StatementDescription> getBehaviors() {
		return behaviors == null ? Collections.EMPTY_LIST : behaviors.values();
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		super.collectMetaInformation(meta);
		if (isBuiltIn()) {
			meta.put(GamlProperties.SPECIES, getName());
		}
	}

}
