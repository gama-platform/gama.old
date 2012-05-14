package msi.gaml.architecture.user;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.FsmStateCommand;
import msi.gaml.commands.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@symbol(name = IKeyword.USER_PANEL, kind = ISymbolKind.BEHAVIOR)
@inside(symbols = IKeyword.FSM, kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = FsmStateCommand.INITIAL, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false) }, omissible = IKeyword.NAME)
public class UserPanel extends FsmStateCommand {

	List<ICommand> userCommands = new ArrayList();

	public UserPanel(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( ISymbol c : commands ) {
			if ( c instanceof UserCommandCommand ) {
				userCommands.add((ICommand) c);
			}
		}
		commands.removeAll(userCommands);
		super.setChildren(commands);
	}

	public List<ICommand> getUserCommands() {
		return userCommands;
	}

	@Override
	protected Object bodyExecution(final IScope scope) throws GamaRuntimeException {
		super.bodyExecution(scope);
		if ( !userCommands.isEmpty() ) {
			GuiUtils.openUserControlPanel(scope, this);
			while (GAMA.getFrontmostSimulation().getScheduler().isUserHold()) {
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
