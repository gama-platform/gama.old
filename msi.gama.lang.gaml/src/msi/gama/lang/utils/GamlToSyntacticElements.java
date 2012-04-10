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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.IErrorCollector;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class GamlToSyntacticElements {

	public static ISyntacticElement doConvert(final Model m, final IErrorCollector collect) {
		IExpressionFactory fact = GAMA.getExpressionFactory();
		if ( fact.getParser() == null ) {
			fact.registerParser(new NewGamlExpressionCompiler());
		}

		AbstractStatementDescription elt = convModel(m, collect);
		// elt.dump();
		return elt;
	}

	private static AbstractStatementDescription convModel(final Model m,
		final IErrorCollector collect) {
		final AbstractStatementDescription model =
			new ECoreBasedStatementDescription(IKeyword.MODEL, m, collect);
		model.setFacet(IKeyword.NAME, new LabelExpressionDescription(m.getName()));
		for ( final Import i : m.getImports() ) {
			final String uri = i.getImportURI();
			if ( !uri.startsWith("platform:") ) {
				final AbstractStatementDescription include =
					new ECoreBasedStatementDescription(IKeyword.INCLUDE, i, collect);
				include.setFacet(IKeyword.FILE, new LabelExpressionDescription(uri));
				model.addChild(include);
			}
		}
		convStatements(model, m.getStatements(), collect);
		return model;
	}

	private static void convStatements(final AbstractStatementDescription elt,
		final EList<Statement> ss, final IErrorCollector collect) {
		for ( final Statement stm : ss ) {
			ISyntacticElement conv = convStatement(stm, collect);
			if ( conv != null ) {
				Array s = EGaml.varDependenciesOf(stm);
				if ( s != null ) {
					conv.setFacet(IKeyword.DEPENDS_ON, new EcoreBasedExpressionDescription(s));
				}
				elt.addChild(conv);
			}
		}
	}

	private static AbstractStatementDescription convStatement(final Statement stm,
		final IErrorCollector collect) {
		if ( stm == null ) { return null; }
		String name = stm.getKey();
		if ( name == null ) { return null; }
		final AbstractStatementDescription elt =
			new ECoreBasedStatementDescription(name, stm, collect);
		Map<String, Expression> facets = EGaml.getFacetsOf(stm);
		// Modified by the "do" command
		if ( name.equals(IKeyword.DO) ) {
			convDo(stm, facets, collect);
		}
		for ( Map.Entry<String, Expression> entry : facets.entrySet() ) {
			String fname = entry.getKey();
			IExpressionDescription fexpr = conv(entry.getValue());
			if ( fexpr == null ) {
				GamlCompilationError error =
					new GamlCompilationError("an expression is expected for facet " + fname,
						new ECoreBasedStatementDescription(name, stm, collect));
				error.setObjectOfInterest(entry.getValue());
				continue;
			}
			elt.setFacet(fname, fexpr);
		}
		if ( name.equals(IKeyword.CONST) ) {
			elt.setKeyword(IKeyword.VAR);
			elt.setFacet(IKeyword.CONST,
				new EcoreBasedExpressionDescription(EGaml.createTerminal(true)));
		}
		String def =
			DescriptionFactory.getModelFactory().getOmissibleFacetForSymbol(elt, elt.getKeyword());

		if ( def != null && elt.getFacet(def) == null ) { // TODO verify this
			Expression expr = EGaml.getValueOfOmittedFacet(stm);
			if ( expr != null ) {
				elt.setFacet(def, conv(expr));
			}
		}
		if ( name.equals(IKeyword.IF) ) {
			convElse(stm, elt, collect);
		}
		Block block = EGaml.getBlockOf(stm);
		if ( block != null ) {
			convStatements(elt, block.getStatements(), collect);
		}
		return elt;
	}

	private static void convElse(final Statement stm, final ISyntacticElement elt,
		final IErrorCollector collect) {
		EObject elseBlock = stm.getElse();
		if ( elseBlock != null ) {
			AbstractStatementDescription elseElt =
				new ECoreBasedStatementDescription(IKeyword.ELSE, elseBlock, collect);
			if ( elseBlock instanceof Statement ) {
				elseElt.addChild(convStatement((Statement) elseBlock, collect));
			} else {
				convStatements(elseElt, ((Block) elseBlock).getStatements(), collect);
			}
			elt.addChild(elseElt);
		}
	}

	static Set<String> builtin = EGaml.getAllowedFacetsFor(IKeyword.DO);

	private static void convDo(final Statement stm, final Map<String, Expression> facets,
		final IErrorCollector collect) {
		Set<String> toRemove = new HashSet();
		Array args = EGaml.getFactory().createArray();
		for ( String facet : facets.keySet() ) {
			if ( !builtin.contains(facet) ) {
				// GuiUtils.debug("Direct argument " + facet + " found ");
				args.getExprs().add(
					EGaml.createPairExpr(EGaml.createTerminal(facet), facets.get(facet)));
				toRemove.add(facet);
			}
		}
		for ( String s : toRemove ) {
			facets.remove(s);
		}
		if ( args.getExprs().size() > 0 ) {
			// GuiUtils.debug("Arguments added to WITH facet : " + args.getExprs());
			facets.put(IKeyword.WITH, args);
		}
		// TODO error if WITH is already present

	}

	private static IExpressionDescription conv(final Expression expr) {
		return expr == null ? null : new EcoreBasedExpressionDescription(expr);
	}

}
