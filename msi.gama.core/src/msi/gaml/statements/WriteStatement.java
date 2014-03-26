/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;

import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@symbol(name = IKeyword.WRITE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(value = { @facet(name = IKeyword.MESSAGE, type = IType.NONE, optional = false, 
		doc =@doc("the message to display. Modelers can add some formatting characters to the message (carriage returns, tabs, or Unicode characters), which will be used accordingly in the console."))
		}, omissible = IKeyword.MESSAGE)
@doc(value = "The statement makes the agent output an arbitrary message in the console.", 
usages = {@usage(examples = {@example("write 'This is a message from ' + self;")})})


public class WriteStatement extends AbstractStatement {

	@Override
	public String getTrace(final IScope scope) {
		// We dont trace write statements
		return "";
	}

	final IExpression message;

	public WriteStatement(final IDescription desc) {
		super(desc);
		message = getFacet(IKeyword.MESSAGE);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		IAgent agent = stack.getAgentScope();
		String mes = null;
		if ( agent != null && !agent.dead() ) {
			mes = Cast.asString(stack, message.value(stack));
			GuiUtils.informConsole(mes);
		}
		return mes;
	}

	@operator(value = "sample", doc = @doc("Returns a string containing the GAML code of the expression passed in parameter, followed by the result of its evaluation"), category={IOperatorCategory.STRING})
	public static String sample(final IScope scope, final IExpression expr) {
		return sample(scope, expr == null ? "nil" : expr.toGaml(), expr);
	}

	@operator(value = "sample", doc = @doc("Returns a string containing the string passed in parameter, followed by the result of the evaluation of the expression"), category={IOperatorCategory.STRING})
	public static String sample(final IScope scope, final String text, final IExpression expr) {
		return text == null ? "" : text.trim() + " -: " + (expr == null ? "nil" : Cast.toGaml(expr.value(scope)));
	}

}
