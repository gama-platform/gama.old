package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.util.*;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.IChildrenProvider;
import msi.gaml.skills.WorldSkill;
import msi.gaml.types.IType;

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
		final IChildrenProvider cp, final ISyntacticElement source) {
		super(keyword, superDesc, cp, source);
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
			addChild(((SymbolDescription) v).copy(this));
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
		String error = key + " " + name + " is declared twice. Only the last will be kept.";
		one.flagWarning(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
		two.flagWarning(error, IGamlIssue.DUPLICATE_DEFINITION, NAME, name);
	}

	protected void addAction(final StatementDescription ce) {
		String actionName = ce.getName();
		StatementDescription existing = getAction(actionName);
		if ( existing != null ) {
			String previous = existing.getKeyword();
			if ( previous.equals(PRIMITIVE) && ce.getKeyword().equals(ACTION) &&
				!existing.isAbstract() ) {
				ce.flagError("Action " + actionName + " replaces a primitive of the same name.",
					IGamlIssue.GENERAL);
			}
			if ( !ce.getArgNames().containsAll(existing.getArgNames()) ) {
				String error =
					"The list of arguments differ in the two implementations of " + actionName;
				existing.flagError(error, IGamlIssue.DIFFERENT_ARGUMENTS);
				ce.flagWarning(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			} else {
				if ( !existing.isAbstract() ) {
					duplicateError(ce, existing);
				}
				children.remove(existing);
			}
		}
		if ( actions == null ) {
			actions = new LinkedHashMap<String, StatementDescription>();
		}
		actions.put(actionName, ce);
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

	protected void addVariable(final VariableDescription v) {
		String vName = v.getName();

		if ( hasVar(vName) ) {
			IDescription builtIn = getVariables().get(vName);
			if ( !builtIn.isBuiltIn() ) {
				duplicateError(v, builtIn);
				getChildren().remove(builtIn);
			}
			IType bType = builtIn.getTypeNamed(builtIn.getFacets().getLabel(TYPE));
			IType vType = v.getTypeNamed(v.getFacets().getLabel(TYPE));
			if ( bType != vType ) {
				String builtInType = bType.toString();
				String varType = vType.toString();
				v.flagError("variable " + vName + " is of type " + builtInType +
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
				if ( !hasVar(v.getName()) ) {
					addChild(v.copy(this));
				}
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

	protected void inheritAction(final TypeDescription parent, final StatementDescription a) {
		String name = a.getName();
		if ( !hasAction(name) ) {
			if ( a.isAbstract() ) {
				this.flagError("Abstract action '" + name + "', inherited from " +
					parent.getName() + ", should be redefined.", IGamlIssue.MISSING_ACTION);
				return;
			}
			addChild(a);
			return;
		}
		StatementDescription existing = getAction(name);
		if ( !existing.getArgNames().containsAll(a.getArgNames()) ) {
			String error =
				"The list of arguments is different from that of action " + name + " defined in " +
					parent.getName() + " and redefined here.";
			existing.flagError(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			// a.flagWarning(error, IGamlIssue.DIFFERENT_ARGUMENTS);
			return;
		}
	}

}