/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.agents;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.interfaces.ICommand.WithArgs;
import msi.gama.internal.compilation.*;
import msi.gama.internal.descriptions.ExecutionContextDescription;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.util.GamaList;
import msi.gaml.commands.*;
import msi.gaml.control.IControl;

public abstract class ExecutionContext extends Symbol implements IExecutionContext {

	private IScope ownStack;
	private Map<String, IVariable> variables;
	private Map<String, AspectCommand> aspects;
	private Map<String, ActionCommand> actions;
	private List<ICommand> behaviors;

	protected ISpecies macroSpecies;

	public ExecutionContext(final IDescription description) {
		super(description);

		setOwnScope(GAMA.obtainNewScope());
	}

	@Override
	protected void initFields() {
		super.initFields();

		variables = new HashMap<String, IVariable>();
		actions = new HashMap<String, ActionCommand>();
		aspects = new HashMap<String, AspectCommand>();
		behaviors = new GamaList<ICommand>();
	}

	@Override
	public ExecutionContextDescription getDescription() {
		return (ExecutionContextDescription) description;
	}

	@Override
	public IControl getControl() {
		return getDescription().getControl();
	}

	@Override
	public void addVariable(final IVariable v) {
		variables.put(v.getName(), v);
	}

	@Override
	public IVariable getVar(final String n) {
		return variables.get(n);
	}

	@Override
	public boolean hasVar(final String name) {
		return variables.containsKey(name);
	}

	@Override
	public List<String> getVarNames() {
		return getDescription().getVarNames();
	}

	@Override
	public Collection<IVariable> getVars() {
		return variables.values();
	}

	@Override
	public void addAction(final ActionCommand ce) {
		actions.put(ce.getName(), ce);
	}

	@Override
	public WithArgs getAction(final String name) {
		return actions.get(name);
	}

	@Override
	public void addAspect(final AspectCommand ce) {
		aspects.put(ce.getName(), ce);
	}

	@Override
	public boolean hasAspect(final String n) {
		return aspects.containsKey(n);
	}

	@Override
	public IAspect getAspect(final String n) {
		return aspects.get(n);
	}

	@Override
	public List<String> getAspectNames() {
		return new GamaList<String>(aspects.keySet());
	}

	@Override
	public void addBehavior(final ICommand c) {
		behaviors.add(c);
	}

	@Override
	public List<ICommand> getBehaviors() {
		return behaviors;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {
		for ( ISymbol s : commands ) {
			addChild(s);
		}
		createControl();
	}

	@Override
	public void addChild(final ISymbol s) {
		if ( s instanceof IVariable ) {
			addVariable((IVariable) s);
		} else if ( s instanceof AspectCommand ) {
			addAspect((AspectCommand) s);
		} else if ( s instanceof ActionCommand ) {
			addAction((ActionCommand) s);
		} else if ( s instanceof ICommand ) {
			addBehavior((ICommand) s); // reflexes, states or tasks
		}
	}

	private void createControl() {
		IControl control = getControl();
		List<ICommand> behaviors = getBehaviors();
		try {
			control.setChildren(behaviors);
			control.verifyBehaviors(this);
		} catch (GamlException e) {
			e.printStackTrace();
			control = null;
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		for ( IVariable v : variables.values() ) {
			v.dispose();
		}
		variables.clear();
		variables = null;

		for ( AspectCommand ac : aspects.values() ) {
			ac.dispose();
		}
		aspects.clear();
		aspects = null;

		for ( ActionCommand ac : actions.values() ) {
			ac.dispose();
		}
		actions.clear();

		for ( ICommand c : behaviors ) {
			c.dispose();
		}
		behaviors.clear();
		behaviors = null;

		macroSpecies = null;
	}

	protected void setOwnScope(final IScope ownStack) {
		this.ownStack = ownStack;
	}

	public IScope getOwnScope() {
		return ownStack;
	}

	@Override
	public IType getAgentType() {
		return ((ExecutionContextDescription) description).getType();
	}

	@Override
	public IAgentConstructor getAgentConstructor() {
		return ((ExecutionContextDescription) description).getAgentConstructor();
	}

	@Override
	public ISpecies getMacroSpecies() {
		return macroSpecies;
	}

	@Override
	public void setMacroSpecies(final ISpecies macroSpecies) {
		this.macroSpecies = macroSpecies;
	}

}
