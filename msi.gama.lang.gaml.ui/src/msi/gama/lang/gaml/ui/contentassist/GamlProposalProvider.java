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

import java.util.Set;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.GAMA;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.*;
import org.eclipse.xtext.ui.editor.contentassist.*;
import org.eclipse.xtext.util.Strings;

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
	private static Image varImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
		"/icons/_var.png").createImage();

	@Override
	public void completeMemberRef_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		completeMemberRef_Right(model, assignment, context, acceptor);
	}

	@Override
	public void completeModel_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeModel_Imports(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeModel_Gaml(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeModel_Statements(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeImport_ImportURI(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeGamlLangDef_B(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeGamlLangDef_R(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeGamlLangDef_Unaries(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeDefBinaryOp_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}

	@Override
	public void completeDefReserved_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeDefUnary_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeClassicStatement_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeClassicStatement_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeClassicStatement_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeClassicStatement_Facets(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeClassicStatement_Block(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeIfEval_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeIfEval_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeIfEval_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeIfEval_Block(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeIfEval_Else(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeDefinition_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeDefinition_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeDefinition_Facets(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeDefinition_Block(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlFacetRef_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFunctionGamlFacetRef_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFacetExpr_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFacetExpr_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeNameFacetExpr_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeReturnsFacetExpr_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeActionFacetExpr_Name(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFunctionFacetExpr_Key(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFunctionFacetExpr_Expr(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeBlock_Statements(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeTernExp_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeTernExp_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeTernExp_IfFalse(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeOrExp_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeOrExp_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeAndExp_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeAndExp_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeRelational_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeRelational_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeArgPairExpr_Arg(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeArgPairExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeArgPairExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completePairExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completePairExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeAddition_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeAddition_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeMultiplication_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeMultiplication_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlBinaryExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlBinaryExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlUnitExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlUnitExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlUnaryExpr_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeGamlUnaryExpr_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completePrimaryExpression_Exprs(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completePrimaryExpression_Left(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completePrimaryExpression_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completePrimaryExpression_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFunction_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeFunction_Args(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeUnitName_Op(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeVariableRef_Ref(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeTerminalExpression_Value(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Model(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Import(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_GamlLangDef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_DefBinaryOp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_DefReserved(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_DefUnary(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_BuiltIn(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Statement(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ClassicStatement(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_IfEval(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Definition(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_FacetRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_GamlFacetRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_FunctionGamlFacetRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_FacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_DefinitionFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_NameFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ReturnsFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ActionFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_FunctionFacetExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Block(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Expression(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_TernExp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_OrExp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_AndExp(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Relational(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ArgPairExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_PairExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Addition(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Multiplication(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_GamlBinaryExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_GamlUnitExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_GamlUnaryExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_PrePrimaryExpr(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_MemberRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_PrimaryExpression(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_AbstractRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_Function(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_UnitName(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_VariableRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_GamlVarRef(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_TerminalExpression(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_INTEGER(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_BOOLEAN(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ID(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_COLOR(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_DOUBLE(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_STRING(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ML_COMMENT(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_SL_COMMENT(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_WS(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void complete_ANY_OTHER(final EObject model, final RuleCall ruleCall,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {

	}

	@Override
	public void completeAssignment(final Assignment assignment,
		final ContentAssistContext contentAssistContext, final ICompletionProposalAcceptor acceptor) {
		ParserRule parserRule = GrammarUtil.containingParserRule(assignment);
		String methodName =
			"complete" + Strings.toFirstUpper(parserRule.getName()) + "_" +
				Strings.toFirstUpper(assignment.getFeature());
		// GuiUtils.debug("Invoking method " + methodName);
		invokeMethod(methodName, acceptor, contentAssistContext.getCurrentModel(), assignment,
			contentAssistContext);

	}

	@Override
	public void completeMemberRef_Right(final EObject model, final Assignment assignment,
		final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		if ( !(model instanceof MemberRef) ) { return; }
		Expression left = ((MemberRef) model).getLeft();
		EObject obj = model;
		IDescription desc = null;
		// Find the upper description
		while (desc == null && obj != null) {
			obj = obj.eContainer();
			desc = EGaml.getGamlDescription(obj, IDescription.class);
		}
		// if ( desc == null ) {
		// GuiUtils.debug("No upper description found");
		// }
		if ( desc != null ) {
			// GuiUtils.debug("Compiling " + EGaml.toString(left) +
			// " to get the type of the left operand");
			IExpressionDescription ed = new EcoreBasedExpressionDescription(left);
			IExpression expression = GAMA.getExpressionFactory().createExpr(ed, desc);
			if ( expression != null ) {
				IType type = expression.type();
				Set<String> strings = type.getFieldGetters();
				for ( String s : strings ) {
					acceptor.accept(this.createCompletionProposal(s, context));
				}
			}
		}

	}

}
