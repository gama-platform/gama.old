package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import java.util.List;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.GroupAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;

@SuppressWarnings("restriction")
public class AbstractGamlSyntacticSequencer extends AbstractSyntacticSequencer {

	protected GamlGrammarAccess grammarAccess;
	protected AbstractElementAlias match_ClassicStatement___IDTerminalRuleCall_1_0_ColonKeyword_1_1__q;
	protected AbstractElementAlias match_DefinitionStatement___LeftParenthesisKeyword_3_0_RightParenthesisKeyword_3_2__q;
	protected AbstractElementAlias match_IfStatement_ConditionKeyword_1_q;
	protected AbstractElementAlias match_Primary_LeftParenthesisKeyword_2_0_a;
	protected AbstractElementAlias match_Primary_LeftParenthesisKeyword_2_0_p;
	
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (GamlGrammarAccess) access;
		match_ClassicStatement___IDTerminalRuleCall_1_0_ColonKeyword_1_1__q = new GroupAlias(false, true, new TokenAlias(false, false, grammarAccess.getClassicStatementAccess().getIDTerminalRuleCall_1_0()), new TokenAlias(false, false, grammarAccess.getClassicStatementAccess().getColonKeyword_1_1()));
		match_DefinitionStatement___LeftParenthesisKeyword_3_0_RightParenthesisKeyword_3_2__q = new GroupAlias(false, true, new TokenAlias(false, false, grammarAccess.getDefinitionStatementAccess().getLeftParenthesisKeyword_3_0()), new TokenAlias(false, false, grammarAccess.getDefinitionStatementAccess().getRightParenthesisKeyword_3_2()));
		match_IfStatement_ConditionKeyword_1_q = new TokenAlias(false, true, grammarAccess.getIfStatementAccess().getConditionKeyword_1());
		match_Primary_LeftParenthesisKeyword_2_0_a = new TokenAlias(true, true, grammarAccess.getPrimaryAccess().getLeftParenthesisKeyword_2_0());
		match_Primary_LeftParenthesisKeyword_2_0_p = new TokenAlias(true, false, grammarAccess.getPrimaryAccess().getLeftParenthesisKeyword_2_0());
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if(ruleCall.getRule() == grammarAccess.getIDRule())
			return getIDToken(semanticObject, ruleCall, node);
		return "";
	}
	
	protected String getIDToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		if (node != null)
			return getTokenText(node);
		return "";
	}
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if(match_ClassicStatement___IDTerminalRuleCall_1_0_ColonKeyword_1_1__q.equals(syntax))
				emit_ClassicStatement___IDTerminalRuleCall_1_0_ColonKeyword_1_1__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_DefinitionStatement___LeftParenthesisKeyword_3_0_RightParenthesisKeyword_3_2__q.equals(syntax))
				emit_DefinitionStatement___LeftParenthesisKeyword_3_0_RightParenthesisKeyword_3_2__q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_IfStatement_ConditionKeyword_1_q.equals(syntax))
				emit_IfStatement_ConditionKeyword_1_q(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_Primary_LeftParenthesisKeyword_2_0_a.equals(syntax))
				emit_Primary_LeftParenthesisKeyword_2_0_a(semanticObject, getLastNavigableState(), syntaxNodes);
			else if(match_Primary_LeftParenthesisKeyword_2_0_p.equals(syntax))
				emit_Primary_LeftParenthesisKeyword_2_0_p(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Syntax:
	 *     (ID ':')?
	 */
	protected void emit_ClassicStatement___IDTerminalRuleCall_1_0_ColonKeyword_1_1__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     ('(' ')')?
	 */
	protected void emit_DefinitionStatement___LeftParenthesisKeyword_3_0_RightParenthesisKeyword_3_2__q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     'condition:'?
	 */
	protected void emit_IfStatement_ConditionKeyword_1_q(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     '('*
	 */
	protected void emit_Primary_LeftParenthesisKeyword_2_0_a(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Syntax:
	 *     '('+
	 */
	protected void emit_Primary_LeftParenthesisKeyword_2_0_p(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
}
