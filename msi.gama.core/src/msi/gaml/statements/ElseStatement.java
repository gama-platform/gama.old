/*******************************************************************************************************
 *
 * ElseStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.IDescription;

/**
 * Written by drogoul Modified on 8 févr. 2010
 *
 * @todo Description
 *
 */
@symbol(name = IKeyword.ELSE, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, concept = { IConcept.CONDITION })
@inside(symbols = IKeyword.IF)
@doc(value="This statement cannot be used alone",see={IKeyword.IF})
public class ElseStatement extends AbstractStatementSequence {

	/**
	 * Instantiates a new else statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public ElseStatement(final IDescription desc) {
		super(desc);
		setName(IKeyword.ELSE);
	}

}
