/*******************************************************************************************************
 *
 * GamlParser.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ide.contentassist.antlr;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Map;
import msi.gama.lang.gaml.ide.contentassist.antlr.internal.InternalGamlParser;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AbstractContentAssistParser;

/**
 * The Class GamlParser.
 */
public class GamlParser extends AbstractContentAssistParser {

	/**
	 * The Class NameMappings.
	 */
	@Singleton
	public static final class NameMappings {
		
		/** The mappings. */
		private final Map<AbstractElement, String> mappings;
		
		/**
		 * Instantiates a new name mappings.
		 *
		 * @param grammarAccess the grammar access
		 */
		@Inject
		public NameMappings(GamlGrammarAccess grammarAccess) {
			ImmutableMap.Builder<AbstractElement, String> builder = ImmutableMap.builder();
			init(builder, grammarAccess);
			this.mappings = builder.build();
		}
		
		/**
		 * Gets the rule name.
		 *
		 * @param element the element
		 * @return the rule name
		 */
		public String getRuleName(AbstractElement element) {
			return mappings.get(element);
		}
		
		/**
		 * Inits the.
		 *
		 * @param builder the builder
		 * @param grammarAccess the grammar access
		 */
		private static void init(ImmutableMap.Builder<AbstractElement, String> builder, GamlGrammarAccess grammarAccess) {
			builder.put(grammarAccess.getEntryAccess().getAlternatives(), "rule__Entry__Alternatives");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getNameAlternatives_2_0(), "rule__HeadlessExperiment__NameAlternatives_2_0");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getAlternatives_5(), "rule__HeadlessExperiment__Alternatives_5");
			builder.put(grammarAccess.getS_SectionAccess().getAlternatives(), "rule__S_Section__Alternatives");
			builder.put(grammarAccess.getS_GlobalAccess().getAlternatives_2(), "rule__S_Global__Alternatives_2");
			builder.put(grammarAccess.getS_SpeciesAccess().getAlternatives_4(), "rule__S_Species__Alternatives_4");
			builder.put(grammarAccess.getS_ExperimentAccess().getNameAlternatives_2_0(), "rule__S_Experiment__NameAlternatives_2_0");
			builder.put(grammarAccess.getS_ExperimentAccess().getAlternatives_4(), "rule__S_Experiment__Alternatives_4");
			builder.put(grammarAccess.getStatementAccess().getAlternatives(), "rule__Statement__Alternatives");
			builder.put(grammarAccess.getStatementAccess().getAlternatives_0(), "rule__Statement__Alternatives_0");
			builder.put(grammarAccess.getStatementAccess().getAlternatives_0_1(), "rule__Statement__Alternatives_0_1");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getAlternatives_4(), "rule__S_1Expr_Facets_BlockOrEnd__Alternatives_4");
			builder.put(grammarAccess.getS_DoAccess().getAlternatives_4(), "rule__S_Do__Alternatives_4");
			builder.put(grammarAccess.getS_IfAccess().getElseAlternatives_4_1_0(), "rule__S_If__ElseAlternatives_4_1_0");
			builder.put(grammarAccess.getS_OtherAccess().getAlternatives_2(), "rule__S_Other__Alternatives_2");
			builder.put(grammarAccess.getS_DeclarationAccess().getAlternatives(), "rule__S_Declaration__Alternatives");
			builder.put(grammarAccess.getS_DefinitionAccess().getNameAlternatives_2_0(), "rule__S_Definition__NameAlternatives_2_0");
			builder.put(grammarAccess.getS_DefinitionAccess().getAlternatives_5(), "rule__S_Definition__Alternatives_5");
			builder.put(grammarAccess.getS_ActionAccess().getAlternatives_6(), "rule__S_Action__Alternatives_6");
			builder.put(grammarAccess.getS_AssignmentAccess().getAlternatives(), "rule__S_Assignment__Alternatives");
			builder.put(grammarAccess.getS_SetAccess().getAlternatives_2(), "rule__S_Set__Alternatives_2");
			builder.put(grammarAccess.getS_EquationsAccess().getAlternatives_3(), "rule__S_Equations__Alternatives_3");
			builder.put(grammarAccess.getS_EquationAccess().getExprAlternatives_0_0(), "rule__S_Equation__ExprAlternatives_0_0");
			builder.put(grammarAccess.getS_SolveAccess().getAlternatives_4(), "rule__S_Solve__Alternatives_4");
			builder.put(grammarAccess.getS_DisplayAccess().getNameAlternatives_2_0(), "rule__S_Display__NameAlternatives_2_0");
			builder.put(grammarAccess.getDisplayStatementAccess().getAlternatives(), "rule__DisplayStatement__Alternatives");
			builder.put(grammarAccess.getSpeciesOrGridDisplayStatementAccess().getAlternatives_3(), "rule__SpeciesOrGridDisplayStatement__Alternatives_3");
			builder.put(grammarAccess.get_SpeciesKeyAccess().getAlternatives(), "rule___SpeciesKey__Alternatives");
			builder.put(grammarAccess.get_1Expr_Facets_BlockOrEnd_KeyAccess().getAlternatives(), "rule___1Expr_Facets_BlockOrEnd_Key__Alternatives");
			builder.put(grammarAccess.get_LayerKeyAccess().getAlternatives(), "rule___LayerKey__Alternatives");
			builder.put(grammarAccess.get_DoKeyAccess().getAlternatives(), "rule___DoKey__Alternatives");
			builder.put(grammarAccess.get_VarOrConstKeyAccess().getAlternatives(), "rule___VarOrConstKey__Alternatives");
			builder.put(grammarAccess.get_ReflexKeyAccess().getAlternatives(), "rule___ReflexKey__Alternatives");
			builder.put(grammarAccess.get_AssignmentKeyAccess().getAlternatives(), "rule___AssignmentKey__Alternatives");
			builder.put(grammarAccess.getFacetAccess().getAlternatives(), "rule__Facet__Alternatives");
			builder.put(grammarAccess.getFirstFacetKeyAccess().getAlternatives(), "rule__FirstFacetKey__Alternatives");
			builder.put(grammarAccess.getDefinitionFacetKeyAccess().getAlternatives(), "rule__DefinitionFacetKey__Alternatives");
			builder.put(grammarAccess.getTypeFacetKeyAccess().getAlternatives(), "rule__TypeFacetKey__Alternatives");
			builder.put(grammarAccess.getSpecialFacetKeyAccess().getAlternatives(), "rule__SpecialFacetKey__Alternatives");
			builder.put(grammarAccess.getActionFacetKeyAccess().getAlternatives(), "rule__ActionFacetKey__Alternatives");
			builder.put(grammarAccess.getClassicFacetAccess().getAlternatives_0(), "rule__ClassicFacet__Alternatives_0");
			builder.put(grammarAccess.getDefinitionFacetAccess().getNameAlternatives_1_0(), "rule__DefinitionFacet__NameAlternatives_1_0");
			builder.put(grammarAccess.getFunctionFacetAccess().getAlternatives_1(), "rule__FunctionFacet__Alternatives_1");
			builder.put(grammarAccess.getTypeFacetAccess().getAlternatives_1(), "rule__TypeFacet__Alternatives_1");
			builder.put(grammarAccess.getActionFacetAccess().getAlternatives_1(), "rule__ActionFacet__Alternatives_1");
			builder.put(grammarAccess.getExpressionAccess().getAlternatives(), "rule__Expression__Alternatives");
			builder.put(grammarAccess.getBinaryOperatorAccess().getAlternatives(), "rule__BinaryOperator__Alternatives");
			builder.put(grammarAccess.getArgumentPairAccess().getAlternatives_0_0(), "rule__ArgumentPair__Alternatives_0_0");
			builder.put(grammarAccess.getArgumentPairAccess().getOpAlternatives_0_0_1_0_0(), "rule__ArgumentPair__OpAlternatives_0_0_1_0_0");
			builder.put(grammarAccess.getCastAccess().getAlternatives_1_1(), "rule__Cast__Alternatives_1_1");
			builder.put(grammarAccess.getComparisonAccess().getOpAlternatives_1_0_1_0(), "rule__Comparison__OpAlternatives_1_0_1_0");
			builder.put(grammarAccess.getAdditionAccess().getOpAlternatives_1_0_1_0(), "rule__Addition__OpAlternatives_1_0_1_0");
			builder.put(grammarAccess.getMultiplicationAccess().getOpAlternatives_1_0_1_0(), "rule__Multiplication__OpAlternatives_1_0_1_0");
			builder.put(grammarAccess.getUnitAccess().getOpAlternatives_1_0_1_0(), "rule__Unit__OpAlternatives_1_0_1_0");
			builder.put(grammarAccess.getUnaryAccess().getAlternatives(), "rule__Unary__Alternatives");
			builder.put(grammarAccess.getUnaryAccess().getAlternatives_1_1(), "rule__Unary__Alternatives_1_1");
			builder.put(grammarAccess.getUnaryAccess().getOpAlternatives_1_1_0_0_0(), "rule__Unary__OpAlternatives_1_1_0_0_0");
			builder.put(grammarAccess.getUnaryAccess().getOpAlternatives_1_1_1_0_0(), "rule__Unary__OpAlternatives_1_1_1_0_0");
			builder.put(grammarAccess.getAccessAccess().getAlternatives_1_1(), "rule__Access__Alternatives_1_1");
			builder.put(grammarAccess.getAccessAccess().getRightAlternatives_1_1_1_1_0(), "rule__Access__RightAlternatives_1_1_1_1_0");
			builder.put(grammarAccess.getPrimaryAccess().getAlternatives(), "rule__Primary__Alternatives");
			builder.put(grammarAccess.getAbstractRefAccess().getAlternatives(), "rule__AbstractRef__Alternatives");
			builder.put(grammarAccess.getExpressionListAccess().getAlternatives(), "rule__ExpressionList__Alternatives");
			builder.put(grammarAccess.getParameterAccess().getAlternatives_1(), "rule__Parameter__Alternatives_1");
			builder.put(grammarAccess.getParameterAccess().getBuiltInFacetKeyAlternatives_1_0_0(), "rule__Parameter__BuiltInFacetKeyAlternatives_1_0_0");
			builder.put(grammarAccess.getTypeRefAccess().getAlternatives(), "rule__TypeRef__Alternatives");
			builder.put(grammarAccess.getGamlDefinitionAccess().getAlternatives(), "rule__GamlDefinition__Alternatives");
			builder.put(grammarAccess.getEquationDefinitionAccess().getAlternatives(), "rule__EquationDefinition__Alternatives");
			builder.put(grammarAccess.getTypeDefinitionAccess().getAlternatives(), "rule__TypeDefinition__Alternatives");
			builder.put(grammarAccess.getVarDefinitionAccess().getAlternatives(), "rule__VarDefinition__Alternatives");
			builder.put(grammarAccess.getVarDefinitionAccess().getAlternatives_1(), "rule__VarDefinition__Alternatives_1");
			builder.put(grammarAccess.getActionDefinitionAccess().getAlternatives(), "rule__ActionDefinition__Alternatives");
			builder.put(grammarAccess.getValid_IDAccess().getAlternatives(), "rule__Valid_ID__Alternatives");
			builder.put(grammarAccess.getTerminalExpressionAccess().getAlternatives(), "rule__TerminalExpression__Alternatives");
			builder.put(grammarAccess.getStandaloneBlockAccess().getGroup(), "rule__StandaloneBlock__Group__0");
			builder.put(grammarAccess.getStringEvaluatorAccess().getGroup(), "rule__StringEvaluator__Group__0");
			builder.put(grammarAccess.getModelAccess().getGroup(), "rule__Model__Group__0");
			builder.put(grammarAccess.getModelBlockAccess().getGroup(), "rule__ModelBlock__Group__0");
			builder.put(grammarAccess.getImportAccess().getGroup(), "rule__Import__Group__0");
			builder.put(grammarAccess.getImportAccess().getGroup_2(), "rule__Import__Group_2__0");
			builder.put(grammarAccess.getPragmaAccess().getGroup(), "rule__Pragma__Group__0");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getGroup(), "rule__HeadlessExperiment__Group__0");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getGroup_3(), "rule__HeadlessExperiment__Group_3__0");
			builder.put(grammarAccess.getS_GlobalAccess().getGroup(), "rule__S_Global__Group__0");
			builder.put(grammarAccess.getS_SpeciesAccess().getGroup(), "rule__S_Species__Group__0");
			builder.put(grammarAccess.getS_ExperimentAccess().getGroup(), "rule__S_Experiment__Group__0");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getGroup(), "rule__S_1Expr_Facets_BlockOrEnd__Group__0");
			builder.put(grammarAccess.getS_DoAccess().getGroup(), "rule__S_Do__Group__0");
			builder.put(grammarAccess.getS_LoopAccess().getGroup(), "rule__S_Loop__Group__0");
			builder.put(grammarAccess.getS_IfAccess().getGroup(), "rule__S_If__Group__0");
			builder.put(grammarAccess.getS_IfAccess().getGroup_4(), "rule__S_If__Group_4__0");
			builder.put(grammarAccess.getS_TryAccess().getGroup(), "rule__S_Try__Group__0");
			builder.put(grammarAccess.getS_TryAccess().getGroup_2(), "rule__S_Try__Group_2__0");
			builder.put(grammarAccess.getS_OtherAccess().getGroup(), "rule__S_Other__Group__0");
			builder.put(grammarAccess.getS_ReturnAccess().getGroup(), "rule__S_Return__Group__0");
			builder.put(grammarAccess.getS_ReflexAccess().getGroup(), "rule__S_Reflex__Group__0");
			builder.put(grammarAccess.getS_ReflexAccess().getGroup_1(), "rule__S_Reflex__Group_1__0");
			builder.put(grammarAccess.getS_ReflexAccess().getGroup_2(), "rule__S_Reflex__Group_2__0");
			builder.put(grammarAccess.getS_DefinitionAccess().getGroup(), "rule__S_Definition__Group__0");
			builder.put(grammarAccess.getS_DefinitionAccess().getGroup_3(), "rule__S_Definition__Group_3__0");
			builder.put(grammarAccess.getS_ActionAccess().getGroup(), "rule__S_Action__Group__0");
			builder.put(grammarAccess.getS_ActionAccess().getGroup_4(), "rule__S_Action__Group_4__0");
			builder.put(grammarAccess.getS_VarAccess().getGroup(), "rule__S_Var__Group__0");
			builder.put(grammarAccess.getS_DirectAssignmentAccess().getGroup(), "rule__S_DirectAssignment__Group__0");
			builder.put(grammarAccess.getS_DirectAssignmentAccess().getGroup_0(), "rule__S_DirectAssignment__Group_0__0");
			builder.put(grammarAccess.getS_SetAccess().getGroup(), "rule__S_Set__Group__0");
			builder.put(grammarAccess.getS_EquationsAccess().getGroup(), "rule__S_Equations__Group__0");
			builder.put(grammarAccess.getS_EquationsAccess().getGroup_3_0(), "rule__S_Equations__Group_3_0__0");
			builder.put(grammarAccess.getS_EquationsAccess().getGroup_3_0_1(), "rule__S_Equations__Group_3_0_1__0");
			builder.put(grammarAccess.getS_EquationAccess().getGroup(), "rule__S_Equation__Group__0");
			builder.put(grammarAccess.getS_SolveAccess().getGroup(), "rule__S_Solve__Group__0");
			builder.put(grammarAccess.getS_DisplayAccess().getGroup(), "rule__S_Display__Group__0");
			builder.put(grammarAccess.getDisplayBlockAccess().getGroup(), "rule__DisplayBlock__Group__0");
			builder.put(grammarAccess.getSpeciesOrGridDisplayStatementAccess().getGroup(), "rule__SpeciesOrGridDisplayStatement__Group__0");
			builder.put(grammarAccess.get_AssignmentKeyAccess().getGroup_2(), "rule___AssignmentKey__Group_2__0");
			builder.put(grammarAccess.get_AssignmentKeyAccess().getGroup_4(), "rule___AssignmentKey__Group_4__0");
			builder.put(grammarAccess.getActionArgumentsAccess().getGroup(), "rule__ActionArguments__Group__0");
			builder.put(grammarAccess.getActionArgumentsAccess().getGroup_1(), "rule__ActionArguments__Group_1__0");
			builder.put(grammarAccess.getArgumentDefinitionAccess().getGroup(), "rule__ArgumentDefinition__Group__0");
			builder.put(grammarAccess.getArgumentDefinitionAccess().getGroup_2(), "rule__ArgumentDefinition__Group_2__0");
			builder.put(grammarAccess.getClassicFacetKeyAccess().getGroup(), "rule__ClassicFacetKey__Group__0");
			builder.put(grammarAccess.getSpecialFacetKeyAccess().getGroup_1(), "rule__SpecialFacetKey__Group_1__0");
			builder.put(grammarAccess.getClassicFacetAccess().getGroup(), "rule__ClassicFacet__Group__0");
			builder.put(grammarAccess.getDefinitionFacetAccess().getGroup(), "rule__DefinitionFacet__Group__0");
			builder.put(grammarAccess.getFunctionFacetAccess().getGroup(), "rule__FunctionFacet__Group__0");
			builder.put(grammarAccess.getFunctionFacetAccess().getGroup_1_0(), "rule__FunctionFacet__Group_1_0__0");
			builder.put(grammarAccess.getFunctionFacetAccess().getGroup_1_1(), "rule__FunctionFacet__Group_1_1__0");
			builder.put(grammarAccess.getTypeFacetAccess().getGroup(), "rule__TypeFacet__Group__0");
			builder.put(grammarAccess.getTypeFacetAccess().getGroup_1_0(), "rule__TypeFacet__Group_1_0__0");
			builder.put(grammarAccess.getActionFacetAccess().getGroup(), "rule__ActionFacet__Group__0");
			builder.put(grammarAccess.getVarFacetAccess().getGroup(), "rule__VarFacet__Group__0");
			builder.put(grammarAccess.getBlockAccess().getGroup(), "rule__Block__Group__0");
			builder.put(grammarAccess.getBlockAccess().getGroup_2(), "rule__Block__Group_2__0");
			builder.put(grammarAccess.getArgumentPairAccess().getGroup(), "rule__ArgumentPair__Group__0");
			builder.put(grammarAccess.getArgumentPairAccess().getGroup_0(), "rule__ArgumentPair__Group_0__0");
			builder.put(grammarAccess.getArgumentPairAccess().getGroup_0_0_0(), "rule__ArgumentPair__Group_0_0_0__0");
			builder.put(grammarAccess.getArgumentPairAccess().getGroup_0_0_1(), "rule__ArgumentPair__Group_0_0_1__0");
			builder.put(grammarAccess.getPairAccess().getGroup(), "rule__Pair__Group__0");
			builder.put(grammarAccess.getPairAccess().getGroup_1(), "rule__Pair__Group_1__0");
			builder.put(grammarAccess.getIfAccess().getGroup(), "rule__If__Group__0");
			builder.put(grammarAccess.getIfAccess().getGroup_1(), "rule__If__Group_1__0");
			builder.put(grammarAccess.getIfAccess().getGroup_1_3(), "rule__If__Group_1_3__0");
			builder.put(grammarAccess.getOrAccess().getGroup(), "rule__Or__Group__0");
			builder.put(grammarAccess.getOrAccess().getGroup_1(), "rule__Or__Group_1__0");
			builder.put(grammarAccess.getAndAccess().getGroup(), "rule__And__Group__0");
			builder.put(grammarAccess.getAndAccess().getGroup_1(), "rule__And__Group_1__0");
			builder.put(grammarAccess.getCastAccess().getGroup(), "rule__Cast__Group__0");
			builder.put(grammarAccess.getCastAccess().getGroup_1(), "rule__Cast__Group_1__0");
			builder.put(grammarAccess.getCastAccess().getGroup_1_0(), "rule__Cast__Group_1_0__0");
			builder.put(grammarAccess.getCastAccess().getGroup_1_1_1(), "rule__Cast__Group_1_1_1__0");
			builder.put(grammarAccess.getComparisonAccess().getGroup(), "rule__Comparison__Group__0");
			builder.put(grammarAccess.getComparisonAccess().getGroup_1(), "rule__Comparison__Group_1__0");
			builder.put(grammarAccess.getComparisonAccess().getGroup_1_0(), "rule__Comparison__Group_1_0__0");
			builder.put(grammarAccess.getAdditionAccess().getGroup(), "rule__Addition__Group__0");
			builder.put(grammarAccess.getAdditionAccess().getGroup_1(), "rule__Addition__Group_1__0");
			builder.put(grammarAccess.getAdditionAccess().getGroup_1_0(), "rule__Addition__Group_1_0__0");
			builder.put(grammarAccess.getMultiplicationAccess().getGroup(), "rule__Multiplication__Group__0");
			builder.put(grammarAccess.getMultiplicationAccess().getGroup_1(), "rule__Multiplication__Group_1__0");
			builder.put(grammarAccess.getMultiplicationAccess().getGroup_1_0(), "rule__Multiplication__Group_1_0__0");
			builder.put(grammarAccess.getExponentiationAccess().getGroup(), "rule__Exponentiation__Group__0");
			builder.put(grammarAccess.getExponentiationAccess().getGroup_1(), "rule__Exponentiation__Group_1__0");
			builder.put(grammarAccess.getExponentiationAccess().getGroup_1_0(), "rule__Exponentiation__Group_1_0__0");
			builder.put(grammarAccess.getBinaryAccess().getGroup(), "rule__Binary__Group__0");
			builder.put(grammarAccess.getBinaryAccess().getGroup_1(), "rule__Binary__Group_1__0");
			builder.put(grammarAccess.getBinaryAccess().getGroup_1_0(), "rule__Binary__Group_1_0__0");
			builder.put(grammarAccess.getUnitAccess().getGroup(), "rule__Unit__Group__0");
			builder.put(grammarAccess.getUnitAccess().getGroup_1(), "rule__Unit__Group_1__0");
			builder.put(grammarAccess.getUnitAccess().getGroup_1_0(), "rule__Unit__Group_1_0__0");
			builder.put(grammarAccess.getUnaryAccess().getGroup_1(), "rule__Unary__Group_1__0");
			builder.put(grammarAccess.getUnaryAccess().getGroup_1_1_0(), "rule__Unary__Group_1_1_0__0");
			builder.put(grammarAccess.getUnaryAccess().getGroup_1_1_1(), "rule__Unary__Group_1_1_1__0");
			builder.put(grammarAccess.getAccessAccess().getGroup(), "rule__Access__Group__0");
			builder.put(grammarAccess.getAccessAccess().getGroup_1(), "rule__Access__Group_1__0");
			builder.put(grammarAccess.getAccessAccess().getGroup_1_1_0(), "rule__Access__Group_1_1_0__0");
			builder.put(grammarAccess.getAccessAccess().getGroup_1_1_1(), "rule__Access__Group_1_1_1__0");
			builder.put(grammarAccess.getPrimaryAccess().getGroup_2(), "rule__Primary__Group_2__0");
			builder.put(grammarAccess.getPrimaryAccess().getGroup_3(), "rule__Primary__Group_3__0");
			builder.put(grammarAccess.getPrimaryAccess().getGroup_4(), "rule__Primary__Group_4__0");
			builder.put(grammarAccess.getPrimaryAccess().getGroup_4_5(), "rule__Primary__Group_4_5__0");
			builder.put(grammarAccess.getFunctionAccess().getGroup(), "rule__Function__Group__0");
			builder.put(grammarAccess.getExpressionListAccess().getGroup_0(), "rule__ExpressionList__Group_0__0");
			builder.put(grammarAccess.getExpressionListAccess().getGroup_0_1(), "rule__ExpressionList__Group_0_1__0");
			builder.put(grammarAccess.getExpressionListAccess().getGroup_1(), "rule__ExpressionList__Group_1__0");
			builder.put(grammarAccess.getExpressionListAccess().getGroup_1_1(), "rule__ExpressionList__Group_1_1__0");
			builder.put(grammarAccess.getParameterAccess().getGroup(), "rule__Parameter__Group__0");
			builder.put(grammarAccess.getParameterAccess().getGroup_1_1(), "rule__Parameter__Group_1_1__0");
			builder.put(grammarAccess.getUnitRefAccess().getGroup(), "rule__UnitRef__Group__0");
			builder.put(grammarAccess.getVariableRefAccess().getGroup(), "rule__VariableRef__Group__0");
			builder.put(grammarAccess.getTypeRefAccess().getGroup_0(), "rule__TypeRef__Group_0__0");
			builder.put(grammarAccess.getTypeRefAccess().getGroup_0_1(), "rule__TypeRef__Group_0_1__0");
			builder.put(grammarAccess.getTypeRefAccess().getGroup_1(), "rule__TypeRef__Group_1__0");
			builder.put(grammarAccess.getTypeRefAccess().getGroup_1_1(), "rule__TypeRef__Group_1_1__0");
			builder.put(grammarAccess.getTypeInfoAccess().getGroup(), "rule__TypeInfo__Group__0");
			builder.put(grammarAccess.getTypeInfoAccess().getGroup_2(), "rule__TypeInfo__Group_2__0");
			builder.put(grammarAccess.getSkillRefAccess().getGroup(), "rule__SkillRef__Group__0");
			builder.put(grammarAccess.getActionRefAccess().getGroup(), "rule__ActionRef__Group__0");
			builder.put(grammarAccess.getEquationRefAccess().getGroup(), "rule__EquationRef__Group__0");
			builder.put(grammarAccess.getUnitFakeDefinitionAccess().getGroup(), "rule__UnitFakeDefinition__Group__0");
			builder.put(grammarAccess.getTypeFakeDefinitionAccess().getGroup(), "rule__TypeFakeDefinition__Group__0");
			builder.put(grammarAccess.getActionFakeDefinitionAccess().getGroup(), "rule__ActionFakeDefinition__Group__0");
			builder.put(grammarAccess.getSkillFakeDefinitionAccess().getGroup(), "rule__SkillFakeDefinition__Group__0");
			builder.put(grammarAccess.getVarFakeDefinitionAccess().getGroup(), "rule__VarFakeDefinition__Group__0");
			builder.put(grammarAccess.getEquationFakeDefinitionAccess().getGroup(), "rule__EquationFakeDefinition__Group__0");
			builder.put(grammarAccess.getTerminalExpressionAccess().getGroup_1(), "rule__TerminalExpression__Group_1__0");
			builder.put(grammarAccess.getTerminalExpressionAccess().getGroup_2(), "rule__TerminalExpression__Group_2__0");
			builder.put(grammarAccess.getTerminalExpressionAccess().getGroup_3(), "rule__TerminalExpression__Group_3__0");
			builder.put(grammarAccess.getTerminalExpressionAccess().getGroup_4(), "rule__TerminalExpression__Group_4__0");
			builder.put(grammarAccess.getStandaloneBlockAccess().getBlockAssignment_1(), "rule__StandaloneBlock__BlockAssignment_1");
			builder.put(grammarAccess.getStringEvaluatorAccess().getTotoAssignment_0(), "rule__StringEvaluator__TotoAssignment_0");
			builder.put(grammarAccess.getStringEvaluatorAccess().getExprAssignment_2(), "rule__StringEvaluator__ExprAssignment_2");
			builder.put(grammarAccess.getModelAccess().getPragmasAssignment_0(), "rule__Model__PragmasAssignment_0");
			builder.put(grammarAccess.getModelAccess().getNameAssignment_2(), "rule__Model__NameAssignment_2");
			builder.put(grammarAccess.getModelAccess().getImportsAssignment_3(), "rule__Model__ImportsAssignment_3");
			builder.put(grammarAccess.getModelAccess().getBlockAssignment_4(), "rule__Model__BlockAssignment_4");
			builder.put(grammarAccess.getModelBlockAccess().getStatementsAssignment_1(), "rule__ModelBlock__StatementsAssignment_1");
			builder.put(grammarAccess.getImportAccess().getImportURIAssignment_1(), "rule__Import__ImportURIAssignment_1");
			builder.put(grammarAccess.getImportAccess().getNameAssignment_2_1(), "rule__Import__NameAssignment_2_1");
			builder.put(grammarAccess.getPragmaAccess().getNameAssignment_1(), "rule__Pragma__NameAssignment_1");
			builder.put(grammarAccess.getExperimentFileStructureAccess().getExpAssignment(), "rule__ExperimentFileStructure__ExpAssignment");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getKeyAssignment_0(), "rule__HeadlessExperiment__KeyAssignment_0");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getFirstFacetAssignment_1(), "rule__HeadlessExperiment__FirstFacetAssignment_1");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getNameAssignment_2(), "rule__HeadlessExperiment__NameAssignment_2");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getImportURIAssignment_3_1(), "rule__HeadlessExperiment__ImportURIAssignment_3_1");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getFacetsAssignment_4(), "rule__HeadlessExperiment__FacetsAssignment_4");
			builder.put(grammarAccess.getHeadlessExperimentAccess().getBlockAssignment_5_0(), "rule__HeadlessExperiment__BlockAssignment_5_0");
			builder.put(grammarAccess.getS_GlobalAccess().getKeyAssignment_0(), "rule__S_Global__KeyAssignment_0");
			builder.put(grammarAccess.getS_GlobalAccess().getFacetsAssignment_1(), "rule__S_Global__FacetsAssignment_1");
			builder.put(grammarAccess.getS_GlobalAccess().getBlockAssignment_2_0(), "rule__S_Global__BlockAssignment_2_0");
			builder.put(grammarAccess.getS_SpeciesAccess().getKeyAssignment_0(), "rule__S_Species__KeyAssignment_0");
			builder.put(grammarAccess.getS_SpeciesAccess().getFirstFacetAssignment_1(), "rule__S_Species__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_SpeciesAccess().getNameAssignment_2(), "rule__S_Species__NameAssignment_2");
			builder.put(grammarAccess.getS_SpeciesAccess().getFacetsAssignment_3(), "rule__S_Species__FacetsAssignment_3");
			builder.put(grammarAccess.getS_SpeciesAccess().getBlockAssignment_4_0(), "rule__S_Species__BlockAssignment_4_0");
			builder.put(grammarAccess.getS_ExperimentAccess().getKeyAssignment_0(), "rule__S_Experiment__KeyAssignment_0");
			builder.put(grammarAccess.getS_ExperimentAccess().getFirstFacetAssignment_1(), "rule__S_Experiment__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_ExperimentAccess().getNameAssignment_2(), "rule__S_Experiment__NameAssignment_2");
			builder.put(grammarAccess.getS_ExperimentAccess().getFacetsAssignment_3(), "rule__S_Experiment__FacetsAssignment_3");
			builder.put(grammarAccess.getS_ExperimentAccess().getBlockAssignment_4_0(), "rule__S_Experiment__BlockAssignment_4_0");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getKeyAssignment_0(), "rule__S_1Expr_Facets_BlockOrEnd__KeyAssignment_0");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getFirstFacetAssignment_1(), "rule__S_1Expr_Facets_BlockOrEnd__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getExprAssignment_2(), "rule__S_1Expr_Facets_BlockOrEnd__ExprAssignment_2");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getFacetsAssignment_3(), "rule__S_1Expr_Facets_BlockOrEnd__FacetsAssignment_3");
			builder.put(grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getBlockAssignment_4_0(), "rule__S_1Expr_Facets_BlockOrEnd__BlockAssignment_4_0");
			builder.put(grammarAccess.getS_DoAccess().getKeyAssignment_0(), "rule__S_Do__KeyAssignment_0");
			builder.put(grammarAccess.getS_DoAccess().getFirstFacetAssignment_1(), "rule__S_Do__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_DoAccess().getExprAssignment_2(), "rule__S_Do__ExprAssignment_2");
			builder.put(grammarAccess.getS_DoAccess().getFacetsAssignment_3(), "rule__S_Do__FacetsAssignment_3");
			builder.put(grammarAccess.getS_DoAccess().getBlockAssignment_4_0(), "rule__S_Do__BlockAssignment_4_0");
			builder.put(grammarAccess.getS_LoopAccess().getKeyAssignment_0(), "rule__S_Loop__KeyAssignment_0");
			builder.put(grammarAccess.getS_LoopAccess().getNameAssignment_1(), "rule__S_Loop__NameAssignment_1");
			builder.put(grammarAccess.getS_LoopAccess().getFacetsAssignment_2(), "rule__S_Loop__FacetsAssignment_2");
			builder.put(grammarAccess.getS_LoopAccess().getBlockAssignment_3(), "rule__S_Loop__BlockAssignment_3");
			builder.put(grammarAccess.getS_IfAccess().getKeyAssignment_0(), "rule__S_If__KeyAssignment_0");
			builder.put(grammarAccess.getS_IfAccess().getFirstFacetAssignment_1(), "rule__S_If__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_IfAccess().getExprAssignment_2(), "rule__S_If__ExprAssignment_2");
			builder.put(grammarAccess.getS_IfAccess().getBlockAssignment_3(), "rule__S_If__BlockAssignment_3");
			builder.put(grammarAccess.getS_IfAccess().getElseAssignment_4_1(), "rule__S_If__ElseAssignment_4_1");
			builder.put(grammarAccess.getS_TryAccess().getKeyAssignment_0(), "rule__S_Try__KeyAssignment_0");
			builder.put(grammarAccess.getS_TryAccess().getBlockAssignment_1(), "rule__S_Try__BlockAssignment_1");
			builder.put(grammarAccess.getS_TryAccess().getCatchAssignment_2_1(), "rule__S_Try__CatchAssignment_2_1");
			builder.put(grammarAccess.getS_OtherAccess().getKeyAssignment_0(), "rule__S_Other__KeyAssignment_0");
			builder.put(grammarAccess.getS_OtherAccess().getFacetsAssignment_1(), "rule__S_Other__FacetsAssignment_1");
			builder.put(grammarAccess.getS_OtherAccess().getBlockAssignment_2_0(), "rule__S_Other__BlockAssignment_2_0");
			builder.put(grammarAccess.getS_ReturnAccess().getKeyAssignment_0(), "rule__S_Return__KeyAssignment_0");
			builder.put(grammarAccess.getS_ReturnAccess().getFirstFacetAssignment_1(), "rule__S_Return__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_ReturnAccess().getExprAssignment_2(), "rule__S_Return__ExprAssignment_2");
			builder.put(grammarAccess.getS_ReflexAccess().getKeyAssignment_0(), "rule__S_Reflex__KeyAssignment_0");
			builder.put(grammarAccess.getS_ReflexAccess().getFirstFacetAssignment_1_0(), "rule__S_Reflex__FirstFacetAssignment_1_0");
			builder.put(grammarAccess.getS_ReflexAccess().getNameAssignment_1_1(), "rule__S_Reflex__NameAssignment_1_1");
			builder.put(grammarAccess.getS_ReflexAccess().getExprAssignment_2_2(), "rule__S_Reflex__ExprAssignment_2_2");
			builder.put(grammarAccess.getS_ReflexAccess().getBlockAssignment_3(), "rule__S_Reflex__BlockAssignment_3");
			builder.put(grammarAccess.getS_DefinitionAccess().getTkeyAssignment_0(), "rule__S_Definition__TkeyAssignment_0");
			builder.put(grammarAccess.getS_DefinitionAccess().getFirstFacetAssignment_1(), "rule__S_Definition__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_DefinitionAccess().getNameAssignment_2(), "rule__S_Definition__NameAssignment_2");
			builder.put(grammarAccess.getS_DefinitionAccess().getArgsAssignment_3_1(), "rule__S_Definition__ArgsAssignment_3_1");
			builder.put(grammarAccess.getS_DefinitionAccess().getFacetsAssignment_4(), "rule__S_Definition__FacetsAssignment_4");
			builder.put(grammarAccess.getS_DefinitionAccess().getBlockAssignment_5_0(), "rule__S_Definition__BlockAssignment_5_0");
			builder.put(grammarAccess.getS_ActionAccess().getKeyAssignment_1(), "rule__S_Action__KeyAssignment_1");
			builder.put(grammarAccess.getS_ActionAccess().getFirstFacetAssignment_2(), "rule__S_Action__FirstFacetAssignment_2");
			builder.put(grammarAccess.getS_ActionAccess().getNameAssignment_3(), "rule__S_Action__NameAssignment_3");
			builder.put(grammarAccess.getS_ActionAccess().getArgsAssignment_4_1(), "rule__S_Action__ArgsAssignment_4_1");
			builder.put(grammarAccess.getS_ActionAccess().getFacetsAssignment_5(), "rule__S_Action__FacetsAssignment_5");
			builder.put(grammarAccess.getS_ActionAccess().getBlockAssignment_6_0(), "rule__S_Action__BlockAssignment_6_0");
			builder.put(grammarAccess.getS_VarAccess().getKeyAssignment_1(), "rule__S_Var__KeyAssignment_1");
			builder.put(grammarAccess.getS_VarAccess().getFirstFacetAssignment_2(), "rule__S_Var__FirstFacetAssignment_2");
			builder.put(grammarAccess.getS_VarAccess().getNameAssignment_3(), "rule__S_Var__NameAssignment_3");
			builder.put(grammarAccess.getS_VarAccess().getFacetsAssignment_4(), "rule__S_Var__FacetsAssignment_4");
			builder.put(grammarAccess.getS_DirectAssignmentAccess().getExprAssignment_0_0(), "rule__S_DirectAssignment__ExprAssignment_0_0");
			builder.put(grammarAccess.getS_DirectAssignmentAccess().getKeyAssignment_0_1(), "rule__S_DirectAssignment__KeyAssignment_0_1");
			builder.put(grammarAccess.getS_DirectAssignmentAccess().getValueAssignment_0_2(), "rule__S_DirectAssignment__ValueAssignment_0_2");
			builder.put(grammarAccess.getS_DirectAssignmentAccess().getFacetsAssignment_0_3(), "rule__S_DirectAssignment__FacetsAssignment_0_3");
			builder.put(grammarAccess.getS_SetAccess().getKeyAssignment_0(), "rule__S_Set__KeyAssignment_0");
			builder.put(grammarAccess.getS_SetAccess().getExprAssignment_1(), "rule__S_Set__ExprAssignment_1");
			builder.put(grammarAccess.getS_SetAccess().getValueAssignment_3(), "rule__S_Set__ValueAssignment_3");
			builder.put(grammarAccess.getS_EquationsAccess().getKeyAssignment_0(), "rule__S_Equations__KeyAssignment_0");
			builder.put(grammarAccess.getS_EquationsAccess().getNameAssignment_1(), "rule__S_Equations__NameAssignment_1");
			builder.put(grammarAccess.getS_EquationsAccess().getFacetsAssignment_2(), "rule__S_Equations__FacetsAssignment_2");
			builder.put(grammarAccess.getS_EquationsAccess().getEquationsAssignment_3_0_1_0(), "rule__S_Equations__EquationsAssignment_3_0_1_0");
			builder.put(grammarAccess.getS_EquationAccess().getExprAssignment_0(), "rule__S_Equation__ExprAssignment_0");
			builder.put(grammarAccess.getS_EquationAccess().getKeyAssignment_1(), "rule__S_Equation__KeyAssignment_1");
			builder.put(grammarAccess.getS_EquationAccess().getValueAssignment_2(), "rule__S_Equation__ValueAssignment_2");
			builder.put(grammarAccess.getS_SolveAccess().getKeyAssignment_0(), "rule__S_Solve__KeyAssignment_0");
			builder.put(grammarAccess.getS_SolveAccess().getFirstFacetAssignment_1(), "rule__S_Solve__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_SolveAccess().getExprAssignment_2(), "rule__S_Solve__ExprAssignment_2");
			builder.put(grammarAccess.getS_SolveAccess().getFacetsAssignment_3(), "rule__S_Solve__FacetsAssignment_3");
			builder.put(grammarAccess.getS_SolveAccess().getBlockAssignment_4_0(), "rule__S_Solve__BlockAssignment_4_0");
			builder.put(grammarAccess.getS_DisplayAccess().getKeyAssignment_0(), "rule__S_Display__KeyAssignment_0");
			builder.put(grammarAccess.getS_DisplayAccess().getFirstFacetAssignment_1(), "rule__S_Display__FirstFacetAssignment_1");
			builder.put(grammarAccess.getS_DisplayAccess().getNameAssignment_2(), "rule__S_Display__NameAssignment_2");
			builder.put(grammarAccess.getS_DisplayAccess().getFacetsAssignment_3(), "rule__S_Display__FacetsAssignment_3");
			builder.put(grammarAccess.getS_DisplayAccess().getBlockAssignment_4(), "rule__S_Display__BlockAssignment_4");
			builder.put(grammarAccess.getDisplayBlockAccess().getStatementsAssignment_2(), "rule__DisplayBlock__StatementsAssignment_2");
			builder.put(grammarAccess.getSpeciesOrGridDisplayStatementAccess().getKeyAssignment_0(), "rule__SpeciesOrGridDisplayStatement__KeyAssignment_0");
			builder.put(grammarAccess.getSpeciesOrGridDisplayStatementAccess().getExprAssignment_1(), "rule__SpeciesOrGridDisplayStatement__ExprAssignment_1");
			builder.put(grammarAccess.getSpeciesOrGridDisplayStatementAccess().getFacetsAssignment_2(), "rule__SpeciesOrGridDisplayStatement__FacetsAssignment_2");
			builder.put(grammarAccess.getSpeciesOrGridDisplayStatementAccess().getBlockAssignment_3_0(), "rule__SpeciesOrGridDisplayStatement__BlockAssignment_3_0");
			builder.put(grammarAccess.getActionArgumentsAccess().getArgsAssignment_0(), "rule__ActionArguments__ArgsAssignment_0");
			builder.put(grammarAccess.getActionArgumentsAccess().getArgsAssignment_1_1(), "rule__ActionArguments__ArgsAssignment_1_1");
			builder.put(grammarAccess.getArgumentDefinitionAccess().getTypeAssignment_0(), "rule__ArgumentDefinition__TypeAssignment_0");
			builder.put(grammarAccess.getArgumentDefinitionAccess().getNameAssignment_1(), "rule__ArgumentDefinition__NameAssignment_1");
			builder.put(grammarAccess.getArgumentDefinitionAccess().getDefaultAssignment_2_1(), "rule__ArgumentDefinition__DefaultAssignment_2_1");
			builder.put(grammarAccess.getClassicFacetAccess().getKeyAssignment_0_0(), "rule__ClassicFacet__KeyAssignment_0_0");
			builder.put(grammarAccess.getClassicFacetAccess().getKeyAssignment_0_1(), "rule__ClassicFacet__KeyAssignment_0_1");
			builder.put(grammarAccess.getClassicFacetAccess().getKeyAssignment_0_2(), "rule__ClassicFacet__KeyAssignment_0_2");
			builder.put(grammarAccess.getClassicFacetAccess().getExprAssignment_1(), "rule__ClassicFacet__ExprAssignment_1");
			builder.put(grammarAccess.getDefinitionFacetAccess().getKeyAssignment_0(), "rule__DefinitionFacet__KeyAssignment_0");
			builder.put(grammarAccess.getDefinitionFacetAccess().getNameAssignment_1(), "rule__DefinitionFacet__NameAssignment_1");
			builder.put(grammarAccess.getFunctionFacetAccess().getKeyAssignment_0(), "rule__FunctionFacet__KeyAssignment_0");
			builder.put(grammarAccess.getFunctionFacetAccess().getExprAssignment_1_0_0(), "rule__FunctionFacet__ExprAssignment_1_0_0");
			builder.put(grammarAccess.getFunctionFacetAccess().getExprAssignment_1_1_1(), "rule__FunctionFacet__ExprAssignment_1_1_1");
			builder.put(grammarAccess.getTypeFacetAccess().getKeyAssignment_0(), "rule__TypeFacet__KeyAssignment_0");
			builder.put(grammarAccess.getTypeFacetAccess().getExprAssignment_1_0_0(), "rule__TypeFacet__ExprAssignment_1_0_0");
			builder.put(grammarAccess.getTypeFacetAccess().getExprAssignment_1_1(), "rule__TypeFacet__ExprAssignment_1_1");
			builder.put(grammarAccess.getActionFacetAccess().getKeyAssignment_0(), "rule__ActionFacet__KeyAssignment_0");
			builder.put(grammarAccess.getActionFacetAccess().getExprAssignment_1_0(), "rule__ActionFacet__ExprAssignment_1_0");
			builder.put(grammarAccess.getActionFacetAccess().getBlockAssignment_1_1(), "rule__ActionFacet__BlockAssignment_1_1");
			builder.put(grammarAccess.getVarFacetAccess().getKeyAssignment_0(), "rule__VarFacet__KeyAssignment_0");
			builder.put(grammarAccess.getVarFacetAccess().getExprAssignment_1(), "rule__VarFacet__ExprAssignment_1");
			builder.put(grammarAccess.getBlockAccess().getStatementsAssignment_2_0(), "rule__Block__StatementsAssignment_2_0");
			builder.put(grammarAccess.getArgumentPairAccess().getOpAssignment_0_0_0_0(), "rule__ArgumentPair__OpAssignment_0_0_0_0");
			builder.put(grammarAccess.getArgumentPairAccess().getOpAssignment_0_0_1_0(), "rule__ArgumentPair__OpAssignment_0_0_1_0");
			builder.put(grammarAccess.getArgumentPairAccess().getRightAssignment_1(), "rule__ArgumentPair__RightAssignment_1");
			builder.put(grammarAccess.getPairAccess().getOpAssignment_1_1(), "rule__Pair__OpAssignment_1_1");
			builder.put(grammarAccess.getPairAccess().getRightAssignment_1_2(), "rule__Pair__RightAssignment_1_2");
			builder.put(grammarAccess.getIfAccess().getOpAssignment_1_1(), "rule__If__OpAssignment_1_1");
			builder.put(grammarAccess.getIfAccess().getRightAssignment_1_2(), "rule__If__RightAssignment_1_2");
			builder.put(grammarAccess.getIfAccess().getIfFalseAssignment_1_3_1(), "rule__If__IfFalseAssignment_1_3_1");
			builder.put(grammarAccess.getOrAccess().getOpAssignment_1_1(), "rule__Or__OpAssignment_1_1");
			builder.put(grammarAccess.getOrAccess().getRightAssignment_1_2(), "rule__Or__RightAssignment_1_2");
			builder.put(grammarAccess.getAndAccess().getOpAssignment_1_1(), "rule__And__OpAssignment_1_1");
			builder.put(grammarAccess.getAndAccess().getRightAssignment_1_2(), "rule__And__RightAssignment_1_2");
			builder.put(grammarAccess.getCastAccess().getOpAssignment_1_0_1(), "rule__Cast__OpAssignment_1_0_1");
			builder.put(grammarAccess.getCastAccess().getRightAssignment_1_1_0(), "rule__Cast__RightAssignment_1_1_0");
			builder.put(grammarAccess.getCastAccess().getRightAssignment_1_1_1_1(), "rule__Cast__RightAssignment_1_1_1_1");
			builder.put(grammarAccess.getComparisonAccess().getOpAssignment_1_0_1(), "rule__Comparison__OpAssignment_1_0_1");
			builder.put(grammarAccess.getComparisonAccess().getRightAssignment_1_1(), "rule__Comparison__RightAssignment_1_1");
			builder.put(grammarAccess.getAdditionAccess().getOpAssignment_1_0_1(), "rule__Addition__OpAssignment_1_0_1");
			builder.put(grammarAccess.getAdditionAccess().getRightAssignment_1_1(), "rule__Addition__RightAssignment_1_1");
			builder.put(grammarAccess.getMultiplicationAccess().getOpAssignment_1_0_1(), "rule__Multiplication__OpAssignment_1_0_1");
			builder.put(grammarAccess.getMultiplicationAccess().getRightAssignment_1_1(), "rule__Multiplication__RightAssignment_1_1");
			builder.put(grammarAccess.getExponentiationAccess().getOpAssignment_1_0_1(), "rule__Exponentiation__OpAssignment_1_0_1");
			builder.put(grammarAccess.getExponentiationAccess().getRightAssignment_1_1(), "rule__Exponentiation__RightAssignment_1_1");
			builder.put(grammarAccess.getBinaryAccess().getOpAssignment_1_0_1(), "rule__Binary__OpAssignment_1_0_1");
			builder.put(grammarAccess.getBinaryAccess().getRightAssignment_1_1(), "rule__Binary__RightAssignment_1_1");
			builder.put(grammarAccess.getUnitAccess().getOpAssignment_1_0_1(), "rule__Unit__OpAssignment_1_0_1");
			builder.put(grammarAccess.getUnitAccess().getRightAssignment_1_1(), "rule__Unit__RightAssignment_1_1");
			builder.put(grammarAccess.getUnaryAccess().getOpAssignment_1_1_0_0(), "rule__Unary__OpAssignment_1_1_0_0");
			builder.put(grammarAccess.getUnaryAccess().getRightAssignment_1_1_0_1(), "rule__Unary__RightAssignment_1_1_0_1");
			builder.put(grammarAccess.getUnaryAccess().getOpAssignment_1_1_1_0(), "rule__Unary__OpAssignment_1_1_1_0");
			builder.put(grammarAccess.getUnaryAccess().getRightAssignment_1_1_1_1(), "rule__Unary__RightAssignment_1_1_1_1");
			builder.put(grammarAccess.getAccessAccess().getOpAssignment_1_1_0_0(), "rule__Access__OpAssignment_1_1_0_0");
			builder.put(grammarAccess.getAccessAccess().getRightAssignment_1_1_0_1(), "rule__Access__RightAssignment_1_1_0_1");
			builder.put(grammarAccess.getAccessAccess().getOpAssignment_1_1_1_0(), "rule__Access__OpAssignment_1_1_1_0");
			builder.put(grammarAccess.getAccessAccess().getRightAssignment_1_1_1_1(), "rule__Access__RightAssignment_1_1_1_1");
			builder.put(grammarAccess.getPrimaryAccess().getExprsAssignment_3_2(), "rule__Primary__ExprsAssignment_3_2");
			builder.put(grammarAccess.getPrimaryAccess().getLeftAssignment_4_2(), "rule__Primary__LeftAssignment_4_2");
			builder.put(grammarAccess.getPrimaryAccess().getOpAssignment_4_3(), "rule__Primary__OpAssignment_4_3");
			builder.put(grammarAccess.getPrimaryAccess().getRightAssignment_4_4(), "rule__Primary__RightAssignment_4_4");
			builder.put(grammarAccess.getPrimaryAccess().getZAssignment_4_5_1(), "rule__Primary__ZAssignment_4_5_1");
			builder.put(grammarAccess.getFunctionAccess().getLeftAssignment_1(), "rule__Function__LeftAssignment_1");
			builder.put(grammarAccess.getFunctionAccess().getTypeAssignment_2(), "rule__Function__TypeAssignment_2");
			builder.put(grammarAccess.getFunctionAccess().getRightAssignment_4(), "rule__Function__RightAssignment_4");
			builder.put(grammarAccess.getExpressionListAccess().getExprsAssignment_0_0(), "rule__ExpressionList__ExprsAssignment_0_0");
			builder.put(grammarAccess.getExpressionListAccess().getExprsAssignment_0_1_1(), "rule__ExpressionList__ExprsAssignment_0_1_1");
			builder.put(grammarAccess.getExpressionListAccess().getExprsAssignment_1_0(), "rule__ExpressionList__ExprsAssignment_1_0");
			builder.put(grammarAccess.getExpressionListAccess().getExprsAssignment_1_1_1(), "rule__ExpressionList__ExprsAssignment_1_1_1");
			builder.put(grammarAccess.getParameterAccess().getBuiltInFacetKeyAssignment_1_0(), "rule__Parameter__BuiltInFacetKeyAssignment_1_0");
			builder.put(grammarAccess.getParameterAccess().getLeftAssignment_1_1_0(), "rule__Parameter__LeftAssignment_1_1_0");
			builder.put(grammarAccess.getParameterAccess().getRightAssignment_2(), "rule__Parameter__RightAssignment_2");
			builder.put(grammarAccess.getUnitRefAccess().getRefAssignment_1(), "rule__UnitRef__RefAssignment_1");
			builder.put(grammarAccess.getVariableRefAccess().getRefAssignment_1(), "rule__VariableRef__RefAssignment_1");
			builder.put(grammarAccess.getTypeRefAccess().getRefAssignment_0_1_0(), "rule__TypeRef__RefAssignment_0_1_0");
			builder.put(grammarAccess.getTypeRefAccess().getParameterAssignment_0_1_1(), "rule__TypeRef__ParameterAssignment_0_1_1");
			builder.put(grammarAccess.getTypeRefAccess().getParameterAssignment_1_1_1(), "rule__TypeRef__ParameterAssignment_1_1_1");
			builder.put(grammarAccess.getTypeInfoAccess().getFirstAssignment_1(), "rule__TypeInfo__FirstAssignment_1");
			builder.put(grammarAccess.getTypeInfoAccess().getSecondAssignment_2_1(), "rule__TypeInfo__SecondAssignment_2_1");
			builder.put(grammarAccess.getSkillRefAccess().getRefAssignment_1(), "rule__SkillRef__RefAssignment_1");
			builder.put(grammarAccess.getActionRefAccess().getRefAssignment_1(), "rule__ActionRef__RefAssignment_1");
			builder.put(grammarAccess.getEquationRefAccess().getRefAssignment_1(), "rule__EquationRef__RefAssignment_1");
			builder.put(grammarAccess.getUnitFakeDefinitionAccess().getNameAssignment_1(), "rule__UnitFakeDefinition__NameAssignment_1");
			builder.put(grammarAccess.getTypeFakeDefinitionAccess().getNameAssignment_1(), "rule__TypeFakeDefinition__NameAssignment_1");
			builder.put(grammarAccess.getActionFakeDefinitionAccess().getNameAssignment_1(), "rule__ActionFakeDefinition__NameAssignment_1");
			builder.put(grammarAccess.getSkillFakeDefinitionAccess().getNameAssignment_1(), "rule__SkillFakeDefinition__NameAssignment_1");
			builder.put(grammarAccess.getVarFakeDefinitionAccess().getNameAssignment_1(), "rule__VarFakeDefinition__NameAssignment_1");
			builder.put(grammarAccess.getEquationFakeDefinitionAccess().getNameAssignment_1(), "rule__EquationFakeDefinition__NameAssignment_1");
			builder.put(grammarAccess.getTerminalExpressionAccess().getOpAssignment_1_1(), "rule__TerminalExpression__OpAssignment_1_1");
			builder.put(grammarAccess.getTerminalExpressionAccess().getOpAssignment_2_1(), "rule__TerminalExpression__OpAssignment_2_1");
			builder.put(grammarAccess.getTerminalExpressionAccess().getOpAssignment_3_1(), "rule__TerminalExpression__OpAssignment_3_1");
			builder.put(grammarAccess.getTerminalExpressionAccess().getOpAssignment_4_1(), "rule__TerminalExpression__OpAssignment_4_1");
			builder.put(grammarAccess.getStringLiteralAccess().getOpAssignment(), "rule__StringLiteral__OpAssignment");
		}
	}
	
	/** The name mappings. */
	@Inject
	private NameMappings nameMappings;

	/** The grammar access. */
	@Inject
	private GamlGrammarAccess grammarAccess;

	@Override
	protected InternalGamlParser createParser() {
		InternalGamlParser result = new InternalGamlParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}

	@Override
	protected String getRuleName(AbstractElement element) {
		return nameMappings.getRuleName(element);
	}

	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}

	/**
	 * Gets the grammar access.
	 *
	 * @return the grammar access
	 */
	public GamlGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}

	/**
	 * Sets the grammar access.
	 *
	 * @param grammarAccess the new grammar access
	 */
	public void setGrammarAccess(GamlGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
	/**
	 * Gets the name mappings.
	 *
	 * @return the name mappings
	 */
	public NameMappings getNameMappings() {
		return nameMappings;
	}
	
	/**
	 * Sets the name mappings.
	 *
	 * @param nameMappings the new name mappings
	 */
	public void setNameMappings(NameMappings nameMappings) {
		this.nameMappings = nameMappings;
	}
}
