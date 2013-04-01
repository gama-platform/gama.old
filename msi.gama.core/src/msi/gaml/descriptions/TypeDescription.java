package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.util.*;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.IChildrenProvider;
import msi.gaml.skills.WorldSkill;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import org.eclipse.emf.ecore.EObject;

/**
 * A class that represents skills and species (either built-in or introduced by users)
 * The class TypeDescription.
 * 
 * @author drogoul
 * @since 23 févr. 2013
 * 
 */
public class TypeDescription extends SymbolDescription {

	private Map<String, StatementDescription> actions;
	private Map<String, VariableDescription> variables;
	protected Class javaBase;
	protected TypeDescription parent;
	private int varCount = 0;
	private IList<String> sortedVariableNames;
	private IList<String> updatableVariableNames;

	public TypeDescription(final String keyword, final Class clazz, final IDescription superDesc,
		final IChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, source, facets);
		initJavaBase(clazz);
	}

	public void initJavaBase(final Class clazz) {
		if ( javaBase != null ) { return; }
		javaBase = clazz == null ? getDefaultJavaBase() : clazz;
	}

	public void copyJavaAdditions() {
		final List<IDescription> children =
			AbstractGamlAdditions.getAllChildrenOf(javaBase, getSkillClasses());
		for ( IDescription v : children ) {
			addChild(v.copy(this));
		}

	}

	public Class getDefaultJavaBase() {
		return AbstractGamlAdditions.DEFAULT_AGENT_CLASS;
	}

	public Set<Class> getSkillClasses() {
		return Collections.EMPTY_SET;
	}

	protected void duplicateError(final IDescription one, final IDescription two) {
		String name = one.getFacets().getLabel(NAME);
		String key = one.getKeyword();
		String error = key + " " + name + " is declared twice. Only this one will be kept.";
		one.info(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
		// two.info(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
	}

	protected void addPrimitive(final StatementDescription primitive) {
		String actionName = primitive.getName();
		StatementDescription existing = getAction(actionName);
		if ( existing != null ) {
			if ( existing.getKeyword().equals(ACTION) && !primitive.isAbstract() ) {
				existing.error(
					"Action " + actionName + " replaces a primitive of the same name defined in " +
						primitive.getOriginName() + ". Consider renaming it.", IGamlIssue.GENERAL);
			}
			children.remove(existing);
		}
		if ( actions == null ) {
			actions = new LinkedHashMap<String, StatementDescription>();
		}
		actions.put(actionName, primitive);
	}

	protected void addAction(final StatementDescription redeclaredAction) {
		String actionName = redeclaredAction.getName();
		StatementDescription existingAction = getAction(actionName);
		if ( existingAction != null ) {
			// Skills primitives are added first
			if ( existingAction.getKeyword().equals(PRIMITIVE) && !existingAction.isAbstract() ) {
				redeclaredAction.error(
					"Action " + actionName + " replaces a primitive of the same name defined in " +
						existingAction.getOriginName() + ". Consider renaming it.",
					IGamlIssue.GENERAL);
				return;
			}
			if ( !existingAction.isAbstract() ) {
				duplicateError(redeclaredAction, existingAction);
			}
			children.remove(existingAction);
		}
		if ( actions == null ) {
			actions = new LinkedHashMap<String, StatementDescription>();
		}
		actions.put(actionName, redeclaredAction);
	}

	@Override
	public StatementDescription getAction(final String aName) {
		return actions == null ? null : actions.get(aName);
	}

	public Collection<String> getActionNames() {
		return actions == null ? Collections.EMPTY_LIST : actions.keySet();
	}

	@Override
	public boolean hasAction(final String a) {
		return actions != null && actions.containsKey(a);
	}

	public VariableDescription getVariable(final String name) {
		return variables == null ? null : getVariables().get(name);
	}

	@Override
	public boolean hasVar(final String a) {
		return variables != null && getVariables().containsKey(a);
	}

	@Override
	public IExpression getVarExpr(final String n) {
		VariableDescription vd = getVariable(n);
		if ( vd == null ) { return null; }
		return vd.getVarExpr();
	}

	public Class getJavaBase() {
		return javaBase;
	}

	@Override
	public IType getType() {
		return getTypeNamed(getName());
	}

	public boolean isArgOf(final String op, final String arg) {
		StatementDescription action = getAction(op);
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
		if ( actions == null ) { return false; }
		for ( StatementDescription a : actions.values() ) {
			if ( a.isAbstract() ) { return true; }
		}
		return false;
	}

	public IList<String> getVarNames() {
		return sortedVariableNames == null ? GamaList.EMPTY_LIST : sortedVariableNames;
	}

	public List<String> getUpdatableVarNames() {
		return updatableVariableNames == null ? GamaList.EMPTY_LIST : updatableVariableNames;
	}

	protected void sortVars() {
		if ( variables == null ) { return; }
		// GuiUtils.debug("***** Sorting variables of " + getNameFacetValue());
		final List<VariableDescription> result = new GamaList();
		final Collection<VariableDescription> vars = getVariables().values();
		for ( final VariableDescription var : vars ) {
			var.usedVariablesIn(getVariables());
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
		if ( sortedVariableNames == null ) {
			sortedVariableNames = new GamaList();
		} else {
			sortedVariableNames.clear();
		}
		for ( int i = 0; i < result.size(); i++ ) {
			VariableDescription v = result.get(i);
			String s = v.getName();
			sortedVariableNames.add(s);
			if ( v.isUpdatable() ) {
				if ( updatableVariableNames == null ) {
					updatableVariableNames = new GamaList();
				}
				updatableVariableNames.add(s);
			}
		}
		// FIXME March 2013: Hack to have the paths always at the beginning
		// TO BE REMOVED LATER BY SPECIFYING A PRIORITY ON VARIABLES -- OR TO MOVE THESE VARIABLES
		// TO THE EXPERIMENTATOR

		if ( sortedVariableNames.remove(WorldSkill.PROJECT_PATH) ) {
			sortedVariableNames.add(0, WorldSkill.PROJECT_PATH);
		}
		if ( sortedVariableNames.remove(WorldSkill.MODEL_PATH) ) {
			sortedVariableNames.add(0, WorldSkill.MODEL_PATH);
		}
	}

	public void resortVarName(final VariableDescription var) {
		var.usedVariablesIn(getVariables());
		var.expandDependencies(new GamaList());
		sortedVariableNames.remove(var.getName());
		int index = 0;
		for ( int j = 0, n = sortedVariableNames.size(); j < n; j++ ) {
			VariableDescription vd = getVariable(sortedVariableNames.get(j));
			if ( var.getDependencies().contains(vd) ) {
				index = j;
			};
		}
		if ( index == sortedVariableNames.size() ) {
			sortedVariableNames.add(var.getName());
		} else {
			sortedVariableNames.add(index + 1, var.getName());
		}
		// FIXME March 2013: Hack to have the paths always at the beginning
		// TO BE REMOVED LATER BY SPECIFYING A PRIORITY ON VARIABLES -- OR TO MOVE THESE VARIABLES
		// TO THE EXPERIMENTATOR

		if ( sortedVariableNames.remove(WorldSkill.PROJECT_PATH) ) {
			sortedVariableNames.add(0, WorldSkill.PROJECT_PATH);
		}
		if ( sortedVariableNames.remove(WorldSkill.MODEL_PATH) ) {
			sortedVariableNames.add(0, WorldSkill.MODEL_PATH);
		}
	}

	@Override
	public void dispose() {
		if ( actions != null ) {
			actions.clear();
		}
		if ( variables != null ) {
			variables.clear();
		}
		parent = null;
		super.dispose();
	}

	protected void inheritFromParent() {
		if ( parent != null ) {
			inheritActions(parent);
			inheritVariables(parent);
		}
	}

	protected void inheritVariables(final TypeDescription parent) {
		if ( parent.variables != null ) {
			// We only copy the variables that are not redefined in this species
			for ( final VariableDescription v : parent.getVariables().values() ) {
				inheritVariable(parent, v);
			}
		}
	}

	protected void inheritActions(final TypeDescription parent) {
		// We only copy the actions that are not redefined in this species
		if ( parent.actions != null ) {
			for ( final StatementDescription action : parent.actions.values() ) {
				inheritAction(parent, action);
			}
		}
	}

	protected void inheritVariable(TypeDescription parent, final VariableDescription parentVariable) {
		String varName = parentVariable.getName();
		if ( !hasVar(varName) ) {
			addChild(parentVariable);
			return;
		}
		VariableDescription myVar = getVariable(varName);
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
			myVar.error("Type (" + myType + ") differs from that (" + parentType +
				") of the implementation of  " + varName + " in " + parent.getName());
		} else if ( myType.hasContents() ) {
			myType = myVar.getContentType();
			parentType = parentVariable.getContentType();
			if ( !myType.isTranslatableInto(parentType) ) {
				myVar.error("Content type (" + myType + ") differs from that (" + parentType +
					") of the implementation of  " + varName + " in " + parent.getName());
			}
		}
		if ( !myVar.isBuiltIn() ) {
			myVar.info("Redefinition, in " + myVar.getOriginName() + ", of the variable " +
				varName + " defined in species " + parent.getName(), IGamlIssue.REDEFINES);
		}

	}

	protected void addVariable(final VariableDescription v) {
		String vName = v.getName();

		if ( hasVar(vName) ) {
			IDescription builtIn = getVariables().get(vName);
			if ( !builtIn.isBuiltIn() ) {
				duplicateError(v, builtIn);
				getChildren().remove(builtIn);
			} else {
				IExpressionDescription expr = v.getFacets().get(INIT);
				if ( expr == null ) {
					expr = v.getFacets().get(VALUE);
					if ( expr == null ) {
						expr = v.getFacets().get(NAME);
					}
				}
				EObject target = expr.getTarget();
				if ( target != null ) {
					v.info("Redefinition, in " + v.originName + ", of the built-in variable " +
						vName + " defined in " + builtIn.getOriginName(), IGamlIssue.REDEFINES,
						target, (String[]) null);
				}
			}
			IType bType = builtIn.getTypeNamed(builtIn.getFacets().getLabel(TYPE));
			IType vType = v.getTypeNamed(v.getFacets().getLabel(TYPE));
			if ( bType != vType ) {
				String builtInType = bType.toString();
				String varType = vType.toString();
				v.error("variable " + vName + " is of type " + builtInType +
					" and cannot be redefined as a " + varType, IGamlIssue.WRONG_REDEFINITION);
			}
			v.copyFrom((VariableDescription) builtIn);
		}
		v.setDefinitionOrder(varCount++);
		if ( variables == null ) {
			variables = new LinkedHashMap<String, VariableDescription>();
		}
		variables.put(vName, v);
	}

	protected void inheritAction(final TypeDescription parent,
		final StatementDescription parentAction) {
		String actionName = parentAction.getName();
		if ( !hasAction(actionName) ) {
			// The current species does not define such an action. If it is abstract in
			// the super species, we issue an error
			if ( parentAction.isAbstract() ) {
				this.error(
					"Abstract action '" + actionName + "', inherited from " + parent.getName() +
						", should be redefined.", IGamlIssue.MISSING_ACTION);
			} else {
				// Otherwise we add it.
				addChild(parentAction);
			}
			return;
		}

		// The action has already been defined in the current species. Just need to check
		// if it coherent with the inherited action
		StatementDescription myAction = getAction(actionName);

		IType myType = myAction.getType();
		IType parentType = parentAction.getType();
		if ( parentType != myType ) {
			myAction.error("Return type (" + myType + ") differs from that (" + parentType +
				") of the implementation of  " + actionName + " in " + parent.getName());
		} else if ( myType.hasContents() ) {
			myType = myAction.getContentType();
			parentType = parentAction.getContentType();
			if ( parentType != myType ) {
				myAction.error("Content type (" + myType + ") differs from that (" + parentType +
					") of the implementation of  " + actionName + " in " + parent.getName());
			}
		}
		if ( !parentAction.getArgNames().containsAll(myAction.getArgNames()) ) {
			String error =
				"The list of arguments (" + myAction.getArgNames() +
					") differs from that of the implementation of " + actionName + " in " +
					parent.getName() + " (" + parentAction.getArgNames() + ")";
			myAction.error(error, IGamlIssue.DIFFERENT_ARGUMENTS);
		}

	}

}