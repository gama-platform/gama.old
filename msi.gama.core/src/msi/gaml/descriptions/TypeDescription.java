/*********************************************************************************************
 *
 * 'TypeDescription.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.expressions.DenotedActionExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * A class that represents skills and species (either built-in or introduced by users) The class TypeDescription.
 *
 * @author drogoul
 * @since 23 fevr. 2013
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class TypeDescription extends SymbolDescription {

	// AD 08/16 : actions and attributes are now inherited dynamically and built
	// lazily
	protected THashMap<String, ActionDescription> actions;
	protected TOrderedHashMap<String, VariableDescription> attributes;
	protected TypeDescription parent;

	public TypeDescription(final String keyword, final Class clazz, final IDescription macroDesc,
			final TypeDescription parent, final Iterable<? extends IDescription> cp, final EObject source,
			final Facets facets, final String plugin) {
		super(keyword, macroDesc, source, /* cp, */ facets);
		addChildren(cp);
		// parent can be null
		if (parent != null)
			setParent(parent);
		if (plugin != null && isBuiltIn())
			this.originName = plugin;
		// System.out.println("Origin name " + getOriginName() + " and plugin "
		// + plugin + " of " + this);

	}

	@Override
	public String getDefiningPlugin() {
		if (isBuiltIn())
			return originName;
		return null;
	}

	public abstract Class getJavaBase();

	/**
	 * ==================================== MANAGEMENT OF VARIABLES
	 */

	public Iterable<VariableDescription> getAttributes() {
		return Iterables.transform(getAttributeNames(), input -> getAttribute(input));
	}

	public Collection<String> getAttributeNames() {
		final Collection<String> accumulator =
				parent != null && parent != this ? parent.getAttributeNames() : new TLinkedHashSet<String>();
		if (attributes != null) {
			attributes.forEachKey(s -> {
				if (accumulator.contains(s))
					accumulator.remove(s);
				accumulator.add(s);
				return true;
			});
		}
		return accumulator;
	}

	public VariableDescription getAttribute(final String name) {
		final VariableDescription attribute = attributes == null ? null : attributes.get(name);
		if (attribute == null && parent != null && parent != this) { return getParent().getAttribute(name); }
		return attribute;
	}

	public boolean redefinesAttribute(final String name) {
		if (!attributes.contains(name))
			return false;
		if (parent == null || parent == this)
			return false;
		return parent.hasAttribute(name);
	}

	@Override
	public boolean hasAttribute(final String a) {
		return attributes != null && attributes.containsKey(a)
				|| parent != null && parent != this && getParent().hasAttribute(a);
	}

	@Override
	public IExpression getVarExpr(final String n, final boolean asField) {
		final VariableDescription vd = getAttribute(n);
		if (vd == null) {
			final IDescription desc = getAction(n);
			if (desc != null) { return new DenotedActionExpression(desc); }
			return null;
		}
		return vd.getVarExpr(asField);
	}

	protected void addAttributeNoCheck(final VariableDescription vd) {
		if (attributes == null)
			attributes = new TOrderedHashMap();
		// synchronized (this) {
		attributes.put(vd.getName(), vd);
		// }
	}

	public boolean assertAttributesAreCompatible(final VariableDescription existingVar,
			final VariableDescription newVar) {
		if (newVar.isBuiltIn() && existingVar.isBuiltIn()) { return true; }
		final IType existingType = existingVar.getType();
		final IType newType = newVar.getType();
		if (!newType.isTranslatableInto(existingType)) {
			markTypeDifference(existingVar, newVar, existingType, newType, true);
		} else if (!newType.equals(existingType) && !newType.isParametricFormOf(existingType)) {
			markTypeDifference(existingVar, newVar, existingType, newType, false);
		}
		return true;
	}

	private void markTypeDifference(final VariableDescription existingVar, final VariableDescription newVar,
			final IType existingType, final IType newType, final boolean error) {
		final String msg = "Type (" + newType + ") differs from that (" + existingType + ") of the implementation of  "
				+ newVar.getName() + " in " + existingVar.getOriginName();
		if (existingVar.isBuiltIn()) {
			if (error) {
				newVar.error(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
			} else {
				newVar.warning(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
			}
		} else {
			final EObject newObject = newVar.getUnderlyingElement(null);
			final Resource newResource = newObject == null ? null : newObject.eResource();
			final EObject existingObject = existingVar.getUnderlyingElement(null);
			final Resource existingResource = existingObject == null ? null : existingObject.eResource();
			final boolean same = newResource == null ? existingResource == null : newResource.equals(existingResource);
			if (same) {
				if (error) {
					newVar.error(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
				} else {
					newVar.info(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
				}
			} else if (existingResource != null) {
				if (error) {
					newVar.error(msg + " in  imported file " + existingResource.getURI().lastSegment(),
							IGamlIssue.WRONG_REDEFINITION, NAME);
				} else {
					newVar.info(msg + " in  imported file " + existingResource.getURI().lastSegment(),
							IGamlIssue.WRONG_REDEFINITION, NAME);
				}
			}
		}

	}

	public void markAttributeRedefinition(final VariableDescription existingVar, final VariableDescription newVar) {
		if (newVar.isBuiltIn() && existingVar.isBuiltIn()) { return; }
		if (newVar.getOriginName().equals(existingVar.getOriginName())) {
			// TODO must be review carefully the inheritance in comodel
			/// temporay fix for co-model, variable in micro-model can be
			// defined multi time
			if (!newVar.getModelDescription().getAlias().equals("")) { return; }
			///
			existingVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION,
					NAME);
			newVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION, NAME);
			return;
		}
		if (existingVar.isBuiltIn()) {
			newVar.info(
					"This definition of " + newVar.getName() + " supersedes the one in " + existingVar.getOriginName(),
					IGamlIssue.REDEFINES, NAME);
		} else {
			// Possibily different resources
			final Resource newResource =
					newVar.getUnderlyingElement(null) == null ? null : newVar.getUnderlyingElement(null).eResource();
			final Resource existingResource = existingVar.getUnderlyingElement(null).eResource();
			if (Objects.equals(newResource, existingResource)) {
				newVar.info("This definition of " + newVar.getName() + " supersedes the one in "
						+ existingVar.getOriginName(), IGamlIssue.REDEFINES, NAME);
			} else {
				newVar.info("This definition of " + newVar.getName() + " supersedes the one in imported file "
						+ existingResource.getURI().lastSegment(), IGamlIssue.REDEFINES, NAME);
			}
		}
	}

	protected void inheritAttributesFrom(final TypeDescription p) {
		for (final VariableDescription v : p.getAttributes()) {
			addInheritedAttribute(v);
		}
	}

	public void addOwnAttribute(final VariableDescription vd) {
		final String newVarName = vd.getName();
		final VariableDescription existing = getAttribute(newVarName);

		if (existing != null) {
			// A previous definition has been found
			// We assert whether their types are compatible or not
			if (assertAttributesAreCompatible(existing, vd)) {
				markAttributeRedefinition(existing, vd);
				vd.copyFrom(existing);
			} else {
				return;
			}
		}

		addAttributeNoCheck(vd);
	}

	public void addInheritedAttribute(final VariableDescription vd) {
		// We dont inherit from previously added variables, as a child and its
		// parent should share the same javaBase

		final String inheritedVarName = vd.getName();

		if (attributes != null) {
			final VariableDescription existing = attributes.get(inheritedVarName);
			if (existing != null && assertAttributesAreCompatible(vd, existing)) {
				if (!existing.isBuiltIn()) {
					markAttributeRedefinition(vd, existing);
				}
				existing.copyFrom(vd);
			}
		}
	}

	Predicate<VariableDescription> IS_UPDATABLE = input -> input.isUpdatable();
	Predicate<String> VAR_IS_UPDATABLE = input -> getAttribute(input).isUpdatable();

	public List<String> getUpdatableAttributeNames() {
		return Lists.newArrayList(Iterables.filter(getOrderedAttributeNames(false), VAR_IS_UPDATABLE));
	}

	public Collection<String> getOrderedAttributeNames(final boolean forInit) {
		// TODO Do it once for built-in species
		final Collection<String> accumulator = parent != null && parent != this
				? parent.getOrderedAttributeNames(forInit) : new TLinkedHashSet<String>();
		if (attributes == null)
			return accumulator;
		if (attributes.size() <= 1) {
			accumulator.addAll(attributes.keySet());
			return accumulator;
		}

		final VariableDescription shape = attributes.get(SHAPE);
		final Collection<VariableDescription> shapeDependencies =
				shape == null ? Collections.EMPTY_SET : shape.getDependencies(forInit);
		final DirectedGraph<VariableDescription, Object> dependencies = new DefaultDirectedGraph<>(Object.class);
		if (shape != null) {
			dependencies.addVertex(shape);
		}
		attributes.forEachEntry((name, var) -> {

			dependencies.addVertex(var);
			if (shape != null && var.isSyntheticSpeciesContainer() && !shapeDependencies.contains(var)) {
				dependencies.addEdge(shape, var);
			}
			final Collection<VariableDescription> varDependencies = var.getDependencies(forInit);
			for (final VariableDescription newVar : varDependencies) {
				if (attributes.containsValue(newVar)) {
					dependencies.addVertex(newVar);
					dependencies.addEdge(newVar, var);
				}
			}
			return true;
		});

		final TopologicalOrderIterator<VariableDescription, Object> iterator =
				new TopologicalOrderIterator<>(dependencies);
		while (iterator.hasNext()) {

			final VariableDescription vd = iterator.next();
			final String name = vd.getName();
			if (accumulator.contains(name))
				accumulator.remove(name);
			accumulator.add(name);
		}
		return accumulator;

	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected boolean verifyAttributeCycles() {
		if (attributes == null || attributes.size() <= 1)
			return true;

		final VariableDescription shape = attributes.get(SHAPE);
		final Collection<VariableDescription> shapeDependencies =
				shape == null ? Collections.EMPTY_SET : shape.getDependencies(true);
		final DirectedGraph<VariableDescription, Object> dependencies = new DefaultDirectedGraph<>(Object.class);
		if (shape != null) {
			dependencies.addVertex(shape);
		}
		attributes.forEachEntry((name, var) -> {

			dependencies.addVertex(var);
			if (shape != null && var.isSyntheticSpeciesContainer() && !shapeDependencies.contains(var)) {
				dependencies.addEdge(shape, var);
			}

			for (final VariableDescription newVar : var.getDependencies(true)) {
				if (attributes.containsValue(newVar)) {
					dependencies.addVertex(newVar);
					dependencies.addEdge(newVar, var);
				}
			}
			return true;
		});

		final CycleDetector cycleDetector = new CycleDetector<VariableDescription, Object>(dependencies);
		if (cycleDetector.detectCycles()) {
			final Set<VariableDescription> inCycles = cycleDetector.findCycles();
			final Collection<String> names = Collections2.transform(inCycles, input -> input.getName());
			for (final VariableDescription vd : inCycles) {
				if (vd.isSyntheticSpeciesContainer() || vd.isBuiltIn())
					continue;
				final Collection<String> strings = new HashSet(names);
				strings.remove(vd.getName());
				vd.error("Cycle detected between " + vd.getName() + " and " + strings
						+ ". These attributes or sub-species depend on each other for the computation of their value. Consider moving one of the initializations to the 'init' section of the "
						+ getKeyword());
			}
			return false;
		}

		return true;
	}

	public void setParent(final TypeDescription parent) {
		this.parent = parent;
	}

	protected void duplicateInfo(final IDescription one, final IDescription two) {
		final String name = one.getName();
		final String key = one.getKeyword();
		final String error = key + " " + name + " is declared twice. This definition supersedes the previous in "
				+ two.getOriginName();
		one.info(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
	}

	protected void addAction(final ActionDescription newAction) {
		final String actionName = newAction.getName();
		if (actions != null) {
			final StatementDescription existing = actions.get(actionName);
			if (existing != null) {
				duplicateInfo(newAction, existing);
			}
		} else {
			actions = new THashMap<>();
		}
		actions.put(actionName, newAction);
	}

	public boolean redefinesAction(final String name) {
		if (!actions.contains(name))
			return false;
		if (parent == null || parent == this)
			return false;
		return parent.hasAction(name);
	}

	@Override
	public ActionDescription getAction(final String aName) {
		ActionDescription ownAction = null;
		if (actions != null)
			ownAction = actions.get(aName);
		if (ownAction == null && parent != null && parent != this)
			ownAction = getParent().getAction(aName);
		return ownAction;
	}

	public Collection<String> getActionNames() {
		final Collection<String> allNames =
				new LinkedHashSet(actions == null ? Collections.EMPTY_LIST : actions.keySet());
		if (parent != null && parent != this)
			allNames.addAll(getParent().getActionNames());
		return allNames;
	}

	public Iterable<ActionDescription> getActions() {
		return Iterables.transform(getActionNames(), input -> getAction(input));
	}

	@Override
	public boolean hasAction(final String a) {
		return actions != null && actions.containsKey(a)
				|| parent != null && parent != this && getParent().hasAction(a);
	}

	public boolean isAbstract() {
		for (final ActionDescription a : getActions()) {
			if (a.isAbstract()) { return true; }
		}
		return false;
	}

	@Override
	protected IType computeType() {
		return getTypeNamed(getName());
	}

	public boolean isArgOf(final String op, final String arg) {
		final ActionDescription action = getAction(op);
		if (action != null) { return action.containsArg(arg); }
		return false;
	}

	/**
	 * Returns the parent species.
	 *
	 * @return a TypeDescription or null
	 */
	public TypeDescription getParent() {
		return parent;
	}

	@Override
	public void dispose() {

		super.dispose();
		if (isBuiltIn()) { return; }
		actions = null;
		attributes = null;
		parent = null;
	}

	protected void inheritFromParent() {
		// Takes care of invalid species (see Issue 711)
		if (parent != null && parent != this) {
			inheritActionsFrom(parent);
			inheritAttributesFrom(parent);
		}
	}

	protected void inheritActionsFrom(final TypeDescription p) {
		if (p == null || p == this)
			return;
		for (final ActionDescription inheritedAction : p.getActions()) {
			final String actionName = inheritedAction.getName();
			final ActionDescription userDeclared = actions == null ? null : actions.get(actionName);
			if (userDeclared != null) {
				if (!(inheritedAction.isBuiltIn() && userDeclared.isBuiltIn())) {
					TypeDescription.assertActionsAreCompatible(userDeclared, inheritedAction,
							inheritedAction.getOriginName());
					if (inheritedAction.isBuiltIn()) {
						if (actionName.equals("die")) {
							userDeclared.warning(
									"Redefining the built-in primitive 'die' is not advised as it can lead to potential troubles in the disposal of simulations. If it was not your intention, consider renaming this action.",
									IGamlIssue.GENERAL);
						} else
							userDeclared.info(
									"Action '" + actionName + "' replaces a primitive of the same name defined in "
											+ userDeclared.getOriginName()
											+ ". If it was not your intention, consider renaming it.",
									IGamlIssue.GENERAL);
					} else {
						userDeclared.info("Action '" + actionName + "' supersedes the one defined in  "
								+ inheritedAction.getOriginName(), IGamlIssue.REDEFINES);
						return;
					}
				}
			} else if (inheritedAction.isAbstract()) {
				this.error(
						"Abstract action '" + actionName + "', inherited from "
								+ inheritedAction.getEnclosingDescription().getName() + ", should be redefined.",
						IGamlIssue.MISSING_ACTION, NAME);
				return;

			}
		}

	}

	public static void assertActionsAreCompatible(final ActionDescription myAction,
			final ActionDescription parentAction, final String parentName) {
		final String actionName = parentAction.getName();
		final IType myType = myAction.getType();
		final IType parentType = parentAction.getType();
		if (!parentType.isAssignableFrom(myType)) {
			myAction.error("Return type (" + myType + ") differs from that (" + parentType
					+ ") of the implementation of  " + actionName + " in " + parentName);
		}
		final List<String> myNames = myAction.getArgNames();
		final List<String> parentNames = parentAction.getArgNames();
		final boolean different = myNames.size() != parentNames.size() || !myNames.containsAll(parentNames);

		if (different) {
			final String error = "The list of arguments " + myNames + " differs from that of the implementation of "
					+ actionName + " in " + parentName + " " + parentNames + "";
			myAction.warning(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement(null));
		}

	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		for (final IDescription d : getAttributes()) {
			if (!visitor.visit(d))
				return false;
		}
		for (final IDescription d : getActions()) {
			if (!visitor.visit(d))
				return false;
		}
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		if (!visitOwnAttributes(visitor))
			return false;
		return visitOwnActions(visitor);
	}

	public boolean visitAllAttributes(final DescriptionVisitor visitor) {
		if (parent != null && parent != this)
			if (!parent.visitAllAttributes(visitor))
				return false;
		return visitOwnAttributes(visitor);
	}

	public boolean visitOwnAttributes(final DescriptionVisitor visitor) {
		if (attributes == null)
			return true;
		return attributes.forEachValue(visitor);
	}

	public boolean visitOwnActions(final DescriptionVisitor visitor) {
		if (actions == null)
			return true;
		return actions.forEachValue(visitor);
	}

	@Override
	public Iterable<IDescription> getOwnChildren() {
		return Iterables.concat(actions == null ? Collections.EMPTY_LIST : actions.values(),
				attributes == null ? Collections.EMPTY_LIST : attributes.values());
	}

	@Override
	public IDescription validate() {
		if (validated)
			return this;
		final IDescription result = super.validate();
		if (result != null && !verifyAttributeCycles())
			return null;
		return result;
	}

	public VariableDescription getOwnAttribute(final String keyword) {
		return attributes == null ? null : attributes.get(keyword);
	}

	public ActionDescription getOwnAction(final String keyword) {
		return actions == null ? null : actions.get(keyword);
	}

}