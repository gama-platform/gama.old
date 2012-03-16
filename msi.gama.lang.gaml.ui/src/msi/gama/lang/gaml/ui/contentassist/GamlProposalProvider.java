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
package msi.gama.lang.gaml.ui.contentassist;

import java.io.File;
import java.net.URL;
import java.util.Set;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.EGaml;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.types.IType;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.*;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.ui.editor.contentassist.*;

/**
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on
 * how to customize content assistant
 */
public class GamlProposalProvider extends AbstractGamlProposalProvider {

	private static Set<String> typeList;
	private static GamlProperties allowedFacets;
	private static Image rgbImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_rgb.png").createImage();
	private static Image facetImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_facet.png").createImage();
	private static Image typeImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_type.png").createImage();

	private static String getPath(final String strURI) {
		try {
			URL url = FileLocator.resolve(new URL(strURI));
			return new File(url.getFile()).getAbsolutePath();
		} catch (Exception e) {}
		return null;
	}

	// @Override
	// protected ConfigurableCompletionProposal doCreateProposal(final String proposal,
	// final StyledString displayString, final Image image, final int priority,
	// final ContentAssistContext context) {
	//
	// ConfigurableCompletionProposal result = null;
	// if ( displayString.toString().endsWith(" ") ) {
	// result = super.doCreateProposal(proposal, displayString, image, priority, context);
	// }
	// return result;
	// }

	public void completeFacetExpr_Expr(final FacetExpr f, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

		final String keyName = EGaml.getKeyOf(f);
		if ( keyName == null ) { return; }
		final String defKeyName = EGaml.getKeyOf(f.eContainer());
		if ( defKeyName == null ) { return; }
		final String valueExpr = EGaml.getLabelFromFacet((Statement) f.eContainer(), IKeyword.TYPE);

		if ( keyName.equals(IKeyword.VALUE) || keyName.equals(IKeyword.INIT) ) {
			if ( valueExpr != null && valueExpr.equals(IType.COLOR_STR) ||
				defKeyName.equals(IType.COLOR_STR) ) {

				ConfigurableCompletionProposal editColor =
					(ConfigurableCompletionProposal) createCompletionProposal("Edit color...",
						" Edit color... ", rgbImage, context);
				if ( editColor != null ) {
					editColor.setTextApplier(new ReplacementTextApplier() {

						@Override
						public String getActualReplacementString(
							final ConfigurableCompletionProposal proposal) {
							Display display = context.getViewer().getTextWidget().getDisplay();
							ColorDialog colorDialog = new ColorDialog(display.getActiveShell());
							RGB newColor = colorDialog.open();
							if ( newColor != null ) { return " rgb[" + newColor.red + "," +
								newColor.green + "," + newColor.blue + "] "; }
							return "";
						}
					});

					acceptor.accept(createCompletionProposal("'white'", " white ", rgbImage,
						context));
					acceptor.accept(createCompletionProposal("'black'", " black ", rgbImage,
						context));
					acceptor.accept(createCompletionProposal("'red'", " red ", rgbImage, context));
					acceptor.accept(createCompletionProposal("'yellow'", " yellow ", rgbImage,
						context));
					acceptor.accept(createCompletionProposal("'green'", " green ", rgbImage,
						context));
					acceptor
						.accept(createCompletionProposal("'blue'", " blue ", rgbImage, context));
					acceptor.accept(editColor);
				}
			}
		} else if ( keyName.equals("type") ) {
			for ( String st : getTypelist() ) {
				acceptor.accept(createCompletionProposal(st, " " + st + " ", typeImage, context));
			}

		} else if ( keyName.equals("torus") ) {
			acceptor.accept(createCompletionProposal("true", " true ", null, context));
			acceptor.accept(createCompletionProposal("false", " false ", null, context));
		}

		// FIXME Extend this with a systematic look at the type of facets as defined in the
		// annotations (available in the meta-definition of symbols)

	}

	public void completeStatementFacets(final Statement s, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		// FIXME Describe a method to enable providing only the right facets
		// Based on the debignning of the word (but how to have it ?) and the facets available in
		// this statement.
	}

	public void completeDefinition_Facets(final Statement d, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		// !!!! FIXME CANNOT WORK LIKE THIS.
		if ( EGaml.getKeyOf(d) != null ) {
			Set<String> facets = EGaml.getAllowedFacetsFor(d);
			for ( String st : facets ) {
				acceptor.accept(createCompletionProposal(st, " " + st + " ", facetImage, context));
			}
		}
	}

	// public void completeEvaluation_Facets(final Evaluation e, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// Set<String> facets = EGaml.getAllowedFacetsFor(e);
	// for ( String st : facets ) {
	// acceptor.accept(createCompletionProposal(st, " " + st + " ", facetImage, context));
	// }
	// }

	// public void completeSetEval_Facets(final SetEval s, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// for ( String st : EGaml.getAllowedFacetsFor(s) ) {
	// acceptor.accept(createCompletionProposal(st, " " + st + " ", facetImage, context));
	// }
	// }

	private static Set<String> getTypelist() {
		if ( typeList == null ) {
			typeList = GamlProperties.loadFrom(GamlProperties.TYPES).values();
		}
		return typeList;
	}

	@Override
	public void completeModel_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeModel_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeModel_Imports(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeModel_Imports(model, assignment, context, acceptor);
	}

	@Override
	public void completeModel_Gaml(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeModel_Gaml(model, assignment, context, acceptor);
	}

	@Override
	public void completeModel_Statements(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeModel_Statements(model, assignment, context, acceptor);
	}

	@Override
	public void completeImport_ImportURI(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeImport_ImportURI(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlLangDef_B(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlLangDef_B(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlLangDef_R(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlLangDef_R(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlLangDef_Unaries(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlLangDef_Unaries(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefBinaryOp_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefBinaryOp_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefReserved_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefReserved_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefUnary_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefUnary_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeClassicStatement_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeClassicStatement_Key(model, assignment, context, acceptor);
	}

	@Override
	public void completeClassicStatement_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeClassicStatement_Ref(model, assignment, context, acceptor);
	}

	@Override
	public void completeClassicStatement_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeClassicStatement_Expr(model, assignment, context, acceptor);
	}

	@Override
	public void completeClassicStatement_Facets(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeClassicStatement_Facets(model, assignment, context, acceptor);
	}

	@Override
	public void completeClassicStatement_Block(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeClassicStatement_Block(model, assignment, context, acceptor);
	}

	@Override
	public void completeIfEval_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeIfEval_Key(model, assignment, context, acceptor);
	}

	@Override
	public void completeIfEval_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeIfEval_Ref(model, assignment, context, acceptor);
	}

	@Override
	public void completeIfEval_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeIfEval_Expr(model, assignment, context, acceptor);
	}

	@Override
	public void completeIfEval_Block(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeIfEval_Block(model, assignment, context, acceptor);
	}

	@Override
	public void completeIfEval_Else(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeIfEval_Else(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefinition_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefinition_Key(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefinition_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefinition_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefinition_Facets(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefinition_Facets(model, assignment, context, acceptor);
	}

	@Override
	public void completeDefinition_Block(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeDefinition_Block(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlFacetRef_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		try {
			super.completeGamlFacetRef_Ref(model, assignment, context, acceptor);
		} catch (ClassCastException e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void completeFunctionGamlFacetRef_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeFunctionGamlFacetRef_Ref(model, assignment, context, acceptor);
	}

	@Override
	public void completeFacetExpr_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeFacetExpr_Key(model, assignment, context, acceptor);
	}

	@Override
	public void completeFacetExpr_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeFacetExpr_Expr(model, assignment, context, acceptor);
	}

	@Override
	public void completeNameFacetExpr_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeNameFacetExpr_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeReturnsFacetExpr_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeReturnsFacetExpr_Name(model, assignment, context, acceptor);
	}

	@Override
	public void completeFunctionFacetExpr_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeFunctionFacetExpr_Key(model, assignment, context, acceptor);
	}

	@Override
	public void completeFunctionFacetExpr_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeFunctionFacetExpr_Expr(model, assignment, context, acceptor);
	}

	@Override
	public void completeBlock_Statements(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeBlock_Statements(model, assignment, context, acceptor);
	}

	@Override
	public void completeTernExp_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeTernExp_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeTernExp_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeTernExp_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeTernExp_IfFalse(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeTernExp_IfFalse(model, assignment, context, acceptor);
	}

	@Override
	public void completeOrExp_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeOrExp_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeOrExp_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeOrExp_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeAndExp_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeAndExp_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeAndExp_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeAndExp_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeRelational_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeRelational_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeRelational_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeRelational_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completePairExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completePairExpr_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completePairExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completePairExpr_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeAddition_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeAddition_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeAddition_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeAddition_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeMultiplication_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeMultiplication_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeMultiplication_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeMultiplication_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlBinaryExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlBinaryExpr_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlBinaryExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlBinaryExpr_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlUnitExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlUnitExpr_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlUnitExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlUnitExpr_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlUnaryExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlUnaryExpr_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeGamlUnaryExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeGamlUnaryExpr_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeMemberRef_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeMemberRef_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completeMemberRef_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeMemberRef_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completePrimaryExpression_Exprs(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completePrimaryExpression_Exprs(model, assignment, context, acceptor);
	}

	@Override
	public void completePrimaryExpression_Left(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completePrimaryExpression_Left(model, assignment, context, acceptor);
	}

	@Override
	public void completePrimaryExpression_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completePrimaryExpression_Op(model, assignment, context, acceptor);
	}

	@Override
	public void completePrimaryExpression_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completePrimaryExpression_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeAbstractRef_Args(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeAbstractRef_Args(model, assignment, context, acceptor);
	}

	@Override
	public void completeVariableRef_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeVariableRef_Ref(model, assignment, context, acceptor);
	}

	@Override
	public void completeTerminalExpression_Value(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.completeTerminalExpression_Value(model, assignment, context, acceptor);
	}

	@Override
	public void complete_Model(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Model(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Import(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Import(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_GamlLangDef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_GamlLangDef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_DefBinaryOp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_DefBinaryOp(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_DefReserved(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_DefReserved(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_DefUnary(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_DefUnary(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_BuiltIn(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_BuiltIn(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Statement(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Statement(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_ClassicStatement(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_ClassicStatement(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_IfEval(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_IfEval(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Definition(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Definition(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_FacetRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_FacetRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_GamlFacetRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_GamlFacetRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_FunctionGamlFacetRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_FunctionGamlFacetRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_FacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_FacetExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_DefinitionFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_DefinitionFacetExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_NameFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_NameFacetExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_ReturnsFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_ReturnsFacetExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_FunctionFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_FunctionFacetExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Block(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Block(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Expression(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Expression(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_TernExp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_TernExp(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_OrExp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_OrExp(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_AndExp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_AndExp(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Relational(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Relational(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_PairExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_PairExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Addition(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Addition(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_Multiplication(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_Multiplication(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_GamlBinaryExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_GamlBinaryExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_GamlUnitExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_GamlUnitExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_GamlUnaryExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_GamlUnaryExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_PrePrimaryExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_PrePrimaryExpr(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_MemberRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_MemberRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_PrimaryExpression(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_PrimaryExpression(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_AbstractRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_AbstractRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_VariableRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_VariableRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_GamlVarRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_GamlVarRef(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_TerminalExpression(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_TerminalExpression(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_INTEGER(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_INTEGER(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_BOOLEAN(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_BOOLEAN(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_ID(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_ID(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_COLOR(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_COLOR(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_DOUBLE(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_DOUBLE(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_STRING(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_STRING(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_ML_COMMENT(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_ML_COMMENT(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_SL_COMMENT(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_SL_COMMENT(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_WS(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_WS(model, ruleCall, context, acceptor);
	}

	@Override
	public void complete_ANY_OTHER(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		super.complete_ANY_OTHER(model, ruleCall, context, acceptor);
	}

	@Override
	public void completeKeyword(final Keyword keyword,
		final ContentAssistContext contentAssistContext, final ICompletionProposalAcceptor acceptor) {
		super.completeKeyword(keyword, contentAssistContext, acceptor);
	}

	@Override
	public void setValueConverter(final IValueConverterService valueConverter) {
		super.setValueConverter(valueConverter);
	}

	@Override
	public IValueConverterService getValueConverter() {
		return super.getValueConverter();
	}

	@Override
	protected Image getImage(final EObject eObject) {
		return super.getImage(eObject);
	}
}
