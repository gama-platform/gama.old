package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.*;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import org.eclipse.emf.ecore.EObject;

/**
 * A class that represents skills and species (either built-in or introduced by users)
 * The class TypeDescription.
 * 
 * @author drogoul
 * @since 23 f�vr. 2013
 * 
 */
public abstract class TypeDescription extends SymbolDescription {

	protected Map<String, StatementDescription> actions;
	protected Map<String, VariableDescription> variables;
	protected Class javaBase;
	protected TypeDescription parent;
	private static int varCount = 0;

	public TypeDescription(final String keyword, final Class clazz, final IDescription macroDesc,
		final IDescription parent, final ChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, macroDesc, cp, source, facets);
		// parent can be null
		setJavaBase(clazz);
		setParent((TypeDescription) parent);
	}

	public void copyJavaAdditions() {
		final Class clazz = getJavaBase();
		if ( clazz == null ) {
			error("This species cannot be compiled as its parent is unknown. ", IGamlIssue.UNKNOWN_SUBSPECIES);
			return;
		}
		final Set<IDescription> children = AbstractGamlAdditions.getAllChildrenOf(getJavaBase(), getSkillClasses());
		for ( final IDescription v : children ) {
			addChild(v.copy(this));
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

	// protected void addPrimitive(final StatementDescription newAction) {
	// final String actionName = newAction.getName();
	// final StatementDescription existing = getAction(actionName);
	// TypeDescription.assertActionsAreCompatible(existing, newAction, newAction.getOriginName());
	// if ( actions == null ) {
	// actions = new LinkedHashMap<String, StatementDescription>();
	// }
	// actions.put(actionName, newAction);
	//
	// }

	protected void addAction(final TypeDescription from, final StatementDescription newAction) {
		final String actionName = newAction.getName();
		final StatementDescription existing = getAction(actionName);
		if ( existing != null ) {
			if ( newAction.isBuiltIn() && existing.isBuiltIn() ) { return; }
			TypeDescription.assertActionsAreCompatible(newAction, existing, existing.getOriginName());
			if ( !existing.isAbstract() ) {
				if ( existing.isBuiltIn() ) {
					newAction.info("Action '" + actionName + "' replaces a primitive of the same name defined in " +
						existing.getOriginName() + ". If it was not your intention, consider renaming it.",
						IGamlIssue.GENERAL);
				} else if ( from == this ) {
					duplicateInfo(newAction, existing);
				} else {
					existing.info(
						"Action '" + actionName + "' supersedes the one defined in  " + newAction.getOriginName(),
						IGamlIssue.REDEFINES);
				}
			} else if ( newAction.isAbstract() && from != this ) {
				this.error("Abstract action '" + actionName + "', inherited from " + from.getName() +
					", should be redefined.", IGamlIssue.MISSING_ACTION, NAME);
				return;
			}
		} else if ( newAction.isAbstract() && from != this ) {
			this.error("Abstract action '" + actionName + "', inherited from " + from.getName() +
				", should be redefined.", IGamlIssue.MISSING_ACTION, NAME);
			return;
		}
		if ( actions == null ) {
			actions = new LinkedHashMap<String, StatementDescription>();
		}
		actions.put(actionName, newAction);
	}

	// protected void inheritAction(final TypeDescription parent, final StatementDescription parentAction) {
	// final String actionName = parentAction.getName();
	// final StatementDescription existingAction = getAction(actionName);
	// if ( existingAction == null ) {
	// // The action does not replace any. Just proceed.
	// // addChild(parentAction);
	// return;
	// }
	// if ( existingAction.isAbstract() && parentAction.isAbstract() ) {
	// this.error("Abstract action '" + actionName + "', inherited from " + parent.getName() +
	// ", should be redefined.", IGamlIssue.MISSING_ACTION, NAME);
	// return;
	// }
	// TypeDescription.assertActionsAreCompatible(existingAction, parentAction, parent.getName());
	// if ( existingAction.isBuiltIn() ) {
	// // addChild(parentAction);
	// return;
	// }
	// existingAction.info(
	// "Redefinition of the action " + actionName + " defined in  " + parentAction.getOriginName(),
	// IGamlIssue.REDEFINES);
	// }

	@Override
	public StatementDescription getAction(final String aName) {
		return actions == null ? null : actions.get(aName);
		// if ( actions != null && actions.containsKey(aName) ) { return actions.get(aName); }

		// return parent == null ? null : parent.getAction(aName);
	}

	public Collection<String> getActionNames() {
		return actions == null ? Collections.EMPTY_LIST : actions.keySet();
		// Set<String> names = new HashSet();
		// if ( actions != null ) {
		// names.addAll(actions.keySet());
		// }
		// if ( parent != null ) {
		// names.addAll(parent.getActionNames());
		// }
		// return names;
	}

	public Collection<StatementDescription> getActions() {
		return actions == null ? Collections.EMPTY_LIST : actions.values();

		// Set<StatementDescription> allActions = new HashSet();
		// if ( actions != null ) {
		// allActions.addAll(actions.values());
		// }
		// if ( parent != null ) {
		// allActions.addAll(parent.getActions());
		// }
		// return allActions;
	}

	@Override
	public boolean hasAction(final String a) {
		return actions != null && actions.containsKey(a);
		// return actions != null && actions.containsKey(a) || parent != null && parent != this && parent.hasAction(a);
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
		if ( vd == null ) { return null; }
		return vd.getVarExpr();
	}

	public abstract Class getJavaBase();

	// {
	// if ( javaBase == null && parent != null ) {
	// javaBase = parent.getJavaBase();
	// }
	// return javaBase;
	// }

	protected void setJavaBase(final Class javaBase) {
		this.javaBase = javaBase;
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
	 * @return
	 */
	public TypeDescription getParent() {
		return parent;
	}

	public Map<String, VariableDescription> getVariables() {
		if ( variables == null ) {
			variables = new LinkedHashMap<String, VariableDescription>();
		}
		return variables;
	}

	public boolean isAbstract() {
		for ( final StatementDescription a : getActions() ) {
			if ( a.isAbstract() ) { return true; }
		}
		return false;
	}

	public IList<String> getVarNames() {
		return new GamaList(variables.keySet());
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
		final List<VariableDescription> result = new GamaList();
		final Collection<VariableDescription> vars = getVariables().values();
		for ( final VariableDescription var : vars ) {
			if ( var != null ) {
				var.usedVariablesIn(getVariables());
			}
		}
		for ( final VariableDescription var : vars ) {
			if ( var != null ) {
				var.expandDependencies(new GamaList());
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
		var.expandDependencies(new GamaList());
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

	@Override
	public void dispose() {
		if ( /* isDisposed || */isBuiltIn() ) { return; }
		if ( actions != null ) {
			actions.clear();
		}
		if ( variables != null ) {
			variables.clear();
		}
		super.dispose();
		// isDisposed = true;
	}

	protected void inheritFromParent() {
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this && !parent.isBuiltIn() ) {
			inheritActions();
			inheritVariables();
		}
	}

	protected void inheritVariables() {
		if ( parent.variables != null ) {
			// We only copy the variables that are not redefined in this species
			for ( final VariableDescription v : parent.getVariables().values() ) {
				inheritVariable(v);
			}
		}
	}

	protected void inheritActions() {
		// We only copy the actions that are not redefined in this species
		if ( parent.actions != null ) {
			for ( final StatementDescription action : parent.actions.values() ) {
				addAction(parent, action);
			}
		}
	}

	protected void inheritVariable(final VariableDescription parentVariable) {
		final String varName = parentVariable.getName();
		if ( !hasVar(varName) ) {
			addChild(parentVariable.copy(this)); // TODO Verify the copy(...)
			return;
		}
		final VariableDescription myVar = getVariable(varName);
		// If the variable already in place is builtin, we replace it
		if ( myVar.isBuiltIn() ) {
			// We inherit another builtin variable. No need to do anything
			if ( parentVariable.isBuiltIn() ) { return; }
		}

		// The variable has already been defined in the current species. Just need to check
		// if it coherent with the inherited variable
		IType myType = myVar.getType();
		IType parentType = parentVariable.getType();
		if ( !myType.isTranslatableInto(parentType) ) {
			myVar.error("Type (" + myType + ") differs from that (" + parentType + ") of the implementation of  " +
				varName + " in " + parent.getName());
		}
		if ( !myVar.isBuiltIn() ) {
			myVar.info("Redefinition of the variable " + varName + " defined in  " + parentVariable.getOriginName(),
				IGamlIssue.REDEFINES);
		}

	}

	protected void addVariable(final VariableDescription v) {
		final String vName = v.getName();
		final IDescription builtIn = getVariables().get(vName);
		if ( builtIn != null ) {
			if ( !builtIn.isBuiltIn() ) {
				duplicateInfo(v, builtIn);
				// getChildren().remove(builtIn);
			}
			final IType bType = builtIn.getType();
			final IType vType = v.getType();
			if ( !vType.isTranslatableInto(bType) ) {
				v.error("variable " + vName + " is of type " + bType + " and cannot be redefined as a " + vType,
					IGamlIssue.WRONG_REDEFINITION);
			}
			v.copyFrom((VariableDescription) builtIn);
		}
		v.setDefinitionOrder(varCount++);
		getVariables().put(vName, v);
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