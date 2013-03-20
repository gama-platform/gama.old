package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import java.util.List;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AlternativeAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;

@SuppressWarnings("restriction")
public class AbstractGamlSyntacticSequencer extends AbstractSyntacticSequencer {

	protected GamlGrammarAccess grammarAccess;
	protected AbstractElementAlias match_S_1Expr_Facets_BlockOrEnd_FirstFacetKeyParserRuleCall_1_q;
	protected AbstractElementAlias match_S_Action_NameKeyword_2_q;
	protected AbstractElementAlias match_S_Definition_NameKeyword_1_q;
	protected AbstractElementAlias match_S_Do_ActionKeyword_1_q;
	protected AbstractElementAlias match_S_Experiment_NameKeyword_1_q;
	protected AbstractElementAlias match_S_If_ConditionKeyword_1_q;
	protected AbstractElementAlias match_S_Reflex_NameKeyword_1_q;
	protected AbstractElementAlias match_S_Set_LessThanSignHyphenMinusKeyword_3_1_or_ValueKeyword_3_0;
	protected AbstractElementAlias match_S_Set___NameKeyword_1_1_or_VarKeyword_1_0__q;
	protected AbstractElementAlias match_S_Species_NameKeyword_1_q;
	protected AbstractElementAlias match_S_Var_NameKeyword_2_q;
	
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (GamlGrammarAccess) access;
		match_S_1Expr_Facets_BlockOrEnd_FirstFacetKeyParserRuleCall_1_q = new TokenAlias(false, true, grammarAccess.getS_1Expr_Facets_BlockOrEndAccess().getFirstFacetKeyParserRuleCall_1());
		match_S_Action_NameKeyword_2_q = new TokenAlias(false, true, grammarAccess.getS_ActionAccess().getNameKeyword_2());
		match_S_Definition_NameKeyword_1_q = new TokenAlias(false, true, grammarAccess.getS_DefinitionAccess().getNameKeyword_1());
		match_S_Do_ActionKeyword_1_q = new TokenAlias(false, true, grammarAccess.getS_DoAccess().getActionKeyword_1());
		match_S_Experiment_NameKeyword_1_q = new TokenAlias(false, true, grammarAccess.getS_ExperimentAccess().getNameKeyword_1());
		match_S_If_ConditionKeyword_1_q = new TokenAlias(false, true, grammarAccess.getS_IfAccess().getConditionKeyword_1());
		match_S_Reflex_NameKeyword_1_q = new TokenAlias(false, true, grammarAccess.getS_ReflexAccess().getNameKeyword_1());
		match_S_Set_LessThanSignHyphenMinusKeyword_3_1_or_ValueKeyword_3_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getS_SetAccess().getLessThanSignHyphenMinusKeyword_3_1()), new TokenAlias(false, false, grammarAccess.getS_SetAccess().getValueKeyword_3_0()));
		match_S_Set___NameKeyword_1_1_or_VarKeyword_1_0__q = new AlternativeAlias(false, true, new TokenAlias(false, false, grammarAccess.getS_SetAccess().getNameKeyword_1_1()), new TokenAlias(false, false, grammarAccess.getS_SetAccess().getVarKeyword_1_0()));
		match_S_Species_NameKeyword_1_q = new TokenAlias(false, true, grammarAccess.getS_SpeciesAccess().getNameKeyword_1());
		match_S_Var_NameKeyword_2_q = new TokenAlias(false, true, grammarAccess.getS_VarAccess().getNameKeyword_2());
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if(ruleCall.getRule() == grammarAccess.getFirstFacetKeyRule())
			return getFirstFacetKeyToken(semanticObject, ruleCall, node);
		return "";
	}
	
	protected String getFirstFacetKeyToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "name:";
	}
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if(match_S_1Expr_Facets_BlockOrEnd_FirstFacetKeyParserRuleCall_1_q.equals(syntax))
				emit_S_1Expr_Facets_BlockOrEnd_FirstFacetKeyParserRuleCall_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Action_NameKeyword_2_q.equals(syntax))
				emit_S_Action_NameKeyword_2_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Definition_NameKeyword_1_q.equals(syntax))
				emit_S_Definition_NameKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Do_ActionKeyword_1_q.equals(syntax))
				emit_S_Do_ActionKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Experiment_NameKeyword_1_q.equals(syntax))
				emit_S_Experiment_NameKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_If_ConditionKeyword_1_q.equals(syntax))
				emit_S_If_ConditionKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Reflex_NameKeyword_1_q.equals(syntax))
				emit_S_Reflex_NameKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Set_LessThanSignHyphenMinusKeyword_3_1_or_ValueKeyword_3_0.equals(syntax))
				emit_S_Set_LessThanSignHyphenMinusKeyword_3_1_or_ValueKeyword_3_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Set___NameKeyword_1_1_or_VarKeyword_1_0__q.equals(syntax))
				emit_S_Set___NameKeyword_1_1_or_VarKeyword_1_0__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Species_NameKeyword_1_q.equals(syntax))
				emit_S_Species_NameKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_S_Var_NameKeyword_2_q.equals(syntax))
				emit_S_Var_NameKeyword_2_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Syntax:
	 *     FirstFacetKey?
	 */
	protected void emit_S_1Expr_Facets_BlockOrEnd_FirstFacetKeyParserRuleCall_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'name:'?
	 */
	protected void emit_S_Action_NameKeyword_2_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'name:'?
	 */
	protected void emit_S_Definition_NameKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'action:'?
	 */
	protected void emit_S_Do_ActionKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'name:'?
	 */
	protected void emit_S_Experiment_NameKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'condition:'?
	 */
	protected void emit_S_If_ConditionKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'name:'?
	 */
	protected void emit_S_Reflex_NameKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'value:' | '<-'
	 */
	protected void emit_S_Set_LessThanSignHyphenMinusKeyword_3_1_or_ValueKeyword_3_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     ('name:' | 'var:')?
	 */
	protected void emit_S_Set___NameKeyword_1_1_or_VarKeyword_1_0__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'name:'?
	 */
	protected void emit_S_Species_NameKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'name:'?
	 */
	protected void emit_S_Var_NameKeyword_2_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
}
