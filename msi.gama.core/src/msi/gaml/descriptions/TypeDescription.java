package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.util.*;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.*;
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

	protected Map<String, StatementDescription> actions;
	private Map<String, VariableDescription> variables;
	private Class javaBase;
	protected TypeDescription parent;
	private static int varCount = 0;
	private IList<String> sortedVariableNames;
	private IList<String> updatableVariableNames;

	public TypeDescription(final String keyword, final Class clazz, final IDescription macroDesc,
		final IDescription parent, final IChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, macroDesc, cp, source, facets);
		// parent can be null
		setJavaBase(clazz);
		setParent((TypeDescription) parent);
	}

	public void copyJavaAdditions() {
		Class clazz = getJavaBase();
		if ( clazz == null ) {
			error("This species cannot be compiled as its parent is unknown. ", IGamlIssue.UNKNOWN_SUBSPECIES);
			return;
		}
		final Set<IDescription> children = AbstractGamlAdditions.getAllChildrenOf(getJavaBase(), getSkillClasses());
		for ( IDescription v : children ) {
			addChild(v.copy(this));
		}
	}

	public void setParent(TypeDescription parent) {
		this.parent = parent;
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
			// FIXME This block is never executed (to verify)
			if ( existing.getKeyword().equals(ACTION) && !primitive.isAbstract() ) {
				existing.error("Action " + actionName + " replaces a primitive of the same name defined in " +
					primitive.getOriginName() + ". Consider renaming it.", IGamlIssue.GENERAL);
			}
			DescriptionValidator.assertActionsAreCompatible(existing, primitive, primitive.getOriginName());
			children.remove(existing);
		}
		if ( actions == null ) {
			actions = new LinkedHashMap<String, StatementDescription>();
		}
		actions.put(actionName, primitive);

	}

	protected void addAction(final StatementDescription redeclaredAction) {

		// TODO VERIFIER LES PARENTS RESPECTIFS ET COMPRENDRE POURQUOI ERREUR SUR L'AJOUT D'ACTIONS ABSTRAITES
		String actionName = redeclaredAction.getName();
		StatementDescription existingAction = getAction(actionName);
		if ( existingAction != null ) {
			// Skills primitives are added first
			// if ( existingAction.getKeyword().equals(PRIMITIVE) && !existingAction.isAbstract() ) {
			// redeclaredAction.error("Action " + actionName + " replaces a primitive of the same name defined in " +
			// existingAction.getOriginName() + ". Consider renaming it.", IGamlIssue.GENERAL);
			// return;
			// }
			DescriptionValidator.assertActionsAreCompatible(redeclaredAction, existingAction,
				existingAction.getOriginName());
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

	protected void inheritAction(final TypeDescription parent, final StatementDescription parentAction) {
		String actionName = parentAction.getName();
		if ( !hasAction(actionName) ) {
			// The current species does not define such an action. If it is abstract in
			// the super species, we issue an error
			if ( parentAction.isAbstract() ) {
				this.error("Abstract action '" + actionName + "', inherited from " + parent.getName() +
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

		DescriptionValidator.assertActionsAreCompatible(myAction, parentAction, parent.getName());
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
		if ( javaBase == null && parent != null ) {
			javaBase = parent.getJavaBase();
		}
		return javaBase;
	}

	protected void setJavaBase(Class javaBase) {
		this.javaBase = javaBase;
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

		if ( sortedVariableNames.remove(ExperimentAgent.PROJECT_PATH) ) {
			sortedVariableNames.add(0, ExperimentAgent.PROJECT_PATH);
		}
		if ( sortedVariableNames.remove(ExperimentAgent.MODEL_PATH) ) {
			sortedVariableNames.add(0, ExperimentAgent.MODEL_PATH);
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

		if ( sortedVariableNames.remove(ExperimentAgent.PROJECT_PATH) ) {
			sortedVariableNames.add(0, ExperimentAgent.PROJECT_PATH);
		}
		if ( sortedVariableNames.remove(ExperimentAgent.MODEL_PATH) ) {
			sortedVariableNames.add(0, ExperimentAgent.MODEL_PATH);
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
		if ( parent != null ) {
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
				inheritAction(parent, action);
			}
		}
	}

	protected void inheritVariable(final VariableDescription parentVariable) {
		String varName = parentVariable.getName();
		// GuiUtils.debug("       **** " + getName() + " receives " + " var " + varName + " from " + parent.getName());

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
			myVar.error("Type (" + myType + ") differs from that (" + parentType + ") of the implementation of  " +
				varName + " in " + parent.getName());
		} else if ( myType.hasContents() ) {
			myType = myVar.getContentType();
			parentType = parentVariable.getContentType();
			if ( !myType.isTranslatableInto(parentType) ) {
				myVar.error("Content type (" + myType + ") differs from that (" + parentType +
					") of the implementation of  " + varName + " in " + parent.getName());
			}
		}
		if ( !myVar.isBuiltIn() ) {
			myVar.info("Redefinition of the variable " + varName + " defined in  " + parentVariable.getOriginName(),
				IGamlIssue.REDEFINES);
		}

	}

	protected void addVariable(final VariableDescription v) {
		String vName = v.getName();

		if ( hasVar(vName) ) {
			IDescription builtIn = getVariables().get(vName);
			if ( !builtIn.isBuiltIn() ) {
				duplicateError(v, builtIn);
				getChildren().remove(builtIn);
			}
			// else {
			// IExpressionDescription expr = v.getFacets().get(INIT);
			// if ( expr == null ) {
			// expr = v.getFacets().get(VALUE);
			// if ( expr == null ) {
			// expr = v.getFacets().get(NAME);
			// }
			// }
			// EObject target = expr.getTarget();
			// if ( target != null ) {
			// v.info("Redefinition, in " + v.originName + ", of the built-in variable " + vName + " defined in " +
			// builtIn.getOriginName(), IGamlIssue.REDEFINES, target, (String[]) null);
			// }
			// }
			IType bType = builtIn.getTypeNamed(builtIn.getFacets().getLabel(TYPE));
			IType vType = v.getTypeNamed(v.getFacets().getLabel(TYPE));
			if ( bType != vType ) {
				String builtInType = bType.toString();
				String varType = vType.toString();
				v.error(
					"variable " + vName + " is of type " + builtInType + " and cannot be redefined as a " + varType,
					IGamlIssue.WRONG_REDEFINITION);
			}
			v.copyFrom((VariableDescription) builtIn);
		}
		v.setDefinitionOrder(varCount++);
		if ( variables == null ) {
			variables = new LinkedHashMap<String, VariableDescription>();
		}
		variables.put(vName, v);
	}

	// public void finalizeDescription() {
	// for ( StatementDescription action : actions.values() ) {
	// if ( action.isAbstract() && !this.isBuiltIn() ) {
	// this.error("Abstract action '" + action.getName() + "', defined in " + action.getOriginName() +
	// ", should be redefined.", IGamlIssue.MISSING_ACTION);
	// }
	// }
	// }

}