/*******************************************************************************************************
 *
 * msi.gaml.descriptions.TypeDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import static msi.gaml.descriptions.VariableDescription.FUNCTION_DEPENDENCIES_FACETS;
import static msi.gaml.descriptions.VariableDescription.INIT_DEPENDENCIES_FACETS;
import static msi.gaml.descriptions.VariableDescription.UPDATE_DEPENDENCIES_FACETS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
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
	protected IMap<String, ActionDescription> actions;
	protected IMap<String, VariableDescription> attributes;
	protected TypeDescription parent;
	protected final boolean isAbstract;

	public TypeDescription(final String keyword, final Class clazz, final IDescription macroDesc,
			final TypeDescription parent, final Iterable<? extends IDescription> cp, final EObject source,
			final Facets facets, final String plugin) {
		super(keyword, macroDesc, source, /* cp, */ facets);
		isAbstract = TRUE.equals(getLitteral(VIRTUAL));
		addChildren(cp);
		// parent can be null
		if (parent != null) {
			setParent(parent);
		}
		if (plugin != null && isBuiltIn()) {
			this.originName = plugin;
			// DEBUG.LOG("Origin name " + getOriginName() + " and plugin "
			// + plugin + " of " + this);
		}
		
	}

	public String getAttributeDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append("<b><br/>Attributes :</b><ul>");
		for (final VariableDescription f : getAttributes()) {
			sb.append("<li>").append("<b>").append(f.getName()).append("</b>").append(f.getShortDescription());
			sb.append("</li>");
		}

		sb.append("</ul>");
		return sb.toString();
	}

	public String getActionDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append("<b><br/>Actions :</b><ul>");
		for (final ActionDescription f : getActions()) {
			sb.append("<li>").append("<b>").append(f.getName()).append("</b>").append(f.getShortDescription());
			sb.append("</li>");
		}

		sb.append("</ul>");
		return sb.toString();
	}

	@Override
	public String getDefiningPlugin() {
		if (isBuiltIn())
			return originName;
		return null;
	}

	public abstract Class getJavaBase();

	/**
	 * ==================================== MANAGEMENT OF ATTRIBUTES
	 */

	public Iterable<VariableDescription> getAttributes() {
		return Iterables.transform(getAttributeNames(), input -> getAttribute(input));
	}

	public Iterable<VariableDescription> getOwnAttributes() {
		return attributes == null ? Collections.EMPTY_LIST : attributes.values();
	}

	public Collection<String> getAttributeNames() {
		final Collection<String> accumulator =
				parent != null && parent != this ? parent.getAttributeNames() : new LinkedHashSet<>();
		if (attributes != null) {
			attributes.forEachKey(s -> {
				if (accumulator.contains(s)) {
					accumulator.remove(s);
				}
				accumulator.add(s);
				return true;
			});
		}
		return accumulator;
	}

	public VariableDescription getAttribute(final String vn) {
		final VariableDescription attribute = attributes == null ? null : attributes.get(vn);
		if (attribute == null && parent != null && parent != this)
			return getParent().getAttribute(vn);
		return attribute;
	}

	public boolean redefinesAttribute(final String vn) {
		if (!attributes.containsKey(name))
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
			if (desc != null)
				return new DenotedActionExpression(desc);
			return null;
		}
		return vd.getVarExpr(asField);
	}

	protected void addAttributeNoCheck(final VariableDescription vd) {
		if (attributes == null) {
			attributes = GamaMapFactory.create();
		}
		// synchronized (this) {
		attributes.put(vd.getName(), vd);
		// }
	}

	public boolean assertAttributesAreCompatible(final VariableDescription existingVar,
			final VariableDescription newVar) {
		if (newVar.isBuiltIn() && existingVar.isBuiltIn())
			return true;
		final IType existingType = existingVar.getGamlType();
		final IType newType = newVar.getGamlType();
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
			final EObject newObject = newVar.getUnderlyingElement();
			final Resource newResource = newObject == null ? null : newObject.eResource();
			final EObject existingObject = existingVar.getUnderlyingElement();
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
		if (newVar.isBuiltIn() && existingVar.isBuiltIn())
			return;
		if (newVar.getOriginName().equals(existingVar.getOriginName())) {
			// TODO must be review carefully the inheritance in comodel
			/// temporay fix for co-model, variable in micro-model can be
			// defined multi time
			if (!newVar.getModelDescription().getAlias().equals(""))
				return;
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
					newVar.getUnderlyingElement() == null ? null : newVar.getUnderlyingElement().eResource();
			final Resource existingResource = existingVar.getUnderlyingElement().eResource();
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
			} else
				return;
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

	public Iterable<String> getUpdatableAttributeNames() {

		// June 2020: moving (back) to Iterables instead of Streams.
		return Iterables.filter(getOrderedAttributeNames(UPDATE_DEPENDENCIES_FACETS),
				input -> getAttribute(input).isUpdatable());
		// final Collection<String> vars = getOrderedAttributeNames(UPDATE_DEPENDENCIES_FACETS);
		// return StreamEx.of(vars).filter(input -> getAttribute(input).isUpdatable()).toList();
	}

	public Collection<String> getOrderedAttributeNames(final Set<String> facetsToConsider) {
		// AD Revised in Aug 2019 for Issue #2869: keep constraints between superspecies and subspecies
		
		final DefaultDirectedGraph<String, Object> dependencies = new DefaultDirectedGraph<>(Object.class);
		final Map<String, VariableDescription> all = new HashMap<>();
		this.visitAllAttributes((d) -> {
			all.put(d.getName(), (VariableDescription) d);
			return true;
		});
		
		Graphs.addAllVertices(dependencies, all.keySet());
		final VariableDescription shape = getAttribute(SHAPE);
		final Collection<VariableDescription> shapeDependencies =
				shape == null ? Collections.EMPTY_LIST : shape.getDependencies(facetsToConsider, false, true);
		
		all.forEach((an, var) -> {
			for (final VariableDescription newVar : var.getDependencies(facetsToConsider, false, true)) {
				final String other = newVar.getName();
				// AD Revision in April 2019 for Issue #2624: prevent cycles when building the graph
				if (!dependencies.containsEdge(an, other)) {
					
					dependencies.addEdge(other, an);
				}
			}
			// Adding a constraint between the shape of the macrospecies and the populations of microspecies
			if (var.isSyntheticSpeciesContainer() && !shapeDependencies.contains(var)) {
				dependencies.addEdge(SHAPE, an);
			}
		});
		
		//TODO: WE HAVE TO FIND A SOLUTION FOR CYCLES IN VARIABLES 
		
		// June 2021: Temporary patch remove cycles to avoid infinite loop in TopologicalOrderIterator and add variables after
		Set<String> varToAdd = new HashSet<>();
		while (true) {
			CycleDetector c  = new CycleDetector<>(dependencies);
			if (c.detectCycles()) {
				Set<String> cycle = c.findCycles();
				for(String s : cycle) {
					dependencies.removeVertex(s);
					varToAdd.add(s);
					break;
				}
			} else {
				break;
			}
			
		}
			
		// June 2020: moving (back) to Iterables instead of Streams.
		//ArrayList<String> list = Lists.newArrayList((dependencies.vertexSet()));
		ArrayList<String> list = Lists.newArrayList(new TopologicalOrderIterator<>(dependencies));
		
		// March 2021: Temporary patch for #3068 - just add missing variables. TopologicalOrderIterator have to be fixed
		for (String s : dependencies.vertexSet()) {
			if (!list.contains(s)) {
				list.add(s);
			}
		}
		for (String s : varToAdd) {
			if (!list.contains(s)) {
				list.add(s);
			}
		}
		return list;
		// return StreamEx.of(new TopologicalOrderIterator<>(dependencies)).toList();
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
		final DefaultDirectedGraph<VariableDescription, Object> dependencies = new DefaultDirectedGraph<>(Object.class);
		if (shape != null) {
			dependencies.addVertex(shape);
		}
		final Collection<VariableDescription> shapeDependencies =
				shape == null ? Collections.EMPTY_SET : shape.getDependencies(INIT_DEPENDENCIES_FACETS, false, true);

		attributes.forEachPair((aName, var) -> {
			dependencies.addVertex(var);
			if (shape != null && var.isSyntheticSpeciesContainer() && !shapeDependencies.contains(var)) {
				dependencies.addEdge(shape, var);
			}

			for (final VariableDescription newVar : var.getDependencies(INIT_DEPENDENCIES_FACETS, false, true)) {
				if (attributes.containsValue(newVar)) {
					dependencies.addVertex(newVar);
					dependencies.addEdge(newVar, var);
				}
			}
			return true;
		});

		final CycleDetector cycleDetector = new CycleDetector<>(dependencies);
		if (cycleDetector.detectCycles()) {
			final Set<VariableDescription> inCycles = cycleDetector.findCycles();
			for (final VariableDescription vd : inCycles) {
				if (vd.isSyntheticSpeciesContainer() || vd.isBuiltIn()) {
					continue;
				}
				final Collection<String> strings = new HashSet(Collections2.transform(inCycles, TO_NAME));
				strings.remove(vd.getName());
				vd.error("Cycle detected between " + vd.getName() + " and " + strings
						+ ". These attributes or sub-species depend on each other for the computation of their value. Consider moving one of the initializations to the 'init' section of the "
						+ getKeyword());
			}
			return false;
		}

		final DefaultDirectedGraph<VariableDescription, Object> fDependencies = new DefaultDirectedGraph<>(Object.class);
		attributes.forEachPair((aName, var) -> {
			if (!var.hasFacet(FUNCTION))
				return true;
			fDependencies.addVertex(var);
			for (final VariableDescription newVar : var.getDependencies(FUNCTION_DEPENDENCIES_FACETS, true, false)) {
				if (attributes.containsValue(newVar)) {
					fDependencies.addVertex(newVar);
					fDependencies.addEdge(newVar, var);
				}
			}
			return true;
		});

		if (!fDependencies.vertexSet().isEmpty()) {
			final CycleDetector functionCycleDetector = new CycleDetector<>(fDependencies);
			if (functionCycleDetector.detectCycles()) {
				final Set<VariableDescription> inCycles = functionCycleDetector.findCycles();
				for (final VariableDescription vd : inCycles) {
					if (vd.isSyntheticSpeciesContainer() || vd.isBuiltIn()) {
						continue;
					}
					final Collection<String> strings = new HashSet(Collections2.transform(inCycles, TO_NAME));
					vd.error("Cycle detected between " + vd.getName() + " and " + strings
							+ "; attributes declared as functions cannot contain references to themselves in their function");
				}
				return false;

			}
		}
		return true;
	}

	public void setParent(final TypeDescription parent) {
		this.parent = parent;
	}

	protected void duplicateInfo(final IDescription one, final IDescription two) {
		final String aName = one.getName();
		final String key = one.getKeyword();
		if (!one.getOriginName().equals(two.getOriginName())) {
			if (key.equals(REFLEX)) {
				one.info(
						"The order in which reflex " + aName + " will be executed in " + one.getOriginName()
								+ " can differ from the order defined in " + two.getOriginName(),
						IGamlIssue.GENERAL, NAME, aName);
			}
			one.info("This definition of " + key + " " + aName + " supersedes the one existing in "
					+ two.getOriginName(), IGamlIssue.DUPLICATE_DEFINITION, NAME, aName);
		} else {
			one.info("This definition of " + key + " " + aName + " supersedes the previous one(s) in the same species",
					IGamlIssue.DUPLICATE_DEFINITION, NAME, aName);

		}
	}

	protected void addAction(final ActionDescription newAction) {
		final String actionName = newAction.getName();
		if (actions != null) {
			final StatementDescription existing = actions.get(actionName);
			if (existing != null) {
				duplicateInfo(newAction, existing);
			}
		} else {
			actions = GamaMapFactory.createUnordered();
		}
		actions.put(actionName, newAction);
	}

	public boolean redefinesAction(final String theName) {
		if (!actions.containsKey(theName))
			return false;
		if (parent == null || parent == this)
			return false;
		return parent.hasAction(theName, false);
	}

	@Override
	public ActionDescription getAction(final String aName) {
		ActionDescription ownAction = null;
		if (actions != null) {
			ownAction = actions.get(aName);
		}
		if (ownAction == null && parent != null && parent != this) {
			ownAction = getParent().getAction(aName);
		}
		return ownAction;
	}

	public Iterable<ActionDescription> getOwnActions() {
		return actions == null ? Collections.EMPTY_LIST : actions.values();
	}

	public void removeAction(final String temp) {
		if (actions == null)
			return;
		actions.remove(temp);

	}

	public Collection<String> getActionNames() {
		final Collection<String> allNames =
				new LinkedHashSet(actions == null ? Collections.EMPTY_LIST : actions.keySet());
		if (parent != null && parent != this) {
			allNames.addAll(getParent().getActionNames());
		}
		return allNames;
	}

	public Iterable<ActionDescription> getActions() {
		return Iterables.transform(getActionNames(), input -> getAction(input));
	}

	@Override
	public boolean hasAction(final String a, final boolean superInvocation) {
		if (superInvocation) {
			if (parent == null || parent == this)
				return false;
			return parent.hasAction(a, false);
		}
		return actions != null && actions.containsKey(a)
				|| parent != null && parent != this && getParent().hasAction(a, superInvocation);
	}

	@Override
	public IDescription getDescriptionDeclaringAction(final String vn, final boolean superInvocation) {
		if (superInvocation) {
			if (parent == null)
				return null;
			return parent.getDescriptionDeclaringAction(vn, false);
		}
		return hasAction(vn, false) ? this : null;
	}

	public final boolean isAbstract() {
		if (isAbstract)
			return true;
		for (final ActionDescription a : getActions()) {
			if (a.isAbstract())
				return true;
		}
		return false;
	}

	@Override
	protected IType computeType() {
		return getTypeNamed(getName());
	}

	public boolean isArgOf(final String op, final String arg) {
		final ActionDescription action = getAction(op);
		if (action != null)
			return action.containsArg(arg);
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
		if (isBuiltIn())
			return;
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
						} else {
							userDeclared.info(
									"Action '" + actionName + "' replaces a primitive of the same name defined in "
											+ userDeclared.getOriginName()
											+ ". If it was not your intention, consider renaming it.",
									IGamlIssue.GENERAL);
						}
					} else {
						userDeclared.info("Action '" + actionName + "' supersedes the one defined in  "
								+ inheritedAction.getOriginName(), IGamlIssue.REDEFINES);
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
		final IType myType = myAction.getGamlType();
		final IType parentType = parentAction.getGamlType();
		if (!parentType.isAssignableFrom(myType)) {
			myAction.error("Return type (" + myType + ") differs from that (" + parentType
					+ ") of the implementation of  " + actionName + " in " + parentName);
			return;
		}
		final Iterable<IDescription> myArgs = myAction.getFormalArgs();
		final Iterable<IDescription> parentArgs = parentAction.getFormalArgs();
		final Iterator<IDescription> myIt = myArgs.iterator();
		final Iterator<IDescription> parentIt = parentArgs.iterator();
		String added = null;
		boolean differentName = false;
		String differentType = null;
		while (myIt.hasNext()) {
			final IDescription myArg = myIt.next();
			if (!parentIt.hasNext()) {
				added = myArg.getName();
				break;
			}
			final IDescription parentArg = parentIt.next();
			final String myName = myArg.getName();
			final String pName = parentArg.getName();
			if (!myName.equals(pName)) {
				differentName = true;
				break;
			}
			if (!parentArg.getGamlType().isAssignableFrom(myArg.getGamlType())) {
				differentType = myName;
				break;
			}
		}
		if (!myIt.hasNext() && parentIt.hasNext()) {
			final String error = "Missing argument: " + parentIt.next().getName();
			myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement());
			return;
		}
		if (added != null) {
			final String error =
					"Argument " + added + " does not belong to the definition of " + actionName + " in " + parentName;
			myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement());
			return;
		}
		if (differentName) {
			final String error = "The  names of arguments should be identical to those of the definition of "
					+ actionName + " in " + parentName;
			myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement());
			return;
		}
		if (differentType != null) {
			final String error = "The  type of argument  " + differentType
					+ " is not compatible with that in the definition of " + actionName + " in " + parentName;
			myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement());
		}

		// final Map<String, IType<?>> myMap = StreamEx.of(myArgs.iterator()).toMap(d -> d.getName(), d -> d.getType());
		// final Map<String, IType<?>> parentMap =
		// StreamEx.of(parentArgs.iterator()).toMap(d -> d.getName(), d -> d.getType());
		//
		// final List<String> myNames = myAction.getArgNames();
		// final List<String> parentNames = parentAction.getArgNames();
		// boolean different = myNames.size() != parentNames.size();
		// if (different) {
		// final String error = "The number of arguments should be identical to that of the definition of "
		// + actionName + " in " + parentName + ": " + parentNames + "";
		// myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement(null));
		// return;
		// }
		// different = !myNames.containsAll(parentNames);
		// if (different) {
		// final String error = "The names of arguments should be identical to those of the definition of "
		// + actionName + " in " + parentName + " " + parentNames + "";
		// myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement(null));
		// return;
		// }
		// final List<IType<?>> myTypes = myAction.getArgTypes();
		// final List<IType<?>> parentTypes = parentAction.getArgTypes();
		// different = !myTypes.containsAll(parentTypes);
		// if (different) {
		// final String error = "The types of arguments should be identical to those in the definition of "
		// + actionName + " in " + parentName + " " + parentTypes + "";
		// myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement(null));
		// }

	}

	@Override
	public boolean visitChildren(final DescriptionVisitor<IDescription> visitor) {
		for (final IDescription d : getAttributes()) {
			if (!visitor.process(d))
				return false;
		}
		for (final IDescription d : getActions()) {
			if (!visitor.process(d))
				return false;
		}
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor<IDescription> visitor) {
		if (!visitOwnAttributes(visitor))
			return false;
		return visitOwnActions(visitor);
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor<IDescription> visitor) {
		if (!visitOwnAttributes(visitor))
			return false;
		return visitOwnActionsRecursively(visitor);
	}

	public boolean visitAllAttributes(final DescriptionVisitor<IDescription> visitor) {
		if (parent != null && parent != this) {
			if (!parent.visitAllAttributes(visitor))
				return false;
		}
		return visitOwnAttributes(visitor);
	}

	public boolean visitOwnAttributes(final DescriptionVisitor<IDescription> visitor) {
		if (attributes == null)
			return true;
		return attributes.forEachValue(visitor);
	}

	public boolean visitOwnActions(final DescriptionVisitor<IDescription> visitor) {
		if (actions == null)
			return true;
		return actions.forEachValue(visitor);
	}

	public boolean visitOwnActionsRecursively(final DescriptionVisitor<IDescription> visitor) {
		if (actions == null)
			return true;
		return actions.forEachValue(each -> {
			if (!visitor.process(each))
				return false;
			return each.visitOwnChildrenRecursively(visitor);
		});
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

	public VariableDescription getOwnAttribute(final String kw) {
		return attributes == null ? null : attributes.get(kw);
	}

	public ActionDescription getOwnAction(final String kw) {
		return actions == null ? null : actions.get(kw);
	}

}