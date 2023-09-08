/*******************************************************************************************************
 *
 * UserInitPanelStatement.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.user;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.IDescription;

/**
 * The Class UserInitPanelStatement.
 */
@symbol(name = IKeyword.USER_INIT, kind = ISymbolKind.BEHAVIOR, with_sequence = true, concept = { IConcept.GUI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@doc(value = "Used in the user control architecture, user_init is executed only once when the agent is created. It opens a special panel (if it contains user_commands statements). It is the equivalent to the init block in the basic agent architecture.", see = {
		IKeyword.USER_COMMAND, IKeyword.USER_INIT, IKeyword.USER_INPUT })
public class UserInitPanelStatement extends UserPanelStatement {

	/**
	 * Instantiates a new user init panel statement.
	 *
	 * @param desc the desc
	 */
	public UserInitPanelStatement(final IDescription desc) {
		super(desc);
	}

}
