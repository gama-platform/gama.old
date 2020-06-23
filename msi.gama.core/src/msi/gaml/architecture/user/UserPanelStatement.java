/*******************************************************************************************************
 *
 * msi.gaml.architecture.user.UserPanelStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.user;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.FsmStateStatement;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;

@symbol (
		name = IKeyword.USER_PANEL,
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
		concept = { IConcept.GUI })
@inside (
		symbols = { IKeyword.FSM, IKeyword.USER_FIRST, IKeyword.USER_LAST, IKeyword.USER_INIT, IKeyword.USER_ONLY },
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = FsmStateStatement.INITIAL,
				type = IType.BOOL,
				optional = true,
				doc = @doc ("Whether or not this panel will be the initial one")),
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = false,
						doc = @doc ("The name of the panel")) },
		omissible = IKeyword.NAME)
@doc (
		value = "It is the basic behavior of the user control architecture (it is similar to state for the FSM architecture). This user_panel translates, in the interface, in a semi-modal view that awaits the user to choose action buttons, change attributes of the controlled agent, etc. Each user_panel, like a state in FSM, can have a enter and exit sections, but it is only defined in terms of a set of user_commands which describe the different action buttons present in the panel.",
		usages = { @usage (
				value = "The general syntax is for example:",
				examples = { @example (
						value = "user_panel default initial: true {",
						isExecutable = false),
						@example (
								value = "	user_input 'Number' returns: number type: int <- 10;",
								isExecutable = false),
						@example (
								value = "	ask (number among list(cells)){ do die; }",
								isExecutable = false),
						@example (
								value = "	transition to: \"Advanced Control\" when: every (10);",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false),
						@example (
								value = "",
								isExecutable = false),
						@example (
								value = "user_panel \"Advanced Control\" {",
								isExecutable = false),
						@example (
								value = "	user_input \"Location\" returns: loc type: point <- {0,0};",
								isExecutable = false),
						@example (
								value = "	create cells number: 10 with: [location::loc];",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }) },
		see = { IKeyword.USER_COMMAND, IKeyword.USER_INIT, IKeyword.USER_INPUT })
public class UserPanelStatement extends FsmStateStatement {

	List<IStatement> userCommands = new ArrayList<>();

	public UserPanelStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
		for (final ISymbol c : children) {
			if (c instanceof UserCommandStatement) {
				userCommands.add((IStatement) c);
			}
		}
		super.setChildren(Iterables.filter(children, each -> !userCommands.contains(each)));
	}

	public List<IStatement> getUserCommands() {
		return userCommands;
	}

	@Override
	protected Object bodyExecution(final IScope scope) throws GamaRuntimeException {
		super.bodyExecution(scope);
		if (!userCommands.isEmpty()) {
			scope.getGui().openUserControlPanel(scope, this);
			while (scope.isOnUserHold()) {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return name;

	}

}
