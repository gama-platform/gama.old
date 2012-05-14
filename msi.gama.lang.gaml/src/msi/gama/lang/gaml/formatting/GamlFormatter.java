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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.formatting;

import org.eclipse.xtext.formatting.impl.*;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class GamlFormatter extends AbstractDeclarativeFormatter {

	@Override
	protected void configureFormatting(final FormattingConfig c) {
		msi.gama.lang.gaml.services.GamlGrammarAccess f =
			(msi.gama.lang.gaml.services.GamlGrammarAccess) getGrammarAccess();

		c.setLinewrap(0, 1, 2).before(f.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(f.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(f.getML_COMMENTRule());

		//
		c.setLinewrap(0, 1, 1).after(f.getModelAccess().getNameAssignment_1());
		c.setLinewrap(0, 1, 1).after(f.getImportRule());
		c.setLinewrap(0, 1, 1).after(f.getStatementRule());
		c.setIndentation(f.getBlockAccess().getLeftCurlyBracketKeyword_1(), f.getBlockAccess()
			.getRightCurlyBracketKeyword_3());
		c.setLinewrap(0, 1, 1).after(f.getBlockAccess().getLeftCurlyBracketKeyword_1());
		c.setLinewrap(0, 1, 1).before(f.getBlockAccess().getRightCurlyBracketKeyword_3());
		c.setLinewrap(0, 1, 1).after(f.getBlockAccess().getRightCurlyBracketKeyword_3());
		// c.setNoSpace().before(f.getDefinitionAccess().getSemicolonKeyword_3_1());
		// c.setNoSpace().before(f.getEvaluationAccess().getSemicolonKeyword_2_1());
		// c.setNoSpace().before(f.getGamlFacetRefAccess().getColonKeyword_1());
		// c.setLinewrap(0, 1, 1).after(f.getDefinitionAccess().getSemicolonKeyword_3_1());
		// c.setLinewrap(0, 1, 1).after(f.getEvaluationAccess().getSemicolonKeyword_2_1());
		// FIXME Ajouter dans EGaml des éléments d'accès aux repères partagés (semiColon, etc.)

	}
}
