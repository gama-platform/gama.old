/*******************************************************************************************************
 *
 * SpeciesDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import static com.google.common.collect.Iterables.transform;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.ISkill;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.topology.grid.GamlGridAgent;
import msi.gama.metamodel.topology.grid.MinimalGridAgent;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.compilation.IGamaHelper;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.ListExpression;
import msi.gaml.expressions.types.DenotedActionExpression;
import msi.gaml.expressions.types.SkillConstantExpression;
import msi.gaml.expressions.types.SpeciesConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Facets;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SpeciesDescription.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class SpeciesDescription extends TypeDescription {

	static {
		DEBUG.ON();
	}

	/** The behaviors. */
	// AD 08/16: Behaviors are now inherited dynamically
	private IMap<String, StatementDescription> behaviors;

	/** The aspects. */
	// AD 08/16: Aspects are now inherited dynamically
	private IMap<String, StatementDescription> aspects;

	/** The micro species. */
	private IMap<String, SpeciesDescription> microSpecies;

	/** The skills. */
	protected LinkedHashSet<SkillDescription> skills;

	/** The control. */
	protected SkillDescription control;

	/** The agent constructor. */
	private IAgentConstructor agentConstructor;

	/** The species expr. */
	private SpeciesConstantExpression speciesExpr;

	/** The java base. */
	protected Class javaBase;

	/**
	 * Instantiates a new species description.
	 *
	 * @param keyword
	 *            the keyword
	 * @param clazz
	 *            the clazz
	 * @param macroDesc
	 *            the macro desc
	 * @param parent
	 *            the parent
	 * @param cp
	 *            the cp
	 * @param source
	 *            the source
	 * @param facets
	 *            the facets
	 */
	public SpeciesDescription(final String keyword, final Class clazz, final SpeciesDescription macroDesc,
			final SpeciesDescription parent, final Iterable<? extends IDescription> cp, final EObject source,
			final Facets facets) {
		this(keyword, clazz, macroDesc, parent, cp, source, facets, Collections.EMPTY_SET);
	}

	/**
	 * Instantiates a new species description.
	 *
	 * @param keyword
	 *            the keyword
	 * @param clazz
	 *            the clazz
	 * @param macroDesc
	 *            the macro desc
	 * @param parent
	 *            the parent
	 * @param cp
	 *            the cp
	 * @param source
	 *            the source
	 * @param facets
	 *            the facets
	 * @param skills
	 *            the skills
	 */
	public SpeciesDescription(final String keyword, final Class clazz, final SpeciesDescription macroDesc,
			final SpeciesDescription parent, final Iterable<? extends IDescription> cp, final EObject source,
			final Facets facets, final Set<String> skills) {
		super(keyword, clazz, macroDesc, parent, cp, source, facets, null);
		set(Flag.CanUseMinimalAgents);
		setIf(Flag.isMirror, hasFacet(MIRRORS));
		setIf(Flag.isGrid, GRID.equals(getKeyword()));
		setJavaBase(clazz);
		setSkills(getFacet(SKILLS), skills);
	}

	/**
	 * This constructor is only called to build built-in species. The parent is passed directly as there is no
	 * ModelFactory to provide it
	 */
	public SpeciesDescription(final String name, final Class clazz, final SpeciesDescription superDesc,
			final SpeciesDescription parent, final IAgentConstructor helper, final Set<String> skills2, final Facets ff,
			final String plugin) {
		super(SPECIES, clazz, superDesc, null, null, null, new Facets(NAME, name), plugin);
		set(Flag.CanUseMinimalAgents);
		setIf(Flag.isMirror, hasFacet(MIRRORS));
		setIf(Flag.isGrid, GRID.equals(getKeyword()));
		setJavaBase(clazz);
		setParent(parent);
		setSkills(ff == null ? null : ff.get(SKILLS), skills2);
		setAgentConstructor(helper);
	}

	/**
	 * Adds the skill.
	 *
	 * @param sk
	 *            the sk
	 */
	protected void addSkill(final SkillDescription sk) {
		if (sk == null) return;
		if (skills == null) { skills = new LinkedHashSet(); }
		skills.add(sk);
	}

	@Override
	public SymbolSerializer createSerializer() {
		return SPECIES_SERIALIZER;
	}

	@Override
	public void dispose() {
		if (isBuiltIn()) return;
		super.dispose();
		behaviors = null;
		aspects = null;
		skills = null;
		if (control != null) {
			control.dispose();
			control = null;
		}
		microSpecies = null;

	}

	/**
	 * Sets the skills.
	 *
	 * @param userDefinedSkills
	 *            the user defined skills
	 * @param builtInSkills
	 *            the built in skills
	 */
	protected void setSkills(final IExpressionDescription userDefinedSkills, final Set<String> builtInSkills) {
		/* We try to add the control architecture if any is defined */
		final String controlName = getLitteral(CONTROL);
		if (controlName != null) {
			final SkillDescription sd = GamaSkillRegistry.INSTANCE.get(controlName);
			if (sd == null || !sd.isControl()) {
				warning("This control  does not belong to the list of known agent controls ("
						+ GamaSkillRegistry.INSTANCE.getArchitectureNames() + ")", IGamlIssue.WRONG_CONTEXT, CONTROL);
			} else {
				control = sd;
				// We add it explicitly so as to add the variables and actions
				// defined in the control. No need to add it in the other cases
				// addSkill(control);
			}
		}

		/* We add the keyword as a possible skill (used for 'grid' species) */
		final SkillDescription skill = GamaSkillRegistry.INSTANCE.get(getKeyword());
		addSkill(skill);
		/*
		 * We add the user defined skills (i.e. as in 'species a skills: [s1, s2...]')
		 */
		if (userDefinedSkills != null) {
			final IExpression expr = userDefinedSkills.compile(this);
			if (expr instanceof ListExpression list) {
				for (final IExpression exp : list.getElements()) {
					if (exp instanceof SkillConstantExpression) {
						final SkillDescription sk = ((ISkill) exp.getConstValue()).getDescription();
						final String dep = sk.getDeprecated();
						if (dep != null) {
							warning("Skill " + sk.getName() + " is deprecated: " + dep, IGamlIssue.DEPRECATED, SKILLS);
						}
						addSkill(sk);
					}
				}
			}
		}
		/*
		 * We add the skills that are defined in Java, either using @species(value='a', skills= {s1,s2}),
		 * or @skill(value="s1", attach_to="a")
		 */
		for (final String s : builtInSkills) { addSkill(GamaSkillRegistry.INSTANCE.get(s)); }

	}

	@Override
	public boolean redefinesAttribute(final String theName) {
		if (super.redefinesAttribute(theName)) return true;
		if (skills != null) {
			for (final SkillDescription skill : skills) { if (skill.hasAttribute(theName)) return true; }
		}
		return false;
	}

	@Override
	public boolean redefinesAction(final String theName) {
		if (super.redefinesAction(theName)) return true;
		if (skills != null) {
			for (final SkillDescription skill : skills) { if (skill.hasAction(theName, false)) return true; }
		}
		return false;
	}

	/**
	 * Gets the control name.
	 *
	 * @return the control name
	 */
	public String getControlName() {
		String controlName = getLitteral(CONTROL);
		// if the "control" is not explicitly declared then inherit it from the
		// parent species. Takes care of invalid species (see Issue 711)
		if (controlName == null) {
			if (parent != null && parent != this) {
				controlName = getParent().getControlName();
			} else {
				controlName = REFLEX;
			}
		}
		return controlName;
	}

	/**
	 * Gets the parent name.
	 *
	 * @return the parent name
	 */
	public String getParentName() { return getLitteral(PARENT); }

	@Override
	public IExpression getVarExpr(final String n, final boolean asField) {
		IExpression result = super.getVarExpr(n, asField);
		if (result == null) {
			StatementDescription desc = getBehavior(n);
			if (desc != null) { result = new DenotedActionExpression(desc); }
			desc = getAspect(n);
			if (desc != null) { result = new DenotedActionExpression(desc); }
		}
		return result;
	}

	/**
	 * Copy java additions.
	 */
	public void copyJavaAdditions() {
		final Class clazz = getJavaBase();
		if (clazz == null) {
			error("This species cannot be compiled as its Java base is unknown. ", IGamlIssue.UNKNOWN_SPECIES);
			return;
		}
		Class<? extends IAgent> javaBase = getJavaBase();
		Iterable<Class<? extends ISkill>> skillClasses = transform(getSkills(), TO_CLASS);
		Iterable<IDescription> javaChildren = GAML.getAllChildrenOf(javaBase, skillClasses);
		for (final IDescription v : javaChildren) { addJavaChild(v); }
	}

	/**
	 * Adds the java child.
	 *
	 * @param v
	 *            the v
	 */
	private void addJavaChild(final IDescription v) {
		if (isBuiltIn()) { v.setOriginName("built-in species " + getName()); }
		if (v instanceof VariableDescription) {
			boolean toAdd = false;
			if (this.isBuiltIn() && !hasAttribute(v.getName()) || ((VariableDescription) v).isContextualType()) {
				toAdd = true;
			} else if (parent != null && parent != this) {
				final VariableDescription existing = parent.getAttribute(v.getName());
				if (existing == null || !existing.getOriginName().equals(v.getOriginName())) { toAdd = true; }
			} else {
				toAdd = true;
			}
			if (toAdd) {
				// Fixes a problem where built-in attributes were not linked with their declaring class
				// Class<?> c = VariableDescription.CLASS_DEFINITIONS.remove(v);
				final VariableDescription var = (VariableDescription) v.copy(this);
				addOwnAttribute(var);
				// var.builtInDoc = ((VariableDescription) v).getBuiltInDoc();
				// VariableDescription.CLASS_DEFINITIONS.put(var, c);
			}

		} else {
			boolean toAdd = false;
			if (parent == null) {
				toAdd = true;
			} else if (parent != this) {
				final StatementDescription existing = parent.getAction(v.getName());
				if (existing == null || !existing.getOriginName().equals(v.getOriginName())) { toAdd = true; }
			}
			if (toAdd) {
				v.setEnclosingDescription(this);
				addAction((ActionDescription) v);
			}
		}
	}

	@Override
	public IDescription addChild(final IDescription child) {
		final IDescription desc = super.addChild(child);
		if (desc == null) return null;
		if (desc instanceof StatementDescription statement) {
			final String kw = desc.getKeyword();
			if (PRIMITIVE.equals(kw) || ACTION.equals(kw)) {
				addAction((ActionDescription) statement);
			} else if (ASPECT.equals(kw)) {
				addAspect(statement);
			} else {
				addBehavior(statement);
			}
		} else if (desc instanceof VariableDescription) {
			addOwnAttribute((VariableDescription) desc);
		} else if (desc instanceof SpeciesDescription) { addMicroSpecies((SpeciesDescription) desc); }
		return desc;
	}

	/**
	 * Adds the micro species.
	 *
	 * @param sd
	 *            the sd
	 */
	protected void addMicroSpecies(final SpeciesDescription sd) {
		if (!isModel() && sd.isGrid()) {
			sd.error("For the moment, grids cannot be defined as micro-species anywhere else than in the model");
		}
		getMicroSpecies().put(sd.getName(), sd);
		// DEBUG.OUT("Adding micro-species " + sd.getName() + " to " + getName());
		invalidateMinimalAgents();
	}

	/**
	 * Invalidate minimal agents.
	 */
	protected void invalidateMinimalAgents() {
		unSet(Flag.CanUseMinimalAgents);
		if (parent != null && parent != this && !parent.isBuiltIn()) { getParent().invalidateMinimalAgents(); }
	}

	/**
	 * Use minimal agents.
	 *
	 * @return true, if successful
	 */
	protected boolean useMinimalAgents() {
		if (!isSet(Flag.CanUseMinimalAgents) || parent != null && parent != this && !getParent().useMinimalAgents())
			return false;
		return !hasFacet("use_regular_agents") || FALSE.equals(getLitteral("use_regular_agents"));
	}

	/**
	 * Adds the behavior.
	 *
	 * @param r
	 *            the r
	 */
	protected void addBehavior(final StatementDescription r) {
		final String behaviorName = r.getName();
		if (behaviors == null) { behaviors = GamaMapFactory.create(); }
		final StatementDescription existing = getBehavior(behaviorName);
		if (existing != null && existing.getKeyword().equals(r.getKeyword())) { duplicateInfo(r, existing); }
		behaviors.put(behaviorName, r);
	}

	/**
	 * Checks for behavior.
	 *
	 * @param a
	 *            the a
	 * @return true, if successful
	 */
	public boolean hasBehavior(final String a) {
		return behaviors != null && behaviors.containsKey(a)
				|| parent != null && parent != this && getParent().hasBehavior(a);
	}

	/**
	 * Gets the behavior.
	 *
	 * @param aName
	 *            the a name
	 * @return the behavior
	 */
	public StatementDescription getBehavior(final String aName) {
		StatementDescription ownBehavior = behaviors == null ? null : behaviors.get(aName);
		if (ownBehavior == null && parent != null && parent != this) { ownBehavior = getParent().getBehavior(aName); }
		return ownBehavior;
	}

	/**
	 * Adds the aspect.
	 *
	 * @param ce
	 *            the ce
	 */
	private void addAspect(final StatementDescription ce) {
		String aspectName = ce.getName();
		if (aspectName == null) {
			aspectName = DEFAULT;
			ce.setName(aspectName);
		}
		if (!DEFAULT.equals(aspectName) && hasAspect(aspectName)) { duplicateInfo(ce, getAspect(aspectName)); }
		if (aspects == null) { aspects = GamaMapFactory.create(); }
		aspects.put(aspectName, ce);
	}

	/**
	 * Gets the aspect.
	 *
	 * @param aName
	 *            the a name
	 * @return the aspect
	 */
	public StatementDescription getAspect(final String aName) {
		StatementDescription ownAspect = aspects == null ? null : aspects.get(aName);
		if (ownAspect == null && parent != null && parent != this) { ownAspect = getParent().getAspect(aName); }
		return ownAspect;
	}

	/**
	 * Gets the behavior names.
	 *
	 * @return the behavior names
	 */
	public Collection<String> getBehaviorNames() {
		final Collection<String> ownNames =
				behaviors == null ? new LinkedHashSet() : new LinkedHashSet(behaviors.keySet());
		if (parent != null && parent != this) { ownNames.addAll(getParent().getBehaviorNames()); }
		return ownNames;
	}

	/**
	 * Gets the aspect names.
	 *
	 * @return the aspect names
	 */
	public Collection<String> getAspectNames() {
		final Collection<String> ownNames = aspects == null ? new LinkedHashSet() : new LinkedHashSet(aspects.keySet());
		if (parent != null && parent != this) { ownNames.addAll(getParent().getAspectNames()); }
		return ownNames;

	}

	/**
	 * Gets the aspects.
	 *
	 * @return the aspects
	 */
	public Iterable<StatementDescription> getAspects() {
		return Iterables.transform(getAspectNames(), this::getAspect);
	}

	/**
	 * Gets the control.
	 *
	 * @return the control
	 */
	public SkillDescription getControl() { return control; }

	/**
	 * Checks for aspect.
	 *
	 * @param a
	 *            the a
	 * @return true, if successful
	 */
	public boolean hasAspect(final String a) {
		return aspects != null && aspects.containsKey(a)
				|| parent != null && parent != this && getParent().hasAspect(a);
	}

	@Override
	public SpeciesDescription getSpeciesContext() { return this; }

	/**
	 * Gets the micro species.
	 *
	 * @param name
	 *            the name
	 * @return the micro species
	 */
	public SpeciesDescription getMicroSpecies(final String name) {
		if (hasMicroSpecies()) {
			final SpeciesDescription retVal = microSpecies.get(name);
			if (retVal != null) return retVal;
		}
		// Takes care of invalid species (see Issue 711)
		if (parent != null && parent != this) return getParent().getMicroSpecies(name);
		return null;
	}

	@Override
	public String toString() {
		return "Description of " + getName();
	}

	/**
	 * Gets the agent constructor.
	 *
	 * @return the agent constructor
	 */
	public IAgentConstructor getAgentConstructor() {
		if (agentConstructor == null && parent != null && parent != this) {
			if (getParent().getJavaBase() == getJavaBase()) {
				agentConstructor = getParent().getAgentConstructor();
			} else {
				agentConstructor = IAgentConstructor.CONSTRUCTORS.get(getJavaBase());
			}
		}
		return agentConstructor;
	}

	/**
	 * Sets the agent constructor.
	 *
	 * @param agentConstructor
	 *            the new agent constructor
	 */
	protected void setAgentConstructor(final IAgentConstructor agentConstructor) {
		this.agentConstructor = agentConstructor;
	}

	/**
	 * Gets the macro species.
	 *
	 * @return the macro species
	 */
	public SpeciesDescription getMacroSpecies() {
		final IDescription d = getEnclosingDescription();
		if (d instanceof SpeciesDescription) return (SpeciesDescription) d;
		return null;
	}

	@Override
	public SpeciesDescription getParent() { return (SpeciesDescription) super.getParent(); }

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
			}
			if (!parent.isBuiltIn()) { inheritMicroSpecies(parent); }

		}
		super.inheritFromParent();

	}

	/**
	 * Inherit micro species.
	 *
	 * @param parent
	 *            the parent
	 */
	// FIXME HACK !
	private void inheritMicroSpecies(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if (parent == null || parent == this) return;
		if (parent.hasMicroSpecies()) {
			parent.getMicroSpecies().forEachPair((k, v) -> {
				getMicroSpecies().putIfAbsent(k, v);
				return true;
			});
		}
	}

	/**
	 * Checks if is grid.
	 *
	 * @return true, if is grid
	 */
	public boolean isGrid() { return isSet(Flag.isGrid); }

	@Override
	public String getTitle() { return getKeyword() + " " + getName(); }

	@Override
	public Doc getDocumentation() {
		final Doc result = new RegularDoc();
		documentThis(result);
		result.append("<hr/>").append(getMeta().getDocumentation().get());
		return result;
	}

	/**
	 * Gets the documentation without meta.
	 *
	 * @return the documentation without meta
	 */
	public void documentThis(final Doc sb) {
		final String parentName = getParent() == null ? "nil" : getParent().getName();
		final String hostName = getMacroSpecies() == null ? null : getMacroSpecies().getName();
		sb.append("<b>Subspecies of:</b> ").append(parentName).append("<br>");
		if (hostName != null) { sb.append("<b>Microspecies of:</b> ").append(hostName).append("<br>"); }
		final Iterable<String> skills = getSkillsNames();
		if (!Iterables.isEmpty(skills)) { sb.append("<b>Skills:</b> ").append(skills.toString()).append("<br>"); }
		documentAttributes(sb);
		documentActions(sb);
	}

	/**
	 * Gets the skills names.
	 *
	 * @return the skills names
	 */
	public Iterable<String> getSkillsNames() {
		return Iterables.concat(Iterables.transform(skills == null ? Collections.EMPTY_LIST : skills, TO_NAME),
				parent != null && parent != this ? getParent().getSkillsNames() : Collections.EMPTY_LIST);

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
	 * Visit micro species.
	 *
	 * @param visitor
	 *            the visitor
	 * @return true, if successful
	 */
	public boolean visitMicroSpecies(final DescriptionVisitor<SpeciesDescription> visitor) {
		if (!hasMicroSpecies()) return true;
		return getMicroSpecies().forEachValue(visitor);
	}

	// public boolean visitSortedMicroSpecies(final DescriptionVisitor<SpeciesDescription> visitor) {
	// if (!hasMicroSpecies()) { return true; }
	// final Iterable<SpeciesDescription> all = getSortedMicroSpecies();
	// for (final SpeciesDescription sd : all) {
	// if (!visitor.process(sd)) { return false; }
	// }
	// return true;
	// }

	@Override
	public void setParent(final TypeDescription parent) {
		super.setParent(parent);
		if (!isBuiltIn() && !verifyParent()) {
			super.setParent(null);
			return;
		}
		if (parent instanceof SpeciesDescription && parent != this && !isSet(Flag.CanUseMinimalAgents)
				&& !parent.isBuiltIn()) {
			((SpeciesDescription) parent).invalidateMinimalAgents();
		}
	}

	/**
	 * Verifies if the specified species can be a parent of this species.
	 *
	 * A species can be parent of other if the following conditions are hold 1. A parent species is visible to the
	 * sub-species. 2. A species can' be a sub-species of itself. 3. 2 species can't be parent of each other. 5. A
	 * species can't be a sub-species of its direct/in-direct micro-species. 6. A species and its direct/indirect
	 * micro/macro-species can't share one/some direct/indirect parent-species having micro-species. 7. The inheritance
	 * between species from different branches doesn't form a "circular" inheritance.
	 *
	 * @param parentName
	 *            the name of the potential parent
	 * @throws GamlException
	 *             if the species with the specified name can not be a parent of this species.
	 */
	protected boolean verifyParent() {
		if (parent == null) return true;
		if (this == parent) {
			error(getName() + " species can't be a sub-species of itself", IGamlIssue.GENERAL);
			return false;
		}
		if (parentIsAmongTheMicroSpecies()) {
			error(getName() + " species can't be a sub-species of one of its micro-species", IGamlIssue.GENERAL);
			return false;
		}
		if (!parentIsVisible()) {
			error(parent.getName() + " can't be a parent species of " + this.getName() + " species.",
					IGamlIssue.WRONG_PARENT, PARENT);
			return false;
		}
		if (hierarchyContainsSelf()) {
			error(this.getName() + " species and " + parent.getName() + " species can't be sub-species of each other.");
			return false;
		}
		return true;
	}

	/**
	 * Parent is among the micro species.
	 *
	 * @return true, if successful
	 */
	private boolean parentIsAmongTheMicroSpecies() {
		final boolean[] result = new boolean[1];
		visitMicroSpecies(new DescriptionVisitor<SpeciesDescription>() {

			@Override
			public boolean process(final SpeciesDescription desc) {
				if (desc == parent) {
					result[0] = true;
					return false;
				}
				desc.visitMicroSpecies(this);
				return true;
			}
		});
		return result[0];
	}

	/**
	 * Hierarchy contains self.
	 *
	 * @return true, if successful
	 */
	private boolean hierarchyContainsSelf() {
		SpeciesDescription currentSpeciesDesc = this;
		while (currentSpeciesDesc != null) {
			final SpeciesDescription p = currentSpeciesDesc.getParent();
			// Takes care of invalid species (see Issue 711)
			if (p == currentSpeciesDesc || p == this) return true;
			currentSpeciesDesc = p;
		}
		return false;
	}

	/**
	 * Parent is visible.
	 *
	 * @return true, if successful
	 */
	protected boolean parentIsVisible() {
		if (getParent().isExperiment()) return false;
		SpeciesDescription host = getMacroSpecies();
		while (host != null) {
			if (host == parent || host.getMicroSpecies(parent.getName()) != null) return true;
			host = host.getMacroSpecies();
		}
		return false;
	}

	/**
	 * Finalizes the species description + Copy the behaviors, attributes from parent; + Creates the control if
	 * necessary. Add a variable representing the population of each micro-species
	 *
	 * @throws GamlException
	 */
	public boolean finalizeDescription() {
		if (isMirror()) {
			addChild(DescriptionFactory.create(AGENT, this, NAME, TARGET, TYPE,
					String.valueOf(ITypeProvider.MIRROR_TYPE)));
		}

		// Add the control if it is not already added
		finalizeControl();
		final boolean isBuiltIn = this.isBuiltIn();

		final DescriptionVisitor<SpeciesDescription> visitor = microSpec -> {
			if (!microSpec.finalizeDescription()) return false;
			if (!microSpec.isExperiment() && !isBuiltIn) {
				final String n = microSpec.getName();
				if (hasAttribute(n) && !getAttribute(n).isSyntheticSpeciesContainer()) {
					microSpec.error(
							microSpec.getName() + " is the name of an existing attribute in " + SpeciesDescription.this,
							IGamlIssue.DUPLICATE_NAME, NAME);
					return false;
				}
				final VariableDescription var =
						(VariableDescription) DescriptionFactory.create(LIST, SpeciesDescription.this, NAME, n);

				var.setSyntheticSpeciesContainer();
				var.setFacet(OF, GAML.getExpressionFactory()
						.createTypeExpression(getModelDescription().getTypeNamed(microSpec.getName())));
				final IGamaHelper get = (scope1, agent1, skill1, values1) -> ((IMacroAgent) agent1)
						.getMicroPopulation(microSpec.getName());
				final IGamaHelper set = (scope2, agent2, skill2, values2) -> null;
				final IGamaHelper init = (scope3, agent3, skill3, values3) -> {
					((IMacroAgent) agent3).initializeMicroPopulation(scope3, microSpec.getName());
					return ((IMacroAgent) agent3).getMicroPopulation(microSpec.getName());
				};

				var.addHelpers(get, init, set);
				addChild(var);
			}
			return true;
		};

		// recursively finalize the sorted micro-species
		if (!visitMicroSpecies(visitor)) return false;
		// Calling sortAttributes later (in compilation)
		// add the listeners to the variables (if any)
		// addListenersToVariables();
		return true;
	}

	/**
	 *
	 */
	private void finalizeControl() {
		if (isSet(Flag.ControlFinalized)) return;
		set(Flag.ControlFinalized);

		if (control == null && parent != this && parent instanceof SpeciesDescription) {
			((SpeciesDescription) parent).finalizeControl();
			control = ((SpeciesDescription) parent).getControl();
		}
		if (control == null) {
			control = GamaSkillRegistry.INSTANCE.get(REFLEX);
			return;
		}
		Class<? extends ISkill> clazz = control.getJavaBase().getSuperclass();
		while (clazz != AbstractArchitecture.class) {
			final SkillDescription sk = GamaSkillRegistry.INSTANCE.get(clazz);
			if (sk != null) { addSkill(sk); }
			clazz = (Class<? extends ISkill>) clazz.getSuperclass();

		}

	}

	@Override
	protected boolean validateChildren() {
		// We try to issue information about the state of the species: at first,
		// abstract.

		for (final ActionDescription a : getActions()) {
			if (a.isAbstract()) {
				this.info("Action '" + a.getName() + "' is defined or inherited as virtual. In consequence, "
						+ getName() + " will be considered as abstract.", IGamlIssue.MISSING_ACTION);
			}
		}

		return super.validateChildren();
	}

	/**
	 * Checks if is experiment.
	 *
	 * @return true, if is experiment
	 */
	public boolean isExperiment() { return false; }

	/**
	 * Checks if is model.
	 *
	 * @return true, if is model
	 */
	public boolean isModel() { return false; }

	/**
	 * Checks for micro species.
	 *
	 * @return true, if successful
	 */
	public boolean hasMicroSpecies() {
		return microSpecies != null;
	}

	/**
	 * Gets the micro species.
	 *
	 * @return the micro species
	 */
	public IMap<String, SpeciesDescription> getMicroSpecies() {
		if (microSpecies == null) { microSpecies = GamaMapFactory.create(); }
		return microSpecies;
	}

	/**
	 * Checks if is mirror.
	 *
	 * @return true, if is mirror
	 */
	public boolean isMirror() { return isSet(Flag.isMirror); }

	/**
	 * Returns whether or not a species implements (directly or indirectly through its parents) a skill named after the
	 * parameter.
	 *
	 * @param skill
	 *            the name of the skill
	 * @return true if this species implements a skill or if its parent does. WARNING: no possibility, right now, to
	 *         know if a skill extends another skill, so this possibility is not considered in this method.
	 */
	public Boolean implementsSkill(final String skill) {
		if (skills != null) {
			for (final SkillDescription sk : skills) { if (sk.getName().equals(skill)) return true; }
		}
		if (parent != null && parent != this) return getParent().implementsSkill(skill);
		return false;
	}

	@Override
	public Class<? extends IAgent> getJavaBase() {
		if (javaBase == null) {
			if (parent != null && parent != this && !AGENT.equals(getParent().getName())) {
				javaBase = getParent().getJavaBase();
			} else if (useMinimalAgents()) {
				javaBase = isGrid() ? MinimalGridAgent.class : MinimalAgent.class;
			} else {
				javaBase = isGrid() ? GamlGridAgent.class : GamlAgent.class;
			}
		}
		return javaBase;
	}

	/**
	 * Sets the java base.
	 *
	 * @param javaBase
	 *            the new java base
	 */
	protected void setJavaBase(final Class javaBase) { this.javaBase = javaBase; }

	/**
	 * @param found_sd
	 * @return
	 */
	public boolean hasMacroSpecies(final SpeciesDescription found_sd) {
		final SpeciesDescription sd = getMacroSpecies();
		if (sd == null) return false;
		if (sd.equals(found_sd)) return true;
		return sd.hasMacroSpecies(found_sd);
	}

	/**
	 * @param macro
	 * @return
	 */
	public boolean hasParent(final SpeciesDescription p) {
		final SpeciesDescription sd = getParent();
		// Takes care of invalid species (see Issue 711)
		if (sd == null || sd == this) return false;
		if (sd.equals(p)) return true;
		return sd.hasParent(p);
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor<IDescription> visitor) {
		if (!super.visitOwnChildren(visitor) || microSpecies != null && !microSpecies.forEachValue(visitor))
			return false;
		if (behaviors != null && !behaviors.forEachValue(visitor) || aspects != null && !aspects.forEachValue(visitor))
			return false;
		return true;
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor<IDescription> visitor) {
		final DescriptionVisitor<IDescription> recursiveVisitor = each -> {
			if (!visitor.process(each)) return false;
			return each.visitOwnChildrenRecursively(visitor);
		};
		if (!super.visitOwnChildrenRecursively(visitor)
				|| microSpecies != null && !microSpecies.forEachValue(recursiveVisitor))
			return false;
		if (behaviors != null && !behaviors.forEachValue(recursiveVisitor)
				|| aspects != null && !aspects.forEachValue(recursiveVisitor))
			return false;
		return true;
	}

	@Override
	public Iterable<IDescription> getOwnChildren() {
		return Iterables.concat(super.getOwnChildren(),
				microSpecies == null ? Collections.EMPTY_LIST : microSpecies.values(),
				behaviors == null ? Collections.EMPTY_LIST : behaviors.values(),
				aspects == null ? Collections.EMPTY_LIST : aspects.values());
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor<IDescription> visitor) {
		boolean result = super.visitChildren(visitor);
		if (!result) return false;
		if (hasMicroSpecies()) { result &= microSpecies.forEachValue(visitor); }
		if (!result) return false;
		for (final IDescription d : getBehaviors()) {
			result &= visitor.process(d);
			if (!result) return false;
		}
		for (final IDescription d : getAspects()) {
			result &= visitor.process(d);
			if (!result) return false;
		}
		return result;
	}

	/**
	 * @return
	 */
	public Iterable<StatementDescription> getBehaviors() {
		return Iterables.transform(getBehaviorNames(), this::getBehavior);
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		super.collectMetaInformation(meta);
		if (isBuiltIn()) { meta.put(GamlProperties.SPECIES, getName()); }
	}

	/**
	 * Belongs to A micro model.
	 *
	 * @return true, if successful
	 */
	public boolean belongsToAMicroModel() {
		return getModelDescription().isMicroModel();
	}

	/**
	 * Gets the skills.
	 *
	 * @return the skills
	 */
	public Iterable<SkillDescription> getSkills() {
		final List<SkillDescription> base =
				control == null ? Collections.EMPTY_LIST : Collections.singletonList(control);
		if (skills == null) return base;
		return Iterables.concat(skills, base);
	}

}
