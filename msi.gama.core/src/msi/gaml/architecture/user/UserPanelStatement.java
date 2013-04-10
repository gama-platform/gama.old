package msi.gaml.architecture.user;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.FsmStateStatement;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.*;
import msi.gaml.types.IType;

@symbol(name = IKeyword.USER_PANEL, kind = ISymbolKind.BEHAVIOR, with_sequence = true)
@inside(symbols = IKeyword.FSM, kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = FsmStateStatement.INITIAL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false) }, omissible = IKeyword.NAME)
public class UserPanelStatement extends FsmStateStatement {

	List<IStatement> userCommands = new ArrayList();

	public UserPanelStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( ISymbol c : children ) {
			if ( c instanceof UserCommandStatement ) {
				userCommands.add((IStatement) c);
			}
		}
		children.removeAll(userCommands);
		super.setChildren(children);
	}

	public List<IStatement> getUserCommands() {
		return userCommands;
	}

	@Override
	protected Object bodyExecution(final IScope scope) throws GamaRuntimeException {
		super.bodyExecution(scope);
		if ( !userCommands.isEmpty() ) {
			GuiUtils.openUserControlPanel(scope, this);
			while (scope.getAgentScope().getScheduler().isUserHold()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return name;

	}

}
