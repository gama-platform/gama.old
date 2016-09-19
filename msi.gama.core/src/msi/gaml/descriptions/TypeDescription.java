/*********************************************************************************************
 *
 *
 * 'TypeDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.ArrayList;
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.expressions.DenotedActionExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * A class that represents skills and species (either built-in or introduced by
 * users) The class TypeDescription.
 *
 * @author drogoul
 * @since 23 fevr. 2013
 *
 */
public abstract class TypeDescription extends SymbolDescription {

	// AD 08/16 : actions and attributes are now inherited dynamically and built
	// lazily
	protected THashMap<String, ActionDescription> actions;
	protected TOrderedHashMap<String, VariableDescription> attributes;
	protected TypeDescription parent;
	private final String plugin;

	public TypeDescription(final String keyword, final Class clazz, final IDescription macroDesc,
			final TypeDescription parent, final ChildrenProvider cp, final EObject source, final Facets facets,
			final String plugin) {
		super(keyword, macroDesc, cp, source, facets);
		// parent can be null
		if (parent != null)
			setParent(parent);
		this.plugin = plugin;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	/**
	 * ==================================== MANAGEMENT OF VARIABLES
	 */

	public Collection<VariableDescription> getAttributes() {
		final Collection<String> names = getAttributeNames();
		final Collection<VariableDescription> result = new ArrayList();
		for (final String name : names) {
			final VariableDescription vd = getAttribute(name);
			result.add(vd);
		}
		return result;
	}

	public Collection<String> getAttributeNames() {
		final Collection<String> accumulator = parent != null && parent != this ? parent.getAttributeNames()
				: new TLinkedHashSet<String>();
		if (attributes != null) {
			attributes.forEachKey(new TObjectProcedure<String>() {

				@Override
				public boolean execute(final String s) {
					if (accumulator.contains(s))
						accumulator.remove(s);
					accumulator.add(s);
					return true;
				}
			});
		}
		return accumulator;
	}

	public VariableDescription getAttribute(final String name) {
		final VariableDescription attribute = attributes == null ? null : attributes.get(name);
		if (attribute == null && parent != null && parent != this) {
			return getParent().getAttribute(name);
		}
		return attribute;
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
			if (desc != null) {
				return new DenotedActionExpression(desc);
			}
			return null;
		}
		return vd.getVarExpr(asField);
	}

	protected void addAttributeNoCheck(final VariableDescription vd) {
		if (attributes == null)
			attributes = new TOrderedHashMap();
		attributes.put(vd.getName(), vd);
	}

	public boolean assertAttributesAreCompatible(final VariableDescription existingVar,
			final VariableDescription newVar) {
		if (newVar.isBuiltIn() && existingVar.isBuiltIn()) {
			return true;
		}
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
		if (newVar.isBuiltIn() && existingVar.isBuiltIn()) {
			return;
		}
		if (newVar.getOriginName().equals(existingVar.getOriginName())) {
			// TODO must be review carefully the inheritance in comodel
			/// temporay fix for co-model, variable in micro-model can be
			// defined multi time
			if (!newVar.getModelDescription().getAlias().equals("")) {
				return;
			}
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
			final Resource newResource = newVar.getUnderlyingElement(null) == null ? null
					: newVar.getUnderlyingElement(null).eResource();
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

	public List<String> getUpdatableAttributeNames() {
		final List<String> result = new ArrayList(
				parent == null || parent == this ? Collections.EMPTY_LIST : parent.getUpdatableAttributeNames());
		visitOwnAttributes(new DescriptionVisitor<VariableDescription>() {

			@Override
			public boolean visit(final VariableDescription desc) {
				if (desc.isUpdatable()) {
					final String s = desc.getName();
					if (!result.contains(s))
						result.add(s);
				}
				return true;
			}
		});
		return result;
	}

	/**
	 * Returns true if the computation of dependencies is ok.
	 * 
	 * @return
	 */
	protected boolean sortAttributes() {
		if (attributes == null || attributes.size() <= 1)
			return true;

		final DirectedGraph<VariableDescription, Object> dependencies = new DefaultDirectedGraph<>(Object.class);
		attributes.forEachEntry(new TObjectObjectProcedure<String, VariableDescription>() {

			@Override
			public boolean execute(final String name, final VariableDescription var) {

				dependencies.addVertex(var);
				for (final String depName : var.getDependenciesNames()) {
					if (depName.equals(name))
						return true;
					final VariableDescription newVar = attributes.get(depName);
					if (newVar == null)
						continue;
					dependencies.addVertex(newVar);
					dependencies.addEdge(newVar, var);
				}
				var.discardDependencies();

				return true;
			}
		});
		final int oldAttributesSize = attributes.size();
		attributes.clear();

		final TopologicalOrderIterator<VariableDescription, Object> iterator = new TopologicalOrderIterator<>(
				dependencies);
		while (iterator.hasNext()) {

			final VariableDescription vd = iterator.next();
			attributes.put(vd.getName(), vd);
		}
		// If we miss some attributes (happens when cycles are present)
		if (oldAttributesSize != attributes.size()) {
			final CycleDetector cycleDetector = new CycleDetector<VariableDescription, Object>(dependencies);
			if (cycleDetector.detectCycles()) {
				final Set<VariableDescription> inCycles = cycleDetector.findCycles();
				final Collection<String> names = Collections2.transform(inCycles,
						new Function<VariableDescription, String>() {

							@Override
							public String apply(final VariableDescription input) {
								return input.getName();
							}
						});
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
		}

		// attributes.compact();
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
		// two.info(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
	}

	protected void addAction(final ActionDescription newAction) {
		// if (isBuiltIn()) {
		// newAction.setOriginName("built-in species " + getName());
		// }
		final String actionName = newAction.getName();
		if (actions != null) {
			final StatementDescription existing = actions.get(actionName);
			if (existing != null) {
				duplicateInfo(newAction, existing);
			}
		} else {
			actions = new THashMap();
		}
		actions.put(actionName, newAction);
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
		final Collection<String> allNames = new LinkedHashSet(
				actions == null ? Collections.EMPTY_LIST : actions.keySet());
		if (parent != null && parent != this)
			allNames.addAll(getParent().getActionNames());
		return allNames;
	}

	public Collection<ActionDescription> getActions() {
		final Collection<ActionDescription> allActions = new ArrayList();
		final Collection<String> actionNames = getActionNames();
		for (final String name : actionNames) {
			allActions.add(getAction(name));
		}
		return allActions;
	}

	@Override
	public boolean hasAction(final String a) {
		return actions != null && actions.containsKey(a)
				|| parent != null && parent != this && getParent().hasAction(a);
	}

	public boolean isAbstract() {
		for (final StatementDescription a : getActions()) {
			if (a.isAbstract()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected IType computeType() {
		return getTypeNamed(getName());
	}

	public boolean isArgOf(final String op, final String arg) {
		final ActionDescription action = getAction(op);
		if (action != null) {
			return action.containsArg(arg);
		}
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
		if (isBuiltIn()) {
			return;
		}
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
		final Collection<ActionDescription> inherited = p.getActions();
		for (final ActionDescription inheritedAction : inherited) {
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
		if (!new HashSet(parentAction.getArgNames()).equals(new HashSet(myAction.getArgNames()))) {
			final String error = "The list of arguments " + myAction.getArgNames()
					+ " differs from that of the implementation of " + actionName + " in " + parentName + " "
					+ parentAction.getArgNames() + "";
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

}