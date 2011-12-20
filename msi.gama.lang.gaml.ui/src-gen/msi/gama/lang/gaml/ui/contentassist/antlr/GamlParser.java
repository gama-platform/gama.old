/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui.contentassist.antlr;

import java.util.*;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.antlr.runtime.RecognitionException;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.*;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;
import com.google.inject.Inject;

public class GamlParser extends AbstractContentAssistParser {

	@Inject
	private GamlGrammarAccess grammarAccess;

	private Map<AbstractElement, String> nameMappings;

	@Override
	protected msi.gama.lang.gaml.ui.contentassist.antlr.internal.InternalGamlParser createParser() {
		msi.gama.lang.gaml.ui.contentassist.antlr.internal.InternalGamlParser result =
			new msi.gama.lang.gaml.ui.contentassist.antlr.internal.InternalGamlParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}

	@Override
	protected String getRuleName(final AbstractElement element) {
		if ( nameMappings == null ) {
			nameMappings = new HashMap<AbstractElement, String>() {

				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getGamlLangDefAccess().getAlternatives_2(),
						"rule__GamlLangDef__Alternatives_2");
					put(grammarAccess.getDefKeywordAccess().getAlternatives_2(),
						"rule__DefKeyword__Alternatives_2");
					put(grammarAccess.getAbstractGamlRefAccess().getAlternatives(),
						"rule__AbstractGamlRef__Alternatives");
					put(grammarAccess.getStatementAccess().getAlternatives(),
						"rule__Statement__Alternatives");
					put(grammarAccess.getSubStatementAccess().getAlternatives(),
						"rule__SubStatement__Alternatives");
					put(grammarAccess.getSetEvalAccess().getAlternatives_3(),
						"rule__SetEval__Alternatives_3");
					put(grammarAccess.getDefinitionAccess().getAlternatives_3(),
						"rule__Definition__Alternatives_3");
					put(grammarAccess.getEvaluationAccess().getAlternatives_3(),
						"rule__Evaluation__Alternatives_3");
					put(grammarAccess.getFacetExprAccess().getAlternatives(),
						"rule__FacetExpr__Alternatives");
					put(grammarAccess.getAbstractDefinitionAccess().getAlternatives(),
						"rule__AbstractDefinition__Alternatives");
					put(grammarAccess.getAssignmentOpAccess().getAlternatives_1_0(),
						"rule__AssignmentOp__Alternatives_1_0");
					put(grammarAccess.getRelationalAccess().getAlternatives_1_0(),
						"rule__Relational__Alternatives_1_0");
					put(grammarAccess.getAdditionAccess().getAlternatives_1_0(),
						"rule__Addition__Alternatives_1_0");
					put(grammarAccess.getMultiplicationAccess().getAlternatives_1_0(),
						"rule__Multiplication__Alternatives_1_0");
					put(grammarAccess.getGamlUnaryExprAccess().getAlternatives(),
						"rule__GamlUnaryExpr__Alternatives");
					put(grammarAccess.getUnarOpAccess().getAlternatives(),
						"rule__UnarOp__Alternatives");
					put(grammarAccess.getPrePrimaryExprAccess().getAlternatives(),
						"rule__PrePrimaryExpr__Alternatives");
					put(grammarAccess.getPrimaryExpressionAccess().getAlternatives(),
						"rule__PrimaryExpression__Alternatives");
					put(grammarAccess.getAbrstractRefAccess().getAlternatives_1(),
						"rule__AbrstractRef__Alternatives_1");
					put(grammarAccess.getTerminalExpressionAccess().getAlternatives(),
						"rule__TerminalExpression__Alternatives");
					put(grammarAccess.getModelAccess().getGroup(), "rule__Model__Group__0");
					put(grammarAccess.getImportAccess().getGroup(), "rule__Import__Group__0");
					put(grammarAccess.getImportedFQNAccess().getGroup(),
						"rule__ImportedFQN__Group__0");
					put(grammarAccess.getImportedFQNAccess().getGroup_1(),
						"rule__ImportedFQN__Group_1__0");
					put(grammarAccess.getGamlLangDefAccess().getGroup(),
						"rule__GamlLangDef__Group__0");
					put(grammarAccess.getDefKeywordAccess().getGroup(),
						"rule__DefKeyword__Group__0");
					put(grammarAccess.getGamlBlockAccess().getGroup(), "rule__GamlBlock__Group__0");
					put(grammarAccess.getGamlBlockAccess().getGroup_2(),
						"rule__GamlBlock__Group_2__0");
					put(grammarAccess.getGamlBlockAccess().getGroup_2_3(),
						"rule__GamlBlock__Group_2_3__0");
					put(grammarAccess.getGamlBlockAccess().getGroup_3(),
						"rule__GamlBlock__Group_3__0");
					put(grammarAccess.getGamlBlockAccess().getGroup_3_3(),
						"rule__GamlBlock__Group_3_3__0");
					put(grammarAccess.getDefFacetAccess().getGroup(), "rule__DefFacet__Group__0");
					put(grammarAccess.getDefFacetAccess().getGroup_2(),
						"rule__DefFacet__Group_2__0");
					put(grammarAccess.getDefFacetAccess().getGroup_3(),
						"rule__DefFacet__Group_3__0");
					put(grammarAccess.getDefBinaryOpAccess().getGroup(),
						"rule__DefBinaryOp__Group__0");
					put(grammarAccess.getDefReservedAccess().getGroup(),
						"rule__DefReserved__Group__0");
					put(grammarAccess.getDefReservedAccess().getGroup_2(),
						"rule__DefReserved__Group_2__0");
					put(grammarAccess.getDefReservedAccess().getGroup_3(),
						"rule__DefReserved__Group_3__0");
					put(grammarAccess.getDefUnitAccess().getGroup(), "rule__DefUnit__Group__0");
					put(grammarAccess.getDefUnitAccess().getGroup_2(), "rule__DefUnit__Group_2__0");
					put(grammarAccess.getGamlFacetRefAccess().getGroup(),
						"rule__GamlFacetRef__Group__0");
					put(grammarAccess.getSetEvalAccess().getGroup(), "rule__SetEval__Group__0");
					put(grammarAccess.getDefinitionAccess().getGroup(),
						"rule__Definition__Group__0");
					put(grammarAccess.getEvaluationAccess().getGroup(),
						"rule__Evaluation__Group__0");
					put(grammarAccess.getEvaluationAccess().getGroup_1(),
						"rule__Evaluation__Group_1__0");
					put(grammarAccess.getFacetExprAccess().getGroup_0(),
						"rule__FacetExpr__Group_0__0");
					put(grammarAccess.getFacetExprAccess().getGroup_1(),
						"rule__FacetExpr__Group_1__0");
					put(grammarAccess.getBlockAccess().getGroup(), "rule__Block__Group__0");
					put(grammarAccess.getAssignmentOpAccess().getGroup(),
						"rule__AssignmentOp__Group__0");
					put(grammarAccess.getAssignmentOpAccess().getGroup_1(),
						"rule__AssignmentOp__Group_1__0");
					put(grammarAccess.getAssignmentOpAccess().getGroup_1_0_0(),
						"rule__AssignmentOp__Group_1_0_0__0");
					put(grammarAccess.getAssignmentOpAccess().getGroup_1_0_1(),
						"rule__AssignmentOp__Group_1_0_1__0");
					put(grammarAccess.getAssignmentOpAccess().getGroup_1_0_2(),
						"rule__AssignmentOp__Group_1_0_2__0");
					put(grammarAccess.getAssignmentOpAccess().getGroup_1_0_3(),
						"rule__AssignmentOp__Group_1_0_3__0");
					put(grammarAccess.getTernExpAccess().getGroup(), "rule__TernExp__Group__0");
					put(grammarAccess.getTernExpAccess().getGroup_1(), "rule__TernExp__Group_1__0");
					put(grammarAccess.getOrExpAccess().getGroup(), "rule__OrExp__Group__0");
					put(grammarAccess.getOrExpAccess().getGroup_1(), "rule__OrExp__Group_1__0");
					put(grammarAccess.getAndExpAccess().getGroup(), "rule__AndExp__Group__0");
					put(grammarAccess.getAndExpAccess().getGroup_1(), "rule__AndExp__Group_1__0");
					put(grammarAccess.getRelationalAccess().getGroup(),
						"rule__Relational__Group__0");
					put(grammarAccess.getRelationalAccess().getGroup_1(),
						"rule__Relational__Group_1__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_0(),
						"rule__Relational__Group_1_0_0__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_1(),
						"rule__Relational__Group_1_0_1__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_2(),
						"rule__Relational__Group_1_0_2__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_3(),
						"rule__Relational__Group_1_0_3__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_4(),
						"rule__Relational__Group_1_0_4__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_5(),
						"rule__Relational__Group_1_0_5__0");
					put(grammarAccess.getRelationalAccess().getGroup_1_0_6(),
						"rule__Relational__Group_1_0_6__0");
					put(grammarAccess.getPairExprAccess().getGroup(), "rule__PairExpr__Group__0");
					put(grammarAccess.getPairExprAccess().getGroup_1(),
						"rule__PairExpr__Group_1__0");
					put(grammarAccess.getPairExprAccess().getGroup_1_0(),
						"rule__PairExpr__Group_1_0__0");
					put(grammarAccess.getAdditionAccess().getGroup(), "rule__Addition__Group__0");
					put(grammarAccess.getAdditionAccess().getGroup_1(),
						"rule__Addition__Group_1__0");
					put(grammarAccess.getAdditionAccess().getGroup_1_0_0(),
						"rule__Addition__Group_1_0_0__0");
					put(grammarAccess.getAdditionAccess().getGroup_1_0_1(),
						"rule__Addition__Group_1_0_1__0");
					put(grammarAccess.getMultiplicationAccess().getGroup(),
						"rule__Multiplication__Group__0");
					put(grammarAccess.getMultiplicationAccess().getGroup_1(),
						"rule__Multiplication__Group_1__0");
					put(grammarAccess.getMultiplicationAccess().getGroup_1_0_0(),
						"rule__Multiplication__Group_1_0_0__0");
					put(grammarAccess.getMultiplicationAccess().getGroup_1_0_1(),
						"rule__Multiplication__Group_1_0_1__0");
					put(grammarAccess.getGamlBinExprAccess().getGroup(),
						"rule__GamlBinExpr__Group__0");
					put(grammarAccess.getGamlBinExprAccess().getGroup_1(),
						"rule__GamlBinExpr__Group_1__0");
					put(grammarAccess.getGamlBinExprAccess().getGroup_1_0(),
						"rule__GamlBinExpr__Group_1_0__0");
					put(grammarAccess.getPowerAccess().getGroup(), "rule__Power__Group__0");
					put(grammarAccess.getPowerAccess().getGroup_1(), "rule__Power__Group_1__0");
					put(grammarAccess.getPowerAccess().getGroup_1_0(), "rule__Power__Group_1_0__0");
					put(grammarAccess.getGamlUnitExprAccess().getGroup(),
						"rule__GamlUnitExpr__Group__0");
					put(grammarAccess.getGamlUnitExprAccess().getGroup_1(),
						"rule__GamlUnitExpr__Group_1__0");
					put(grammarAccess.getGamlUnitExprAccess().getGroup_1_0(),
						"rule__GamlUnitExpr__Group_1_0__0");
					put(grammarAccess.getGamlUnaryExprAccess().getGroup_1(),
						"rule__GamlUnaryExpr__Group_1__0");
					put(grammarAccess.getGamlUnaryExprAccess().getGroup_1_1(),
						"rule__GamlUnaryExpr__Group_1_1__0");
					put(grammarAccess.getMemberRefAccess().getGroup(), "rule__MemberRef__Group__0");
					put(grammarAccess.getMemberRefAccess().getGroup_1(),
						"rule__MemberRef__Group_1__0");
					put(grammarAccess.getPrimaryExpressionAccess().getGroup_0(),
						"rule__PrimaryExpression__Group_0__0");
					put(grammarAccess.getPrimaryExpressionAccess().getGroup_1(),
						"rule__PrimaryExpression__Group_1__0");
					put(grammarAccess.getPrimaryExpressionAccess().getGroup_2(),
						"rule__PrimaryExpression__Group_2__0");
					put(grammarAccess.getPointAccess().getGroup(), "rule__Point__Group__0");
					put(grammarAccess.getMatrixAccess().getGroup(), "rule__Matrix__Group__0");
					put(grammarAccess.getMatrixAccess().getGroup_1(), "rule__Matrix__Group_1__0");
					put(grammarAccess.getMatrixAccess().getGroup_1_1(),
						"rule__Matrix__Group_1_1__0");
					put(grammarAccess.getRowAccess().getGroup(), "rule__Row__Group__0");
					put(grammarAccess.getRowAccess().getGroup_1(), "rule__Row__Group_1__0");
					put(grammarAccess.getRightMemberRefAccess().getGroup(),
						"rule__RightMemberRef__Group__0");
					put(grammarAccess.getRightMemberRefAccess().getGroup_1(),
						"rule__RightMemberRef__Group_1__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup(),
						"rule__AbrstractRef__Group__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup_1_0(),
						"rule__AbrstractRef__Group_1_0__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup_1_0_2(),
						"rule__AbrstractRef__Group_1_0_2__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup_1_0_2_1(),
						"rule__AbrstractRef__Group_1_0_2_1__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup_1_1(),
						"rule__AbrstractRef__Group_1_1__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup_1_1_2(),
						"rule__AbrstractRef__Group_1_1_2__0");
					put(grammarAccess.getAbrstractRefAccess().getGroup_1_1_2_1(),
						"rule__AbrstractRef__Group_1_1_2_1__0");
					put(grammarAccess.getTerminalExpressionAccess().getGroup_0(),
						"rule__TerminalExpression__Group_0__0");
					put(grammarAccess.getTerminalExpressionAccess().getGroup_1(),
						"rule__TerminalExpression__Group_1__0");
					put(grammarAccess.getTerminalExpressionAccess().getGroup_2(),
						"rule__TerminalExpression__Group_2__0");
					put(grammarAccess.getTerminalExpressionAccess().getGroup_3(),
						"rule__TerminalExpression__Group_3__0");
					put(grammarAccess.getTerminalExpressionAccess().getGroup_4(),
						"rule__TerminalExpression__Group_4__0");
					put(grammarAccess.getFQNAccess().getGroup(), "rule__FQN__Group__0");
					put(grammarAccess.getFQNAccess().getGroup_1(), "rule__FQN__Group_1__0");
					put(grammarAccess.getModelAccess().getNameAssignment_1(),
						"rule__Model__NameAssignment_1");
					put(grammarAccess.getModelAccess().getImportsAssignment_2(),
						"rule__Model__ImportsAssignment_2");
					put(grammarAccess.getModelAccess().getGamlAssignment_3(),
						"rule__Model__GamlAssignment_3");
					put(grammarAccess.getModelAccess().getStatementsAssignment_4(),
						"rule__Model__StatementsAssignment_4");
					put(grammarAccess.getImportAccess().getImportURIAssignment_1(),
						"rule__Import__ImportURIAssignment_1");
					put(grammarAccess.getGamlLangDefAccess().getKAssignment_2_0(),
						"rule__GamlLangDef__KAssignment_2_0");
					put(grammarAccess.getGamlLangDefAccess().getFAssignment_2_1(),
						"rule__GamlLangDef__FAssignment_2_1");
					put(grammarAccess.getGamlLangDefAccess().getBAssignment_2_2(),
						"rule__GamlLangDef__BAssignment_2_2");
					put(grammarAccess.getGamlLangDefAccess().getRAssignment_2_3(),
						"rule__GamlLangDef__RAssignment_2_3");
					put(grammarAccess.getGamlLangDefAccess().getUAssignment_2_4(),
						"rule__GamlLangDef__UAssignment_2_4");
					put(grammarAccess.getDefKeywordAccess().getNameAssignment_1(),
						"rule__DefKeyword__NameAssignment_1");
					put(grammarAccess.getDefKeywordAccess().getBlockAssignment_2_0(),
						"rule__DefKeyword__BlockAssignment_2_0");
					put(grammarAccess.getGamlBlockAccess().getFacetsAssignment_2_2(),
						"rule__GamlBlock__FacetsAssignment_2_2");
					put(grammarAccess.getGamlBlockAccess().getFacetsAssignment_2_3_1(),
						"rule__GamlBlock__FacetsAssignment_2_3_1");
					put(grammarAccess.getGamlBlockAccess().getChildsAssignment_3_2(),
						"rule__GamlBlock__ChildsAssignment_3_2");
					put(grammarAccess.getGamlBlockAccess().getChildsAssignment_3_3_1(),
						"rule__GamlBlock__ChildsAssignment_3_3_1");
					put(grammarAccess.getDefFacetAccess().getNameAssignment_1(),
						"rule__DefFacet__NameAssignment_1");
					put(grammarAccess.getDefFacetAccess().getTypeAssignment_2_1(),
						"rule__DefFacet__TypeAssignment_2_1");
					put(grammarAccess.getDefFacetAccess().getDefaultAssignment_3_1(),
						"rule__DefFacet__DefaultAssignment_3_1");
					put(grammarAccess.getDefBinaryOpAccess().getNameAssignment_1(),
						"rule__DefBinaryOp__NameAssignment_1");
					put(grammarAccess.getDefReservedAccess().getNameAssignment_1(),
						"rule__DefReserved__NameAssignment_1");
					put(grammarAccess.getDefReservedAccess().getTypeAssignment_2_1(),
						"rule__DefReserved__TypeAssignment_2_1");
					put(grammarAccess.getDefReservedAccess().getValueAssignment_3_1(),
						"rule__DefReserved__ValueAssignment_3_1");
					put(grammarAccess.getDefUnitAccess().getNameAssignment_1(),
						"rule__DefUnit__NameAssignment_1");
					put(grammarAccess.getDefUnitAccess().getCoefAssignment_2_1(),
						"rule__DefUnit__CoefAssignment_2_1");
					put(grammarAccess.getGamlKeywordRefAccess().getRefAssignment(),
						"rule__GamlKeywordRef__RefAssignment");
					put(grammarAccess.getGamlFacetRefAccess().getRefAssignment_0(),
						"rule__GamlFacetRef__RefAssignment_0");
					put(grammarAccess.getGamlBinarOpRefAccess().getRefAssignment(),
						"rule__GamlBinarOpRef__RefAssignment");
					put(grammarAccess.getGamlUnitRefAccess().getRefAssignment(),
						"rule__GamlUnitRef__RefAssignment");
					put(grammarAccess.getGamlReservedRefAccess().getRefAssignment(),
						"rule__GamlReservedRef__RefAssignment");
					put(grammarAccess.getSetEvalAccess().getVarAssignment_1(),
						"rule__SetEval__VarAssignment_1");
					put(grammarAccess.getSetEvalAccess().getFacetsAssignment_2(),
						"rule__SetEval__FacetsAssignment_2");
					put(grammarAccess.getSetEvalAccess().getBlockAssignment_3_0(),
						"rule__SetEval__BlockAssignment_3_0");
					put(grammarAccess.getDefinitionAccess().getKeyAssignment_0(),
						"rule__Definition__KeyAssignment_0");
					put(grammarAccess.getDefinitionAccess().getNameAssignment_1(),
						"rule__Definition__NameAssignment_1");
					put(grammarAccess.getDefinitionAccess().getFacetsAssignment_2(),
						"rule__Definition__FacetsAssignment_2");
					put(grammarAccess.getDefinitionAccess().getBlockAssignment_3_0(),
						"rule__Definition__BlockAssignment_3_0");
					put(grammarAccess.getEvaluationAccess().getKeyAssignment_0(),
						"rule__Evaluation__KeyAssignment_0");
					put(grammarAccess.getEvaluationAccess().getVarAssignment_1_1(),
						"rule__Evaluation__VarAssignment_1_1");
					put(grammarAccess.getEvaluationAccess().getFacetsAssignment_2(),
						"rule__Evaluation__FacetsAssignment_2");
					put(grammarAccess.getEvaluationAccess().getBlockAssignment_3_0(),
						"rule__Evaluation__BlockAssignment_3_0");
					put(grammarAccess.getFacetExprAccess().getNameAssignment_0_1(),
						"rule__FacetExpr__NameAssignment_0_1");
					put(grammarAccess.getFacetExprAccess().getKeyAssignment_1_0(),
						"rule__FacetExpr__KeyAssignment_1_0");
					put(grammarAccess.getFacetExprAccess().getExprAssignment_1_1(),
						"rule__FacetExpr__ExprAssignment_1_1");
					put(grammarAccess.getBlockAccess().getStatementsAssignment_2(),
						"rule__Block__StatementsAssignment_2");
					put(grammarAccess.getAssignmentOpAccess().getRightAssignment_1_1(),
						"rule__AssignmentOp__RightAssignment_1_1");
					put(grammarAccess.getTernExpAccess().getIfTrueAssignment_1_2(),
						"rule__TernExp__IfTrueAssignment_1_2");
					put(grammarAccess.getTernExpAccess().getIfFalseAssignment_1_4(),
						"rule__TernExp__IfFalseAssignment_1_4");
					put(grammarAccess.getOrExpAccess().getRightAssignment_1_2(),
						"rule__OrExp__RightAssignment_1_2");
					put(grammarAccess.getAndExpAccess().getRightAssignment_1_2(),
						"rule__AndExp__RightAssignment_1_2");
					put(grammarAccess.getRelationalAccess().getRightAssignment_1_1(),
						"rule__Relational__RightAssignment_1_1");
					put(grammarAccess.getPairExprAccess().getRightAssignment_1_1(),
						"rule__PairExpr__RightAssignment_1_1");
					put(grammarAccess.getAdditionAccess().getRightAssignment_1_1(),
						"rule__Addition__RightAssignment_1_1");
					put(grammarAccess.getMultiplicationAccess().getRightAssignment_1_1(),
						"rule__Multiplication__RightAssignment_1_1");
					put(grammarAccess.getGamlBinExprAccess().getOpAssignment_1_0_1(),
						"rule__GamlBinExpr__OpAssignment_1_0_1");
					put(grammarAccess.getGamlBinExprAccess().getRightAssignment_1_1(),
						"rule__GamlBinExpr__RightAssignment_1_1");
					put(grammarAccess.getPowerAccess().getRightAssignment_1_1(),
						"rule__Power__RightAssignment_1_1");
					put(grammarAccess.getGamlUnitExprAccess().getRightAssignment_1_1(),
						"rule__GamlUnitExpr__RightAssignment_1_1");
					put(grammarAccess.getGamlUnaryExprAccess().getOpAssignment_1_1_0(),
						"rule__GamlUnaryExpr__OpAssignment_1_1_0");
					put(grammarAccess.getGamlUnaryExprAccess().getRightAssignment_1_1_1(),
						"rule__GamlUnaryExpr__RightAssignment_1_1_1");
					put(grammarAccess.getMemberRefAccess().getRightAssignment_1_2(),
						"rule__MemberRef__RightAssignment_1_2");
					put(grammarAccess.getPointAccess().getXAssignment_0(),
						"rule__Point__XAssignment_0");
					put(grammarAccess.getPointAccess().getYAssignment_2(),
						"rule__Point__YAssignment_2");
					put(grammarAccess.getMatrixAccess().getRowsAssignment_1_0(),
						"rule__Matrix__RowsAssignment_1_0");
					put(grammarAccess.getMatrixAccess().getRowsAssignment_1_1_1(),
						"rule__Matrix__RowsAssignment_1_1_1");
					put(grammarAccess.getRowAccess().getExprsAssignment_0(),
						"rule__Row__ExprsAssignment_0");
					put(grammarAccess.getRowAccess().getExprsAssignment_1_1(),
						"rule__Row__ExprsAssignment_1_1");
					put(grammarAccess.getRightMemberRefAccess().getRightAssignment_1_2(),
						"rule__RightMemberRef__RightAssignment_1_2");
					put(grammarAccess.getAbrstractRefAccess().getArgsAssignment_1_0_2_0(),
						"rule__AbrstractRef__ArgsAssignment_1_0_2_0");
					put(grammarAccess.getAbrstractRefAccess().getArgsAssignment_1_0_2_1_1(),
						"rule__AbrstractRef__ArgsAssignment_1_0_2_1_1");
					put(grammarAccess.getAbrstractRefAccess().getArgsAssignment_1_1_2_0(),
						"rule__AbrstractRef__ArgsAssignment_1_1_2_0");
					put(grammarAccess.getAbrstractRefAccess().getArgsAssignment_1_1_2_1_1(),
						"rule__AbrstractRef__ArgsAssignment_1_1_2_1_1");
					put(grammarAccess.getVariableRefAccess().getRefAssignment(),
						"rule__VariableRef__RefAssignment");
					put(grammarAccess.getTerminalExpressionAccess().getValueAssignment_0_1(),
						"rule__TerminalExpression__ValueAssignment_0_1");
					put(grammarAccess.getTerminalExpressionAccess().getValueAssignment_1_1(),
						"rule__TerminalExpression__ValueAssignment_1_1");
					put(grammarAccess.getTerminalExpressionAccess().getValueAssignment_2_1(),
						"rule__TerminalExpression__ValueAssignment_2_1");
					put(grammarAccess.getTerminalExpressionAccess().getValueAssignment_3_1(),
						"rule__TerminalExpression__ValueAssignment_3_1");
					put(grammarAccess.getTerminalExpressionAccess().getValueAssignment_4_1(),
						"rule__TerminalExpression__ValueAssignment_4_1");
				}
			};
		}
		return nameMappings.get(element);
	}

	@Override
	protected Collection<FollowElement> getFollowElements(
		final AbstractInternalContentAssistParser parser) {
		try {
			msi.gama.lang.gaml.ui.contentassist.antlr.internal.InternalGamlParser typedParser =
				(msi.gama.lang.gaml.ui.contentassist.antlr.internal.InternalGamlParser) parser;
			typedParser.entryRuleModel();
			return typedParser.getFollowElements();
		} catch (RecognitionException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}

	public GamlGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	public void setGrammarAccess(final GamlGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
