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

import java.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;

/**
 * A class that represents skills and species (either built-in or introduced by users)
 * The class TypeDescription.
 *
 * @author drogoul
 * @since 23 fevr. 2013
 *
 */
public abstract class TypeDescription extends SymbolDescription {

	protected Map<String, StatementDescription> actions;
	protected Map<String, VariableDescription> variables;
	protected Class javaBase;
	protected TypeDescription parent;
	private static int varCount = 0;
	private final String plugin;

	public TypeDescription(final String keyword, final Class clazz, final IDescription macroDesc,
		final IDescription parent, final ChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, macroDesc, cp, source, facets);
		// parent can be null
		setJavaBase(clazz);
		setParent((TypeDescription) parent);
		plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	public void copyJavaAdditions() {
		final Class clazz = getJavaBase();
		if ( clazz == null ) {
			error("This species cannot be compiled as its Java base is unknown. ", IGamlIssue.UNKNOWN_SUBSPECIES);
			return;
		}
		final Set<IDescription> children = AbstractGamlAdditions.getAllChildrenOf(getJavaBase(), getSkillClasses());
		for ( final IDescription v : children ) {
			if ( v instanceof VariableDescription ) {
				addBuiltInVariable((VariableDescription) v);
			} else {
				addChild(v.copy(this));
			}
		}
	}

	/**
	 * ==================================== MANAGEMENT OF VARIABLES
	 */

	public Map<String, VariableDescription> getVariables() {
		if ( variables == null ) {
			variables = new TOrderedHashMap<String, VariableDescription>();
		}
		return variables;
	}

	public Collection<String> getVarNames() {
		if ( variables != null ) { return variables.keySet(); }
		return GamaListFactory.EMPTY_LIST;
	}

	public VariableDescription getVariable(final String name) {
		return variables == null ? null : variables.get(name);
	}

	@Override
	public boolean hasVar(final String a) {
		return variables != null && variables.containsKey(a);
	}

	@Override
	public IExpression getVarExpr(final String n) {
		final VariableDescription vd = getVariable(n);
		if ( vd == null ) {
			IDescription desc = getAction(n);
			if ( desc != null ) { return new DenotedActionExpression(desc); }
			return null;
		}
		return vd.getVarExpr();
	}

	protected void addVariableNoCheck(final VariableDescription vd) {
		vd.setDefinitionOrder(varCount++);
		getVariables().put(vd.getName(), vd);
	}

	public boolean assertVarsAreCompatible(final VariableDescription existingVar, final VariableDescription newVar) {
		if ( newVar.isBuiltIn() && existingVar.isBuiltIn() ) { return true; }
		IType existingType = existingVar.getType();
		IType newType = newVar.getType();
		if ( !newType.isTranslatableInto(existingType) ) {
			markTypeDifference(existingVar, newVar, existingType, newType, true);
		} else if ( !newType.equals(existingType) && !newType.isParametricFormOf(existingType) ) {
			markTypeDifference(existingVar, newVar, existingType, newType, false);
		}
		return true;
	}

	private void markTypeDifference(final VariableDescription existingVar, final VariableDescription newVar,
		final IType existingType, final IType newType, final boolean error) {
		String msg = "Type (" + newType + ") differs from that (" + existingType + ") of the implementation of  " +
			newVar.getName() + " in " + existingVar.getOriginName();
		if ( existingVar.isBuiltIn() ) {
			if ( error ) {
				newVar.error(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
			} else {
				newVar.warning(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
			}
		} else {
			Resource newResource = newVar.getUnderlyingElement(null).eResource();
			Resource existingResource = existingVar.getUnderlyingElement(null).eResource();
			if ( newResource.equals(existingResource) ) {
				if ( error ) {
					newVar.error(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
				} else {
					newVar.info(msg, IGamlIssue.WRONG_REDEFINITION, NAME);
				}
			} else {
				if ( error ) {
					newVar.error(msg + " in  imported file " + existingResource.getURI().lastSegment(),
						IGamlIssue.WRONG_REDEFINITION, NAME);
				} else {
					newVar.info(msg + " in  imported file " + existingResource.getURI().lastSegment(),
						IGamlIssue.WRONG_REDEFINITION, NAME);
				}
			}
		}

	}

	public void markVariableRedefinition(final VariableDescription existingVar, final VariableDescription newVar) {
		if ( newVar.isBuiltIn() && existingVar.isBuiltIn() ) { return; }
		if ( newVar.getOriginName().equals(existingVar.getOriginName()) ) {
			existingVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION,
				NAME);
			newVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION, NAME);
			return;
		}
		if ( existingVar.isBuiltIn() ) {
			newVar.info(
				"This definition of " + newVar.getName() + " supersedes the one in " + existingVar.getOriginName(),
				IGamlIssue.REDEFINES, NAME);
		} else {
			// Possibily different resources
			Resource newResource = newVar.getUnderlyingElement(null).eResource();
			Resource existingResource = existingVar.getUnderlyingElement(null).eResource();
			if ( newResource.equals(existingResource) ) {
				newVar.info(
					"This definition of " + newVar.getName() + " supersedes the one in " + existingVar.getOriginName(),
					IGamlIssue.REDEFINES, NAME);
			} else {
				newVar.info("This definition of " + newVar.getName() + " supersedes the one in imported file " +
					existingResource.getURI().lastSegment(), IGamlIssue.REDEFINES, NAME);
			}
		}
	}

	protected void inheritVariablesFrom(final TypeDescription p) {
		if ( p.variables != null ) {
			for ( final VariableDescription v : p.getVariables().values() ) {
				addInheritedVariable(v);
			}
		}
	}

	public void addBuiltInVariable(final VariableDescription vd) {
		// We just add a copy of the variable
		addVariableNoCheck(vd.copy(this));
	}

	public void addOwnVariable(final VariableDescription vd) {
		String newVarName = vd.getName();
		// If no previous definition is found, just add the variable
		if ( !hasVar(newVarName) ) {
			addVariableNoCheck(vd);
			return;
		}
		// A previous deifnition has been found
		VariableDescription existing = getVariable(newVarName);
		// We assert whether their types are compatible or not
		if ( assertVarsAreCompatible(existing, vd) ) {
			markVariableRedefinition(existing, vd);
			vd.copyFrom(existing);
			addVariableNoCheck(vd);
		}
	}

	public void addInheritedVariable(final VariableDescription vd) {
		// We dont inherit from previously added variables, as a child and its parent should
		// share the same javaBase

		String inheritedVarName = vd.getName();

		// If no previous definition is found, just add the variable
		if ( !hasVar(inheritedVarName) ) {
			addVariableNoCheck(vd.copy(this));
			return;
		}
		// A redefinition has been found
		VariableDescription existing = getVariable(inheritedVarName);
		if ( assertVarsAreCompatible(vd, existing) ) {
			if ( !existing.isBuiltIn() ) {
				markVariableRedefinition(vd, existing);
			}
			existing.copyFrom(vd);
		}
	}

	public List<String> getUpdatableVarNames() {
		if ( variables == null ) { return Collections.EMPTY_LIST; }
		List<String> result = new ArrayList();
		for ( Map.Entry<String, VariableDescription> entry : variables.entrySet() ) {
			if ( entry.getValue().isUpdatable() ) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	protected void sortVars() {
		if ( variables == null ) { return; }
		// GuiUtils.debug("***** Sorting variables of " + getNameFacetValue());

		final List<VariableDescription> result = new ArrayList();
		final Collection<VariableDescription> vars = getVariables().values();

		for ( final VariableDescription var : vars ) {
			if ( var != null ) {
				var.usedVariablesIn(getVariables());
			}
		}
		for ( final VariableDescription var : vars ) {
			if ( var != null ) {
				var.expandDependencies(new ArrayList());
			}
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
		variables.clear();
		for ( int i = 0; i < result.size(); i++ ) {
			final VariableDescription v = result.get(i);
			final String s = v.getName();
			variables.put(s, v);
		}

	}

	public void resortVarName(final VariableDescription var) {
		var.usedVariablesIn(getVariables());
		var.expandDependencies(new ArrayList());
		variables.remove(var.getName());
		List<VariableDescription> vl = new ArrayList(variables.values());
		ListIterator<VariableDescription> li = vl.listIterator(vl.size());
		boolean added = false;
		while (li.hasPrevious() && !added) {
			VariableDescription vd = li.previous();
			if ( var.getDependencies().contains(vd) ) {
				li.next();
				li.add(var);
				added = true;
			};
		}
		if ( !added ) {
			vl.add(0, var);
		}
		variables.clear();
		for ( VariableDescription vd : vl ) {
			variables.put(vd.getName(), vd);
		}
	}

	public void setParent(final TypeDescription parent) {
		this.parent = parent;
	}

	public Set<Class> getSkillClasses() {
		return Collections.EMPTY_SET;
	}

	protected void duplicateInfo(final IDescription one, final IDescription two) {
		final String name = one.getFacets().getLabel(NAME);
		final String key = one.getKeyword();
		final String error =
			key + " " + name + " is declared twice. This definition supersedes the previous in " + two.getOriginName();
		one.info(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
		// two.info(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
	}

	protected void addAction(final TypeDescription from, final StatementDescription newAction) {
		final String actionName = newAction.getName();

		final StatementDescription existing = getAction(actionName);
		if ( existing != null ) {
			if ( !(newAction.isBuiltIn() && existing.isBuiltIn()) ) {

				TypeDescription.assertActionsAreCompatible(newAction, existing, existing.getOriginName());
				if ( !existing.isAbstract() ) {
					if ( existing.isBuiltIn() ) {
						newAction.info(
							"Action '" + actionName + "' replaces a primitive of the same name defined in " +
								existing.getOriginName() + ". If it was not your intention, consider renaming it.",
							IGamlIssue.GENERAL);
					} else if ( from == this ) {
						duplicateInfo(newAction, existing);
					} else {
						existing.info(
							"Action '" + actionName + "' supersedes the one defined in  " + newAction.getOriginName(),
							IGamlIssue.REDEFINES);
						return;
					}
				} else if ( newAction.isAbstract() && from != this ) {
					this.error("Abstract action '" + actionName + "', inherited from " + from.getName() +
						", should be redefined.", IGamlIssue.MISSING_ACTION, NAME);
					return;
				}
			}
		} else if ( newAction.isAbstract() && from != this ) {
			this.error(
				"Abstract action '" + actionName + "', inherited from " + from.getName() + ", should be redefined.",
				IGamlIssue.MISSING_ACTION, NAME);
			return;

		}
		if ( actions == null ) {
			actions = new TOrderedHashMap<String, StatementDescription>();
		}
		actions.put(actionName, newAction);
	}

	@Override
	public StatementDescription getAction(final String aName) {
		return actions == null ? null : actions.get(aName);
	}

	public Collection<String> getActionNames() {
		return actions == null ? Collections.EMPTY_LIST : actions.keySet();
	}

	public Collection<StatementDescription> getActions() {
		return actions == null ? Collections.EMPTY_LIST : actions.values();
	}

	@Override
	public boolean hasAction(final String a) {
		return actions != null && actions.containsKey(a);
	}

	public abstract Class getJavaBase();

	protected void setJavaBase(final Class javaBase) {
		this.javaBase = javaBase;
	}

	public boolean isAbstract() {
		for ( final StatementDescription a : getActions() ) {
			if ( a.isAbstract() ) { return true; }
		}
		return false;
	}

	@Override
	public IType getType() {
		// WARNING: this leads to numerous computations. Could we cache the type somehow ?
		// WARNING: Before, we should count how many invocations are made
		return getTypeNamed(getName());
	}

	public boolean isArgOf(final String op, final String arg) {
		final StatementDescription action = getAction(op);
		if ( action != null ) { return action.containsArg(arg); }
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
		if ( isBuiltIn() ) { return; }
		if ( actions != null ) {
			actions.clear();
		}
		if ( variables != null ) {
			variables.clear();
		}
		super.dispose();
	}

	protected void inheritFromParent() {
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this && !parent.isBuiltIn() ) {
			inheritActionsFrom(parent);
			inheritVariablesFrom(parent);
		}
	}

	protected void inheritActionsFrom(final TypeDescription p) {
		// We only copy the actions that are not redefined in this species
		if ( p.actions != null ) {
			for ( final StatementDescription action : p.actions.values() ) {
				addAction(p, action);
			}
		}
	}

	public static void assertActionsAreCompatible(final StatementDescription myAction,
		final StatementDescription parentAction, final String parentName) {
		final String actionName = parentAction.getName();
		IType myType = myAction.getType();
		IType parentType = parentAction.getType();
		if ( !parentType.isAssignableFrom(myType) ) {
			myAction.error("Return type (" + myType + ") differs from that (" + parentType +
				") of the implementation of  " + actionName + " in " + parentName);
		}
		if ( !new HashSet(parentAction.getArgNames()).equals(new HashSet(myAction.getArgNames())) ) {
			final String error =
				"The list of arguments " + myAction.getArgNames() + " differs from that of the implementation of " +
					actionName + " in " + parentName + " " + parentAction.getArgNames() + "";
			myAction.warning(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement(null));
		}

	}

	@Override
	public List<IDescription> getChildren() {
		List<IDescription> result = new ArrayList();
		if ( variables != null ) {
			result.addAll(variables.values());
		}
		if ( actions != null ) {
			result.addAll(actions.values());
		}
		return result;
	}

}